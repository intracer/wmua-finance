package org.intracer.finance.slick

import org.intracer.finance.{Account, Grant}
import slick.driver.H2Driver.api._

class Accounts(tag: Tag) extends Table[Account](tag, "ACCOUNT") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def code = column[String]("code")
  def name = column[String]("name")

  def codeIndex = index("acc_code", code, unique = true)
  def nameIndex = index("acc_name", name, unique = true)

  def * = (id.?, code, name) <> (Account.tupled, Account.unapply)

}
