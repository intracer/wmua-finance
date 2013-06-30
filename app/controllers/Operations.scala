package controllers

import play.api.mvc.{Controller, Action}

object Operations extends Controller {


  def list = Action {
    implicit request =>

      val operations = Global.operations.sortBy(_.date.toString()).toSeq

      Ok(views.html.operations(operations))
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
}
