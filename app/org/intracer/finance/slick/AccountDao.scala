package org.intracer.finance.slick

import org.intracer.finance.Account
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class AccountDao(val mwDb: FinDatabase, val query: TableQuery[Accounts], val driver: JdbcProfile) {

  import driver.api._

  val db = mwDb.db

  def insert(account: Account): Long = {
    db.run(query += account).await
  }

  def insertAll(accounts: Seq[Account]): Unit = {
    db.run(query.forceInsertAll(accounts)).await
  }

  def list: Seq[Account] = db.run(query.sortBy(_.name).result).await

  def get(name: String): Option[Account] =
    db.run(query.filter(_.name === name).result.headOption).await

}
