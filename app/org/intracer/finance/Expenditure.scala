package org.intracer.finance

import java.sql.Timestamp

import client.finance.GrantItem
import org.joda.time.DateTime

case class Expenditure(id: Option[Int] = None,
                       date: Timestamp,
                       amount: Option[BigDecimal],
                       account: Account,
                       category: CategoryF,
                       project: Project,
                       grant: Option[Grant],
                       grantItem: Option[GrantItem],
                       desc: String,
                       user: User,
                       logDate: Timestamp = new Timestamp(DateTime.now.getMillis)
                      ) extends OpPoint {
  override def name = desc

  override def toString: String =
    s"""project: $projectName, category: $categoryName, grant: $grantName, description: + $desc, """

  def projectName = project.name

  def categoryName = category.name

  def grantName = grant.map(_.name).getOrElse("")

}
