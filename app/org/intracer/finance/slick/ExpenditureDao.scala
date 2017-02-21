package org.intracer.finance.slick

import java.sql.Timestamp

import controllers.{OpFilter, Update}
import org.intracer.finance.{Expenditure, User}
import org.joda.time.format.DateTimeFormat
import spray.util.pimpFuture

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class ExpenditureDao extends BaseDao {

  import driver.api._

  val query = TableQuery[Expenditures]

  val opIdQuery = TableQuery[OpIds]

  def insertCmd(exp: Expenditure): Int = run {
    query returning query.map(_.id) += exp
  }

  def insertOpId(): Int = run {
    opIdQuery returning opIdQuery.map(_.opId) += OpId()
  }

  def updateLastRevId(opId: Int, revId: Int): Int = run {
    opIdQuery
      .filter(_.opId === opId)
      .map(_.revId)
      .update(Some(revId))
  }

  def insertAll(exps: Seq[Expenditure]): Unit = run {
    query.forceInsertAll(exps)
  }

  def findById(id: Int): Option[Expenditure] = run {
    query.filter(_.id === id).result.headOption
  }

  def log: Seq[Expenditure] = run {
    query.sortBy(_.id.desc).result
  }

  def list: Seq[Expenditure] = {
    db.run {
      (opIdQuery
        join query on (_.revId === _.id)
        sortBy { case (opId, exp) => exp.date.desc }
        ).result
    }.map { r =>
      r.map { case (opId, exp) => exp }
    }.await
  }

  def filtered(opFilter: OpFilter): Seq[Expenditure] = {
    var filtered = opIdQuery join query on (_.revId === _.id)
    if (opFilter.projects.nonEmpty) {
      filtered = filtered filter (_._2.projectId inSet opFilter.projects)
    }
    if (opFilter.categories.nonEmpty) {
      filtered = filtered filter (_._2.categoryId inSet opFilter.categories)
    }
    if (opFilter.grants.nonEmpty) {
      filtered = filtered filter (_._2.grantId inSet opFilter.grants)
    }
    if (opFilter.grantItems.nonEmpty) {
      filtered = filtered filter (_._2.grantItem inSet opFilter.grantItems)
    }
    if (opFilter.accounts.nonEmpty) {
      filtered = filtered filter (_._2.accountId inSet opFilter.accounts)
    }

    db.run {
      (filtered
        sortBy { case (opId, exp) => opId.opId }
        ).result
    }.map { r =>
      r.map { case (opId, exp) => exp }
    }.await
  }

  def revisions(opId: Int): Seq[Expenditure] = run {
    query.filter(_.opId === opId).sortBy(_.logDate.desc).result
  }

  def update(upd: Update, user: User): Future[Int] = {

    val opId = upd.pk.toInt
    val exp = findById(opId).get
    val newId = insertCmd(exp.copy(id = None))
    updateLastRevId(opId, newId)

    val opFilter = query.filter {
      e =>
        e.id === newId &&
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
        opFilter.map(_.accountId).update(upd.value.toInt)

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

  def insertWithOpId(exp: Expenditure): Int = {
    val opId = insertOpId()
    val withOpId = exp.copy(opId = Some(opId))
    val expId = insertCmd(withOpId)
    updateLastRevId(opId, expId)
  }
}
