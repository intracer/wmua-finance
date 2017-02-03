package org.intracer.finance.slick

import client.finance.GrantItem

class GrantItemsDao extends BaseDao {

  import driver.api._

  val query = TableQuery[GrantItems]

  def insert(grantItem: GrantItem): Int = run {
    query returning query.map(_.id) += grantItem
  }

  def insertAll(grantItems: Seq[GrantItem]): Unit = run {
    query.forceInsertAll(grantItems)
  }

  def list(grantId: Int): Seq[GrantItem] = run {
    query.filter(_.grantId === grantId).sortBy(_.id).result
  }

  def listAll(): Seq[GrantItem] = run {
    query.sortBy(_.id).result
  }

  def get(id: Int): Option[GrantItem] = run {
    query.filter(_.id === id).result.headOption
  }

}
