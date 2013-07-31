package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import org.intracer.finance.User

object Operations extends Controller with Secured {


//  def list = Action {
//    implicit request =>
//
//      val operations = Global.operations.sortBy(_.date.toString()).toSeq
//
//      Ok(views.html.operations(operations, Seq("x")))
//  }

  def list = withAuth { username =>
    implicit request =>

      val map = request.queryString
      val projects = map.getOrElse("projects", Nil).toSet
      val categories = map.getOrElse("categories", Nil).toSet
      val grants = map.getOrElse("grants", Nil).toSet

      //      val (projects, categories, grants) = form.bindFromRequest.get

      var operations = Global.operations.sortBy(_.date.toString()).toSeq

      if (!projects.isEmpty) {
        operations = operations.filter(op => projects.contains(op.to.projectCode.name))
      }
      if (!categories.isEmpty) {
        operations = operations.filter(op => categories.contains(op.to.categoryCode.name))
      }

      if (!grants.isEmpty) {
        operations = operations.filter(op => op.to.grantCode.exists(grant => grants.contains(grant.name)))
      }

      Ok(views.html.operations(new User("Illia Korniiko"), operations, projects, categories, grants))
  }

  def statistics() = Action {
    implicit request =>

      val operations = Global.operations.sortBy(_.date.toString()).toSeq

      val operationsByProject = operations.groupBy(o => o.to.projectCode.name)
      val operationsByCategory = operations.groupBy(o => o.to.categoryCode.name)
      val operationsByGrant = operations.groupBy(o => o.to.grantCode.map(_.name).getOrElse("No"))

      val operationsByProjectAndCategory = operations.groupBy(o => o.to.projectCode.name + "." + o.to.categoryCode.name)


      Ok(views.html.statistics(operationsByProject, operationsByCategory, operationsByGrant, operationsByProjectAndCategory))
  }

  val form = Form(
    tuple(
      "projects" -> Forms.list(text),
      "categories" -> Forms.list(text),
      "grants" -> Forms.list(text)
    )
  )
}
