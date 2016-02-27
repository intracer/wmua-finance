package org.intracer.finance

import java.text.NumberFormat
import java.util.Locale

import com.github.nscala_time.time.Imports._

trait HasName {
  def name: String

  override def toString: String = name
}

class Account(val name: String) extends OpPoint

case class CategoryF(id: Option[Long] = None, code: String, name: String = "") extends HasName

case class Grant(id: Option[Long] = None, code: String, name: String = "", url: String = "") extends HasName

case class Project(id: Option[Long] = None, code: String, name: String = "") extends HasName

trait OpPoint extends HasName

//class Target(val category: Option[CategoryF], grant: Option[Grant], project: Option[Project], detail: String) extends OpPoint {
//  def name = detail
//}

class Operation(
                 val from: OpPoint,
                 val to: Expenditure,
                 val amount: BigDecimal,
                 val date: DateTime) {
  override def toString: String = s"${date.toString().substring(0, 10)}: $from -> $to, amount: $amount"

  def amountString = Formatter.fmt.format(amount.toDouble)
}

object Formatter {
  val fmt = {
    val nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-US")) // uk-UA
    nf.setMaximumFractionDigits(2)
    nf.setGroupingUsed(false)
    nf
  }
}


object CacheAccount extends Account("Cache")