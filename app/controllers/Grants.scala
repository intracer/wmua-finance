package controllers

import client.finance.GrantReader
import org.intracer.finance.slick.{Expenditures, GrantDao, GrantItemsDao, UserDao}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Controller
import javax.inject.Inject
import javax.inject.Singleton

import com.mohiva.play.silhouette.api.Silhouette

@Singleton
class Grants @Inject()(val userDao: UserDao,
                       val grantDao: GrantDao,
                       val silhouette: Silhouette[DefaultEnv]) extends Controller with Secured {

  def items(id: Int) = withAuth() {
    user =>
      implicit request =>

        val grant = grantDao.byId(id).get
        val items = GrantReader.grantItems(grant)

        Ok(views.html.grantItems(user, grant, items))
  }

  def list() = withAuth() {
    user =>
      implicit request =>

        val grants = grantDao.list
        Ok(views.html.grants(user, grants))
  }

  def importItems(id: Int) = withAuth() {
    user =>
      implicit request =>

        val grant = grantDao.byId(id).get
        val items = GrantReader.grantItems(grant)

        new GrantItemsDao().insertAll(items)

        Ok(views.html.grantItems(user, grant, items))
  }
}
