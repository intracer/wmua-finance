package controllers

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.Silhouette
import org.intracer.finance.User
import org.intracer.finance.slick.UserDao
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Lang
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Results._
import play.api.mvc._
import slick.driver.H2Driver

@Singleton
class Login @Inject()(val userDao: UserDao, val silhouette: Silhouette[DefaultEnv]) extends Controller with Secured {

  def index = login

  def login = Action {
    implicit request =>
      if (userDao.count == 0 &&
        userDao.dbConfig.driver == H2Driver) {
        val dev = User(None, "developer", "dev@dot.com", password = Some(userDao.sha1("123")))
        userDao.insert(dev)
      }
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
          val result = Redirect("/assets/index.html#!/operations").withSession(Security.username -> value._1.trim)
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



