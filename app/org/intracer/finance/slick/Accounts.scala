package org.intracer.finance.slick

import org.intracer.finance.{Account, Grant}
import slick.driver.H2Driver.api._

class Accounts(tag: Tag) extends Table[Account](tag, "account") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def nameIndex = index("acc_name", name, unique = true)

  def * = (id.?, name) <> (Account.tupled, Account.unapply)

}
