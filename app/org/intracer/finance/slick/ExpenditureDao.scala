package org.intracer.finance.slick

import java.sql.Timestamp
import javax.inject.Inject

import controllers.{NewOp, Update}
import org.intracer.finance.{Expenditure, User}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.db.slick.DatabaseConfigProvider
import slick.profile.SqlProfile.ColumnOption.SqlType

import scala.concurrent.Future
import scala.util.Try

class ExpenditureDao @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                               val schema: Schema)
  extends BaseDao[Expenditure] {

  import driver.api._

  val query = schema.expendituresQuery

  def list: Seq[Expenditure] = run {
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

    db.run(cmd)
  }

  def insert(op: NewOp, user: User) = {

    val exp = Expenditure(
      date = new Timestamp(op.date.getTime),
      amount = op.amount,
      account = op.account.flatMap(schema.accounts.get).orNull,
      category = schema.categories.get(op.category).orNull,
      project = schema.projects.get(op.project).orNull,
      grant = op.grant.flatMap(schema.grants.get),
      grantItem = op.grantItem.flatMap(item => schema.grantItems(17).find(_.id.exists(_ == item))),
      desc = op.descr.orNull,
      logDate = new Timestamp(DateTime.now().getMillis),
      user = user
    )

    db.run(query += exp)
  }
}
