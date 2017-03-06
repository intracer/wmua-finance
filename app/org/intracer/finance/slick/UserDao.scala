package org.intracer.finance.slick

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import org.intracer.finance.User
import play.api.libs.Codecs

class UserDao() extends BaseDao with IdentityService[User] {

  import driver.api._

  val query = TableQuery[Users]

  def insert(user: User): Int = run {
    query returning query.map(_.id) += user
  }

  def insertAll(users: Seq[User]): Unit = run {
    query.forceInsertAll(users)
  }

  def list: Seq[User] = run {
    query.sortBy(_.fullname).result
  }

  def count: Int = run {
    query.countDistinct.result
  }

  def byEmail(email: String): Option[User] = run {
    query.filter(_.email === email).result.headOption
  }

  def byId(id: Int): Option[User] = run {
    query.filter(_.id === id).result.headOption
  }

  def login(username: String, password: String): Option[User] = {
    byEmail(username).filter(user => {
      val passwordTrimmed = password.trim
      val inputHash = hash(user, passwordTrimmed)
      val dbHash = user.password.get
      inputHash == dbHash
    })
  }

  def hash(user: User, password: String): String =
    sha1(password)

  def sha1(input: String) = Codecs.sha1(input.getBytes)

  override def retrieve(loginInfo: LoginInfo) = {
    db.run {
      query
        .filter(_.email === loginInfo.providerKey)
        .result.headOption
    }
  }
}
