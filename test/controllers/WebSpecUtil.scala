package controllers

import org.intracer.finance.{Expenditure, User}
import org.intracer.finance.slick._
import org.specs2.mock.Mockito
import play.api.db.DBApi
import play.api.db.slick.{SlickApi, SlickModule}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.Codecs
import play.api.test.TestBrowser

trait WebSpecUtil extends Mockito {
  val defaultEmail = "dev@dot.com"
  val defaultPassword = "1234"
  val defaultUserId = 12
  val defaultUser = User(Some(defaultUserId), "Dev", defaultEmail, password = Some(defaultPassword))

  val accountDao = mock[AccountDao]
  val categoryDao = mock[CategoryDao]
  val grantDao = mock[GrantDao]
  val grantItemsDao = mock[GrantItemsDao]
  val projectDao = mock[ProjectDao]
  val userDao = mockUserDao()
  val expenditureDao = mockExpenditureDao()

  def noDbApp = new GuiceApplicationBuilder()
    .disable(classOf[SlickModule])
    .disable(classOf[play.api.db.evolutions.EvolutionsModule])
    .disable(classOf[play.api.db.slick.evolutions.EvolutionsModule])
    .bindings(bind(classOf[DBApi]).to(mock[DBApi]))
    .bindings(bind(classOf[play.api.db.Database]).to(mock[play.api.db.Database]))
    .bindings(bind(classOf[play.db.Database]).to(mock[play.db.Database]))
    .bindings(bind(classOf[SlickApi]).to(mock[SlickApi]))
    .bindings(bind[AccountDao].to(accountDao))
    .bindings(bind[CategoryDao].to(categoryDao))
    .bindings(bind[ExpenditureDao].to(expenditureDao))
    .bindings(bind[GrantDao].to(grantDao))
    .bindings(bind[GrantItemsDao].to(grantItemsDao))
    .bindings(bind[ProjectDao].to(projectDao))
    .bindings(bind[UserDao].to(userDao))

  def login(browser: TestBrowser, user: User = defaultUser) =
    browser.goTo("/")
      .fill("#login").`with`(user.email)
      .fill("#password").`with`(user.password.get)
      .submit("#submit")

  def waitForUrl(url: String, browser: TestBrowser) =
    browser.waitUntil(browser.url() == url)

  def mockUserDao(user: User = defaultUser): UserDao = {
    val userDao = mock[UserDao]

    userDao.count returns 1
    userDao.login(user.email, user.password.get) returns Some(withSha1(user))
    userDao.byEmail(user.email) returns Some(withSha1(user))
    userDao
  }

  def withSha1(user: User) =
    user.copy(password = user.password.map(s => Codecs.sha1(s.getBytes)))

  def mockExpenditureDao(list: Seq[Expenditure] = Nil): ExpenditureDao = {
    val expenditureDao = mock[ExpenditureDao]
    expenditureDao.list returns list
    expenditureDao
  }

}
