package controllers

import _root_.slick.backend.DatabaseConfig
import _root_.slick.driver.MySQLDriver
import org.intracer.finance._
import org.intracer.finance.slick.{Expenditures, FinDatabase}
import org.joda.time.DateTime
import play.api._

object Global extends GlobalSettings {

  def operations: Seq[Operation] = {
    db.expDao.list.map { e =>
      new Operation(e.from, e, e.amount, new DateTime(e.date.getTime))
    }
  }

  var uahToUsd: Double = 22.0

  val fileDate = "13-NOV-2015"

  val dbConfig: DatabaseConfig[MySQLDriver] = DatabaseConfig.forConfig("slick.dbs.default")

  val db = new FinDatabase(dbConfig.db)

  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }


  def projectsJson: String = {
    Expenditures.projects.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, project) => s"""{ value: "$id", text: "${project.name}"}"""
    }.mkString(", ")
  }

  def categoriesJson: String = {
    Expenditures.categories.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, cat) => s"""{ value: "$id", text: "${cat.name}"}"""
    }.mkString(", ")
  }

  def grantsJson: String = {
    Expenditures.grants.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, grant) => s"""{ value: "$id", text: "${grant.name}"}"""
    }.mkString(", ")
  }

  def grantItemsJson(grantId: Int): String = {
    val programs = Seq(
      "Program 1: Outreach", "Program 2: Contests", "Program 3: Community Support", "Administrative costs"
    )
    (1 to 4).map { program =>
      s"""{text: "${programs(program - 1)}", children: [""" +
        Expenditures.grantItems.getOrElse(grantId, Seq.empty).filter(_.number.startsWith(program.toString)).map {
          item => s"""{ value: "${item.id.get}", text: "${item.name}"}"""
        }.mkString(", ") + "]}"
    }.mkString(", ")
  }

  def accountsJson: String = {
    Expenditures.accounts.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, account) => s"""{ value: "$id", text: "${account.name}"}"""
    }.mkString(", ")
  }

  def isNumber(s: String): Boolean = s.matches("[+-]?\\d+.?\\d+")


}

case class WMF(code: String, description: String, value: Double)