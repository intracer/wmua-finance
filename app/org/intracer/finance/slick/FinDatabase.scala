package org.intracer.finance.slick

import slick.driver.{H2Driver, JdbcProfile}
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import slick.driver.H2Driver.api._
import slick.driver.{H2Driver, JdbcProfile}
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery
import spray.util.pimpFuture
import scala.concurrent.ExecutionContext.Implicits.global

class FinDatabase(val db: Database, val driver: JdbcProfile = H2Driver) {

  val categories = TableQuery[Categories](
    (tag: Tag) => new Categories(tag)
  )

  val grants = TableQuery[Grants](
    (tag: Tag) => new Grants(tag))

  val projects = TableQuery[Projects](
    (tag: Tag) => new Projects(tag))

  val exps = TableQuery[Expenditures](
    (tag: Tag) => new Expenditures(tag))

  val accounts = TableQuery[Accounts] (
    (tag: Tag) => new Accounts(tag)
  )

  def tables =
    Seq(
      categories,
      projects,
      grants,
      accounts
    )

  val categoryDao = new CategoryDao(this, categories, driver)
  val grantDao = new GrantDao(this, grants, driver)
  val projectDao = new ProjectDao(this, projects, driver)
  val expDao = new ExpenditureDao(this, exps, driver)
  val accountDao = new AccountDao(this, accounts, driver)

  def createTables() {
    createIfNotExists()
  }

  def existingTables(existing: Boolean) = {
    db.run(MTable.getTables).map {
      dbTables =>
        val dbTableNames = dbTables.map(_.name.name).toSet
        tables.filter { t =>
          val tableName = t.baseTableRow.tableName
          val contains = dbTableNames.contains(tableName)

          if (existing)
            contains
          else
            !contains
        }
    }
  }

  def dropTables() {
    val toDrop = existingTables(true).await
    db.run(
      DBIO.sequence(toDrop.map(_.schema.drop))
    ).await
  }

  def createIfNotExists() {
    val toCreate = existingTables(false).await
    db.run(
      DBIO.sequence(toCreate.map(_.schema.create))
    ).await
  }
}

object FinDatabase {

  def create(host: String) = {
    val db = Database.forURL("jdbc:h2:~/scalawiki", driver = "org.h2.Driver")
    val database = new FinDatabase(db)
    database.createTables()
    database
  }

}
