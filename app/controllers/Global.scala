package controllers

object Global {

  var uahToUsd: Double = 22.0

  val fileDate = "13-NOV-2015"

  def isNumber(s: String): Boolean = s.matches("[+-]?\\d+.?\\d+")
}

case class WMF(code: String, description: String, value: Double)