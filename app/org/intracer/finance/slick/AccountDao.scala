package org.intracer.finance.slick

import org.intracer.finance.Account
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class AccountDao(val mwDb: FinDatabase, val query: TableQuery[Accounts], val driver: JdbcProfile) extends BaseDao {

  import driver.api._

  def insert(account: Account): Int = db {
    query += account
  }

  def insertAll(accounts: Seq[Account]): Unit = db {
    query.forceInsertAll(accounts)
  }

  def list: Seq[Account] = db {
    query.sortBy(_.name).result
  }

  def get(name: String): Option[Account] = db {
    query.filter(_.name === name).result.headOption
  }
}
