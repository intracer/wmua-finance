package org.intracer.finance.slick

import org.intracer.finance.{CategoryF, Grant, Project}
import org.specs2.mutable.{BeforeAfter, Specification}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

class DbSpec extends Specification with BeforeAfter {

  sequential

  var db: FinDatabase = _

  def categoryDao = db.categoryDao
  def projectDao = db.projectDao
  def grantDao = db.grantDao

  def createSchema() = {
    db.dropTables()
    db.createTables()
  }

  override def before = {
    val dc = DatabaseConfig.forConfig[JdbcProfile]("h2mem")
    db = new FinDatabase(dc.db, dc.driver)
    createSchema()
  }

  override def after = {}

  "category" should {
    "insert" in {
      val category = new CategoryF(code = "code", name = "name")

      categoryDao.insert(category)

      val list = categoryDao.list
      list.size === 1

      val fromDb = list.head

      fromDb.id.isDefined === true
      fromDb.copy(id = None) === category
    }
  }

  "project" should {
    "insert" in {
      val project = new Project(code = "code", name = "name")

      projectDao.insert(project)

      val list = projectDao.list
      list.size === 1

      val fromDb = list.head

      fromDb.id.isDefined === true
      fromDb.copy(id = None) === project
    }
  }

  "grants" should {
    "insert" in {
      val grant = new Grant(code = "code", name = "name")

      grantDao.insert(grant)

      val list = grantDao.list
      list.size === 1

      val fromDb = list.head

      fromDb.id.isDefined === true
      fromDb.copy(id = None) === grant
    }
  }
}
