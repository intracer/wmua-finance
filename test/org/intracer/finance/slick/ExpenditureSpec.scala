package org.intracer.finance.slick

import java.sql.Timestamp

import client.finance.GrantItem
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

  def newExp(amount: Int, grant: String, account: String, category: String, project: String, grantItem: String, description: String) = {
    val grant = grantDao.list.find(_.name == grant)

    val exp = Expenditure(
      None,
      new Timestamp(0L),
      Some(BigDecimal(amount + ".00")),
      accountDao.list.find(_.name == account).get,
      categoryDao.list.find(_.name == category).get,
      projectDao.list.find(_.name == project).get,
      grant,
      grantItemDao.list(grant.flatMap(_.id).get).find(_.description == grantItem),
      description,
      userDao.list.head
    )
    exp
  }

  "expenditure" should {
    "insert" in {
      inMemDbApp {
        val exp = newExp(10, "Grant1", "Account1", "Category1", "Project1", "GrantItem1", "exp1")

        val id = expDao.insert(exp)

        val exps = expDao.list
        exps.size === 1
        exps.head === exp.copy(id = Some(id))
      }
    }
  }
}
