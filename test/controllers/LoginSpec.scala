package controllers

import org.intracer.finance.slick.{ExpenditureDao, UserDao}
import org.specs2.mock.Mockito
import play.api.Mode
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{PlaySpecification, WebDriverFactory, WithBrowser}

class LoginSpec extends PlaySpecification with Mockito with WebSpecUtil {

  val userDao = mockUserDao()
  val expenditureDao = mockExpenditureDao()

  def app = new GuiceApplicationBuilder()
    .bindings(bind[UserDao].to(userDao))
    .bindings(bind[ExpenditureDao].to(expenditureDao))
    .in(Mode.Test)
    .build()

  "login" should {
    "login" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT), app = app) {
      login(browser)
      waitForUrl("/operations", browser)

      browser.url() === "/operations"
    }
  }
}
