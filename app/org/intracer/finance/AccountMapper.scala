package org.intracer.finance

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.CellReference

import scalaz._
import Scalaz._
import org.joda.time.DateTime
import scala.collection.JavaConverters._
import scala.collection.mutable


object AccountMapper {

  var lastDateCell: Option[Cell] = None

  def map(row: Row, cfg: ColumnsConfig): Option[Operation] = {

    val cell1 = Option(row.getCell(0)).flatMap(cell => (cell.getCellType != Cell.CELL_TYPE_BLANK).option({lastDateCell = Some(cell); cell})).orElse(lastDateCell)

    val dateOpt = cell1.flatMap(cell => (cell.getCellType == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)).option(cell.getDateCellValue))
    dateOpt flatMap {
      date =>

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
                if (parts.length < 3) {
                  println("Oops!")
                }

                val (category, project, grant) = (new CategoryF(parts(1)), new Project(parts(0)), new Grant(parts(2)))

                val grantRow = wleRow.grantRow

                val cellRef = new CellReference(/*row.getSheet.getSheetName, */ row.getRowNum, cfg(c => c.expenditure), false, false)

                val to = new Expenditure(category, project, Some(grant), grantRow, desc, cellRef)
                new Operation(CacheAccount, to, cost, new DateTime(date))
            }
        }

    }


  }

  def readMapping(sheet: Sheet) = {
    val mapping = new CodeMapping()

    var map = new mutable.HashMap[String, String]()

    var maps = Seq[mutable.HashMap[String, String]]()

    for (row <- sheet.asScala.tail) {
      val c1 = row.getCell(0)
      val c2 = row.getCell(1)

      if (c2 == null || c2.getCellType == Cell.CELL_TYPE_BLANK) {

        if (!map.isEmpty)
          maps = maps ++ Seq(map)
        println("New map, old = " + map)
        map = new mutable.HashMap[String, String]()
      }

      if (c2 != null && c2.getCellType != Cell.CELL_TYPE_BLANK) {
        val c3 = row.getCell(2)
        val tp = if (c3 != null && c3.getCellType != Cell.CELL_TYPE_BLANK) {
          c3.getStringCellValue + "/" + c2.getStringCellValue
        } else c2.getStringCellValue
        map(c1.getStringCellValue) = tp

        println(c1.getStringCellValue + " = " + tp)
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

class ColumnsConfig(
                     val income: String,
                     val incomeDesc: String,
                     val expenditure: String,
                     val expenditureDesc: String,
                     val mapping: String = "",
                     val grantRow: String = ""
                     ) {
  def apply(f: ColumnsConfig => String): Int = CellReference.convertColStringToIndex(f(this))
}

class RowMapping(val row: Row, val cfg: ColumnsConfig) {
  private def getCell(f: ColumnsConfig => String) = {
    val index: Int = cfg(f)
    require(index >= 0, "column index not found")
    row.getCell(index)
  }

  private def amount(f: ColumnsConfig => String): Option[Double] = {
    Option(getCell(f)).flatMap {
      cell =>
        getCellSimpleNumericValue(cell)
          .orElse((cell.getCellType == Cell.CELL_TYPE_FORMULA).option(Main.evaluator.evaluate(cell)).flatMap(getCellValueNumericValue))
    }
  }

  def getCellSimpleNumericValue(cell: Cell): Option[Double] =
    (cell.getCellType == Cell.CELL_TYPE_NUMERIC && !DateUtil.isCellDateFormatted(cell)).option(cell.getNumericCellValue)

  def getCellValueNumericValue(value: CellValue): Option[Double] = (value.getCellType == Cell.CELL_TYPE_NUMERIC).option(value.getNumberValue)

  private def description(f: ColumnsConfig => String): Option[String] = {
    val cell = getCell(f)

    if (cell==null){

      val errMsg = s"Empty cell at ${row.getSheet.getSheetName} ${row.getRowNum} ${cfg(f)}}"
      println(errMsg)
    }

    (cell.getCellType == Cell.CELL_TYPE_STRING).option(cell.getRichStringCellValue.getString)
  }

  def income = amount(c => c.income)

  def incomeDesc = description(c => c.incomeDesc)

  def expenditure = amount(c => c.expenditure)

  def expenditureDesc = description(c => c.expenditureDesc)

  def mapping = description(c => c.mapping)
  
  def grantRow = if (!cfg.grantRow.isEmpty) description(c => c.grantRow) else None
}


object CacheConfig extends ColumnsConfig(
  income = "C",
  incomeDesc = "D",
  expenditure = "E",
  expenditureDesc = "F",
  mapping = "G"
)

object UahConfig extends ColumnsConfig(
  income = "K",
  incomeDesc = "L",
  expenditure = "M",
  expenditureDesc = "N",
  grantRow = "O",
  mapping = "P"
)

object UahProgram extends ColumnsConfig(
  income = "T",
  incomeDesc = "U",
  expenditure = "V",
  expenditureDesc = "W",
  grantRow = "X",
  mapping = "Y"
)

object UahColessa extends ColumnsConfig(
  income = "AC",
  incomeDesc = "AD",
  expenditure = "AE",
  expenditureDesc = "AF",
  grantRow = "AG",
  mapping = "AH"
)

object WleConfig extends ColumnsConfig(
  income = "",
  incomeDesc = "",
  expenditure = "D",
  expenditureDesc = "A",
  mapping = "F",
  grantRow = ""
)



