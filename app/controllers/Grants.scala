package controllers

import client.finance.GrantReader
import org.intracer.finance.User
import play.api.mvc.Controller

object Grants extends Controller with Secured {

  def stat(name: String) = withAuth {
    username =>
      implicit request =>

        val grantItems = GrantReader.grantItems(name)
        Ok(views.html.grantStat(new User(***REMOVED***), name, grantItems))
  }

}
