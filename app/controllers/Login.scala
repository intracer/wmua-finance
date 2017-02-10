package controllers

import javax.inject.{Inject, Singleton}

import org.intracer.finance.slick.{Schema, UserDao}
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Lang
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Results._
import play.api.mvc._

@Singleton
class Login @Inject()(val schema: Schema, val userDao: UserDao) extends Controller with Secured {

  def index = Action {
    implicit request =>
      Ok(views.html.index(loginForm))
  }

  def login = Action {
    implicit request =>
      Ok(views.html.index(loginForm))
  }

  def auth() = Action {
    implicit request =>

      loginForm.bindFromRequest.fold(
        formWithErrors => // binding failure, you retrieve the form containing errors,
          BadRequest(views.html.index(formWithErrors)),
        value => {
          // binding success, you get the actual value
          val user = userDao.login(value._1, value._2).get
          val result = Redirect(routes.Operations.list()).withSession(Security.username -> value._1.trim)
          user.lang.fold(result)(l => result.withLang(Lang(l)))
        }
      )
  }

  /**
    * Logout and clean the session.
    *
    * @return Index page
    */
  def logout = Action {
    Redirect(routes.Login.login()).withNewSession
  }

  def error(message: String) = withAuth() {
    user =>
      implicit request =>
        Ok(views.html.error(message, user, user.id.get, user))
  }

  val loginForm = Form(
    tuple(
      "login" -> nonEmptyText(),
      "password" -> nonEmptyText()
    ) verifying("invalid.user.or.password", fields => fields match {
      case (l, p) => userDao.login(l, p).isDefined
    })
  )
}



