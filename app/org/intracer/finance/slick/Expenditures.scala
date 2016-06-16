package org.intracer.finance.slick

import _root_.slick.driver.H2Driver.api._
import org.intracer.finance._

class Expenditures(tag: Tag) extends Table[Expenditure](tag, "operation") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def date = column[String]("op_date")

  def amount = column[BigDecimal]("amount")

  def from = column[Int]("account_id")
  def categoryId = column[Int]("cat_id")
  def projectId = column[Int]("proj_id")

  def grantId = column[Option[Int]]("grant_id")

  def grantRow = column[Option[String]]("grant_row")
  def desc = column[String]("descr")

  def account = foreignKey("ACC_FK", categoryId, TableQuery[Accounts])(_.id)
  def category = foreignKey("CAT_FK", categoryId, TableQuery[Categories])(_.id)
  def projects = foreignKey("PROJ_FK", projectId, TableQuery[Projects])(_.id)
  def grants = foreignKey("GRANT_FK", grantId, TableQuery[Grants])(_.id)

  def * = (id.?, date, amount, from, categoryId, projectId, grantId, grantRow, desc) <> (Expenditures.fromDb, Expenditures.toDb)

}

object Expenditures {

  var categories: Map[Int, CategoryF] = _

  var projects: Map[Int, Project] = _

  var grants: Map[Int, Grant] = _

  var accounts: Map[Int, Account] = _

  def fromDb(t: (Option[Int], String, BigDecimal, Int, Int, Int, Option[Int], Option[String], String)) = {
    new Expenditure(t._1, t._2, t._3,  accounts(t._4), categories(t._5), projects(t._6), t._7.map(grants),  t._8, t._9, null)
  }

  def toDb(exp: Expenditure) = {
    Some((exp.id, exp.date, exp.amount, exp.from.id.get, exp.category.id.get, exp.project.id.get, exp.grant.flatMap(_.id), exp.grantRow, exp.desc))
  }

}