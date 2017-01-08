package org.intracer.finance.slick

import org.intracer.finance.User
import slick.driver.H2Driver.api._

class Users(tag: Tag) extends Table[User](tag, "user") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def fullname = column[String]("fullname")
  def email = column[String]("email")
  def roles = column[String]("roles")
  def password = column[Option[String]]("password")
  def lang = column[Option[String]]("lang")
  def wikiAccount = column[Option[String]]("wiki_account")

  def emailIndex = index("acc_email", email, unique = true)

  def * = (id.?, fullname, email, roles, password, lang, wikiAccount) <> (User.tupled, User.unapply)

}
