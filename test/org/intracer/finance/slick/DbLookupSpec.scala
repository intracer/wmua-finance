package org.intracer.finance.slick

import java.sql.Timestamp

import client.finance.GrantItem
import org.intracer.finance._
import org.specs2.mutable.Specification

class DbLookupSpec extends Specification with InMemDb {

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
        val grantItemDao = new GrantItemDao
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
}
