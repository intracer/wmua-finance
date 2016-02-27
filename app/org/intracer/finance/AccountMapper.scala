package org.intracer.finance

import java.util.Date

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.CellReference
import org.joda.time.DateTime

import scala.collection.JavaConverters._


object AccountMapper {

  //If date is absent in a spreadsheet row, a date from earlier row can be used
  var lastDateCell: Option[Cell] = None

  /**
    *
    * @param row spreadsheet row
    * @param cfg columns configuration
    * @return optional operation at this spreadsheet row
    */
  def map(row: Row, cfg: ColumnsConfig): Option[Operation] = {

    rowDate(row) flatMap {
      date =>

        val mappedRow = new RowMapping(row, cfg)

        mappedRow.expenditure flatMap {
          cost =>

            mappedRow.mappingId.map {
              mappingId =>
                val desc = mappedRow.expenditureDesc.getOrElse("-")
                val parts = if (mappingId.contains(".")) mappingId.split("\\.") else mappingId.split("-")
                if (parts.length < 3) {
                  println("Oops!")
                }

                val (category, project, grant) = (new CategoryF(parts(1)), new Project(parts(0)), new Grant(parts(2)))

                val grantRow = mappedRow.grantRow

                val cellRef = new CellReference(/*row.getSheet.getSheetName, */ row.getRowNum, cfg(cfg.expenditure), false, false)

                val to = new Expenditure(category, project, Some(grant), grantRow, desc, cellRef)
                new Operation(CacheAccount, to, cost, new DateTime(date))
            }
        }
    }
  }


  private def rowDate(row: Row): Option[Date] = {
    val cell = row.getCell(0)
    if (cell != null && cell.getCellType != Cell.CELL_TYPE_BLANK) {
      lastDateCell = Some(cell)
    }

    lastDateCell.flatMap { cell =>
      if (cell.getCellType == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell))
        Some(cell.getDateCellValue)
      else
        None
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

    val mappedMap = maps.zipWithIndex.map { case (m, i) => (i, m) }.toMap

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
  def apply(сellReference: String): Int = CellReference.convertColStringToIndex(сellReference)
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



