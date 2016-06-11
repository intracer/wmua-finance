package org.intracer.finance.slick

import org.intracer.finance.Grant
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class GrantDao(val mwDb: FinDatabase, val query: TableQuery[Grants], val driver: JdbcProfile) {

  import driver.api._

  val db = mwDb.db

  def insert(grant: Grant): Long = {
    db.run(query += grant).await
  }

  def insertAll(grants: Seq[Grant]): Unit = {
    db.run(query.forceInsertAll(grants)).await
  }

  def list: Seq[Grant] = db.run(query.sortBy(_.name).result).await

  def get(name: String): Option[Grant] =
    db.run(query.filter(_.name === name).result.headOption).await

}
