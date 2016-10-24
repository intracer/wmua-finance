package controllers

import java.sql.Timestamp
import java.util.Date

import client.finance.GrantItem
import com.github.nscala_time.time.Imports._
import org.intracer.finance.slick.Expenditures
import org.intracer.finance.{Expenditure, Operation, User}
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

object Operations extends Controller with Secured {


  //  def list = Action {
  //    implicit request =>
  //
  //      val operations = Global.operations.sortBy(_.date.toString()).toSeq
  //
  //      Ok(views.html.operations(operations, Seq("x")))
  //  }
  val defaultDateRange: String = "01/01/2016 - 12/20/2016"

  def list = withAuth {
    username =>
      implicit request =>

        val map = request.queryString
        val projects = map.getOrElse("projects", Nil).toSet.map((x: String) => x.toInt)
        val categories = map.getOrElse("categories", Seq.empty[String]).toSet.map((x: String) => x.toInt)
        val grants = map.getOrElse("grants", Nil).toSet.map((x: String) => x.toInt)
        val grantItems = map.getOrElse("grantItems", Nil).map(_.toInt).toSet
        val accounts = map.getOrElse("accounts", Nil).map(_.toInt).toSet

        val daterange = map.get("daterange").orElse(Option(Seq(defaultDateRange)))
        var operations = filterOperations(projects, categories, grants, grantItems, accounts, daterange)

        val amounts = operations.map(_.amount.map(_.toDouble).getOrElse(0.0))
        val total = amounts.sum

        Ok(views.html.operations(new User(***REMOVED***),
          operations, total, projects, categories, grants, grantItems, accounts,
          daterange.map(_.head).getOrElse(defaultDateRange),
          "/operations"))
  }

  def byGrantRow = withAuth {
    username =>
      implicit request =>

        val map = request.queryString
        val projects = map.getOrElse("projects", Nil).toSet.map((x: String) => x.toInt)
        val categories = map.getOrElse("categories", Nil).toSet.map((x: String) => x.toInt)
        val grants = map.getOrElse("grants", Nil).toSet.map((x: String) => x.toInt)
        val grantItems = map.getOrElse("grantItems", Nil).map(_.toInt).toSet
        val accounts = map.getOrElse("accounts", Nil).map(_.toInt).toSet

        val daterange = map.get("daterange").orElse(Option(Seq(defaultDateRange)))
        val filtered = filterOperations(projects, categories, grants, grantItems, accounts, daterange)
        val sorted = filtered.sortBy(o => o.to.grantItem.map(_.name).getOrElse("?????") + o.date.toString())

        val keys = filtered.map(o => o.to.grantItem.map(_.name).getOrElse("?????") + o.date.toString()).sorted

        val total = filtered.map(_.amount.map(_.toDouble).getOrElse(0.0)).sum

        Ok(views.html.operations(
          new User(***REMOVED***), sorted, total, projects, categories, grants, grantItems, accounts,
          daterange.map(_.head).getOrElse(defaultDateRange),
          "/bygrantrow"))
  }

  def byGrantRowStat = Action {
    implicit request =>

      val map = request.queryString
      val projects = map.getOrElse("projects", Nil).toSet.map((x: String) => x.toInt)
      val categories = map.getOrElse("categories", Nil).map(_.toInt).toSet
      val grants = map.getOrElse("grants", Nil).map(_.toInt).toSet
      val grantItems = map.getOrElse("grantItems", Nil).map(_.toInt).toSet
      val accounts = map.getOrElse("accounts", Nil).map(_.toInt).toSet
      val rate = map.get("rate").map(_.head.toDouble).getOrElse(Global.uahToUsd)

      val daterange = map.get("daterange").orElse(Option(Seq(defaultDateRange)))

      Global.uahToUsd = rate

      val operations: Seq[Operation] = filterOperations(projects, categories, grants, grantItems, accounts, daterange)

      val operationsByGrantRow = operations.groupBy(o => o.to.grantItem.flatMap(_.id).getOrElse(-1))

      //val zeros = Global.wmf.keySet -- operationsByGrantRow.keySet

      val withZeros = operationsByGrantRow //++ zeros.map(code => code -> Seq.empty)

      val total = operations.map(_.amount.map(_.toDouble).getOrElse(0.0)).sum

      val grantItemsMap = Global.db.grantItemDao.listAll().groupBy(_.id.getOrElse(-1)).mapValues(_.head) ++
        Seq(-1 -> GrantItem(Some(-1), None, "", "", BigDecimal.valueOf(0), None))

      Ok(views.html.grantStatistics(operations, total, projects, categories, grants, grantItems, accounts,
        daterange.map(_.head).getOrElse(defaultDateRange),
        withZeros, Some(rate), grantItemsMap))
  }


