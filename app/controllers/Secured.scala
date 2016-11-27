package controllers

import org.intracer.finance.{User, UserObj}
import play.api.mvc._

trait Secured {

  type Permission = User => Boolean

  def userDao = Global.db.userDao

  def user(request: RequestHeader): Option[User] = {
    request.session.get(Security.username).map(_.trim.toLowerCase).flatMap(userDao.get)
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

  val AllowPermission = (_: User) => true

  def rolePermission(roles: Set[String])(user: User) = user.hasAnyRole(roles)

}






