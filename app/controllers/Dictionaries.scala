package controllers

import javax.inject.Inject
import javax.inject.Singleton

import client.finance.GrantItem
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

  def categoryMap: Map[Int, CategoryF] =
    categoryDao.list.groupBy(_.id.get).mapValues(_.head)

  def projectMap: Map[Int, Project] =
    projectDao.list.groupBy(_.id.get).mapValues(_.head)

  def grantMap: Map[Int, Grant] =
    grantDao.list.groupBy(_.id.get).mapValues(_.head)

  def grantItemMap: Map[Int, Seq[GrantItem]] =
    grantItemDao.listAll().groupBy(_.grantId.get)

  def accountMap: Map[Int, Account] =
    accountDao.list.groupBy(_.id.get).mapValues(_.head)

  def userMap: Map[Int, User] =
    userDao.list.groupBy(_.id.get).mapValues(_.head)

  def dictionary(): Dictionary = {
    Dictionary(
      accountMap,
      categoryMap,
      grantMap,
      grantItemMap,
      projectMap,
      userMap)
  }

  def list() = withAuth() { user =>
    implicit request =>
      Ok(views.html.dictionaries(user, "", Seq.empty))
  }

  def accounts() = withAuth() { user =>
    implicit request =>
      val accounts = accountDao.list.sortBy(_.id)
      Ok(views.html.dictionaries(user, "account", accounts))
  }

  def accountsWs() = withAuth() { user =>
    implicit request =>
      val accounts = accountDao.list.sortBy(_.id)
      Ok(views.html.dictionaries(user, "account", accounts))
  }

  def categories() = withAuth() { user =>
    implicit request =>
      val categories = categoryDao.list.sortBy(_.id)
      Ok(views.html.dictionaries(user, "category", categories))
  }

  def projects() = withAuth() { user =>
    implicit request =>
      val projects = projectDao.list.sortBy(_.id)
      Ok(views.html.dictionaries(user, "project", projects))
  }

  def update() = Action.async {
    implicit request =>
      updateForm.bindFromRequest.fold(
        formWithErrors => // binding failure, you retrieve the form containing errors,
          Future.successful(BadRequest(insertForm.errorsAsJson)),
        u => {
          val q = u.table match {
            case "account" => accountDao.query.filter(_.id === u.pk.toInt).map(_.name).update(u.value)
            case "project" => projectDao.query.filter(_.id === u.pk.toInt).map(_.name).update(u.value)
            case "category" => categoryDao.query.filter(_.id === u.pk.toInt).map(_.name).update(u.value)
          }

          db.run(q).map(_ => Ok(u.toString)).recover { case cause => BadRequest(cause.getMessage) }
        })
  }

  def insert() = Action.async {
    implicit request =>

      insertForm.bindFromRequest.fold(
        formWithErrors => // binding failure, you retrieve the form containing errors,
          Future.successful(BadRequest(insertForm.errorsAsJson)),
        u => {
          val q = u.table match {
            case "account" => accountDao.query += Account(name = u.value)
            case "project" => projectDao.query += Project(name = u.value)
            case "category" => categoryDao.query += CategoryF(name = u.value)
          }

          db.run(q).map(id => Ok(s"""{"id": $id}""")).recover { case cause => BadRequest(cause.getMessage) }
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
