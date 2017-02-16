package org.intracer.finance.slick

import java.sql.Timestamp

import client.finance.GrantItem
import slick.driver.H2Driver.api._
import slick.profile.SqlProfile.ColumnOption.SqlType
import org.intracer.finance._

case class OpId(opId: Option[Int] = None,
                lastRevId: Option[Int] = None)

class OpIds(tag: Tag) extends Table[OpId](tag, "op_id") {

  def opId = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def revId = column[Option[Int]]("rev_id")

  def * = (opId.?, revId) <> (OpId.tupled, OpId.unapply)
}

class Expenditures(tag: Tag) extends Table[Expenditure](tag, "operation") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def opId = column[Int]("op_id")

  def date = column[Timestamp]("op_date", SqlType("datetime"))

  def amount = column[Option[BigDecimal]]("amount")

  def accountId = column[Int]("account_id")

  def categoryId = column[Int]("cat_id")

  def projectId = column[Int]("proj_id")

  def grantId = column[Option[Int]]("grant_id")

  def grantItem = column[Option[Int]]("grant_item")

  def grantRow = column[Option[String]]("grant_row")

  def descr = column[String]("descr")

  def account = foreignKey("ACC_FK", accountId, TableQuery[Accounts])(_.id)

  def category = foreignKey("CAT_FK", categoryId, TableQuery[Categories])(_.id)

  def projects = foreignKey("PROJ_FK", projectId, TableQuery[Projects])(_.id)

  def grants = foreignKey("GRANT_FK", grantId, TableQuery[Grants])(_.id)

  def logDate = column[Timestamp]("log_date", SqlType("datetime"))

  def userId = column[Int]("user_id")

  def * = (id.?, opId, date, amount, accountId, categoryId, projectId, grantId, grantItem, descr, logDate, userId) <>
    ((Expenditures.fromDb _).tupled, Expenditures.toDb)
}

object Expenditures {

  lazy val categories: Map[Int, CategoryF] = new CategoryDao().list.groupBy(_.id.get).mapValues(_.head)

  lazy val projects: Map[Int, Project] = new ProjectDao().list.groupBy(_.id.get).mapValues(_.head)

  lazy val grants: Map[Int, Grant] = new GrantDao().list.groupBy(_.id.get).mapValues(_.head)

  lazy val grantItems: Map[Int, Seq[GrantItem]] = {
    new GrantItemsDao().listAll().groupBy(_.grantId.get)
  }

  lazy val accounts: Map[Int, Account] = new AccountDao().list.groupBy(_.id.get).mapValues(_.head)

  lazy val users: Map[Int, User] = new UserDao().list.groupBy(_.id.get).mapValues(_.head)

  def fromDb(id: Option[Int],
             opId: Int,
             opDate: Timestamp,
             amount: Option[BigDecimal],
             accountId: Int,
             categoryId: Int,
             projectId: Int,
             maybeGrantId: Option[Int],
             maybeGrantItemId: Option[Int],
             descr: String,
             logDate: Timestamp,
             userId: Int): Expenditure = {

    val grantItem = for (grantId <- maybeGrantId;
                         grantItemId <- maybeGrantItemId;
                         grantItemsForGrant <- grantItems.get(grantId);
                         grantItem <- grantItemsForGrant.find(_.id.exists(_ == grantItemId))
    ) yield grantItem

    val user = users.getOrElse(userId, User(None, "-", "-"))

    Expenditure(id, Some(opId), opDate, amount, accounts(accountId), categories(categoryId), projects(projectId),
      maybeGrantId.map(grants), grantItem, descr, user, logDate)
  }

  def toDb(exp: Expenditure) = {
    Some((exp.id, exp.opId.get, exp.date, exp.amount,
      exp.account.id.get,
      exp.category.id.get,
      exp.project.id.get,
      exp.grant.flatMap(_.id),
      exp.grantItem.flatMap(_.id),
      exp.description,
      exp.logDate,
      exp.user.id.get))
  }

}