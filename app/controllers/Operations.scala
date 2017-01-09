package controllers

import java.util.Date

import client.finance.GrantItem
import com.github.nscala_time.time.Imports._
import org.intracer.finance.{Operation, User}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class OpFilter(projects: Set[Int],
                    categories: Set[Int],
                    grants: Set[Int],
                    grantItems: Set[Int],
                    accounts: Set[Int],
                    dateRange: String,
                    users: Seq[User]) {

  val pattern = DateTimeFormat.forPattern("MM/dd/yyyy")

  val dates = dateRange.split("-").map(entry => DateTime.parse(entry.trim, pattern))

  val interval = if (dates.length >= 2)
    Some(new Interval(dates(0), dates(1)))
  else
    None

  def filter(): Seq[Operation] = {

    def bySet(set: Set[Int], id: Option[Int]) = set.isEmpty || id.exists(set.contains)

    Global.operations
      .filter(op => bySet(projects, op.to.project.id))
      .filter(op => bySet(categories, op.to.category.id))
      .filter(op => bySet(accounts, op.from.id))
      .filter(op => bySet(grants, op.to.grant.flatMap(_.id)))
      .filter(op => bySet(grantItems, op.to.grantItem.flatMap(_.id)))
      .filter(op => interval.exists(_.contains(op.date)))
      .filter(op => bySet(users.flatMap(_.id).toSet, op.to.user.id))
      .sortBy(_.date.toString())
  }
}

object OpFilter {
  val defaultDateRange: String = "01/01/2016 - 12/31/2016"

  def apply(map: Map[String, Seq[String]], users: Seq[User]) = {

    def toIntSet(name: String): Set[Int] = {
      val toSet = map.getOrElse(name, Seq.empty[String]).toSet
      toSet.map(_.toInt)
    }

    val projects = toIntSet("projects")
    val categories = toIntSet("categories")
    val grants = toIntSet("grants")
    val grantItems = toIntSet("grantItems")
    val accounts = toIntSet("accounts")

    val dateRange = map.get("daterange").flatMap(_.headOption).getOrElse(defaultDateRange)

    new OpFilter(projects, categories, grants, grantItems, accounts, dateRange, users)
  }

}

object Operations extends Controller with Secured {

  def list = withAuth() {
    user =>
      implicit request =>

        val opFilter = OpFilter(request.queryString, Seq(user))
        val operations = opFilter.filter()

        val amounts = operations.map(_.amount.map(_.toDouble).getOrElse(0.0))
        val total = amounts.sum

        Ok(views.html.operations(user, operations, total, opFilter, "/operations"))
  }

  def byGrantRow = withAuth() {
    user =>
      implicit request =>

        val opFilter = OpFilter(request.queryString, Seq(user))
        val filtered = opFilter.filter()

        val sorted = filtered.sortBy(o => o.to.grantItem.map(_.name).getOrElse("?????") + o.date.toString())

        val keys = filtered.map(o => o.to.grantItem.map(_.name).getOrElse("?????") + o.date.toString()).sorted

        val total = filtered.map(_.amount.map(_.toDouble).getOrElse(0.0)).sum

        Ok(views.html.operations(user, sorted, total, opFilter, "/bygrantrow"))
  }

  def byGrantRowStat = Action {
    implicit request =>

      val map = request.queryString
      val opFilter = OpFilter(map, Nil)
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

      val opFilter = OpFilter(request.queryString, Nil)
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

  def update() = formAction(updateForm, Global.db.expDao.update)

  def insert() = formAction(insertForm, Global.db.expDao.insert)

  def formAction[T](form: Form[T],
                    process: (T, User) => Future[Int]): EssentialAction =
    withAuthAsync(isContributor) { user =>
      implicit request =>
        form.bindFromRequest.fold(
          error =>
            Future.successful {
              BadRequest(form.errorsAsJson)
            },
          success =>
            process(success, user)
              .map { id =>
                Ok(s"""{"id": $id}""")
              }
              .recover { case cause =>
                BadRequest(cause.getMessage)
              }
        )
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