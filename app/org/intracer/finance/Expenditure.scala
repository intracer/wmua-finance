package org.intracer.finance

import java.sql.Timestamp

import client.finance.GrantItem
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Expenditure(id: Option[Int] = None,
                       opId: Option[Int],
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

  def projectName = project.name

  def categoryName = category.name

  def grantName = grant.fold("")(_.name).split("/").last
}

class ExpenditureJson(dictionary: Dictionary) {

  def dtToTs(dt: DateTime): Timestamp = new Timestamp(dt.getMillis)

  implicit val ExpenditureFromJson: Reads[Expenditure] = (
    (__ \ "id").readNullable[Int] ~
      (__ \ "op_id").readNullable[Int] ~
      (__ \ "op_date").read[DateTime].map(dtToTs) ~
      (__ \ "amount").readNullable[BigDecimal] ~
      (__ \ "account_id").read[Int].map(dictionary.account) ~
      (__ \ "category_id").read[Int].map(dictionary.category) ~
      (__ \ "project_id").read[Int].map(dictionary.project) ~
      (__ \ "grant_id").readNullable[Int].map(_.map(dictionary.grant)) ~
      (__ \ "grant_item_id").readNullable[Int].map(_.map(dictionary.grantItem)) ~
      (__ \ "description").read[String] ~
      (__ \ "user_id").read[Int].map(dictionary.user) ~
      (__ \ "log_data").read[DateTime].map(dtToTs)
    ) (Expenditure.apply _)
}

object ExpenditureJson {

  def tsToDt(ts: Timestamp) = new DateTime(ts.getTime)

  implicit val ExpenditureToJson: Writes[Expenditure] = (
    (__ \ "id").writeNullable[Int] ~
      (__ \ "op_id").writeNullable[Int] ~
      (__ \ "op_date").write[DateTime] ~
      (__ \ "amount").writeNullable[BigDecimal] ~
      (__ \ "account_id").write[Int] ~
      (__ \ "account_name").write[String] ~
      (__ \ "category_id").write[Int] ~
      (__ \ "category_name").write[String] ~
      (__ \ "project_id").write[Int] ~
      (__ \ "project_name").write[String] ~
      (__ \ "grant_id").writeNullable[Int] ~
      (__ \ "grant_name").writeNullable[String] ~
      (__ \ "grant_item_id").writeNullable[Int] ~
      (__ \ "grant_item_name").writeNullable[String] ~
      (__ \ "description").write[String] ~
      (__ \ "user_id").write[Int] ~
      (__ \ "user_name").write[String] ~
      (__ \ "log_data").write[DateTime]
    ) ((e: Expenditure) => (
    e.id,
    e.opId,
    tsToDt(e.date),
    e.amount,
    e.account.id.get,
    e.account.name,
    e.category.id.get,
    e.category.name,
    e.project.id.get,
    e.project.name,
    e.grant.flatMap(_.id),
    e.grant.map(_.name),
    e.grantItem.flatMap(_.id),
    e.grantItem.map(_.name),
    e.description,
    e.user.id.get,
    e.user.fullname,
    tsToDt(e.logDate)
  ))
}