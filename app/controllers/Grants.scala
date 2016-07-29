package controllers

import client.finance.{GrantItem, GrantReader}
import org.intracer.finance.User
import org.intracer.finance.slick.Expenditures
import play.api.mvc.Controller
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

object Grants extends Controller with Secured {

  def items(id: Int) = withAuth {
    username =>
      implicit request =>

        val grant = Expenditures.grants(id)
        val items = GrantReader.grantItems(grant)

        Ok(views.html.grantItems(new User(***REMOVED***), grant, items))
  }

  def list() = withAuth {
    username =>
      implicit request =>

        val grants = Expenditures.grants.values.toSeq

        Ok(views.html.grants(new User(***REMOVED***), grants))

  }

  def importItems(id: Int) = withAuth {
    username =>
      implicit request =>

        val grant = Expenditures.grants(id)
        val items = GrantReader.grantItems(grant)

        Global.db.grantItemDao.insertAll(items)

        Ok(views.html.grantItems(new User(***REMOVED***), grant, items))

  }

}
