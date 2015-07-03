package controllers

import client.finance.GrantItem
import org.intracer.finance.User
import play.api.mvc.Controller

object Grants extends Controller with Secured {

  def stat(name: String) = withAuth {
    username =>
      implicit request =>

        //GrantReader.grantItems(name)
        Ok(views.html.grantStat(new User(***REMOVED***), name, Seq.empty[GrantItem]))
  }

}
