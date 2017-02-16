package controllers

import java.sql.Timestamp
import java.util.Date
import javax.inject.{Inject, Singleton}

import client.finance.GrantItem
import com.github.nscala_time.time.Imports._
import org.intracer.finance.slick.{ExpenditureDao, Expenditures, GrantItemsDao, UserDao}
import org.intracer.finance.{Dictionary, Expenditure, Operation, User}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class OpFilter(projects: Set[Int] = Set.empty,
                    categories: Set[Int] = Set.empty,
                    grants: Set[Int] = Set.empty,
                    grantItems: Set[Int] = Set.empty,
                    accounts: Set[Int] = Set.empty,
                    dateRange: String = "01/01/2016 - 12/31/2016",
                    users: Seq[User] = Seq.empty,
                    operations: Seq[Operation] = Seq.empty,
                    dictionary: Dictionary = Dictionary()) {

  val pattern = DateTimeFormat.forPattern("MM/dd/yyyy")

  val dates = dateRange.split("-").map { entry =>
    DateTime.parse(entry.trim, pattern)
  }

  val interval = if (dates.length >= 2)
    Some(new Interval(dates(0), dates(1)))
  else
    None

  def filter(): Seq[Operation] = {

    def filterOp(op: Operation): Boolean = {

      def bySet(set: Set[Int], id: Option[Int]) =
        set.isEmpty || id.exists(set.contains)

      val to = op.to
      bySet(projects, to.project.id) &&
        bySet(categories, to.category.id) &&
        bySet(accounts, op.from.id) &&
        bySet(grants, to.grant.flatMap(_.id)) &&
        bySet(grantItems, to.grantItem.flatMap(_.id)) &&
        bySet(users.flatMap(_.id).toSet, to.user.id) &&
        interval.exists(_.contains(op.date))
    }

    operations
      .filter(filterOp)
      .sortBy(_.date.toString())
  }
}

@Singleton
class Operations @Inject()(val expenditureDao: ExpenditureDao,
                           val userDao: UserDao,
                           val dictionaries: Dictionaries)
  extends Controller with Secured {

  def allOperations: Seq[Operation] = {
    expToOps(expenditureDao.list)
  }

  def operationsWithRevisions: Seq[Operation] = {
    expToOps(expenditureDao.log)
  }

  def expToOps(exps: Seq[Expenditure]): Seq[Operation] = {
    exps.map { e =>
      new Operation(e.account, e, e.amount, new DateTime(e.date.getTime))
    }
  }

  def makeFilter(request: Request[_], users: Seq[User], operations: Seq[Operation]) = {
    val defaultDateRange: String = "01/01/2016 - 12/31/2016"

    val map = request.queryString

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

    val dictionary = dictionaries.dictionary()
    OpFilter(projects, categories, grants, grantItems, accounts, dateRange, users, operations, dictionary)
  }


  def withFilter(loader: => Seq[Operation] = allOperations)
                (f: (User, OpFilter, Seq[Operation]) => Request[AnyContent] => Result) = {
    withAuth() {
      user =>
        implicit request =>
          val opFilter = makeFilter(request, Seq(user), loader)
          val operations = opFilter.filter()

          f(user, opFilter, operations)(request)
    }
  }

  def list = withFilter() {
    (user, opFilter, operations) =>
      implicit request =>
        val total = operations.map(_.toDouble).sum
        Ok(views.html.operations(user, operations, total, opFilter, "/operations"))
  }

  def log = withFilter(operationsWithRevisions) {
    (user, opFilter, operations) =>
      implicit request =>
        val total = operations.map(_.toDouble).sum
        Ok(views.html.operations(user, operations, total, opFilter, "/log"))
  }

  def revisions(id: Int) = withAuth() {
    user =>
      implicit request =>

        val operations = expenditureDao.revisions(id)

        Ok(views.html.operations(user, expToOps(operations), 0.0d, new OpFilter(), "/revisions"))
  }

  def byGrantRow = withFilter() {
    (user, opFilter, operations) =>
      implicit request =>

        val sorted = operations.sortBy { o =>
          o.to.grantItem.map(_.name).getOrElse("?????") + o.date.toString()
        }

        val total = operations.map(_.toDouble).sum

        Ok(views.html.operations(user, sorted, total, opFilter, "/bygrantrow"))
  }

  def byGrantRowStat = withFilter() {
    (_, opFilter, ops) =>
      implicit request =>

      val rate = request.queryString.get("rate")
        .map(_.head.toDouble)
        .getOrElse(Global.uahToUsd)

      Global.uahToUsd = rate

      val grantItemsMap = new GrantItemsDao()
        .listAll()
        .groupBy(_.id.getOrElse(-1)).mapValues(_.head) ++
        Seq(-1 -> GrantItem(Some(-1), None, "", "", BigDecimal.valueOf(0), None))

      Ok(
        views.html.grantStatistics(ops, ops.map(_.toDouble).sum, opFilter,
          ops.groupBy(o => o.to.grantItem.flatMap(_.id).getOrElse(-1)),
          Some(rate), grantItemsMap)
      )
  }

  def statistics() = withFilter() {
    (_, opFilter, ops) =>
      implicit request =>

        Ok(
          views.html.statistics(ops, ops.map(_.toDouble).sum, opFilter,
            ops.groupBy(_.to.project.name),
            ops.groupBy(_.to.category.name),
            ops.groupBy(_.to.grant.map(_.name).getOrElse("No")),
            ops.groupBy(_.to.grantItem.map(_.description).getOrElse("")),
            ops.groupBy(o => o.to.project.name + "." + o.to.category.name)
          )
        )
  }

  def update() = formAction(updateForm, expenditureDao.update)

  def insert() = formAction(insertForm, insertCmd)

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

  def insertCmd(op: NewOp, user: User): Future[Int] = {

    val dictionary = dictionaries.dictionary()

    val exp = Expenditure(
      date = new Timestamp(op.date.getTime),
      opId = None,
      amount = op.amount,
      account = op.account.flatMap(dictionary.accountMap.get).orNull,
      category = dictionary.categoryMap.get(op.category).orNull,
      project = dictionary.projectMap.get(op.project).orNull,
      grant = op.grant.flatMap(dictionary.grantMap.get),
      grantItem = op.grantItem.flatMap(item => dictionary.grantItemMap(17).find(_.id.exists(_ == item))),
      description = op.descr.orNull,
      logDate = new Timestamp(DateTime.now().getMillis),
      user = user
    )

    val result = expenditureDao.insertWithOpId(exp)
    Future.successful(result) // TODO async
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