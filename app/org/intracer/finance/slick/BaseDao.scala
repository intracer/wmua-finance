package org.intracer.finance.slick

import slick.dbio.{DBIOAction, Effect, NoStream}
import spray.util.pimpFuture

trait BaseDao {

  def mwDb: FinDatabase

  def db[R, E <: Effect](a: DBIOAction[R, NoStream, E]): R =
    mwDb.db.run(a).await

}
