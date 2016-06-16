package org.intracer.finance.slick

import controllers.Global
import org.intracer.finance.{Account, CategoryF, Grant, Project}
import slick.jdbc.JdbcBackend.Database
object Import {

  def main(args: Array[String]) {
    val operations = Global.loadFinance()

    val projects = Global.mapping.project.map { case (id, name) => new Project(Some(id), name) }.toSeq
    val categories = Global.mapping.category.map { case (id, name) => new CategoryF(Some(id), name) }.toSeq
    val grants = Global.mapping.grant.map { case (id, name) => new Grant(Some(id), name) }.toSeq

    val accounts = operations.groupBy(_.from.asInstanceOf[Account]).keys.toSeq

    val db = new FinDatabase(Database.forConfig("slick.dbs.default"))

    db.projectDao.insertAll(projects)
    db.categoryDao.insertAll(categories)
    db.grantDao.insertAll(grants)
    db.accountDao.insertAll(accounts)

    val exp = operations.map(_.to)
    db.expDao.insertAll(exp)

  }

}
