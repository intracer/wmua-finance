package controllers

import org.intracer.finance.slick.{ExpenditureDao, UserDao}
import org.mockito.{Mockito => JM}
import org.openqa.selenium.chrome.ChromeDriver
import org.specs2.mock.Mockito
import play.api.Mode
import play.api.db.DBApi
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{PlaySpecification, WebDriverFactory, WithBrowser}

class OperationsSpec extends PlaySpecification with Mockito with WebSpecUtil {

  val admin = defaultUser.copy(roles = "admin")
  override val userDao = mockUserDao(admin)

  def app = noDbApp.in(Mode.Test).build()

  "add operation" should {
    "work with 2016 grant" in new WithBrowser(webDriver = WebDriverFactory(classOf[ChromeDriver]), app = app) {

      //      val newOp = NewOp(date = new Date(new DateTime(2016, 12, 30, 0, 0).getMillis),
      //        project = 0,
      //        category = 19,
      //        grant = Some(17),
      //        grantItem = some(74),
      //        amount = Some(BigDecimal("1.5")),
      //        account = Some(1),
      //        descr = Some("банківські"))
      //
      //      expenditureDao.insertCmd(===(newOp), ===(admin)) returns Future.successful(10)

      login(browser)
      waitForUrl("/operations", browser)
      browser.url() === "/operations"

      Thread.sleep(2000)
      browser.takeScreenShot("operations.png")

      browser
        .click("#tr_new_date")
        .click("select[class*='day'] option[value='30']")
        .click("select[class*='month'] option[value='11']")
        .click("select[class*='year'] option[value='2016']")
        .click("button[class*='editable-submit']")

      browser.click("button[class*='save-btn']")

      //      val date = new Timestamp(new DateTime(2016, 12, 30, 0, 0).getMillis)
      //      val account = Account(Some(1), "2202")
      //      val category = CategoryF(Some(19), "адміністративні/банківські витрати")
      //      val project = Project(Some(0), "адміністрування")
      //      val grant = Grant(Some(17), "Grants:APG/Proposals/2015-2016 round1")
      //      val grantItem = GrantItem(Some(74), Some(17), "4.1", "Operations (excludes staff and programs)", BigDecimal("7368.00"))
      //
      //      val exp = Expenditure(None, None,
      //        date,
      //        Some(BigDecimal("1.5")),
      //        account, category, project,
      //        Some(grant), Some(grantItem),
      //        "банківські", admin
      //      )
      //      there was one(expenditureDao).insertWithOpId(exp)


      // there was one(expenditureDao).list
      //    JM.verify(expenditureDao, JM.times(1)).insertCmd(===(newOp), ===(admin))
    }
  }
}
