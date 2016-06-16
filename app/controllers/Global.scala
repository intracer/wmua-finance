package controllers

import java.io.File

import _root_.slick.backend.DatabaseConfig
import _root_.slick.driver.MySQLDriver
import org.apache.poi.ss.usermodel.{Cell, Sheet}
import org.intracer.finance._
import org.intracer.finance.slick.{Expenditures, FinDatabase}
import org.joda.time.DateTime
import play.api._

import scala.collection.JavaConverters._

object Global extends GlobalSettings {

  def operations: Seq[Operation] = {
    db.expDao.list.map{ e =>
      new Operation(e.from, e, e.amount, new DateTime(e.date.getTime))
    }
  }

  var mapping = new CodeMapping

  var uahToUsd: Double = 22.0

  lazy val wmf: Map[String, WMF] = loadBudget()

  val fileDate = "13-NOV-2015"

  val dbConfig: DatabaseConfig[MySQLDriver] = DatabaseConfig.forConfig("slick.dbs.default")

  val db = new FinDatabase(dbConfig.db)


  override def onStart(app: Application) {

    loadFinance()
    //loadBudget()
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


  def loadFinance(): Seq[Operation] = {

    //    val wb = XlsTools.load("wmua7")
    val wb = XlsTools.load(fileDate + ".xls")
    //  val configSheet = wb.getSheetAt(2)
    //
    //  val mapping = AccountMapper.readMapping(configSheet)

    //"wmua2011-2013_revised")
    Main.evaluator = wb.getCreationHelper.createFormulaEvaluator()

    val configSheet = wb.getSheetAt(0)

    mapping = CodeMapping.readMapping(configSheet)

    val sheet1 = wb.getSheetAt(1)

    val year1 = getOperations(sheet1)

    val operations = year1.toSeq

    Logger.info("Operations:" + operations.mkString("\n"))

    operations
  }

  def isNumber(s: String): Boolean = s.matches("[+-]?\\d+.?\\d+")

  def loadBudget() = {
    val wb = XlsTools.load(new File("conf/resources/PR2014.xlsx"))
    val sheet = wb.getSheetAt(0)

    val entries = sheet.asScala.flatMap {
      row =>
        val cell0 = row.getCell(0)

        if (cell0 != null) {

          val codeOption = if (cell0.getCellType == Cell.CELL_TYPE_NUMERIC)
            Some(cell0.getNumericCellValue.toString)
          else {
            val value = cell0.getStringCellValue
            if (isNumber(value))
              Some(value)
            else None
          }

          codeOption.flatMap { code =>
            val value = row.getCell(5).getNumericCellValue
            val description = row.getCell(1).getStringCellValue
            println(s"$code | $description | $value")

            Some(WMF(code, description, value))
          }

        } else {
          val string = row.cellIterator().asScala.mkString(" | ")
          println(string)
          None
        }
    }

    val map = entries.map(e => e.code -> e).toMap
    map
  }

  def getOperations(sheet: Sheet) = {
    val cacheOperations = sheet.asScala.flatMap(row => AccountMapper.map(row, CacheConfig))
    val uahOperations = sheet.asScala.flatMap(row => AccountMapper.map(row, UahConfig))
    val uahOperations1 = sheet.asScala.flatMap(row => AccountMapper.map(row, UahProgram))
    val uahOperations2 = sheet.asScala.flatMap(row => AccountMapper.map(row, UahColessa))

    cacheOperations ++ uahOperations ++ uahOperations1 ++ uahOperations2
  }

  def main(args: Array[String]) {
    loadBudget()
  }


}

case class WMF(code: String, description: String, value: Double)