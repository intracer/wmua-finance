package org.intracer.finance.slick

import java.sql.Timestamp

import client.finance.GrantItem
import org.intracer.finance._
import org.specs2.mutable.Specification

class DbSpec extends Specification with InMemDb {

  sequential

  "category" should {
    "insert" in {
      inMemDbApp {
        val categoryDao = new CategoryDao
        val category = CategoryF(name = "name")

        categoryDao.insert(category)

        val list = categoryDao.list
        list.size === 1

        val fromDb = list.head

        fromDb.id.isDefined === true
        fromDb.copy(id = None) === category
      }
    }
  }

  "project" should {
    "insert" in {
      inMemDbApp {
        val projectDao = new ProjectDao

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
  }

  "grants" should {
    "insert" in {
      inMemDbApp {
        val grantDao = new GrantDao

        val grant = Grant(name = "name")

        val id: Long = grantDao.insert(grant)

        val list = grantDao.list
        list.size === 1

        val fromDb = list.head

        fromDb.id.isDefined === true
        fromDb.copy(id = None) === grant
        fromDb.id === Some(id)
      }
    }
  }

  "accounts" should {
    "insert" in {
      inMemDbApp {
        val accountDao = new AccountDao

        val account = Account(name = "name")

        val id: Long = accountDao.insert(account)

        val list = accountDao.list
        list.size === 1

        val fromDb = list.head

        fromDb.id.isDefined === true
        fromDb.copy(id = None) === account
        fromDb.id === Some(id)
      }
    }
  }

  "grantItems" should {
    "insertGI" in {
      inMemDbApp {
        val grantItemDao = new GrantItemsDao
        val grantDao = new GrantDao

        val grant = Grant(name = "name")
        val grantId = grantDao.insert(grant)

        val grantItem = GrantItem(None, Some(grantId.toInt), "1", "item", BigDecimal.valueOf(100))

        val id: Long = grantItemDao.insert(grantItem)

        val list = grantItemDao.list(grantId)
        list.size === 1

        val fromDb = list.head

        fromDb.id.isDefined === true
        fromDb.copy(id = None) === grantItem
        fromDb.id === Some(id)
      }
    }
  }

  "expenditure" should {
    "insert" in {
      inMemDbApp {
        val categoryDao = new CategoryDao
        val projectDao = new ProjectDao
        val grantDao = new GrantDao
        val grantItemDao = new GrantItemsDao
        val expDao = new ExpenditureDao
        val accountDao = new AccountDao
        val userDao = new UserDao

        val category1 = CategoryF(name = "cat1")
        val category2 = CategoryF(name = "cat2")

        categoryDao.insertAll(Seq(category1, category2))

        val cats = categoryDao.list.groupBy(_.id.get).mapValues(_.head)
        cats.size === 2

        val project1 = Project(name = "p1")
        val project2 = Project(name = "p2")
        projectDao.insertAll(Seq(project1, project2))

        val projects = projectDao.list.groupBy(_.id.get).mapValues(_.head)
        projects.size === 2

        val grant1 = Grant(name = "Grant1")
        val grant2 = Grant(name = "Grant2")
        grantDao.insertAll(Seq(grant1, grant2))
        val grants = grantDao.list.groupBy(_.id.get).mapValues(_.head)

        val grant = grants.values.find(_.name == "Grant1").get
        val grantId = grant.id
        val grantItem1 = GrantItem(None, grantId, "1", "item1", BigDecimal.valueOf(100))
        val grantItem2 = GrantItem(None, grantId, "2", "item2", BigDecimal.valueOf(300))
        grantItemDao.insertAll(Seq(grantItem1, grantItem2))

        val grantItems = grantItemDao.list(grantId.get).groupBy(_.id.get).mapValues(_.head)

        val account1 = Account(name = "Account1")
        val account2 = Account(name = "Account2")
        accountDao.insertAll(Seq(account1, account2))
        val accounts = accountDao.list.groupBy(_.id.get).mapValues(_.head)

        //      Expenditures.accounts = accounts
        //      Expenditures.grants = grants
        //      Expenditures.categories = cats
        //      Expenditures.projects = projects

        val user = User(None, "user", "mail@dot.com")
        val userId = userDao.insert(user)

        val exp = Expenditure(
          Some(1),
          new Timestamp(0L),
          Some(BigDecimal("10.00")),
          accounts.values.find(_.name == "Account1").get,
          cats.values.find(_.name == "cat1").get,
          projects.values.find(_.name == "p1").get,
          Some(grant),
          Some(grantItems.values.head), "exp1",
          user.copy(id = Some(userId))
        )

        expDao.insert(exp)

        val exps = expDao.list
        exps.size === 1
        exps.head === exp
      }
    }
  }

}
