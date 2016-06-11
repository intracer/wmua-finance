package org.intracer.finance

import com.norbitltd.spoiwo.model._
import com.norbitltd.spoiwo.natures.xlsx.Model2XlsxConversions._
import org.apache.poi.ss.usermodel.DateUtil
import org.joda.time.DateTime
import org.specs2.mutable.Specification


class AccountMapperSpec extends Specification {

  val projects = Seq(
    Row().withCellValues("проект", "назва проекту"),
    Row().withCellValues("00", "адміністрування"),
    Row().withCellValues("01", "ВЛЗ")
  )

  val grants = Seq(
    Row().withCellValues("джерело коштів"),
    Row().withCellValues("00", "власні кошти"),
    Row().withCellValues("01", "Grants:WM UA/Programs in Ukraine 2012")
  )

  val categories =
    Seq(
      Row().withCellValues("бюджетна категорія"),
      Row().withCellValues("00", "проїзд", "заходи"),
      Row().withCellValues("01", "проживання", "заходи"),
      Row().withCellValues("02", "транспорт", "відрядження"),
      Row().withCellValues("03", "проживання", "відрядження")
    )

  "readMapping" should {
    "read empty" in {
      val wb = Workbook(Sheet(Row()))

      val poiBook = wb.convertAsXlsx()

      val mapping = CodeMapping.readMapping(poiBook.getSheetAt(0))

      mapping.grant === Map.empty
      mapping.category === Map.empty
      mapping.project === Map.empty
    }

    "read projects" in {
      val wb = Workbook(Sheet(projects:_*))

      val poiBook = wb.convertAsXlsx()

      val mapping = CodeMapping.readMapping(poiBook.getSheetAt(0))

      mapping.project === Map("00" -> "адміністрування", "01" -> "ВЛЗ")
      mapping.grant === Map.empty
      mapping.category === Map.empty
    }

    "read projects and grants" in {
      val wb = Workbook(Sheet(projects ++ grants:_*))

      val poiBook = wb.convertAsXlsx()

      val mapping = CodeMapping.readMapping(poiBook.getSheetAt(0))

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
      val wb = Workbook(Sheet(projects ++ grants ++ categories:_*))

      val poiBook = wb.convertAsXlsx()

      val mapping = CodeMapping.readMapping(poiBook.getSheetAt(0))

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
      val wb = Workbook(Sheet(Row()))
      val poiBook = wb.convertAsXlsx()

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig("A", "B", "C", "D", "E")

      val opOp = AccountMapper.map(row, cc)
      opOp === None
    }

    "map text" in {
      val wb = Workbook(Sheet(Row().withCellValues("date", "income")))
      val poiBook = wb.convertAsXlsx

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig(income = "A", incomeDesc = "B", "C", "D", "E")
      val rm = new RowMapping(row, cc)

      val opOp = AccountMapper.map(row, cc)
      opOp === None
    }

    "map date and no cells" in {
      val wb = Workbook(Sheet(Row().withCellValues(new DateTime(2013, 7, 1, 0, 0))))
      val poiBook = wb.convertAsXlsx

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig(income = "A", incomeDesc = "B", "C", "D", "E")
      val rm = new RowMapping(row, cc)

      val opOp = AccountMapper.map(row, cc)
      opOp === None
    }

    "map date and empty cells" in {
      val wb = Workbook(Sheet(Row().withCellValues(
        new DateTime(2013, 7, 1, 0, 0), ""
      )))
      val poiBook = wb.convertAsXlsx

      val row = poiBook.getSheetAt(0).getRow(0)

      val cc = new ColumnsConfig(income = "A", incomeDesc = "B", "C", "D", "E")
      val rm = new RowMapping(row, cc)

      val opOp = AccountMapper.map(row, cc)
      opOp === None
    }

    "map date and full cells" in {
      val wb = Workbook(Sheet(Row().withCells(
        Cell(new DateTime(2013, 7, 1, 0, 0), style = CellStyle(dataFormat = CellDataFormat("dd\\.mm\\.yy;@"))),
        Cell(100.0),
        Cell("income"),
        Cell(200.0),
        Cell("expenditure")
      )))
      val poiBook = wb.convertAsXlsx

      val row = poiBook.getSheetAt(0).getRow(0)

      val cell = row.getCell(0)

      cell.getCellType === org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC
      cell.getNumericCellValue === 41456
//      val style = cell.getCellStyle
//      style.getDataFormat === 14
      DateUtil.isCellDateFormatted(cell) === true
      cell.getDateCellValue.getTime === new DateTime(2013, 7, 1, 0, 0).getMillis

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

  "zzz" should { "set cell date style with DateTime same" in {
      //    val row = Row().withCellValues(new DateTime(2013, 7, 1, 0, 0))
      //    val model = row.cells.head
      //    val xlsx = convert(model)

      val wb = Workbook(Sheet(Row().withCellValues(
        new DateTime(2013, 7, 1, 0, 0), 100.0, "income", 200.0, "expenditure"
      )))
      val poiBook = wb.convertAsXlsx

      val row = poiBook.getSheetAt(0).getRow(0)

      val xlsx = row.getCell(0)

      DateUtil.isCellDateFormatted(xlsx) === true
    }
  }
}
