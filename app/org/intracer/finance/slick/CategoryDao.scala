package org.intracer.finance.slick

import org.intracer.finance.CategoryF
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class CategoryDao(val mwDb: FinDatabase, val query: TableQuery[Categories], val driver: JdbcProfile) {

  import driver.api._

  val db = mwDb.db

  def insert(category: CategoryF): Long = {
    db.run(query += category).await
  }

  def insertAll(categories: Seq[CategoryF]): Unit = {
    db.run(query.forceInsertAll(categories)).await
  }

  def list: Seq[CategoryF] = db.run(query.sortBy(_.name).result).await

  def get(name: String): Option[CategoryF] =
    db.run(query.filter(_.name === name).result.headOption).await

}
