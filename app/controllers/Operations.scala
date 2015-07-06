package controllers

import com.github.nscala_time.time.Imports._
import org.intracer.finance.{Operation, User}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

object Operations extends Controller with Secured {


  //  def list = Action {
  //    implicit request =>
  //
  //      val operations = Global.operations.sortBy(_.date.toString()).toSeq
  //
  //      Ok(views.html.operations(operations, Seq("x")))
  //  }
  val defaultDateRange: String = "12/15/2013 - 12/20/2015"

  def list = withAuth {
    username =>
      implicit request =>

        val map = request.queryString
        val projects = map.getOrElse("projects", Nil).toSet
        val categories = map.getOrElse("categories", Nil).toSet
        val grants = map.getOrElse("grants", Nil).toSet

        val daterange = map.get("daterange").orElse(Option(Seq(defaultDateRange)))
        var operations: Seq[Operation] = filterOperations(projects, categories, grants, daterange)

        val total = operations.map(_.amount).sum.toDouble

        Ok(views.html.operations(new User(***REMOVED***),
          operations, total, projects, categories, grants,
          daterange.map(_.head).getOrElse(defaultDateRange),
          "/operations"))
  }

  def byGrantRow = withAuth {
    username =>
      implicit request =>

        val map = request.queryString
        val projects = map.getOrElse("projects", Nil).toSet
        val categories = map.getOrElse("categories", Nil).toSet
        val grants = map.getOrElse("grants", Nil).toSet

        val daterange = map.get("daterange").orElse(Option(Seq(defaultDateRange)))
        val filtered = filterOperations(projects, categories, grants, daterange)
        val sorted = filtered.sortBy(o => o.to.grantRow.getOrElse("?????") + o.date.toString())

        val keys = filtered.map(o => o.to.grantRow.getOrElse("?????") + o.date.toString()).sorted

        val total = filtered.map(_.amount).sum.toDouble

        Ok(views.html.operations(
          new User(***REMOVED***), sorted, total, projects, categories, grants,
          daterange.map(_.head).getOrElse(defaultDateRange),
          "/bygrantrow"))
  }


  def byGrantRowStat = Action {
    implicit request =>

      val map = request.queryString
      val projects = map.getOrElse("projects", Nil).toSet
      val categories = map.getOrElse("categories", Nil).toSet
      val grants = map.getOrElse("grants", Nil).toSet
      val rate = map.get("rate").map(_.head.toDouble).getOrElse(Global.uahToUsd)

      val daterange = map.get("daterange").orElse(Option(Seq(defaultDateRange)))

       Global.uahToUsd = rate

      val operations: Seq[Operation] = filterOperations(projects, categories, grants, daterange)

      val operationsByGrantRow = operations.groupBy(o => o.to.grantRow.getOrElse(""))

      val total = operations.map(_.amount).sum.toDouble

      Ok(views.html.grantStatistics(operations, total, projects, categories, grants, daterange.map(_.head).getOrElse(defaultDateRange),
        operationsByGrantRow, Some(rate)))
  }



  def filterOperations(projects: Set[String], categories: Set[String], grants: Set[String], daterange: Option[Seq[String]]): Seq[Operation] = {
    var operations = Global.operations.sortBy(_.date.toString()).toSeq

    if (projects.nonEmpty) {
      operations = operations.filter(op => projects.contains(op.to.projectCode.name))
    }
    if (categories.nonEmpty) {
      operations = operations.filter(op => categories.contains(op.to.categoryCode.name))
    }

    if (grants.nonEmpty) {
      operations = operations.filter(op => op.to.grantCode.exists(grant => grants.contains(grant.name)))
    }

    val pattern = "MM/dd/yyyy"

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
//    val max = DateTime.parse("12/14/2012", DateTimeFormat.forPattern(pattern))
//    operations = operations.filter(op => op.date <= max)

    operations
  }

  def statistics() = Action {
    implicit request =>

      val map = request.queryString
      val projects = map.getOrElse("projects", Nil).toSet
      val categories = map.getOrElse("categories", Nil).toSet
      val grants = map.getOrElse("grants", Nil).toSet

      val daterange = map.get("daterange").orElse(Option(Seq(defaultDateRange)))

      val operations: Seq[Operation] = filterOperations(projects, categories, grants, daterange)

      val operationsByProject = operations.groupBy(o => o.to.projectCode.name)
      val operationsByCategory = operations.groupBy(o => o.to.categoryCode.name)
      val operationsByGrant = operations.groupBy(o => o.to.grantCode.map(_.name).getOrElse("No"))

      val operationsByProjectAndCategory = operations.groupBy(o => o.to.projectCode.name + "." + o.to.categoryCode.name)

      val operationsByGrantRow = operations.groupBy(o => o.to.grantRow.getOrElse(""))

      val total = operations.map(_.amount).sum.toDouble

      Ok(views.html.statistics(operations, total, projects, categories, grants, daterange.map(_.head).getOrElse(defaultDateRange),
        operationsByProject, operationsByCategory, operationsByGrant, operationsByGrantRow, operationsByProjectAndCategory))
  }

  import play.api.data.format.Formats._
  val form = Form(
    tuple(
      "projects" -> Forms.list(text),
      "categories" -> Forms.list(text),
      "grants" -> Forms.list(text),
      "rate" -> of(doubleFormat)
    )
  )
}
