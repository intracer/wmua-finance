package controllers

import org.intracer.finance.User
import org.intracer.finance.slick.{ExpenditureDao, UserDao}
import org.specs2.mock.Mockito
import play.api.Mode
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.Codecs
import play.api.test.{PlaySpecification, WebDriverFactory, WithBrowser}

class LoginSpec extends PlaySpecification with Mockito {

  val userDao = mock[UserDao]
  val expenditureDao = mock[ExpenditureDao]

  def app = new GuiceApplicationBuilder()
    .bindings(bind[UserDao].to(userDao))
    .bindings(bind[ExpenditureDao].to(expenditureDao))
    .in(Mode.Test)
    .build()

  "login" should {
    "login" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = app) {
      val email = "dev@dot.com"
      val password = "1234"
      val user = User(Some(12), "Dev", email, password = Some(Codecs.sha1(password.getBytes)))

      userDao.count returns 1
      userDao.login(email, password) returns Some(user)
      userDao.byEmail(email) returns Some(user)

      expenditureDao.list returns Nil

      browser.goTo("/")
        .fill("#login").`with`(email)
        .fill("#password").`with`(password)
        .submit("#submit")

      browser.waitUntil(browser.url() == "/operations")
      browser.url() === "/operations"
    }
  }
}
