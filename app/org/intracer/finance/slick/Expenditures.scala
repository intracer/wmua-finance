package org.intracer.finance.slick

import java.sql.Timestamp

import _root_.slick.driver.H2Driver.api._
import org.intracer.finance._
import _root_.slick.profile.SqlProfile.ColumnOption.SqlType
import controllers.Global

class Expenditures(tag: Tag) extends Table[Expenditure](tag, "operation") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

//  def date = column[String]("op_date")

  def date = column[Timestamp]("op_date", SqlType("datetime "))

  def amount = column[BigDecimal]("amount")

  def from = column[Int]("account_id")
  def categoryId = column[Int]("cat_id")
  def projectId = column[Int]("proj_id")

  def grantId = column[Option[Int]]("grant_id")

  def grantRow = column[Option[String]]("grant_row")
  def descr = column[String]("descr")

  def account = foreignKey("ACC_FK", categoryId, TableQuery[Accounts])(_.id)
  def category = foreignKey("CAT_FK", categoryId, TableQuery[Categories])(_.id)
  def projects = foreignKey("PROJ_FK", projectId, TableQuery[Projects])(_.id)
  def grants = foreignKey("GRANT_FK", grantId, TableQuery[Grants])(_.id)

  def * = (id.?, date, amount, from, categoryId, projectId, grantId, grantRow, descr) <> (Expenditures.fromDb, Expenditures.toDb)

}

object Expenditures {

  lazy val categories: Map[Int, CategoryF] = Global.db.categoryDao.list.groupBy(_.id.get).mapValues(_.head)

  lazy val projects: Map[Int, Project] = Global.db.projectDao.list.groupBy(_.id.get).mapValues(_.head)

  lazy val grants: Map[Int, Grant] = Global.db.grantDao.list.groupBy(_.id.get).mapValues(_.head)

  lazy val accounts: Map[Int, Account] = Global.db.accountDao.list.groupBy(_.id.get).mapValues(_.head)

  def fromDb(t: (Option[Int], Timestamp, BigDecimal, Int, Int, Int, Option[Int], Option[String], String)) = {
    new Expenditure(t._1, t._2, t._3,  accounts(t._4), categories(t._5), projects(t._6), t._7.map(grants),  t._8, t._9, null)
  }

  def toDb(exp: Expenditure) = {
    Some((exp.id, exp.date, exp.amount, exp.from.id.get, exp.category.id.get, exp.project.id.get, exp.grant.flatMap(_.id), exp.grantRow, exp.desc))
  }

}