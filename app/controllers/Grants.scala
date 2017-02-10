package controllers

import javax.inject.{Inject, Singleton}

import client.finance.GrantReader
import org.intracer.finance.slick.{Schema, UserDao}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Controller

@Singleton
class Grants @Inject()(val schema: Schema, val userDao: UserDao) extends Controller with Secured {

  def items(id: Int) = withAuth() {
    user =>
      implicit request =>
        val grant = schema.grants(id)
        val items = GrantReader.grantItems(grant)
        Ok(views.html.grantItems(user, grant, items, schema))
  }

  def list() = withAuth() {
    user =>
      implicit request =>
        val grants = schema.grants.values.toSeq
        Ok(views.html.grants(user, grants))
  }

  def importItems(id: Int) = withAuth() {
    user =>
      implicit request =>

        val grant = schema.grants(id)
        val items = GrantReader.grantItems(grant)

        schema.grantItemDao.insertAll(items)

        Ok(views.html.grantItems(user, grant, items, schema))
  }

}
