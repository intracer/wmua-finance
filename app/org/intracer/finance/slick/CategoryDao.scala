package org.intracer.finance.slick

import org.intracer.finance.CategoryF
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class CategoryDao(val mwDb: FinDatabase, val query: TableQuery[Categories], val driver: JdbcProfile) extends BaseDao {

  import driver.api._

  def insert(category: CategoryF): Int = db {
    query += category
  }

  def insertAll(categories: Seq[CategoryF]): Unit = db {
    query.forceInsertAll(categories)
  }

  def list: Seq[CategoryF] = db {
    query.sortBy(_.name).result
  }

  def get(name: String): Option[CategoryF] = db {
    query.filter(_.name === name).result.headOption
  }

}
