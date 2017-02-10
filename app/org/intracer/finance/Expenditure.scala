package org.intracer.finance

import java.sql.Timestamp

import client.finance.GrantItem
import org.intracer.finance.slick.Expenditures
import org.joda.time.DateTime

case class Expenditure(id: Option[Int] = None,
                       date: Timestamp,
                       amount: Option[BigDecimal],
                       account: Account,
                       category: CategoryF,
                       project: Project,
                       grant: Option[Grant],
                       grantItem: Option[GrantItem],
                       description: String,
                       user: User,
                       logDate: Timestamp = new Timestamp(DateTime.now.getMillis)
                      ) extends OpPoint {
  override def name = description

  override def toString: String =
    s"""project: $projectName, category: $categoryName, grant: $grantName, description: + $description, """

  def projectName = Expenditures.projects(project.id.get).name

  def categoryName = Expenditures.categories(category.id.get).name

  def grantName = grant.map { grant =>
    Expenditures.grants(grant.id.get).name
  }.getOrElse("")

}