  def filterOperations(
                        projects: Set[Int],
                        categories: Set[Int],
                        grants: Set[Int],
                        grantItems: Set[Int],
                        accounts: Set[Int],
                        daterange: Option[Seq[String]]
                      ): Seq[Operation] = {
    var operations = Global.operations.sortBy(_.date.toString())(Ordering.fromLessThan((s1: String, s2: String) => s1 > s2))

    if (projects.nonEmpty) {
      operations = operations.filter(op => projects.contains(op.to.project.id.get))
    }
    if (categories.nonEmpty) {
      operations = operations.filter(op => categories.contains(op.to.category.id.get))
    }

    if (grants.nonEmpty) {
      operations = operations.filter(op => op.to.grant.exists(grant => grants.contains(grant.id.get)))
    }

    if (grantItems.nonEmpty) {
      operations = operations.filter(op => op.to.grantItem.exists(item => grantItems.contains(item.id.get)))
    }

    if (accounts.nonEmpty) {
      operations = operations.filter(op => accounts.contains(op.from.id.get))
    }

    val pattern = "MM/dd/yyyy"

    daterange.foreach {
      range =>
        val head: String = range.head

        if (!head.trim.isEmpty) {
          val arr = head.split("-")

          val dates = arr.map(entry =>
            DateTime.parse(entry.trim, DateTimeFormat.forPattern(pattern))
          )

          operations = operations.filter(op => op.date >= dates(0) && op.date <= dates(1))
        }

    }
    //    val max = DateTime.parse("12/14/2012", DateTimeFormat.forPattern(pattern))
    //    operations = operations.filter(op => op.date <= max)

    operations
  }

  def statistics() = Action {
    implicit request =>

      val map = request.queryString
      val projects = map.getOrElse("projects", Nil).toSet
      val categories = map.getOrElse("categories", Nil).toSet
      val grants = map.getOrElse("grants", Nil).toSet
      val grantItems = map.getOrElse("grantItems", Nil).toSet
      val accounts = map.getOrElse("accounts", Nil).toSet

      val daterange = map.get("daterange").orElse(Option(Seq(defaultDateRange)))

      val operations: Seq[Operation] = filterOperations(
        projects.map(_.toInt),
        categories.map(_.toInt),
        grants.map(_.toInt),
        grantItems.map(_.toInt),
        accounts.map(_.toInt),
        daterange
      )

      val operationsByProject = operations.groupBy(o => o.to.project.name)
      val operationsByCategory = operations.groupBy(o => o.to.category.name)
      val operationsByGrant = operations.groupBy(o => o.to.grant.map(_.name).getOrElse("No"))

      val operationsByProjectAndCategory = operations.groupBy(o => o.to.project.name + "." + o.to.category.name)

      val operationsByGrantRow = operations.groupBy(o => o.to.grantItem.map(_.id.toString).getOrElse(""))

      val total = operations.map(_.amount.map(_.toDouble).getOrElse(0.0)).sum

      Ok(views.html.statistics(operations, total, projects, categories, grants, grantItems, accounts,
        daterange.map(_.head).getOrElse(defaultDateRange),
        operationsByProject, operationsByCategory, operationsByGrant, operationsByGrantRow, operationsByProjectAndCategory))
  }

  def update() = Action.async {
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

  def insert() = Action.async {
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
      "projects" -> Forms.list(text),
      "categories" -> Forms.list(text),
      "grants" -> Forms.list(text),
      "rate" -> of(doubleFormat)
    )
  )
}

case class Update(name: String, pk: Long, value: String)

case class NewOp(
                  date: Date,
                  project: Int,
                  category: Int,
                  grant: Option[Int],
                  grantItem: Option[Int],
                  amount: Option[BigDecimal],
                  account: Option[Int],
                  descr: Option[String]
                )
