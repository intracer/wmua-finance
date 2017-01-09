package org.intracer.finance

import javax.mail.internet.InternetAddress

import play.api.data.validation.{Constraints, Invalid, Valid}

case class User(id: Option[Int],
                fullname: String,
                email: String,
                roles: String = "",
                password: Option[String] = None,
                lang: Option[String] = None,
                wikiAccount: Option[String] = None) {

  def emailLo = email.trim.toLowerCase

  def hasRole(role: String) = roles.contains(role)

  def hasAnyRole(otherRoles: Set[String]) = roles.split(",").toSet.intersect(otherRoles).nonEmpty

  def canEdit(otherUser: User) =
    hasRole(UserObj.ADMIN_ROLE) ||
      id == otherUser.id ||
      hasRole(UserObj.ROOT_ROLE)

  def canViewOrgInfo = hasAnyRole(Set("viewer")) || canContribute

  def canContribute = hasAnyRole(Set("contributor", "organizer")) || isAdmin

  def isAdmin = hasAnyRole(Set("admin", "root"))

  def description: String = Seq(fullname, wikiAccount.fold("")(u => "User:" + u), email).mkString(" / ")
}

object UserObj {
  val JURY_ROLE = "jury"
  val JURY_ROLES = Set(JURY_ROLE)
  val ORG_COM_ROLES = Set("organizer")
  val ADMIN_ROLE = "admin"
  val ROOT_ROLE = "root"
  val ADMIN_ROLES = Set(ADMIN_ROLE, ROOT_ROLE)
  val LANGS = Map("en" -> "English", "ru" -> "Русский", "uk" -> "Українська")

  def unapplyEdit(user: User): Option[(Int, String, Option[String], String, Option[String], Option[String], Option[String])] = {
    Some((user.id.get, user.fullname, user.wikiAccount, user.email, None, Some(user.roles), user.lang))
  }

  def applyEdit(id: Int, fullname: String, wikiAccount: Option[String], email: String, password: Option[String],
                roles: Option[String], contest: Option[Long], lang: Option[String]): User = {
    User(Some(id), fullname, email.trim.toLowerCase, roles.getOrElse(""), password, lang,
      wikiAccount = wikiAccount)
  }

  val emailConstraint = Constraints.emailAddress

  def parseList(usersText: String): Seq[User] = {
    InternetAddress.parse(usersText.replaceAll("\n", ","), false).map { internetAddress =>
      val address = internetAddress.getAddress

      Constraints.emailAddress(address) match {
        case Valid => User(id = None, fullname = Option(internetAddress.getPersonal).getOrElse(""), email = internetAddress.getAddress)
        case Invalid(errors) => User(id = None, fullname = "", email = "", wikiAccount = Some(internetAddress.getAddress))
      }
    }
  }
}

//object User {
//  def login(username: String, password: String): Option[User] = {
//    if (sha1(username + "/" + password) == "***REMOVED***") {
//      Some(new User(***REMOVED***))
//    } else {
//      None
//    }
//  }
//
//  def sha1(input: String) = {
//
//    val digest = MessageDigest.getInstance("SHA-1")
//
//    digest.update(input.getBytes(), 0, input.length())
//
//    new BigInteger(1, digest.digest()).toString(16)
//  }
//
//}