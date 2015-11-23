package org.intracer.finance

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.CellReference

import scalaz._
import Scalaz._
import org.joda.time.DateTime
import scala.collection.JavaConverters._


object AccountMapper {

  var lastDateCell: Option[Cell] = None

  def map(row: Row, cfg: ColumnsConfig): Option[Operation] = {

    val cell1 = Option(row.getCell(0)).flatMap(cell => (cell.getCellType != Cell.CELL_TYPE_BLANK).option({
      lastDateCell = Some(cell)
      cell
    })).orElse(lastDateCell)

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

  def readMapping(sheet: Sheet): CodeMapping = {
    val mapping = new CodeMapping()

    var map = Map.empty[String, String]

    var maps = Seq.empty[Map[String, String]]

    for (row <- sheet.asScala.tail) {
      val c1 = row.getCell(0)
      val c2 = row.getCell(1)

      if (c2 == null || c2.getCellType == Cell.CELL_TYPE_BLANK) {

        if (map.nonEmpty)
          maps = maps ++ Seq(map)
        println("New map, old = " + map)
        map = Map.empty[String, String]
      }

      if (c2 != null && c2.getCellType != Cell.CELL_TYPE_BLANK) {
        val c3 = row.getCell(2)
        val tp = if (c3 != null && c3.getCellType != Cell.CELL_TYPE_BLANK) {
          c3.getStringCellValue + "/" + c2.getStringCellValue
        } else c2.getStringCellValue
        map += c1.getStringCellValue -> tp

        println(c1.getStringCellValue + " = " + tp)
      }
    }
    maps = maps ++ Seq(map)

    val mappedMap = maps.zipWithIndex.map{case (m, i) => (i, m)}.toMap

    mapping.project = mappedMap.getOrElse(0, Map.empty)
    mapping.grant = mappedMap.getOrElse(1, Map.empty)
    mapping.category = mappedMap.getOrElse(2, Map.empty)

    mapping
  }

}

class CodeMapping {
  var project = Map.empty[String, String]
  var category = Map.empty[String, String]
  var grant = Map.empty[String, String]

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


object CacheConfig extends ColumnsConfig(
  income = "C",
  incomeDesc = "D",
  expenditure = "E",
  expenditureDesc = "F",
  grantRow = "G",
  mapping = "H"
)

object UahConfig extends ColumnsConfig(
  income = "L",
  incomeDesc = "M",
  expenditure = "N",
  expenditureDesc = "O",
  grantRow = "P",
  mapping = "Q"
)

object UahProgram extends ColumnsConfig(
  income = "U",
  incomeDesc = "V",
  expenditure = "W",
  expenditureDesc = "X",
  grantRow = "Y",
  mapping = "Z"
)

object UahColessa extends ColumnsConfig(
  income = "AD",
  incomeDesc = "AE",
  expenditure = "AF",
  expenditureDesc = "AG",
  grantRow = "AH",
  mapping = "AI"
)

object WleConfig extends ColumnsConfig(
  income = "",
  incomeDesc = "",
  expenditure = "D",
  expenditureDesc = "A",
  mapping = "F",
  grantRow = ""
)



