package controllers

import client.finance.GrantReader
import org.intracer.finance.slick.{Expenditures, GrantItemsDao, UserDao}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Controller
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Grants @Inject()(val userDao: UserDao) extends Controller with Secured {

  def items(id: Int) = withAuth() {
    user =>
      implicit request =>

        val grant = Expenditures.grants(id)
        val items = GrantReader.grantItems(grant)

        Ok(views.html.grantItems(user, grant, items))
  }

  def list() = withAuth() {
    user =>
      implicit request =>

        val grants = Expenditures.grants.values.toSeq
        Ok(views.html.grants(user, grants))
  }

  def importItems(id: Int) = withAuth() {
    user =>
      implicit request =>

        val grant = Expenditures.grants(id)
        val items = GrantReader.grantItems(grant)

        new GrantItemsDao().insertAll(items)

        Ok(views.html.grantItems(user, grant, items))
  }
}
