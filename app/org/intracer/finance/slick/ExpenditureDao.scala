package org.intracer.finance.slick

import java.sql.Timestamp

import controllers.{NewOp, Update}
import org.intracer.finance.{Expenditure, User}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

import scala.concurrent.Future
import scala.util.Try

class ExpenditureDao(val mwDb: FinDatabase, val query: TableQuery[Expenditures], val driver: JdbcProfile) extends BaseDao {

  import driver.api._

  def insert(exp: Expenditure): Int = db {
    query += exp
  }

  def insertAll(exps: Seq[Expenditure]): Unit = db {
    query.forceInsertAll(exps)
  }

  def list: Seq[Expenditure] = db {
    query.sortBy(_.date.desc).result
  }

  def update(upd: Update, user: User): Future[Int] = {

    val opFilter = query.filter {
      e =>
        e.id === upd.pk.toInt &&
          e.userId === user.id.get
    }

    val cmd = upd.name match {
      case "descr" =>
        opFilter.map(_.descr).update(upd.value)

      case "amount" =>
        opFilter.map(_.amount)
          .update(
            Option(upd.value)
              .filter(_.nonEmpty)
              .map(new java.math.BigDecimal(_))
          )

      case "grant" =>
        opFilter.map(_.grantId).update(Try(upd.value.toInt).toOption)

      case "grantItem" =>
        opFilter.map(_.grantItem).update(Try(upd.value.toInt).toOption)

      case "account" =>
        opFilter.map(_.from).update(upd.value.toInt)

      case "project" =>
        opFilter.map(_.projectId).update(upd.value.toInt)

      case "category" =>
        opFilter.map(_.categoryId).update(upd.value.toInt)

      case "date" =>
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val dt = formatter.parseDateTime(upd.value)
        opFilter.map(_.date).update(new Timestamp(dt.getMillis))

      case "grantRow" =>
        opFilter.map(_.grantRow).update(Some(upd.value))
    }

    mwDb.db.run(cmd)
  }

  def insert(op: NewOp, user: User) = {

    val exp = Expenditure(
      date = new Timestamp(op.date.getTime),
      amount = op.amount,
      from = op.account.flatMap(Expenditures.accounts.get).orNull,
      category = Expenditures.categories.get(op.category).orNull,
      project = Expenditures.projects.get(op.project).orNull,
      grant = op.grant.flatMap(Expenditures.grants.get),
      grantItem = op.grantItem.flatMap(item => Expenditures.grantItems(17).find(_.id.exists(_ == item))),
      desc = op.descr.orNull,
      logDate = new Timestamp(DateTime.now().getMillis),
      user = user
    )

    mwDb.db.run(query += exp)
  }
}
