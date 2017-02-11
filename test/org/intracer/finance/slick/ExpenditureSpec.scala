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

      val grantId = grantDao.get("Grant1").flatMap(_.id)
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
             project: String, grantItem: Option[String], description: String): Expenditure = {
    val grant = grantDao.get(grantName)

    Expenditure(
      None,
      None,
      new Timestamp(0L),
      Some(BigDecimal(amount + ".00")),
      accountDao.get(account).get,
      categoryDao.get(category).get,
      projectDao.get(project).get,
      grant,
      grantItem.flatMap { description =>
        grantItemDao.list(grant.flatMap(_.id).get).find(_.description == description)
      },
      description,
      userDao.list.head
    )
  }

  "expenditure" should {
    "insert one" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", Some("GrantItem1"), "exp1")

        val id = expDao.insertWithOpId(exp)
        val withId = exp.copy(id = Some(id))

        val exps = expDao.log
        exps.size === 1
        val fromDb = exps.head
        fromDb.opId.isDefined === true
        fromDb.copy(opId = None) === withId

        expDao.revisions(fromDb.opId.get) === exps
      }
    }

    "insert two" in {
      inMemDbApp {
        val newExps = (1 to 2).map { i =>
          newExp(i * 10, "Grant" + i, "Account" + i, "Category" + i, "Project" + i, Some("GrantItem" + i), "exp" + i)
        }

        val ids = newExps.map(expDao.insertWithOpId)

        val exps = expDao.log
        exps.size === 2
//        exps.head === exp.copy(id = Some(id))
      }
    }

    "update amount in" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", Some("GrantItem1"), "exp1")
        val id = expDao.insertWithOpId(exp)

        expDao.update(Update("amount", id, "20"), user)

        val exps = expDao.log
        exps.size === 2
        val (v1, v2) = (exps.head, exps.last)

        v1.amount === Some(BigDecimal("20.00"))
        v2.amount === Some(BigDecimal("10.00"))
        v2.copy(id = None, opId = None) === exp

        v1.opId.isDefined === true
        v1.copy(id = None, amount = None) === v2.copy(id = None, amount = None)
      }
    }

    "update grant in" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", None, "exp1")
        val id = expDao.insertWithOpId(exp)

        val grant2 = grantDao.get("Grant2")
        val grant2Id = grant2.flatMap(_.id)
        expDao.update(Update("grant", id, grant2Id.get.toString), user)

        val exps = expDao.log
        exps.size === 2
        val (v1, v2) = (exps.head, exps.last)

        v1.grant.map(_.name) === Some("Grant2")
        v2.grant.map(_.name) === Some("Grant1")
        v2.copy(id = None, opId = None) === exp

        v1.opId.isDefined === true
        v1.copy(id = None, amount = None) === v2.copy(id = None, amount = None)
      }
    }

    "update account in" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", None, "exp1")
        val id = expDao.insertWithOpId(exp)

        val account2 = accountDao.get("Account2").get
        expDao.update(Update("account", id, account2.id.get.toString), user)

        val exps = expDao.log
        exps.size === 2
        val (v1, v2) = (exps.head, exps.last)

        v1.account.name === Some("Account2")
        v2.account.name === Some("Account1")
        v2.copy(id = None, opId = None) === exp

        v1.opId.isDefined === true
        v1.copy(id = None, account = v2.account) === v2.copy(id = None)
      }
    }

    "update category in" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", None, "exp1")
        val id = expDao.insertWithOpId(exp)

        val category2 = categoryDao.get("Category2").get
        expDao.update(Update("category", id, category2.id.get.toString), user)

        val exps = expDao.log
        exps.size === 2
        val (v1, v2) = (exps.head, exps.last)

        v1.category.name === Some("Category2")
        v2.category.name === Some("Category1")
        v2.copy(id = None, opId = None) === exp

        v1.opId.isDefined === true
        v1.copy(id = None, category = v2.category) === v2.copy(id = None)
      }
    }

    "update project in" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", None, "exp1")
        val id = expDao.insertWithOpId(exp)

        val project2 = projectDao.get("Project2").get
        expDao.update(Update("project", id, project2.id.get.toString), user)

        val exps = expDao.log
        exps.size === 2
        val (v1, v2) = (exps.head, exps.last)

        v1.project.name === Some("Project2")
        v2.project.name === Some("Project1")
        v2.copy(id = None, opId = None) === exp

        v1.opId.isDefined === true
        v1.copy(id = None, project = v2.project) === v2.copy(id = None)
      }
    }

    "update description in" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", None, "descr1")
        val id = expDao.insertWithOpId(exp)

        expDao.update(Update("descr", id, "descr2"), user)

        val exps = expDao.log
        exps.size === 2
        val (v1, v2) = (exps.head, exps.last)

        v1.description === Some("descr2")
        v2.description === Some("descr1")
        v2.copy(id = None, opId = None) === exp

        v1.opId.isDefined === true
        v1.copy(id = None, description = v2.description) === v2.copy(id = None)
      }
    }
  }
}
