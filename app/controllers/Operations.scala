package controllers

import java.sql.Timestamp
import java.util.Date

import client.finance.GrantItem
import com.github.nscala_time.time.Imports._
import org.intracer.finance.slick.Expenditures
import org.intracer.finance.{Expenditure, Operation}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

case class OpFilter(projects: Set[Int],
                    categories: Set[Int],
                    grants: Set[Int],
                    grantItems: Set[Int],
                    accounts: Set[Int],
                    dateRange: String) {

  val pattern = DateTimeFormat.forPattern("MM/dd/yyyy")

  val dates = dateRange.split("-").map(entry => DateTime.parse(entry.trim, pattern))

  val interval = if (dates.length >= 2)
    Some(new Interval(dates(0), dates(1)))
  else
    None

  def filter(): Seq[Operation] = {
    Global.operations
      .filter(op => projects.isEmpty || projects.contains(op.to.project.id.get))
      .filter(op => categories.isEmpty || categories.contains(op.to.category.id.get))
      .filter(op => grants.isEmpty || op.to.grant.exists(grant => grants.contains(grant.id.get)))
      .filter(op => grantItems.isEmpty || op.to.grantItem.exists(item => grantItems.contains(item.id.get)))
      .filter(op => accounts.isEmpty || accounts.contains(op.from.id.get))
      .filter(op => interval.exists(_.contains(op.date)))
      .sortBy(_.date.toString())
  }
}

object OpFilter {
  val defaultDateRange: String = "01/01/2016 - 12/20/2016"

  def apply(map: Map[String, Seq[String]]) = {
    val projects = map.getOrElse("projects", Nil).toSet.map((x: String) => x.toInt)
    val categories = map.getOrElse("categories", Seq.empty[String]).toSet.map((x: String) => x.toInt)
    val grants = map.getOrElse("grants", Nil).toSet.map((x: String) => x.toInt)
    val grantItems = map.getOrElse("grantItems", Nil).map(_.toInt).toSet
    val accounts = map.getOrElse("accounts", Nil).map(_.toInt).toSet

    val dateRange = map.get("daterange").flatMap(_.headOption).getOrElse(defaultDateRange)

    new OpFilter(projects, categories, grants, grantItems, accounts, dateRange)
  }

}

object Operations extends Controller with Secured {

  def list = withAuth() {
    user =>
      implicit request =>

        val opFilter = OpFilter(request.queryString)
        val operations = opFilter.filter()

        val amounts = operations.map(_.amount.map(_.toDouble).getOrElse(0.0))
        val total = amounts.sum

        Ok(views.html.operations(user, operations, total, opFilter, "/operations"))
  }

  def byGrantRow = withAuth() {
    user =>
      implicit request =>

        val opFilter = OpFilter(request.queryString)
        val filtered = opFilter.filter()

        val sorted = filtered.sortBy(o => o.to.grantItem.map(_.name).getOrElse("?????") + o.date.toString())

        val keys = filtered.map(o => o.to.grantItem.map(_.name).getOrElse("?????") + o.date.toString()).sorted

        val total = filtered.map(_.amount.map(_.toDouble).getOrElse(0.0)).sum

        Ok(views.html.operations(user, sorted, total, opFilter, "/bygrantrow"))
  }

  def byGrantRowStat = Action {
    implicit request =>

      val map = request.queryString
      val opFilter = OpFilter(map)
      val operations = opFilter.filter()

      val rate = map.get("rate").map(_.head.toDouble).getOrElse(Global.uahToUsd)

      Global.uahToUsd = rate

      val operationsByGrantRow = operations.groupBy(o => o.to.grantItem.flatMap(_.id).getOrElse(-1))

      //val zeros = Global.wmf.keySet -- operationsByGrantRow.keySet

      val withZeros = operationsByGrantRow //++ zeros.map(code => code -> Seq.empty)

      val total = operations.map(_.amount.map(_.toDouble).getOrElse(0.0)).sum

      val grantItemsMap = Global.db.grantItemDao.listAll().groupBy(_.id.getOrElse(-1)).mapValues(_.head) ++
        Seq(-1 -> GrantItem(Some(-1), None, "", "", BigDecimal.valueOf(0), None))

      Ok(views.html.grantStatistics(operations, total, opFilter, withZeros, Some(rate), grantItemsMap))
  }

