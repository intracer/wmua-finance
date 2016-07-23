package org.intracer.finance.slick

import org.intracer.finance.Expenditure
import org.joda.time.DateTime
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

//  def plus(n: Int) = {
//    val exps = (1 to n).map { _ =>
//      new Expenditure(
//        None,
//        new DateTime(2016, 1, 1, 1, 1),
//        BigDecimal(0),
//        Expenditures.accounts(1),
//        Expenditures.projects(1),
//        Expenditures.categories(1),
//        Expenditures.grants(1),
//        17,
//        None)
//
//      insertAll(exps)
//    }
//
//  }

}
