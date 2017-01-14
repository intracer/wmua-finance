package org.intracer.finance.slick

import client.finance.GrantItem
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class GrantItemsDao(val mwDb: FinDatabase, val query: TableQuery[GrantItems], val driver: JdbcProfile) extends BaseDao {

  import driver.api._

  def insert(grantItem: GrantItem): Int = db {
    query += grantItem
  }

  def insertAll(grantItems: Seq[GrantItem]): Unit = db {
    query.forceInsertAll(grantItems)
  }

  def list(grantId: Int): Seq[GrantItem] = db {
    query.filter(_.grantId === grantId).sortBy(_.id).result
  }

  def listAll(): Seq[GrantItem] = db {
    query.sortBy(_.id).result
  }

  def get(id: Int): Option[GrantItem] = db {
    query.filter(_.id === id).result.headOption
  }

}
