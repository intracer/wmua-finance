package org.intracer.finance.slick

import org.intracer.finance.Project
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class ProjectDao(val mwDb: FinDatabase, val query: TableQuery[Projects], val driver: JdbcProfile) {

  import driver.api._

  val db = mwDb.db

  def insert(project: Project): Long = {
    db.run(query += project).await
  }

  def insertAll(projects: Seq[Project]): Unit = {
    db.run(query.forceInsertAll(projects)).await
  }

  def list: Seq[Project] = db.run(query.sortBy(_.name).result).await

  def get(name: String): Option[Project] =
    db.run(query.filter(_.name === name).result.headOption).await

}
