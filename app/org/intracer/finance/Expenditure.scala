package org.intracer.finance

import controllers.Global
import org.apache.poi.ss.util.CellReference

class Expenditure(
                   val category: CategoryF,
                   val project: Project,
                   val grant: Option[Grant],
                   val grantRow: Option[String],
                   val desc: String,
                   val ref: CellReference
                 ) extends OpPoint {
  override def name = desc

  override def toString: String = s"""project: $projectName, category: $categoryName, grant: $grantName, description: + $desc, """

  def projectName = Global.mapping.project(project.code)

  def categoryName = Global.mapping.category(category.code)

  def grantName = grant.map(grant => Global.mapping.grant(grant.code)).getOrElse("")

  def grantUrl = {
    if (grantName.startsWith("Grants:PEG/WM UA/")) {
      Some(("grant/" + grantName.replaceAll(" ", "_"), grantName.replace("Grants:PEG/WM UA/", "")))
    } else None
  }

}
