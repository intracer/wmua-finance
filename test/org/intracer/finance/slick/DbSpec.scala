package org.intracer.finance.slick

import java.sql.Timestamp

import org.intracer.finance._
import org.specs2.mutable.{BeforeAfter, Specification}
import _root_.slick.backend.DatabaseConfig
import _root_.slick.driver.JdbcProfile
import client.finance.GrantItem

class DbSpec extends Specification with BeforeAfter {

  sequential

  var db: FinDatabase = _

  def categoryDao = db.categoryDao

  def projectDao = db.projectDao

  def grantDao = db.grantDao

  def grantItemDao = db.grantItemDao

  def expDao = db.expDao

  def accountDao = db.accountDao

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
      val category = new CategoryF(name = "name")

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
      val project = Project(name = "name")

      val id: Long = projectDao.insert(project)

      val list = projectDao.list
      list.size === 1

      val fromDb = list.head

      fromDb.id.isDefined === true
      fromDb.copy(id = None) === project
      fromDb.id === Some(id)
    }
  }

  "grants" should {
    "insert" in {
      val grant = new Grant(name = "name")

      val id: Long = grantDao.insert(grant)

      val list = grantDao.list
      list.size === 1

      val fromDb = list.head

      fromDb.id.isDefined === true
      fromDb.copy(id = None) === grant
      fromDb.id === Some(id)
    }
  }

  "accounts" should {
    "insert" in {
      val account = new Account(name = "name")

      val id: Long = accountDao.insert(account)

      val list = accountDao.list
      list.size === 1

      val fromDb = list.head

      fromDb.id.isDefined === true
      fromDb.copy(id = None) === account
      fromDb.id === Some(id)
    }
  }

  "grantItems" should {
    "insert" in {

      val grant = new Grant(name = "name")
      val grantId = grantDao.insert(grant)

      val grantItem = new GrantItem(None, Some(grantId.toInt), "1", "item", BigDecimal.valueOf(100))

      val id: Long = grantItemDao.insert(grantItem)

      val list = accountDao.list
      list.size === 1

      val fromDb = list.head

      fromDb.id.isDefined === true
      fromDb.copy(id = None) === grantItem
      fromDb.id === Some(id)
    }
  }

  "expenditure" should {
    "insert" in {
      val category1 = new CategoryF(name = "cat1")
      val category2 = new CategoryF(name = "cat2")

      categoryDao.insertAll(Seq(category1, category2))

      val cats = categoryDao.list.groupBy(_.id.get).mapValues(_.head)
      cats.size === 2

      val project1 = new Project(name = "p1")
      val project2 = new Project(name = "p2")
      projectDao.insertAll(Seq(project1, project2))

      val projects = projectDao.list.groupBy(_.id.get).mapValues(_.head)
      projects.size === 2

      val grant1 = new Grant(name = "Grant1")
      val grant2 = new Grant(name = "Grant2")
      grantDao.insertAll(Seq(grant1, grant2))
      val grants = grantDao.list.groupBy(_.id.get).mapValues(_.head)

      val grant = grants.values.find(_.code == "01").get
      val grantId = grant.id
      val grantItem1 = new GrantItem(None, grantId, "1", "item1", BigDecimal.valueOf(100))
      val grantItem2 = new GrantItem(None, grantId, "2", "item2", BigDecimal.valueOf(300))
      grantItemDao.insertAll(Seq(grantItem1, grantItem2))

      val grantItems = grantItemDao.list(grantId.get).groupBy(_.id.get).mapValues(_.head)

      val account1 = new Account(name = "Account1")
      val account2 = new Account(name = "Account2")
      accountDao.insertAll(Seq(account1, account2))
      val accounts = accountDao.list.groupBy(_.id.get).mapValues(_.head)

//      Expenditures.accounts = accounts
//      Expenditures.grants = grants
//      Expenditures.categories = cats
//      Expenditures.projects = projects

      val exp = new Expenditure(
        None,
        new Timestamp(0L),
        Some(BigDecimal(10)),
        accounts.values.find(_.code == "code1").get,
        cats.values.find(_.code == "code1").get,
        projects.values.find(_.code == "code1").get,
        Some(grant),
        Some(grantItems.values.head), Some("exp1"),
        desc = "123"
      )

      expDao.insert(exp)

      val exps = expDao.list
      exps.size === 1
      exps.head.copy(id = None) === exp
    }
  }

}
