package org.intracer.finance

import java.util.Date

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.CellReference
import org.joda.time.DateTime

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

                val (category, project, grant) = (new CategoryF(code = parts(1)), new Project(code = parts(0)), new Grant(code = parts(2)))

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

}


