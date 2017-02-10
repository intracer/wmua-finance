package org.intracer.finance.slick

import org.intracer.finance.Project

class ProjectDao extends BaseDao {

  import driver.api._

  val query = TableQuery[Projects]

  def insert(project: Project): Int = run {
    query returning query.map(_.id) += project
  }

  def insertAll(projects: Seq[Project]): Unit = run {
    query.forceInsertAll(projects)
  }

  def list: Seq[Project] = run {
    query.sortBy(_.name).result
  }

  def get(name: String): Option[Project] = run {
    query.filter(_.name === name).result.headOption
  }
}
