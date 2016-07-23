package controllers

import com.github.nscala_time.time.Imports._
import org.intracer.finance.{Operation, User}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

object Resolutions extends Controller with Secured {


  //  def list = Action {
  //    implicit request =>
  //
  //      val operations = Global.operations.sortBy(_.date.toString()).toSeq
  //
  //      Ok(views.html.operations(operations, Seq("x")))
  //  }
  val defaultDateRange: String = "12/17/2011 - 12/14/2012"

  def list = withAuth {
    username =>
      implicit request =>

        val map = request.queryString
        val projects = map.getOrElse("projects", Nil).toSet
        val categories = map.getOrElse("categories", Nil).toSet
        val grants = map.getOrElse("grants", Nil).toSet

        val daterange = map.get("daterange").orElse(Option(Seq(defaultDateRange)))
        var operations: Seq[Operation] = filterOperations(projects, categories, grants, daterange)

        val total = operations.map(_.amount.map(_.toDouble).getOrElse(0.0)).sum

        Ok(views.html.resolutions(new User(***REMOVED***), operations, total, projects, categories, grants, daterange.map(_.head).getOrElse(defaultDateRange)))
  }


  def filterOperations(projects: Set[String], categories: Set[String], grants: Set[String], daterange: Option[Seq[String]]): Seq[Operation] = {
    var operations = Global.operations.sortBy(_.date.toString()).toSeq

    if (!projects.isEmpty) {
      operations = operations.filter(op => projects.contains(op.to.project.name))
    }
    if (!categories.isEmpty) {
      operations = operations.filter(op => categories.contains(op.to.category.name))
    }

    if (!grants.isEmpty) {
      operations = operations.filter(op => op.to.grant.exists(grant => grants.contains(grant.name)))
    }

    val pattern = "MM/dd/yyyy";

    daterange.foreach {
      range =>
        val head: String = range.head

        if (!head.trim.isEmpty) {
          val arr = head.split("-")

          val dates = arr.map(entry =>
            DateTime.parse(entry.trim, DateTimeFormat.forPattern(pattern))
          )

          operations = operations.filter(op => op.date >= dates(0) && op.date <= dates(1))
        }

    }
    val max = DateTime.parse("12/14/2012", DateTimeFormat.forPattern(pattern))
    operations = operations.filter(op => op.date <= max)

    operations
  }

  val form = Form(
    tuple(
      "projects" -> Forms.list(text),
      "categories" -> Forms.list(text),
      "grants" -> Forms.list(text)
    )
  )
}
