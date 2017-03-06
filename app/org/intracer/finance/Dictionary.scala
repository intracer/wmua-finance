package org.intracer.finance

import client.finance.GrantItem
import org.intracer.finance.Dictionary._

import scala.collection.SortedSet


case class Dictionary(accountMap: Map[Int, Account] = Map.empty,
                      categoryMap: Map[Int, CategoryF] = Map.empty,
                      grantMap: Map[Int, Grant] = Map.empty,
                      grantItemMap: Map[Int, Seq[GrantItem]] = Map.empty,
                      projectMap: Map[Int, Project] = Map.empty,
                      userMap: Map[Int, User] = Map.empty) {

  val grantItemsById: Map[Int, GrantItem] = grantItemMap.values.flatten.groupBy(_.id.get).mapValues(_.head)

  def account(id: Int) = accountMap(id)

  def category(id: Int) = categoryMap(id)

  def grant(id: Int) = grantMap(id)

  def grantItem(id: Int) = grantItemsById(id)

  def project(id: Int) = projectMap(id)

  def user(id: Int) = userMap(id)

  def accountsJson: String = hasNameMapToJson(accountMap)

  def grantsJson: String = hasNameMapToJson(grantMap)

  def projectsJson: String = hasNameMapToJson(projectMap)

  def categoriesJson: String = {
    val elems = categoryMap.values.flatMap(_.name.split("/").headOption).toSeq
    val parents = SortedSet(elems: _*)

    val map = parents.map { parent =>
      parent ->
        categoryMap.values.toSeq
          .filter(_.name.toLowerCase.startsWith(parent.toLowerCase))
          .sortBy(_.name.toLowerCase)
    }.toMap
    hasNameWithParentsToJson(map)
  }

  def grantItemsJsonMap: Map[Int, String] = {
    grantItemMap.collect {
      case (grantId, items) if items.nonEmpty =>
        grantId -> grantItemsJson(grantId)
    }
  }

  def grantItemsJson(grantId: Int): String = {
    val parents = Seq("Program 1: Outreach",
      "Program 2: Contests",
      "Program 3: Community Support",
      "Administrative costs")

    val map = parents.zipWithIndex.map { case (parent, index) =>
      parent ->
        grantItemMap
          .getOrElse(grantId, Seq.empty)
          .filter(_.number.startsWith((index + 1).toString))
    }.toMap
    hasNameWithParentsToJson(map)
  }
}

object Dictionary {

  def hasNameMapToJson(map: Map[Int, HasName]): String = hasNameSeqToJson(map.values)

  def hasNameSeqToJson(hasNames: Iterable[HasName]): String = {
    hasNames.toSeq.sortBy(_.name.toLowerCase).map { hasName =>
      s"""{ "value": ${hasName.id.get}, "text": "${hasName.name}"}"""
    }.mkString("[", ", ", "]")
  }

  def hasNameWithParentsToJson(map: Map[String, Seq[HasName]]) = {
    map.map { case (parent, children) =>
      s"""{"text": "$parent", "children": """ + hasNameSeqToJson(children) + "}"
    }.mkString(", ")
  }

}