package org.intracer.finance.slick

import controllers.Global
import org.intracer.finance._
import _root_.slick.backend.DatabaseConfig
import _root_.slick.driver.MySQLDriver
object Import {

  def main(args: Array[String]) {
    val operations = Global.loadFinance()

    val projects = Global.mapping.project.map { case (id, name) => new Project(Some(id), name) }.toSeq
    val categories = Global.mapping.category.map { case (id, name) => new CategoryF(Some(id), name) }.toSeq
    val grants = Global.mapping.grant.map { case (id, name) => new Grant(Some(id), name) }.toSeq

    val accounts = operations.groupBy(_.from.asInstanceOf[Account]).keys.toSeq

    val dbConfig:DatabaseConfig[MySQLDriver] = DatabaseConfig.forConfig("slick.dbs.remote")

    val db = new FinDatabase(dbConfig.db)

//    db.projectDao.insertAll(projects)
//    db.categoryDao.insertAll(categories)
    //db.grantDao.insertAll(grants)
    //db.accountDao.insertAll(accounts)

  //    val exp = operations.map(_.to)

    //db.expDao.insertAll(exp)

//    val exp = new Expenditure(date =)

  //      db.expDao.insertAll(exp)

//    db.expDao.

  }

}
