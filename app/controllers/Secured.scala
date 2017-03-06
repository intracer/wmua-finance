package controllers

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import org.intracer.finance.User
import org.intracer.finance.slick.UserDao
import play.api.mvc._

import scala.concurrent.Future

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

trait Secured {

  type Permission = User => Boolean

  def userDao: UserDao

  def user(request: RequestHeader): Option[User] = {
    request.session.get(Security.username)
      .map(_.trim.toLowerCase)
      .flatMap(userDao.byEmail)
  }

  def onUnAuthenticated(request: RequestHeader) = Results.Redirect(routes.Login.login())

  def onUnAuthorized(user: User) = Results.Redirect(routes.Login.error("You don't have permission to access this page"))

  def withAuth(permission: Permission = AllowPermission)
              (f: => User => Request[AnyContent] => Result) = {
    Security.Authenticated(user, onUnAuthenticated) { user =>
      Action(request =>
        if (permission(user))
          f(user)(request)
        else
          onUnAuthorized(user)
      )
    }
  }

  def withAuthAsync(permission: Permission = AllowPermission)
                   (f: => User => Request[AnyContent] => Future[Result]) = {
    Security.Authenticated(user, onUnAuthenticated) { user =>
      Action.async(request =>
        if (permission(user))
          f(user)(request)
        else
          Future.successful(onUnAuthorized(user))
      )
    }
  }

  val AllowPermission = (_: User) => true

  def isAdmin(user: User) = user.hasRole("admin")

  def isContributor(user: User) = user.hasAnyRole(Set("admin", "organizer", "contributor"))

  def rolePermission(roles: Set[String])(user: User) = user.hasAnyRole(roles)

}






