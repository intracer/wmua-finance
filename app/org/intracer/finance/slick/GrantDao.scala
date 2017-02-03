package org.intracer.finance.slick

import org.intracer.finance.Grant

class GrantDao() extends BaseDao {

  import driver.api._

  val query = TableQuery[Grants]

  def insert(grant: Grant): Int = run {
    query += grant
  }

  def insertAll(grants: Seq[Grant]): Unit = run {
    query.forceInsertAll(grants)
  }

  def list: Seq[Grant] = run {
    query.sortBy(_.name).result
  }

  def get(name: String): Option[Grant] = run {
    query.filter(_.name === name).result.headOption
  }

}
