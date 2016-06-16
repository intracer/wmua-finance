package org.intracer.finance

import org.apache.poi.ss.usermodel.{Cell, Sheet}
import scala.collection.JavaConverters._

class CodeMapping {
  var project = Map.empty[Int, String]
  var category = Map.empty[Int, String]
  var grant = Map.empty[Int, String]
}


object CodeMapping {

  def readMapping(sheet: Sheet): CodeMapping = {
    val mapping = new CodeMapping()

    var map = Map.empty[Int, String]

    var maps = Seq.empty[Map[Int, String]]

    for (row <- sheet.asScala.tail) {
      val c1 = row.getCell(0)
      val c2 = row.getCell(1)

      if (c2 == null || c2.getCellType == Cell.CELL_TYPE_BLANK) {

        if (map.nonEmpty)
          maps = maps ++ Seq(map)
        println("New map, old = " + map)
        map = Map.empty[Int, String]
      }

      if (c2 != null && c2.getCellType != Cell.CELL_TYPE_BLANK) {
        val c3 = row.getCell(2)
        val tp = if (c3 != null && c3.getCellType != Cell.CELL_TYPE_BLANK) {
          c3.getStringCellValue + "/" + c2.getStringCellValue
        } else c2.getStringCellValue
        map += c1.getStringCellValue.toInt -> tp

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