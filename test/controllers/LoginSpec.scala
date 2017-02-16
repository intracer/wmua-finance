package controllers

import org.openqa.selenium.chrome.ChromeDriver
import org.specs2.mock.Mockito
import play.api.Mode
import play.api.test.{PlaySpecification, WebDriverFactory, WithBrowser}

class LoginSpec extends PlaySpecification with Mockito with WebSpecUtil {

  def app = noDbApp.in(Mode.Test).build()

  "login" should {
    "login" in new WithBrowser(webDriver = WebDriverFactory(classOf[ChromeDriver]), app = app) {
      login(browser)
      waitForUrl("/operations", browser)

      browser.url() === "/operations"
    }
  }
}
