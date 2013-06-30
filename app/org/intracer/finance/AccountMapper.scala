package org.intracer.finance

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.CellReference

import scalaz._
import Scalaz._
import org.joda.time.DateTime
import scala.collection.JavaConverters._
import scala.collection.mutable


object AccountMapper {

  var lastDateCell: Cell = _

  def map(row: Row, cfg: ColumnsConfig): Option[Operation] = {

    val cell1 = Option(row.getCell(0)).flatMap(cell => (cell.getCellType != Cell.CELL_TYPE_BLANK).option(cell)).getOrElse(lastDateCell)

    val dateOpt = (cell1.getCellType == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell1)).option(cell1.getDateCellValue)
    dateOpt flatMap {
      date =>
        lastDateCell = cell1
        //            val cacheRow = new RowMapping(row, CacheConfig)
        //            val uahRow = new RowMapping(row, UahConfig)
        val wleRow = new RowMapping(row, cfg)

        wleRow.expenditure flatMap {
          cost =>

          //            val project = if (desc.contains("ВЛП") || desc.contains("ВЛМ")) WLM else NoProject
          //            val category = if (desc.contains("транспорт")) Transport else if (desc.contains("накладні втрати")) SPD else NoCategory

            val mappingOpt = wleRow.mapping
            mappingOpt.map {
              mapping =>
                val desc = wleRow.expenditureDesc.getOrElse("-")
                val parts = if (mapping.contains(".")) mapping.split("\\.") else mapping.split("-")
                val (category, project, grant) = (new CategoryF(parts(1)), new Project(parts(0)), new Grant(parts(2)))

                val cellRef = new CellReference(/*row.getSheet.getSheetName, */ row.getRowNum, cfg(c => c.expenditure), false, false)

                val to = new Expenditure(category, project, Some(grant), desc, cellRef)
                new Operation(CacheAccount, to, cost, new DateTime(date))
            }
        }

    }


  }

  def readMapping(sheet: Sheet) = {
    val mapping = new CodeMapping()

    var map = new mutable.HashMap[String, String]()

    var maps = Seq[mutable.HashMap[String, String]]()

    for (row <- sheet.asScala) {
      val c1 = row.getCell(0)
      val c2 = row.getCell(1)

      if (row.getRowNum == 16 || row.getRowNum == 24) {
        maps = maps ++ Seq(map)
        map = new mutable.HashMap[String, String]()
      }

      if (c2 != null && c2.getCellType != Cell.CELL_TYPE_BLANK) {
        map(c1.getStringCellValue) = c2.getStringCellValue
      }
    }
    maps = maps ++ Seq(map)

    mapping.project = maps(0)
    mapping.grant = maps(1)
    mapping.category = maps(2)

    mapping

  }

}

class CodeMapping {
  var project = new mutable.HashMap[String, String]()
  var category = new mutable.HashMap[String, String]()
  var grant = new mutable.HashMap[String, String]()

}

class ColumnsConfig(val income: String, val incomeDesc: String, val expenditure: String, val expenditureDesc: String, val mapping: String = "") {
  def apply(f: ColumnsConfig => String) = CellReference.convertColStringToIndex(f(this))
}

class RowMapping(val row: Row, val cfg: ColumnsConfig) {
  private def getCell(f: ColumnsConfig => String) = row.getCell(cfg(f))

  private def amount(f: ColumnsConfig => String): Option[Double] = {
    Option(getCell(f)).flatMap {
      cell =>
        getCellSimpleNumericValue(cell)
          .orElse((cell.getCellType == Cell.CELL_TYPE_FORMULA).option(Main.evaluator.evaluate(cell)).flatMap(getCellValueNumericValue(_)))
    }
  }

  def getCellSimpleNumericValue(cell: Cell): Option[Double] =
    (cell.getCellType == Cell.CELL_TYPE_NUMERIC && !DateUtil.isCellDateFormatted(cell)).option(cell.getNumericCellValue)

  def getCellValueNumericValue(value: CellValue): Option[Double] = (value.getCellType == Cell.CELL_TYPE_NUMERIC).option(value.getNumberValue)

  private def description(f: ColumnsConfig => String): Option[String] = {
    val cell = getCell(f)

    (cell.getCellType == Cell.CELL_TYPE_STRING).option(cell.getRichStringCellValue.getString)
  }

  def income = amount(c => c.income)

  def incomeDesc = description(c => c.incomeDesc)

  def expenditure = amount(c => c.expenditure)

  def expenditureDesc = description(c => c.expenditureDesc)

  def mapping = description(c => c.mapping)
}


object CacheConfig extends ColumnsConfig("C", "D", "E", "F", "G")

object UahConfig extends ColumnsConfig("K", "L", "M", "N", "O")

object WleConfig extends ColumnsConfig("", "", "D", "A", "F")



