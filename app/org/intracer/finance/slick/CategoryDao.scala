package org.intracer.finance.slick

import org.intracer.finance.CategoryF

class CategoryDao extends BaseDao {

  import driver.api._

  val query = TableQuery[Categories]

  def insert(category: CategoryF): Int = run {
    query += category
  }

  def insertAll(categories: Seq[CategoryF]): Unit = run {
    query.forceInsertAll(categories)
  }

  def list: Seq[CategoryF] = run {
    query.sortBy(_.name).result
  }

  def get(name: String): Option[CategoryF] = run {
    query.filter(_.name === name).result.headOption
  }

}
