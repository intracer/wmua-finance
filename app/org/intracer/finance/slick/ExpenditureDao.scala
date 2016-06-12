package org.intracer.finance.slick

import org.intracer.finance.Expenditure
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture



class ExpenditureDao(val mwDb: FinDatabase, val query: TableQuery[Expenditures], val driver: JdbcProfile) {

  import driver.api._
  val db = mwDb.db

  def insert(exp: Expenditure): Int = {
    db.run(query += exp).await
  }

  def insertAll(exps: Seq[Expenditure]): Unit = {
    db.run(query.forceInsertAll(exps)).await
  }

  def list: Seq[Expenditure] = db.run(query.sortBy(_.date).result).await


}
