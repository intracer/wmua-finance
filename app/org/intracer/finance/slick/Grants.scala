package org.intracer.finance.slick

import org.intracer.finance.Grant
import slick.driver.H2Driver.api._

class Grants(tag: Tag) extends Table[Grant](tag, "GRANT") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def code = column[String]("code")
  def name = column[String]("name")
  def url = column[String]("url")

  def codeIndex = index("grant_code", code, unique = true)
  def nameIndex = index("grant_name", name, unique = true)

  def * = (id.?, code, name, url) <> (Grant.tupled, Grant.unapply)

}
