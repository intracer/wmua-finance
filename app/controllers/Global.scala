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

    val wb = XlsTools.load("wmua")
    //  val configSheet = wb.getSheetAt(2)
    //
    //  val mapping = AccountMapper.readMapping(configSheet)

    //"wmua2011-2013_revised")
    Main.evaluator = wb.getCreationHelper.createFormulaEvaluator()

    val configSheet = wb.getSheetAt(2)

    mapping = AccountMapper.readMapping(configSheet)

    val sheet1 = wb.getSheetAt(0)
    val sheet2 = wb.getSheetAt(1)

    val year1 = getOperations(sheet1)
    val year2 = getOperations(sheet2)

    val operations = (year1 ++ year2).toSeq

    Logger.info("Operations:" + operations.mkString("\n"))

    operations
  }


  def getOperations(sheet: Sheet) = {
    val cacheOperations = sheet.asScala.flatMap(row => AccountMapper.map(row, CacheConfig))
    val uahOperations = sheet.asScala.flatMap(row => AccountMapper.map(row, UahConfig))

    cacheOperations ++ uahOperations
  }


}