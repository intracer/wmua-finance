package org.intracer.finance

import controllers.Global
import org.apache.poi.ss.util.CellReference
import org.joda.time.DateTime

case class Expenditure(id: Option[Int] = None,
                       date: String,
                       amount: BigDecimal,
                       from: Account,
                       category: CategoryF,
                       project: Project,
                       grant: Option[Grant],
                       grantRow: Option[String],
                       desc: String,
                       ref: CellReference = null
                      ) extends OpPoint {
  override def name = desc

  override def toString: String = s"""project: $projectName, category: $categoryName, grant: $grantName, description: + $desc, """

  def projectName = Global.mapping.project(project.id.get)

  def categoryName = Global.mapping.category(category.id.get)

  def grantName = grant.map(grant => Global.mapping.grant(grant.id.get)).getOrElse("")

  def grantUrl = {
    if (grantName.startsWith("Grants:PEG/WM UA/")) {
      Some(("grant/" + grantName.replaceAll(" ", "_"), grantName.replace("Grants:PEG/WM UA/", "")))
    } else None
  }

}
