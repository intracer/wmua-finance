package controllers

import play.api._
import org.intracer.finance._
import org.apache.poi.ss.usermodel.Sheet
import scala.collection.JavaConverters._

object Global extends GlobalSettings {

  lazy val operations = loadFinance()

  var mapping: CodeMapping = _

  override def onStart(app: Application) {

    loadFinance()
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }


  def loadFinance(): Seq[Operation] = {

//    val wb = XlsTools.load("wmua7")
    val wb = XlsTools.load("WMUA_finance")
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


  def getOperations(sheet: Sheet) = {
    val cacheOperations = sheet.asScala.flatMap(row => AccountMapper.map(row, CacheConfig))
    val uahOperations = sheet.asScala.flatMap(row => AccountMapper.map(row, UahConfig))
    val uahOperations1 = sheet.asScala.flatMap(row => AccountMapper.map(row, UahProgram))
    val uahOperations2 = sheet.asScala.flatMap(row => AccountMapper.map(row, UahColessa))

    cacheOperations ++ uahOperations ++ uahOperations1 ++ uahOperations2
  }


}