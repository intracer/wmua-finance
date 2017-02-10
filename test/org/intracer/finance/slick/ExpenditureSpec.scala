package org.intracer.finance.slick

import java.sql.Timestamp

import client.finance.GrantItem
import controllers.Update
import org.intracer.finance._
import org.specs2.mutable.Specification

class ExpenditureSpec extends Specification with InMemDb {

  sequential

  var categoryDao: CategoryDao = _
  var projectDao: ProjectDao = _
  var grantDao: GrantDao = _
  var grantItemDao: GrantItemsDao = _
  var expDao: ExpenditureDao = _
  var accountDao: AccountDao = _
  var userDao: UserDao = _

  override def inMemDbApp[T](block: => T): T = {
    super.inMemDbApp {
      categoryDao = new CategoryDao
      projectDao = new ProjectDao
      grantDao = new GrantDao
      grantItemDao = new GrantItemsDao
      expDao = new ExpenditureDao
      accountDao = new AccountDao
      userDao = new UserDao

      categoryDao.insertAll((1 to 2).map(i => CategoryF(name = "Category" + i)))
      projectDao.insertAll((1 to 2).map(i => Project(name = "Project" + i)))
      grantDao.insertAll((1 to 2).map(i => Grant(name = "Grant" + i)))

      val grantId = grantDao.list.find(_.name == "Grant1").flatMap(_.id)
      grantItemDao.insertAll((1 to 2).map { i =>
        GrantItem(None, grantId, i.toString, "GrantItem" + i, BigDecimal.valueOf(i * 100))
      })

      accountDao.insertAll((1 to 2).map(i => Account(name = "Account" + i)))

      userDao.insert(User(None, "user", "mail@dot.com"))

      block
    }
  }

  def user = userDao.list.head

  def newExp(amount: Int, grantName: String, account: String, category: String,
             project: String, grantItem: Option[String], description: String) = {
    val grant = grantDao.list.find(_.name == grantName)

    Expenditure(
      None,
      new Timestamp(0L),
      Some(BigDecimal(amount + ".00")),
      accountDao.list.find(_.name == account).get,
      categoryDao.list.find(_.name == category).get,
      projectDao.list.find(_.name == project).get,
      grant,
      grantItem.flatMap { description =>
        grantItemDao.list(grant.flatMap(_.id).get).find(_.description == description)
      },
      description,
      userDao.list.head
    )
  }

  "expenditure" should {
    "insert" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", Some("GrantItem1"), "exp1")

        val id = expDao.insert(exp)

        val exps = expDao.list
        exps.size === 1
        exps.head === exp.copy(id = Some(id))
      }
    }

    "update amount in" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", Some("GrantItem1"), "exp1")
        val id = expDao.insert(exp)

        expDao.update(Update("amount", id, "20"), user)

        val exps = expDao.list
        exps.size === 1
        exps.head === exp.copy(id = Some(id), amount = Some(BigDecimal("20.00")))
      }
    }

    "update grant in" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", None, "exp1")
        val id = expDao.insert(exp)

        val grant2 = grantDao.list.find(_.name == "Grant2")
        val grant2Id = grant2.flatMap(_.id)
        expDao.update(Update("grant", id, grant2Id.get.toString), user)

        val exps = expDao.list
        exps.size === 1
        exps.head === exp.copy(id = Some(id), grant = grant2)
      }
    }

    "update account in" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", None, "exp1")
        val id = expDao.insert(exp)

        val account2 = accountDao.list.find(_.name == "Account2").get
        val account2Id = account2.id
        expDao.update(Update("account", id, account2Id.get.toString), user)

        val exps = expDao.list
        exps.size === 1
        exps.head === exp.copy(id = Some(id), account = account2)
      }
    }

  }
}
