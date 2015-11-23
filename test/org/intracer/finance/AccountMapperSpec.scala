package org.intracer.finance

import de.sciss.sheet._
import org.apache.poi.ss.usermodel.{DateUtil}
import org.joda.time.DateTime
import org.specs2.mutable.Specification

class AccountMapperSpec extends Specification {

  val projects = Set(
    Row(0) {
      Set(StringCell(0, "проект"), StringCell(1, "назва проекту"))
    },
    Row(1) {
      Set(StringCell(0, "00"), StringCell(1, "адміністрування"))
    },
    Row(2) {
      Set(StringCell(0, "01"), StringCell(1, "ВЛЗ"))
    }
  )

  val grants = Set(
    Row(3) {
      Set(StringCell(0, "джерело коштів"))
    },
    Row(4) {
      Set(StringCell(0, "00"), StringCell(1, "власні кошти"))
    },
    Row(5) {
      Set(StringCell(0, "01"), StringCell(1, "Grants:WM UA/Programs in Ukraine 2012"))
    }
  )

  val categories = Set(
    Row(6) {
      Set(StringCell(0, "бюджетна категорія"))
    },
    Row(7) {
      Set(StringCell(0, "00"), StringCell(1, "проїзд"), StringCell(2, "заходи"))
    },
    Row(8) {
      Set(StringCell(0, "01"), StringCell(1, "проживання"), StringCell(2, "заходи"))
    },
    Row(9) {
      Set(StringCell(0, "02"), StringCell(1, "транспорт"), StringCell(2, "відрядження"))
    },
    Row(10) {
      Set(StringCell(0, "03"), StringCell(1, "проживання"), StringCell(2, "відрядження"))
    }
  )

  "readMapping" should {
    "read empty" in {
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set.empty[Cell]
          })
        })
      }

      val poiBook = wb.peer

      val mapping = AccountMapper.readMapping(poiBook.getSheetAt(0))

      mapping.grant === Map.empty
      mapping.category === Map.empty
      mapping.project === Map.empty
    }

    "read projects" in {
      val wb = Workbook {
        Set(Sheet("name") {
          projects
        })
      }

      val poiBook = wb.peer

      val mapping = AccountMapper.readMapping(poiBook.getSheetAt(0))

      mapping.project === Map("00" -> "адміністрування", "01" -> "ВЛЗ")
      mapping.grant === Map.empty
      mapping.category === Map.empty
    }

    "read projects and grants" in {
      val wb = Workbook {
        Set(Sheet("name") {
          projects ++ grants
        })
      }

      val poiBook = wb.peer

      val mapping = AccountMapper.readMapping(poiBook.getSheetAt(0))

      mapping.project === Map(
        "00" -> "адміністрування",
        "01" -> "ВЛЗ"
      )
      mapping.grant === Map(
        "00" -> "власні кошти",
        "01" -> "Grants:WM UA/Programs in Ukraine 2012"
      )
      mapping.category === Map.empty
    }

    "read projects, grants and categories" in {
      val wb = Workbook {
        Set(Sheet("name") {
          projects ++ grants ++ categories
        })
      }

      val poiBook = wb.peer

      val mapping = AccountMapper.readMapping(poiBook.getSheetAt(0))

      mapping.project === Map(
        "00" -> "адміністрування",
        "01" -> "ВЛЗ"
      )
      mapping.grant === Map(
        "00" -> "власні кошти",
        "01" -> "Grants:WM UA/Programs in Ukraine 2012"
      )
      mapping.category === Map(
        "00" -> "заходи/проїзд",
        "01" -> "заходи/проживання",
        "02" -> "відрядження/транспорт",
        "03" -> "відрядження/проживання"
      )
    }
  }

  "map" should {
    "map empty" in {
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

      val opOp = AccountMapper.map(row, cc)
      opOp === None
    }

    "map text" in {
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set(StringCell(0, "date"), StringCell(1, "income"))
          })
        })
      }

      val poiBook = wb.peer

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig(income = "A", incomeDesc = "B", "C", "D", "E")
      val rm = new RowMapping(row, cc)

      val opOp = AccountMapper.map(row, cc)
      opOp === None
    }

    "map date and no cells" in {
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set(StringCell(0, "01.07.13"))
          })
        })
      }

      val poiBook = wb.peer
      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig(income = "A", incomeDesc = "B", "C", "D", "E")
      val rm = new RowMapping(row, cc)

      val opOp = AccountMapper.map(row, cc)
      opOp === None
    }

    "map date and empty cells" in {
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set(StringCell(0, "01.07.13"), StringCell(1, ""))
          })
        })
      }

      val poiBook = wb.peer
      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig(income = "A", incomeDesc = "B", "C", "D", "E")
      val rm = new RowMapping(row, cc)

      val opOp = AccountMapper.map(row, cc)
      opOp === None
    }

    "map date and full cells" in {
      val wb = Workbook {
        Set(Sheet("name") {
          Set(Row(0) {
            Set(
              StyledCell(
                NumericCell(0, ScDateUtil.jodaToExcel(new DateTime(2013, 7, 1, 0, 0))),
                CellStyle(CellStyle.Font(), CellStyle.DataFormat("m/d/yy"))
              ),
              NumericCell(1, 100.0), StringCell(2, "income"),
              NumericCell(3, 200.0), StringCell(4, "expenditure"),
              StringCell(4, "00")
            )
          })
        })
      }

      val poiBook = wb.peer
      val row = poiBook.getSheetAt(0).getRow(0)

      val cell = row.getCell(0)


      cell.getCellType === org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC
      cell.getNumericCellValue === 41456
      val style = cell.getCellStyle
      style.getDataFormat === 14
      DateUtil.isCellDateFormatted(cell) === true
      cell.getDateCellValue.getTime === new DateTime(2013, 7, 1, 0, 0).toDate

      val cc = new ColumnsConfig(income = "B", incomeDesc = "C", expenditure = "D", expenditureDesc =  "E", mapping = "F", grantRow = "G")
      val rm = new RowMapping(row, cc)

      val opOp = AccountMapper.map(row, cc)
      opOp.isDefined === true
      val op = opOp.get

      //op.
      op.amount.toDouble === 200.0
      op.date === new DateTime(2013, 7, 1, 0, 0)
    }
  }
}
