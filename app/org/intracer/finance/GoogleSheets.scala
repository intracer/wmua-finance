package org.intracer.finance

import com.google.gdata.client.spreadsheet.SpreadsheetService
import com.google.gdata.data.spreadsheet.SpreadsheetFeed
import java.net.URL


object GoogleSheets {


  def main(args: Array[String]) {
    val service = new SpreadsheetService("org.intracer.finance")

    val SPREADSHEET_FEED_URL = new URL("https://docs.google.com/spreadsheets/d/1OSiu31XSl8C4w80bmpVpedvk4LFlouIHHof2onT2gR8/edit?usp=sharing")
    val feed = service.getFeed(SPREADSHEET_FEED_URL, classOf[SpreadsheetFeed])

    val entries = feed.getEntries
  }

}
