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

        val names = (1 to 2).map("name" + _)
        val categories = names.map(n => CategoryF(name = n))

        val ids = categories.map(categoryDao.insert)

        val byName = names.flatMap(categoryDao.get)

        byName.map(_.name) === names
        byName.flatMap(_.id) === ids

        val list = categoryDao.list
        list.size === 2

        list === byName
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

        categoryDao.insertAll((1 to 2).map(i => CategoryF(name = "cat" + i)))
        val cats = categoryDao.list

        projectDao.insertAll((1 to 2).map(i => Project(name = "p" + i)))
        val projects = projectDao.list

        grantDao.insertAll((1 to 2).map(i => Grant(name = "Grant" + i)))
        val grants = grantDao.list

        val grant = grants.find(_.name == "Grant1").get
        val grantId = grant.id
        val grantItem1 = GrantItem(None, grantId, "1", "item1", BigDecimal.valueOf(100))
        val grantItem2 = GrantItem(None, grantId, "2", "item2", BigDecimal.valueOf(300))
        grantItemDao.insertAll(Seq(grantItem1, grantItem2))

        val grantItems = grantItemDao.list(grantId.get)

        accountDao.insertAll((1 to 2).map(i => Account(name = "Account" + i)))
        val accounts = accountDao.list

        val user = User(None, "user", "mail@dot.com")
        val userId = userDao.insert(user)

        val exp = Expenditure(
          Some(1),
          new Timestamp(0L),
          Some(BigDecimal("10.00")),
          accounts.find(_.name == "Account1").get,
          cats.find(_.name == "cat1").get,
          projects.find(_.name == "p1").get,
          Some(grant),
          Some(grantItems.head), "exp1",
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
