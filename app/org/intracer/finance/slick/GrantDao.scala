package org.intracer.finance.slick

import org.intracer.finance.Grant
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class GrantDao(val mwDb: FinDatabase, val query: TableQuery[Grants], val driver: JdbcProfile) extends BaseDao {

  import driver.api._

  def insert(grant: Grant): Int = db {
    query += grant
  }

  def insertAll(grants: Seq[Grant]): Unit = db {
    query.forceInsertAll(grants)
  }

  def list: Seq[Grant] = db {
    query.sortBy(_.name).result
  }

  def get(name: String): Option[Grant] = db {
    query.filter(_.name === name).result.headOption
  }

}
