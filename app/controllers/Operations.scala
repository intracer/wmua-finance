package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import org.intracer.finance.User
import org.intracer.finance.Operation
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

object Operations extends Controller with Secured {


  //  def list = Action {
  //    implicit request =>
  //
  //      val operations = Global.operations.sortBy(_.date.toString()).toSeq
  //
  //      Ok(views.html.operations(operations, Seq("x")))
  //  }

  def list = withAuth {
    username =>
      implicit request =>

        val map = request.queryString
        val projects = map.getOrElse("projects", Nil).toSet
        val categories = map.getOrElse("categories", Nil).toSet
        val grants = map.getOrElse("grants", Nil).toSet

        val daterange = map.get("daterange")

        var operations: Seq[Operation] = filterOperations(projects, categories, grants, daterange)

        val total = operations.map(_.amount).sum.toDouble

        Ok(views.html.operations(new User("WM UA"), operations, total, projects, categories, grants, daterange.map(_.head).getOrElse("")))
  }


  def filterOperations(projects: Set[String], categories: Set[String], grants: Set[String], daterange: Option[Seq[String]]): Seq[Operation] = {
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

    daterange.foreach {
      range =>
        val head: String = range.head

        if (!head.trim.isEmpty) {
          val arr = head.split("-")
          val pattern = "MM/dd/yyyy";

          val dates = arr.map(entry =>
            DateTime.parse(entry.trim, DateTimeFormat.forPattern(pattern))
          )

          operations = operations.filter(op => op.date >= dates(0) && op.date <= dates(1))
        }

    }
    operations
  }

  def statistics() = Action {
    implicit request =>

      val map = request.queryString
      val projects = map.getOrElse("projects", Nil).toSet
      val categories = map.getOrElse("categories", Nil).toSet
      val grants = map.getOrElse("grants", Nil).toSet

      val daterange = map.get("daterange")

      var operations: Seq[Operation] = filterOperations(projects, categories, grants, daterange)

      val operationsByProject = operations.groupBy(o => o.to.projectCode.name)
      val operationsByCategory = operations.groupBy(o => o.to.categoryCode.name)
      val operationsByGrant = operations.groupBy(o => o.to.grantCode.map(_.name).getOrElse("No"))

      val operationsByProjectAndCategory = operations.groupBy(o => o.to.projectCode.name + "." + o.to.categoryCode.name)

      val total = operations.map(_.amount).sum.toDouble

      Ok(views.html.statistics(operations, total, projects, categories, grants, daterange.map(_.head).getOrElse(""),
        operationsByProject, operationsByCategory, operationsByGrant, operationsByProjectAndCategory))
  }

  val form = Form(
    tuple(
      "projects" -> Forms.list(text),
      "categories" -> Forms.list(text),
      "grants" -> Forms.list(text)
    )
  )
}
