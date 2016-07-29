package org.intracer.finance.slick

import client.finance.GrantItem
import slick.driver.H2Driver.api._

class GrantItems(tag: Tag) extends Table[GrantItem](tag, "grant_item") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def grantId = column[Int]("grant_id")

  def number = column[String]("number")

  def description = column[String]("description")

  //  def category = column[String]("category")

  //  def unit = column[String]("unit")
  //
  //  def qty = column[String]("qty")
  //
  //  def costPerUnit = column[String]("cost_per_unit")

  def totalCost = column[BigDecimal]("total_cost")

  //  def wmfContrib = column[Option[BigDecimal]]("wmf_contrib")
  //
  //  def otherSources = column[Option[BigDecimal]]("other_sources")

  def notes = column[Option[String]]("notes")

  def * = (id.?, grantId.?, number, description, /*category, , unit, qty, costPerUnit*/
    totalCost, /*, wmfContrib, otherSources,*/ notes) <>(GrantItems.fromDb, GrantItems.toDb)

}

object GrantItems {
  def fromDb(v: (Option[Int], Option[Int], String, String, BigDecimal, Option[String])) = {
    new GrantItem(
      id = v._1,
      grantId = v._2,
      number = v._3,
      description = v._4,
      totalCost = v._5,
      notes = v._6
    )
  }

  def toDb(g: GrantItem) = {
    Some((
      g.id,
      g.grantId,
      g.number,
      g.description,
      g.totalCost,
      g.notes
      ))
  }

}