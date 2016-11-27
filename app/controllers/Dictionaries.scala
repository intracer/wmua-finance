package controllers

import org.intracer.finance._
import org.intracer.finance.slick.Expenditures
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object Dictionaries extends Controller with Secured {

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
          import Global.db.driver.api._

          val db = Global.db

          val q = u.table match {
            case "account" => db.db.run(db.accountDao.query.filter(_.id === u.pk.toInt).map(_.name).update(u.value))
            case "project" => db.db.run(db.projectDao.query.filter(_.id === u.pk.toInt).map(_.name).update(u.value))
            case "category" => db.db.run(db.categoryDao.query.filter(_.id === u.pk.toInt).map(_.name).update(u.value))
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
          import Global.db.driver.api._

          val db = Global.db

          val q = u.table match {
            case "account" => db.db.run(db.accountDao.query += Account(name = u.value))
            case "project" => db.db.run(db.projectDao.query += Project(name = u.value))
            case "category" => db.db.run(db.categoryDao.query += CategoryF(name = u.value))
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
