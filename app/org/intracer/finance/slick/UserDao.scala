package org.intracer.finance.slick

import java.math.BigInteger
import java.security.MessageDigest

import org.intracer.finance.User
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class UserDao(val mwDb: FinDatabase, val query: TableQuery[Users], val driver: JdbcProfile) {

  import driver.api._

  val db = mwDb.db

  def insert(user: User): Long = {
    db.run(query += user).await
  }

  def insertAll(users: Seq[User]): Unit = {
    db.run(query.forceInsertAll(users)).await
  }

  def list: Seq[User] = db.run(query.sortBy(_.fullname).result).await

  def byEmail(email: String): Option[User] =
    db.run(query.filter(_.email === email).result.headOption).await

  def byId(id: Int): Option[User] =
    db.run(query.filter(_.id === id).result.headOption).await

  def login(username: String, password: String): Option[User] = {
    byEmail(username).filter(user => {
      val passwordTrimmed = password.trim
      val inputHash = hash(user, passwordTrimmed)
      val dbHash = user.password.get
      inputHash == dbHash
    })
  }

  def hash(user: User, password: String): String = {
    sha1(password)
  }

  def sha1(input: String) = {
    val digest = MessageDigest.getInstance("SHA-1")

    digest.update(input.getBytes, 0, input.length())

    new BigInteger(1, digest.digest()).toString(16)
  }



}
