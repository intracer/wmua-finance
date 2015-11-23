package org.intracer.finance

import org.apache.poi.ss.usermodel.DateUtil
import org.joda.time.DateTime

object ScDateUtil {

  def jodaToExcel(joda: DateTime): Double =
    DateUtil.getExcelDate(joda.toDate)

}
