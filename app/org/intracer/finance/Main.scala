package org.intracer.finance

import org.apache.poi.ss.util.CellReference
import scala.collection.JavaConverters._
import org.apache.poi.ss.usermodel._

object Main {

  var evaluator: FormulaEvaluator = _


  def main(args: Array[String]) {

    val wb = XlsTools.load("wmua2")
    val configSheet = wb.getSheetAt(2)

    val mapping = CodeMapping.readMapping(configSheet)

    //"wmua2011-2013_revised")
    initEvaluator(wb)

    val sheet1 = wb.getSheetAt(0)
    val sheet2 = wb.getSheetAt(1)

    updateSheet(sheet1, mapping)
    updateSheet(sheet2, mapping)

    XlsTools.save(wb, "wmua_out")


    //
    //    val sum = operations.foldLeft(BigDecimal(0))(_ + _.amount)
    //
    //    println(sum)

    //    printAll(sheet, evaluator)
  }


  def initEvaluator(wb: Workbook): Unit = {
    evaluator = wb.getCreationHelper.createFormulaEvaluator()
  }

  def updateSheet(sheet: Sheet, mapping: CodeMapping) {
    val cacheOperations = sheet.asScala.flatMap(row => AccountMapper.map(row, CacheConfig))
    val uahOperations = sheet.asScala.flatMap(row => AccountMapper.map(row, UahConfig))

    val operations = cacheOperations ++ uahOperations

    operations.foreach(println)

    val operationsByProject = operations.groupBy(o => o.to.project.name)
    val operationsByCategory = operations.groupBy(o => o.to.category.name)
    val operationsByGrant = operations.groupBy(o => o.to.grant.map(_.name).getOrElse("No"))

    val operationsByProjectAndCategory = operations.groupBy(o => o.to.project.name + "." + o.to.category.name)

    val obpSheet = sheet //wb.createSheet("operationsByProject")

    var rowNum = sheet.getLastRowNum

    def groupCell(groupId: Int) = groupId + " " + mapping.project(groupId)

    rowNum = addStatistics(rowNum, obpSheet, operationsByProject, "Project", groupId => groupId + " " + mapping.project(groupId))
    rowNum = addStatistics(rowNum, obpSheet, operationsByCategory, "Category", groupId => groupId + " " + mapping.category(groupId))
    rowNum = addStatistics(rowNum, obpSheet, operationsByGrant, "Grant", groupId => groupId + " " + mapping.grant(groupId))

    def groupCell2(groupId: Int) = {
      val parts = Seq(groupId, groupId, groupId)
      groupId * 1000 +  mapping.project(parts(0)) * 100 +  mapping.category(parts(1))
    }

    rowNum = addStatistics(rowNum, obpSheet, operationsByProjectAndCategory, "Project-Category", groupCell2)
  }


  def addStatistics(rowNum: Int, obpSheet: Sheet, groupedOperations: Map[String, Iterable[Operation]],
                    groupName: String, groupCell: Int => String) = {
    var rowIndex = rowNum
    rowIndex += 1
    val row = obpSheet.createRow(rowNum)
    rowIndex += 1

    row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(groupName)
    row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("Amount")

    for (groupId <- groupedOperations.keys.toSeq.sorted) {
      val operations = groupedOperations(groupId)
      val row = obpSheet.createRow(rowNum)
      rowIndex += 1

      val projectCell = row.createCell(0, Cell.CELL_TYPE_STRING)
      projectCell.setCellValue(groupCell(groupId.toInt))

      val formulaCell = row.createCell(1, Cell.CELL_TYPE_FORMULA)
      val formula = operations.map(_.to.ref.formatAsString).mkString("+")
      formulaCell.setCellFormula(formula)
    }
    rowIndex
  }

  def printAll(sheet: Sheet, evaluator: FormulaEvaluator) {
    for (row <- sheet.asScala) {
      for ((cell, i) <- row.asScala.zipWithIndex) {
        val cellRef = new CellReference(row.getRowNum, cell.getColumnIndex)
        print(cellRef.formatAsString())
        print(" - ")

        cell.getCellType match {
          case Cell.CELL_TYPE_STRING =>
            println(cell.getRichStringCellValue.getString)
          case Cell.CELL_TYPE_NUMERIC =>
            if (DateUtil.isCellDateFormatted(cell)) {
              println(cell.getDateCellValue)
            } else {
              println(cell.getNumericCellValue)
            }
          case Cell.CELL_TYPE_BOOLEAN =>
            println(cell.getBooleanCellValue)
          case Cell.CELL_TYPE_FORMULA =>
            print(cell.getCellFormula + " = "); evaluate(cell, evaluator)
          case Cell.CELL_TYPE_BLANK => println
          case x =>
            println("? " + x);
        }
      }
    }
  }

  def evaluate(cell: Cell, evaluator: FormulaEvaluator) {

    val cellValue = evaluator.evaluate(cell)

    cellValue.getCellType match {

      case Cell.CELL_TYPE_BOOLEAN =>
        println(cellValue.getBooleanValue)
      case Cell.CELL_TYPE_NUMERIC =>
        println(cellValue.getNumberValue)
      case Cell.CELL_TYPE_STRING =>
        println(cellValue.getStringValue)
      case Cell.CELL_TYPE_BLANK =>
        println
      case Cell.CELL_TYPE_ERROR =>
        println("error")
      case Cell.CELL_TYPE_FORMULA =>
    }

  }
}
