package controllers.minecraft
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._



trait Secured {

  //Get connected username
  private def minecraftid(request: RequestHeader) = {
    request.session.get("minecraftid")
  }

  //Redirect to login if unauthorized
  private def onUnauthorized(request: RequestHeader) = 
    Results.Redirect(controllers.minecraft.routes.Authentication.loginPage)

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = 
    Security.Authenticated(minecraftid, onUnauthorized){
      user => Action(request => f(user)(request))
  }
  

}