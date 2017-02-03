package org.intracer.finance.slick

import java.math.BigInteger
import java.security.MessageDigest

import org.intracer.finance.User

class UserDao() extends BaseDao {

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

  def sha1(input: String) = {
    val digest = MessageDigest.getInstance("SHA-1")

    digest.update(input.getBytes, 0, input.length())

    new BigInteger(1, digest.digest()).toString(16)
  }
}
