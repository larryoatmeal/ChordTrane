package controllers
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._


trait Secured {

  //Get connected user's id
  private def id(request: RequestHeader) = {
    request.session.get("id")
  }

  //Redirect to login if unauthorized
  private def onUnauthorized(request: RequestHeader) = 
    Results.Redirect(routes.Application.homeGuest)

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = 
    Security.Authenticated(id, onUnauthorized){
      user => Action(request => f(user)(request))
  }

  

  

}