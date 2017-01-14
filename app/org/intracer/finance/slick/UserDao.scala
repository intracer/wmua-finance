package org.intracer.finance.slick

import java.math.BigInteger
import java.security.MessageDigest

import org.intracer.finance.User
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

class UserDao(val mwDb: FinDatabase, val query: TableQuery[Users], val driver: JdbcProfile) extends BaseDao {

  import driver.api._

  def insert(user: User): Int = db {
    query += user
  }

  def insertAll(users: Seq[User]): Unit = db {
    query.forceInsertAll(users)
  }

  def list: Seq[User] = db {
    query.sortBy(_.fullname).result
  }

  def byEmail(email: String): Option[User] = db {
    query.filter(_.email === email).result.headOption
  }

  def byId(id: Int): Option[User] = db {
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
