package controllers

import org.intracer.finance.User
import org.intracer.finance.slick.Expenditures
import play.api.mvc.Controller
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

object Dictionaries extends Controller with Secured {

  def list() = withAuth { username =>
    implicit request =>

      Ok(views.html.dictionaries(new User(***REMOVED***), Seq.empty))
  }

  def accounts() = withAuth { username =>
    implicit request =>

      val accounts = Expenditures.accounts.values.toSeq.sortBy(_.id)

      Ok(views.html.dictionaries(new User(***REMOVED***), accounts))
  }

  def categories() = withAuth { username =>
    implicit request =>

      val categories = Expenditures.categories.values.toSeq.sortBy(_.id)

      Ok(views.html.dictionaries(new User(***REMOVED***), categories))
  }

  def projects() = withAuth { username =>
    implicit request =>

      val projects = Expenditures.projects.values.toSeq.sortBy(_.id)

      Ok(views.html.dictionaries(new User(***REMOVED***), projects))
  }

}
