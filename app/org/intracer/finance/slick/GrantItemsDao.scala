package org.intracer.finance.slick

import client.finance.GrantItem
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class GrantItemsDao(val mwDb: FinDatabase, val query: TableQuery[GrantItems], val driver: JdbcProfile) {

  import driver.api._

  val db = mwDb.db

  def insert(grantItem: GrantItem): Long = {
    db.run(query += grantItem).await
  }

  def insertAll(grantItems: Seq[GrantItem]): Unit = {
    db.run(query.forceInsertAll(grantItems)).await
  }

  def list(grantId: Int): Seq[GrantItem] = db.run(query.filter(_.grantId === grantId).sortBy(_.id).result).await

  def listAll(): Seq[GrantItem] = db.run(query.sortBy(_.id).result).await

  def get(id: Int): Option[GrantItem] =
    db.run(query.filter(_.id === id).result.headOption).await

}
