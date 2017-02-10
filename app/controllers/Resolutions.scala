package controllers
import javax.inject.{Inject, Singleton}

import org.intracer.finance.slick.{Schema, UserDao}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

@Singleton
class Resolutions @Inject()(val schema: Schema, val userDao: UserDao) extends Controller with Secured {

  val defaultDateRange: String = "12/17/2011 - 12/14/2012"

  def list() = withAuth() {
    user =>
      implicit request =>

        Ok(views.html.resolutions(user))
  }

}
