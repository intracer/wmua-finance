package controllers

import client.finance.{GrantItem, GrantReader}
import org.intracer.finance.User
import org.intracer.finance.slick.Expenditures
import play.api.mvc.Controller
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

object Grants extends Controller with Secured {

  def stat(name: String) = withAuth {
    username =>
      implicit request =>

        val items = GrantReader.grantItems(name)
        Ok(views.html.grantStat(new User(***REMOVED***), name, items))
  }

  def list() = withAuth {
    username =>
      implicit request =>

        val grants = Expenditures.grants.values.toSeq

        Ok(views.html.grants(new User(***REMOVED***), grants))

  }

}
