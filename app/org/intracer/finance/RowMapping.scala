package org.intracer.finance

import org.apache.poi.ss.usermodel.{Row, Cell, CellValue, DateUtil}

import scalaz.Scalaz._

class RowMapping(val row: Row, val cfg: ColumnsConfig) {

  private def getCell(f: ColumnsConfig => String): Option[Cell] = {
    val index: Int = cfg(f)
    require(index >= 0, "column index not found")

    Option(row.getCell(index))
  }

  private def amount(f: ColumnsConfig => String): Option[Double] = {
    getCell(f).flatMap {
      cell =>
        getCellSimpleNumericValue(cell)
          .orElse(
            (cell.getCellType == Cell.CELL_TYPE_FORMULA)
              .option(Main.evaluator.evaluate(cell)).flatMap(getCellValueNumericValue)
          )
    }
  }

  def getCellSimpleNumericValue(cell: Cell): Option[Double] =
    (cell.getCellType == Cell.CELL_TYPE_NUMERIC && !DateUtil.isCellDateFormatted(cell))
      .option(cell.getNumericCellValue)

  def getCellValueNumericValue(value: CellValue): Option[Double] =
    (value.getCellType == Cell.CELL_TYPE_NUMERIC)
      .option(value.getNumberValue)

  private def description(f: ColumnsConfig => String): Option[String] =
    getCell(f).flatMap {
      cell => (cell.getCellType == Cell.CELL_TYPE_STRING)
        .option(cell.getRichStringCellValue.getString)
    }


  def income = amount(_.income)

  def incomeDesc = description(_.incomeDesc)

  def expenditure = amount(_.expenditure)

  def expenditureDesc = description(_.expenditureDesc)

  def mapping = description(_.mapping)

  def grantRow = (!cfg.grantRow.isEmpty).fold(description(_.grantRow), None)

  //if (!cfg.grantRow.isEmpty) description(c => c.grantRow) else None

}
