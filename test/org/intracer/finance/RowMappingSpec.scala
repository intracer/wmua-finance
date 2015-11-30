package org.intracer.finance

import org.specs2.mutable.Specification
import com.norbitltd.spoiwo.model._
import com.norbitltd.spoiwo.natures.xlsx.Model2XlsxConversions._

class RowMappingSpec extends Specification {

  sequential

  "RowMapping" should {
    "empty row" in {
      val wb = Workbook(Sheet(Row()))
      val poiBook = wb.convertAsXlsx

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig("A", "B", "C", "D", "E")
      val rm = new RowMapping(row, cc)

      rm.income === None
      rm.incomeDesc === None
      rm.expenditure === None
      rm.expenditureDesc === None
      rm.grantRow === None
    }

    "income row" in {
      val wb = Workbook(Sheet(Row().withCellValues(100.0, "income")))
      val poiBook = wb.convertAsXlsx

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig(income = "A", incomeDesc = "B", "C", "D", "E")
      val rm = new RowMapping(row, cc)

      rm.income === Some(100.0)
      rm.incomeDesc === Some("income")
      rm.expenditure === None
      rm.expenditureDesc === None
      rm.grantRow === None
    }

    "income row formula" in {
      val wb = Workbook(Sheet(Row().withCellValues("= 100 + 50", "income")))
      val poiBook = wb.convertAsXlsx

      Main.initEvaluator(poiBook)

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig(income = "A", incomeDesc = "B", "C", "D", "E")
      val rm = new RowMapping(row, cc)

      rm.income === Some(150.0)
      rm.incomeDesc === Some("income")
      rm.expenditure === None
      rm.expenditureDesc === None
      rm.grantRow === None
    }

    "expenditure row" in {
      val wb = Workbook(Sheet(Row().withCells(
        Cell(200.0, 2), Cell("expenditure", 3)
      )))

      val poiBook = wb.convertAsXlsx

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig("A", "B", expenditure = "C", expenditureDesc = "D", "E")
      val rm = new RowMapping(row, cc)

      rm.income === None
      rm.incomeDesc === None
      rm.expenditure === Some(200.0)
      rm.expenditureDesc === Some("expenditure")
      rm.grantRow === None
    }

    "expenditure row formula" in {
      val wb = Workbook(Sheet(Row().withCells(
        Cell("= 100 + 150", 2), Cell("expenditure", 3)
      )))

      val poiBook = wb.convertAsXlsx

      Main.initEvaluator(poiBook)

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig("A", "B", expenditure = "C", expenditureDesc = "D", "E")
      val rm = new RowMapping(row, cc)

      rm.income === None
      rm.incomeDesc === None
      rm.expenditure === Some(250.0)
      rm.expenditureDesc === Some("expenditure")
      rm.grantRow === None
    }
  }

}
