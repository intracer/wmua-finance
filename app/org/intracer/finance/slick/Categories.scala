package org.intracer.finance.slick

import org.intracer.finance.CategoryF
import slick.driver.H2Driver.api._

class Categories(tag: Tag) extends Table[CategoryF](tag, "category") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def nameIndex = index("category_name", name, unique = true)

  def * = (id.?, name) <> (CategoryF.tupled, CategoryF.unapply)
}
