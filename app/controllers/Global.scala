package controllers

import java.io.File

import play.api._
import org.intracer.finance._
import org.apache.poi.ss.usermodel.{Cell, Sheet}
import scala.collection.JavaConverters._

object Global extends GlobalSettings {

  lazy val operations: Seq[Operation] = loadFinance()

  var mapping: CodeMapping = _

  var uahToUsd: Double = 22.0

  lazy val wmf: Map[String, WMF] = loadBudget()

  override def onStart(app: Application) {

    loadFinance()
    loadBudget()
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }


  def loadFinance(): Seq[Operation] = {

    //    val wb = XlsTools.load("wmua7")
    val wb = XlsTools.load("31-MAY-2015-UPD-UPD3")
    //  val configSheet = wb.getSheetAt(2)
    //
    //  val mapping = AccountMapper.readMapping(configSheet)

    //"wmua2011-2013_revised")
    Main.evaluator = wb.getCreationHelper.createFormulaEvaluator()

    val configSheet = wb.getSheetAt(0)

    mapping = AccountMapper.readMapping(configSheet)

    val sheet1 = wb.getSheetAt(1)

    val year1 = getOperations(sheet1)

    val operations = year1.toSeq

    Logger.info("Operations:" + operations.mkString("\n"))

    operations
  }

  def loadBudget() = {
    val wb = XlsTools.load(new File("conf/resources/PR2014.xlsx"))
    val sheet = wb.getSheetAt(0)

    val entries = sheet.asScala.flatMap {
      row =>
        val cell0 = row.getCell(0)

        if (cell0 != null && cell0.getCellType == Cell.CELL_TYPE_NUMERIC) {
          val code = cell0.getNumericCellValue.toString
          val value = row.getCell(5).getNumericCellValue
          val description = row.getCell(1).getStringCellValue
          println(s"$code | $description | $value")

          Some(WMF(code, description, value))

        } else {
          val string = row.cellIterator().asScala.mkString(" | ")
          println(string)
          None
        }
    }

    entries.map(e => e.code -> e ).toMap
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