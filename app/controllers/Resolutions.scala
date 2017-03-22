package controllers

import javax.inject.Singleton
import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import org.intracer.finance.slick.UserDao
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

@Singleton
class Resolutions @Inject()(val userDao: UserDao, val silhouette: Silhouette[DefaultEnv])
  extends Controller with Secured {

  val defaultDateRange: String = "12/17/2011 - 12/14/2012"

  def list() = withAuth() {
    user =>
      implicit request =>
        Ok(views.html.resolutions(user))
  }
}
