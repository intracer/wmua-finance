package org.intracer.finance.slick

import org.intracer.finance.Account

class AccountDao extends BaseDao {

  import driver.api._

  val query = TableQuery[Accounts]

  def insert(account: Account): Int = run {
    query += account
  }

  def insertAll(accounts: Seq[Account]): Unit = run {
    query.forceInsertAll(accounts)
  }

  def list: Seq[Account] = run {
    query.sortBy(_.name).result
  }

  def get(name: String): Option[Account] = run {
    query.filter(_.name === name).result.headOption
  }
}
