package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

object Application extends Controller {

  def index = Action {
    implicit request =>

      Ok(views.html.index(Application.loginForm))
  }

  def auth() = Action {
    implicit request =>

    loginForm.bindFromRequest.fold(
      formWithErrors => // binding failure, you retrieve the form containing errors,
        BadRequest(views.html.index(formWithErrors)),
      value => // binding success, you get the actual value
        Redirect(routes.Operations.list()).withSession(Security.username -> value._1))
  }

  /**
   * Logout and clean the session.
   *
   * @return Index page
   */
  def logout = Action {
    //      session.data = Map()
    Redirect(routes.Application.index()).withNewSession
  }

  val loginForm = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText()
    ) verifying("Invalid user name or password", fields => fields match {
          case (e, p) => e == "intracer@gmail.com"
      })
  )
}



