package org.intracer.finance

import org.apache.poi.ss.util.CellReference

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
