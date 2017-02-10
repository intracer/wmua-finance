package org.intracer.finance.slick

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}

import slick.driver.JdbcProfile
import slick.profile.SqlProfile.ColumnOption.SqlType
import client.finance.GrantItem
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import org.intracer.finance._
import _root_.slick.lifted.Rep

import scala.collection.SortedSet

trait IdTable {
  def id: Rep[Int]
}

@Singleton()
class Schema @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val accountsQuery = TableQuery[Accounts]
  val categoriesQuery = TableQuery[Categories]
  val grantsQuery = TableQuery[Grants]
  val grantItemsQuery = TableQuery[GrantItems]
  val projectsQuery = TableQuery[Projects]
  val usersQuery = TableQuery[Users]
  val expendituresQuery = TableQuery[Expenditures]

  val accountDao = new AccountDao
  val categoryDao = new CategoryDao
  val grantDao = new GrantDao
  val grantItemDao = new GrantItemDao
  val projectDao = new ProjectDao
  val userDao = new UserDao(dbConfigProvider, this)

  def accounts: Map[Int, Account] = accountDao.list.groupBy(_.id.get).mapValues(_.head)

  def categories: Map[Int, CategoryF] = categoryDao.list.groupBy(_.id.get).mapValues(_.head)

  def projects: Map[Int, Project] = projectDao.list.groupBy(_.id.get).mapValues(_.head)

  lazy val grants: Map[Int, Grant] = grantDao.list.groupBy(_.id.get).mapValues(_.head)

  lazy val grantItems: Map[Int, Seq[GrantItem]] = grantItemDao.listAll().groupBy(_.grantId.get)

  def projectsJson: String = {
    projects.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, project) => s"""{ value: "$id", text: "${project.name}"}"""
    }.mkString(", ")
  }

  def categoriesJson: String = {
    val elems = categories.values.flatMap(_.name.split("/").headOption).toSeq
    val parents = SortedSet(elems: _*)

    parents.map { parent =>
      s"""{text: "$parent", children: [""" +
        categories.toSeq
          .filter(_._2.name.toLowerCase.startsWith(parent.toLowerCase))
          .sortBy(_._2.name.toLowerCase)
          .map {
            case (id, cat) => s"""{ value: "$id", text: "${cat.name}"}"""
          }.mkString(", ") + "]}"
    }.mkString(", ")
  }

  def grantsJson: String = {
    grants.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, grant) => s"""{ value: "$id", text: "${grant.name}"}"""
    }.mkString(", ")
  }

  def grantItemsJson(grantId: Int): String = {
    val programs = Seq(
      "Program 1: Outreach", "Program 2: Contests", "Program 3: Community Support", "Administrative costs"
    )
    (1 to 4).map { program =>
      s"""{text: "${programs(program - 1)}", children: [""" +
        grantItems
          .getOrElse(grantId, Seq.empty)
          .filter(_.number.startsWith(program.toString))
          .map {
            item => s"""{ value: "${item.id.get}", text: "${item.name}"}"""
          }.mkString(", ") + "]}"
    }.mkString(", ")
  }

  def accountsJson: String = {
    accounts.toSeq.sortBy(_._2.name.toLowerCase).map {
      case (id, account) => s"""{ value: "$id", text: "${account.name}"}"""
    }.mkString(", ")
  }

  def readExpenditure(id: Option[Int],
             opDate: Timestamp,
             amount: Option[BigDecimal],
             accountId: Int,
             categoryId: Int,
             projectId: Int,
             maybeGrantId: Option[Int],
             maybeGrantItemId: Option[Int],
             descr: String,
             logDate: Timestamp,
             userId: Int): Expenditure = {

    val grantItem = for (grantId <- maybeGrantId;
                         grantItemId <- maybeGrantItemId;
                         grantItemsForGrant <- grantItems.get(grantId);
                         grantItem <- grantItemsForGrant.find(_.id.exists(_ == grantItemId))
    ) yield grantItem

    val user = userDao.byId(userId).get

    Expenditure(id, opDate, amount, accounts(accountId), categories(categoryId), projects(projectId),
      maybeGrantId.map(grants), grantItem, descr, user, logDate)
  }

  def writeExpenditure(exp: Expenditure) = {
    Some((exp.id, exp.date, exp.amount,
      exp.account.id.get,
      exp.category.id.get,
      exp.project.id.get,
      exp.grant.flatMap(_.id),
      exp.grantItem.flatMap(_.id),
      exp.desc,
      exp.logDate,
      exp.user.id.get))
  }

  class Accounts(tag: Tag) extends Table[Account](tag, "account") with IdTable {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def nameIndex = index("acc_name", name, unique = true)

    def * = (id.?, name) <> (Account.tupled, Account.unapply)
  }

  class Categories(tag: Tag) extends Table[CategoryF](tag, "category") with IdTable {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def nameIndex = index("category_name", name, unique = true)

    def * = (id.?, name) <> (CategoryF.tupled, CategoryF.unapply)
  }

  class Grants(tag: Tag) extends Table[Grant](tag, "grant_list") with IdTable {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def url = column[Option[String]]("url")

    def nameIndex = index("grant_name", name, unique = true)

    def * = (id.?, name, url) <> (Grant.tupled, Grant.unapply)
  }

  class GrantItems(tag: Tag) extends Table[GrantItem](tag, "grant_item") with IdTable {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def grantId = column[Int]("grant_id")

    def number = column[String]("number")

    def description = column[String]("description")

    //  def category = column[String]("category")

    //  def unit = column[String]("unit")
    //
    //  def qty = column[String]("qty")
    //
    //  def costPerUnit = column[String]("cost_per_unit")

    def totalCost = column[BigDecimal]("total_cost")

    //  def wmfContrib = column[Option[BigDecimal]]("wmf_contrib")
    //
    //  def otherSources = column[Option[BigDecimal]]("other_sources")

    def notes = column[Option[String]]("notes")

    def * = (id.?, grantId.?, number, description, /*category, , unit, qty, costPerUnit*/
      totalCost, /*, wmfContrib, otherSources,*/ notes) <> (fromDb, toDb)

    def fromDb(v: (Option[Int], Option[Int], String, String, BigDecimal, Option[String])) = {
      GrantItem(
        id = v._1,
        grantId = v._2,
        number = v._3,
        description = v._4,
        totalCost = v._5,
        notes = v._6
      )
    }

    def toDb(g: GrantItem) = {
      Some((
        g.id,
        g.grantId,
        g.number,
        g.description,
        g.totalCost,
        g.notes
      ))
    }
  }

  class Projects(tag: Tag) extends Table[Project](tag, "project") with IdTable {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def nameIndex = index("project_name", name, unique = true)

    def * = (id.?, name) <> (Project.tupled, Project.unapply)

  }


  class Users(tag: Tag) extends Table[User](tag, "user") with IdTable {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def fullname = column[String]("fullname")

    def email = column[String]("email")

    def roles = column[String]("roles")

    def password = column[Option[String]]("password")

    def lang = column[Option[String]]("lang")

    def wikiAccount = column[Option[String]]("wiki_account")

    def emailIndex = index("acc_email", email, unique = true)

    def * = (id.?, fullname, email, roles, password, lang, wikiAccount) <> (User.tupled, User.unapply)

  }

  class Expenditures(tag: Tag) extends Table[Expenditure](tag, "operation") with IdTable {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    //  def rev_id = column[Int]("rev_id", O.PrimaryKey, O.AutoInc)

    def date = column[Timestamp]("op_date", SqlType("datetime"))

    def amount = column[Option[BigDecimal]]("amount")

    def from = column[Int]("account_id")

    def categoryId = column[Int]("cat_id")

    def projectId = column[Int]("proj_id")

    def grantId = column[Option[Int]]("grant_id")

    def grantItem = column[Option[Int]]("grant_item")

    def grantRow = column[Option[String]]("grant_row")

    def descr = column[String]("descr")

    def account = foreignKey("ACC_FK", from, accountsQuery)(_.id)

    def category = foreignKey("CAT_FK", categoryId, categoriesQuery)(_.id)

    def projects = foreignKey("PROJ_FK", projectId, projectsQuery)(_.id)

    def grants = foreignKey("GRANT_FK", grantId, grantsQuery)(_.id)

    def logDate = column[Timestamp]("log_date", SqlType("datetime"))

    def userId = column[Int]("user_id")

    def * = (id.?, date, amount, from, categoryId, projectId, grantId, grantItem, descr, logDate, userId) <>
      ((readExpenditure _).tupled, writeExpenditure)
  }


  class AccountDao extends BaseDao[Account] {
    override val dbConfig = Schema.this.dbConfigProvider.get

    val query = accountsQuery

    def list: Seq[Account] = run {
      query.sortBy(_.name).result
    }

    def get(name: String): Option[Account] = run {
      query.filter(_.name === name).result.headOption
    }
  }

  class CategoryDao extends BaseDao[CategoryF] {
    override val dbConfig = Schema.this.dbConfigProvider.get

    val query = categoriesQuery

    def list: Seq[CategoryF] = run {
      query.sortBy(_.name).result
    }

    def get(name: String): Option[CategoryF] = run {
      query.filter(_.name === name).result.headOption
    }
  }

  class GrantDao extends BaseDao[Grant] {
    override val dbConfig = Schema.this.dbConfigProvider.get

    val query = grantsQuery

    def list: Seq[Grant] = run {
      query.sortBy(_.name).result
    }

    def get(name: String): Option[Grant] = run {
      query.filter(_.name === name).result.headOption
    }
  }

  class GrantItemDao extends BaseDao[GrantItem] {
    override val dbConfig = Schema.this.dbConfigProvider.get

    val query = grantItemsQuery

    def list(grantId: Int): Seq[GrantItem] = run {
      query.filter(_.grantId === grantId).sortBy(_.id).result
    }

    def listAll(): Seq[GrantItem] = run {
      query.sortBy(_.id).result
    }

    def get(id: Int): Option[GrantItem] = run {
      query.filter(_.id === id).result.headOption
    }
  }

  class ProjectDao extends BaseDao[Project] {
    override val dbConfig = Schema.this.dbConfigProvider.get

    val query = projectsQuery

    def list: Seq[Project] = run {
      query.sortBy(_.name).result
    }

    def get(name: String): Option[Project] = run {
      query.filter(_.name === name).result.headOption
    }
  }

}