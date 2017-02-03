package org.intracer.finance.slick

import play.api.test.FakeApplication
import play.api.test.Helpers._

trait InMemDb {

  private def inMemoryDatabase(name: String = "default", options: Map[String, Any] = Map.empty[String, Any]): Map[String, Any] = {
    val optionsForDbUrl = options.map { case (k, v) => k + "=" + v }.mkString(";", ";", "")

    Map(
      "play.evolutions.enabled" -> true,
      ("slick.dbs." + name + ".driver") -> "slick.driver.H2Driver$",
      ("slick.dbs." + name + ".db.driver") -> "org.h2.Driver",
      ("slick.dbs." + name + ".db.url") -> ("jdbc:h2:mem:play-test-" + scala.util.Random.nextInt + optionsForDbUrl)
    )
  }

  def inMemDbApp[T](block: => T): T = {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase()))(block)
  }

}
