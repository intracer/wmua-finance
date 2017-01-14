package org.intracer.finance.slick

import org.intracer.finance.Project
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import spray.util.pimpFuture

class ProjectDao(val mwDb: FinDatabase, val query: TableQuery[Projects], val driver: JdbcProfile) extends BaseDao {

  import driver.api._

  def insert(project: Project): Int = db {
    query += project
  }

  def insertAll(projects: Seq[Project]): Unit = db {
    query.forceInsertAll(projects)
  }

  def list: Seq[Project] = db {
    query.sortBy(_.name).result
  }

  def get(name: String): Option[Project] = db {
    query.filter(_.name === name).result.headOption
  }
}
