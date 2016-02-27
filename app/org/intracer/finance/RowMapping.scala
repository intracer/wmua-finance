package org.intracer.finance

import org.apache.poi.ss.usermodel.{Row, Cell, CellValue, DateUtil}

import scalaz.Scalaz._

class RowMapping(val row: Row, val cfg: ColumnsConfig) {

  def income = amount(cfg.income)

  def incomeDesc = string(cfg.incomeDesc)

  def expenditure = amount(cfg.expenditure)

  def expenditureDesc = string(cfg.expenditureDesc)

  def mappingId = string(cfg.mapping)

  def grantRow = (!cfg.grantRow.isEmpty).fold(string(cfg.grantRow), None)

  //if (!cfg.grantRow.isEmpty) string(c => c.grantRow) else None

  private def mapCell[T](cellReference: String)(f: Cell => Option[T]): Option[T] =
    getCell(cellReference).flatMap(f)

  private def getCell(cellReference: String): Option[Cell] = {
    val index: Int = cfg(cellReference)
    require(index >= 0, "column index not found")

    Option(row.getCell(index))
  }

  private def amount(cellReference: String): Option[Double] = {
    mapCell(cellReference) {
      cell =>
        simpleNumericValue(cell).orElse(formulaValue(cell))
    }
  }

  private def string(cellReference: String): Option[String] =
    mapCell(cellReference) {
      cell => (cell.getCellType == Cell.CELL_TYPE_STRING)
        .option(cell.getRichStringCellValue.getString)
    }

  private def numericValue(value: CellValue): Option[Double] =
    (value.getCellType == Cell.CELL_TYPE_NUMERIC)
      .option(value.getNumberValue)

  private def simpleNumericValue(cell: Cell): Option[Double] =
    (cell.getCellType == Cell.CELL_TYPE_NUMERIC && !DateUtil.isCellDateFormatted(cell))
      .option(cell.getNumericCellValue)

  private def formulaValue(cell: Cell): Option[Double] =
    (cell.getCellType == Cell.CELL_TYPE_FORMULA)
      .option(Main.evaluator.evaluate(cell)).flatMap(numericValue)

}
