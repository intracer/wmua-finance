package org.intracer.finance

import java.sql.Timestamp

import client.finance.GrantItem
import org.intracer.finance.slick.Expenditures

case class Expenditure(id: Option[Int] = None,
                       date: Timestamp,
                       amount: Option[BigDecimal],
                       from: Account,
                       category: CategoryF,
                       project: Project,
                       grant: Option[Grant],
                       grantItem: Option[GrantItem],
                       desc: String,
                       logDate: Timestamp,
                       user: User
                      ) extends OpPoint {
  override def name = desc

  override def toString: String = s"""project: $projectName, category: $categoryName, grant: $grantName, description: + $desc, """

  def projectName = Expenditures.projects(project.id.get).name

  def categoryName = Expenditures.categories(category.id.get).name

  def grantName = grant.map(grant => Expenditures.grants(grant.id.get).name).getOrElse("")

  def grantUrl = {
    if (grantName.startsWith("Grants:PEG/WM UA/")) {
      Some(("grant/" + grantName.replaceAll(" ", "_"), grantName.replace("Grants:PEG/WM UA/", "")))
    } else None
  }

}
