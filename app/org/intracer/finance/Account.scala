package org.intracer.finance

import com.github.nscala_time.time.Imports._
import org.apache.poi.ss.util.CellReference
import controllers.Global

trait HasName {
  def name: String

  override def toString: String = name
}

class Account(val name: String) extends OpPoint

class CategoryF(val name:String) extends HasName

class Grant(val name:String) extends HasName

class Project(val name:String) extends HasName

trait OpPoint extends HasName

//class Target(val category: Option[CategoryF], grant: Option[Grant], project: Option[Project], detail: String) extends OpPoint {
//  def name = detail
//}

class Operation(val from: OpPoint, val to: Expenditure, val amount: BigDecimal, val date: DateTime) {
  override def toString: String = s"${date.toString().substring(0,10)}: $from -> $to, amount: $amount"
}

class Expenditure(val categoryCode: CategoryF, val projectCode: Project, val grantCode: Option[Grant], val desc: String, val ref: CellReference) extends OpPoint {
  override def name = desc

  override def toString: String = s"""project: $projectCode, category: $categoryCode, grant: ${grantCode.getOrElse("")}, description: + $desc, """

  def project = Global.mapping.project(projectCode.name)
  def category = Global.mapping.category(categoryCode.name)
  def grant = grantCode.map(grant => Global.mapping.grant(grant.name)).getOrElse("")

}

class OpLog {
  val operations =  Seq[Operation]()
}

object WMUA_12_13 extends Grant("WMUA_12_13")
object WLEGrant extends Grant("WLE")

object CacheAccount extends Account("Cache")
object UahAccount extends Account("Hryvnya")


object Transport extends CategoryF("Transport")
object SPD extends CategoryF("SPD")
object NoCategory extends CategoryF("No category")

object WLM extends Project("WLM")
object WLE extends Project("WLE")
object NoProject extends Project("No project")


