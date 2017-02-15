package controllers

import javax.inject.Inject
import javax.inject.Singleton

import slick.driver.JdbcProfile
import org.intracer.finance._
import org.intracer.finance.slick._
import play.api.Play
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.i18n.Messages.Implicits._
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class Dictionaries @Inject()(val categoryDao: CategoryDao,
                             val projectDao: ProjectDao,
                             val grantDao: GrantDao,
                             val grantItemDao: GrantItemsDao,
                             val expDao: ExpenditureDao,
                             val accountDao: AccountDao,
                             val userDao: UserDao)
  extends Controller with Secured with HasDatabaseConfig[JdbcProfile] {

  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import driver.api._

  def list() = withAuth() { user =>
    implicit request =>

      Ok(views.html.dictionaries(user, "", Seq.empty))
  }

  def accounts() = withAuth() { user =>
    implicit request =>

      val accounts = Expenditures.accounts.values.toSeq.sortBy(_.id)

      Ok(views.html.dictionaries(user, "account", accounts))
  }

  def categories() = withAuth() { user =>
    implicit request =>

      val categories = Expenditures.categories.values.toSeq.sortBy(_.id)

      Ok(views.html.dictionaries(user, "category", categories))
  }

  def projects() = withAuth() { user =>
    implicit request =>

      val projects = Expenditures.projects.values.toSeq.sortBy(_.id)

      Ok(views.html.dictionaries(user, "project", projects))
  }

  def update() = Action.async {
    implicit request =>
      updateForm.bindFromRequest.fold(
        formWithErrors => // binding failure, you retrieve the form containing errors,
          Future.successful(BadRequest(insertForm.errorsAsJson)),
        u => {


          val q = u.table match {
            case "account" => db.run(accountDao.query.filter(_.id === u.pk.toInt).map(_.name).update(u.value))
            case "project" => db.run(projectDao.query.filter(_.id === u.pk.toInt).map(_.name).update(u.value))
            case "category" => db.run(categoryDao.query.filter(_.id === u.pk.toInt).map(_.name).update(u.value))
          }

          q.map(r => Ok(u.toString)).recover { case cause => BadRequest(cause.getMessage) }
        })
  }

  def insert() = Action.async {
    implicit request =>

      insertForm.bindFromRequest.fold(
        formWithErrors => // binding failure, you retrieve the form containing errors,
          Future.successful(BadRequest(insertForm.errorsAsJson)),
        u => {

          val q = u.table match {
            case "account" => db.run(accountDao.query += Account(name = u.value))
            case "project" => db.run(projectDao.query += Project(name = u.value))
            case "category" => db.run(categoryDao.query += CategoryF(name = u.value))
          }

          q.map(id => Ok(s"""{"id": $id}""")).recover { case cause => BadRequest(cause.getMessage) }
        })
  }

  val updateForm = Form(
    mapping(
      "table" -> text,
      "name" -> text,
      "pk" -> longNumber,
      "value" -> text
    )(Update.apply)(Update.unapply)
  )

  val insertForm = Form(
    mapping(
      "table" -> text,
      "name" -> text
    )(Insert.apply)(Insert.unapply)
  )

  case class Update(table: String, name: String, pk: Long, value: String)

  case class Insert(table: String, value: String)

}
