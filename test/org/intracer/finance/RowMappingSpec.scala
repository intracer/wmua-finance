package org.intracer.finance

import de.sciss.sheet._
import org.specs2.mutable.Specification

class RowMappingSpec extends Specification {

  sequential

  "RowMapping" should {
    "empty row" in {
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set.empty[Cell]
          })
        })
      }

      val poiBook = wb.peer

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
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set(NumericCell(0, 100.0), StringCell(1, "income"))
          })
        })
      }

      val poiBook = wb.peer

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
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set(FormulaCell(0, "= 100 + 50"), StringCell(1, "income"))
          })
        })
      }

      val poiBook = wb.peer

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
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set(NumericCell(2, 200.0), StringCell(3, "expenditure"))
          })
        })
      }

      val poiBook = wb.peer

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
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set(FormulaCell(2, "= 100 + 150"), StringCell(3, "expenditure"))
          })
        })
      }

      val poiBook = wb.peer
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
