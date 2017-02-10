package org.intracer.finance.slick

import play.api.db.slick.{HasDatabaseConfig, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.profile.RelationalProfile
import spray.util.pimpFuture

trait BaseDao[T] extends HasDatabaseConfig[JdbcProfile] {

  import driver.api._

  def query: TableQuery[_ <: RelationalProfile#Table[T] with IdTable]

  def run[R, E <: Effect](a: DBIOAction[R, NoStream, E]): R =
    db.run(a).await

  def insert(account: T): Int = run {
    query returning query.map(_.id) += account
  }

  def insertAll(accounts: Seq[T]): Unit =
    query.forceInsertAll(accounts)


}
