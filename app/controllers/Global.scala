package controllers

import play.api._


object Global extends GlobalSettings {

  var uahToUsd: Double = 22.0

  val fileDate = "13-NOV-2015"

  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

  def isNumber(s: String): Boolean = s.matches("[+-]?\\d+.?\\d+")
}

case class WMF(code: String, description: String, value: Double)