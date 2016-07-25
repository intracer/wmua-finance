package org.intracer.finance

import java.text.NumberFormat
import java.util.Locale

import com.github.nscala_time.time.Imports._

trait HasName {
  def name: String

  def id: Option[Int]

  def code = id.map(_.toString).getOrElse("")

  override def toString: String = name
}


case class CategoryF(id: Option[Int] = None, name: String = "") extends HasName

case class Grant(id: Option[Int] = None, name: String = "", url: Option[String] = None) extends HasName {
  def toUrl = if (name.startsWith("Grants:")) {
    Some("https://meta.wikimedia.org/wiki/" + name)
  } else {
    None
  }
}

case class Project(id: Option[Int] = None, name: String = "") extends HasName

trait OpPoint extends HasName

case class Account(id: Option[Int] = None, name: String) extends OpPoint

//class Target(val category: Option[CategoryF], grant: Option[Grant], project: Option[Project], detail: String) extends OpPoint {
//  def name = detail
//}

class Operation(
                 val from: OpPoint,
                 val to: Expenditure,
                 val amount: Option[BigDecimal],
                 val date: DateTime) {
  override def toString: String = s"${date.toString().substring(0, 10)}: $from -> $to, amount: $amount"

  def amountString = amount.map(_.toDouble).map(Formatter.fmt.format).getOrElse("")
}

object Formatter {
  val fmt = {
    val nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-US")) // uk-UA
    nf.setMaximumFractionDigits(2)
    nf.setGroupingUsed(false)
    nf
  }
}