  def statistics() = Action {
    implicit request =>

      val opFilter = OpFilter(request.queryString)
      val operations = opFilter.filter()

      val byProject = operations.groupBy(o => o.to.project.name)
      val byCategory = operations.groupBy(o => o.to.category.name)
      val byGrant = operations.groupBy(o => o.to.grant.map(_.name).getOrElse("No"))

      val byProjectAndCategory = operations.groupBy(o => o.to.project.name + "." + o.to.category.name)

      val byGrantRow = operations.groupBy(o => o.to.grantItem.map(_.description).getOrElse(""))

      val total = operations.map(_.amount.map(_.toDouble).getOrElse(0.0)).sum

      Ok(views.html.statistics(operations, total, opFilter,
        byProject, byCategory, byGrant, byGrantRow, byProjectAndCategory))
  }

  def update() = withAuthAsync(isAdmin) {
    user =>
      implicit request =>
        updateForm.bindFromRequest.fold(
          formWithErrors => // binding failure, you retrieve the form containing errors,
            Future.successful(BadRequest(updateForm.errorsAsJson)),
          u => {

            import Global.db.expDao.driver.api._
            val q = Global.db.expDao.query
            val db = Global.db.expDao.db

            val idFilter = q.filter(_.id === u.pk.toInt)

            val cmd = u.name match {
              case "descr" =>
                idFilter.map(_.descr).update(u.value)

              case "amount" =>
                idFilter.map(_.amount).update(
                  Option(u.value).filter(_.nonEmpty).map(x => new java.math.BigDecimal(x))
                )

              case "grant" =>
                idFilter.map(_.grantId).update(Try(u.value.toInt).toOption)

              case "grantItem" =>
                idFilter.map(_.grantItem).update(Try(u.value.toInt).toOption)

              case "account" =>
                idFilter.map(_.from).update(u.value.toInt)

              case "project" =>
                idFilter.map(_.projectId).update(u.value.toInt)

              case "category" =>
                idFilter.map(_.categoryId).update(u.value.toInt)

              case "date" =>
                val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
                val dt = formatter.parseDateTime(u.value)
                idFilter.map(_.date).update(new Timestamp(dt.getMillis))

              case "grantRow" =>
                idFilter.map(_.grantRow).update(Some(u.value))
            }

            db.run(cmd).map(r => Ok(u.toString)).recover { case cause => BadRequest(cause.getMessage) }
          })
  }


  def insert() = withAuthAsync(isAdmin) {
    user =>
      implicit request =>
        insertForm.bindFromRequest.fold(
          formWithErrors => // binding failure, you retrieve the form containing errors,
            Future.successful(BadRequest(insertForm.errorsAsJson)),
          u => {
            import Global.db.expDao.driver.api._
            val db = Global.db.expDao.db
            val q = Global.db.expDao.query
            val exp = new Expenditure(
              date = new Timestamp(u.date.getTime),
              amount = u.amount,
              from = u.account.flatMap(Expenditures.accounts.get).orNull,
              category = Expenditures.categories.get(u.category).orNull,
              project = Expenditures.projects.get(u.project).orNull,
              grant = u.grant.flatMap(Expenditures.grants.get),
              grantItem = u.grantItem.flatMap(item => Expenditures.grantItems(17).find(_.id.exists(_ == item))),
              desc = u.descr.orNull
            )

            db.run(q += exp).map(id => Ok(s"""{"id": $id}""")).recover { case cause => BadRequest(cause.getMessage) }
          })
  }

  import play.api.data.format.Formats._

  val updateForm = Form(
    mapping(
      "name" -> text,
      "pk" -> longNumber,
      "value" -> text
    )(Update.apply)(Update.unapply)
  )

  val insertForm = Form(
    mapping(
      "date" -> date("yyyy-MM-dd"),
      "project" -> number,
      "category" -> number,
      "grant" -> optional(number),
      "grantItem" -> optional(number),
      "amount" -> optional(bigDecimal),
      "account" -> optional(number),
      "descr" -> optional(text)
    )(NewOp.apply)(NewOp.unapply)
  )

  val form = Form(
    tuple(
      "projects" -> seq(text),
      "categories" -> seq(text),
      "grants" -> seq(text),
      "rate" -> of(doubleFormat)
    )
  )
}

case class Update(name: String, pk: Long, value: String)

case class NewOp(date: Date,
                 project: Int,
                 category: Int,
                 grant: Option[Int],
                 grantItem: Option[Int],
                 amount: Option[BigDecimal],
                 account: Option[Int],
                 descr: Option[String])