package org.intracer.finance.slick

import org.intracer.finance.Project
import slick.driver.H2Driver.api._

class Projects(tag: Tag) extends Table[Project](tag, "PROJECT") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def nameIndex = index("project_name", name, unique = true)

  def * = (id.?, name) <> (Project.tupled, Project.unapply)

}
