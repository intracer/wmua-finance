package org.intracer.finance.slick

import java.sql.Timestamp

import slick.driver.H2Driver.api._
import slick.profile.SqlProfile.ColumnOption.SqlType
import org.intracer.finance._
import client.finance.GrantItem
import controllers.Global
import org.joda.time.DateTime

class Expenditures(tag: Tag) extends Table[Expenditure](tag, "operation") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def date = column[Timestamp]("op_date", SqlType("datetime "))

  def amount = column[Option[BigDecimal]]("amount")

  def from = column[Int]("account_id")

  def categoryId = column[Int]("cat_id")

  def projectId = column[Int]("proj_id")

  def grantId = column[Option[Int]]("grant_id")

  def grantItem = column[Option[Int]]("grant_item")

  def grantRow = column[Option[String]]("grant_row")

  def descr = column[String]("descr")

  def account = foreignKey("ACC_FK", categoryId, TableQuery[Accounts])(_.id)

  def category = foreignKey("CAT_FK", categoryId, TableQuery[Categories])(_.id)

  def projects = foreignKey("PROJ_FK", projectId, TableQuery[Projects])(_.id)

  def grants = foreignKey("GRANT_FK", grantId, TableQuery[Grants])(_.id)

  def logDate = column[Timestamp]("log_date", SqlType("datetime"))

  def userId = column[Int]("user_id")


  def * = (id.? , date , amount, from, categoryId, projectId, grantId, grantItem, descr, logDate, userId) <>
    ((Expenditures.fromDb _).tupled, Expenditures.toDb)

}

object Expenditures {

  def categories: Map[Int, CategoryF] = Global.db.categoryDao.list.groupBy(_.id.get).mapValues(_.head)

  def projects: Map[Int, Project] = Global.db.projectDao.list.groupBy(_.id.get).mapValues(_.head)

  lazy val grants: Map[Int, Grant] = Global.db.grantDao.list.groupBy(_.id.get).mapValues(_.head)

  lazy val grantItems: Map[Int, Seq[GrantItem]] = {
    Global.db.grantItemDao.listAll().groupBy(_.grantId.get)
  }

  def accounts: Map[Int, Account] = Global.db.accountDao.list.groupBy(_.id.get).mapValues(_.head)

  def fromDb(
              id: Option[Int],
              opDate: Timestamp,
              amount: Option[BigDecimal],
              accountId: Int,
              categoryId: Int,
              projectId: Int,
              maybeGrantId: Option[Int],
              maybeGrantItemId: Option[Int],
              descr: String,
              logDate: Timestamp,
              userId: Int
            ): Expenditure = {

     val grantItem = for (grantId <- maybeGrantId;
                          grantItemId <- maybeGrantItemId;
                          grantItemsForGrant <- grantItems.get(grantId);
                          grantItem <- grantItemsForGrant.find(_.id.exists(_ == grantItemId))
     ) yield grantItem

     val user = Global.db.userDao.byId(userId).get

     Expenditure(id, opDate, amount, accounts(accountId), categories(categoryId), projects(projectId),
       maybeGrantId.map(grants), grantItem, descr, logDate, user)
  }

  def toDb(exp: Expenditure) = {
    Some((exp.id, exp.date, exp.amount,
      exp.from.id.get,
      exp.category.id.get,
      exp.project.id.get,
      exp.grant.flatMap(_.id),
      exp.grantItem.flatMap(_.id),
      exp.desc,
      exp.logDate,
      exp.user.id.get))
  }

}