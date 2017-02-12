package org.intracer.finance.slick

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.driver.JdbcProfile
import spray.util.pimpFuture

trait BaseDao extends HasDatabaseConfig[JdbcProfile] {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def run[R, E <: Effect](a: DBIOAction[R, NoStream, E]): R =
    db.run(a).await

}
