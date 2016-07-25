package org.intracer.finance.slick

import client.finance.GrantItem
import org.intracer.finance.Account
import slick.driver.H2Driver.api._
import squants._

class GrantItems(tag: Tag) extends Table[GrantItem](tag, "grant_item") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def number = column[String]("number")

  def numberIndex = index("gi_number", number, unique = true)

  def category = column[String]("category")

  def description = column[String]("description")

  def unit = column[String]("unit")

  def qty = column[String]("qty")

  def costPerUnit = column[String]("cost_per_unit")

  def totalCost = column[BigDecimal]("total_cost")

  def wmfContrib = column[Option[BigDecimal]]("wmf_contrib")

  def otherSources = column[Option[BigDecimal]]("other_sources")

  def notes = column[Option[String]]("notes")

  def * = (id.?, number, category, description, unit, qty, costPerUnit,
    totalCost, wmfContrib, otherSources, notes) <> (GrantItem.tupled, GrantItem.unapply)

}
