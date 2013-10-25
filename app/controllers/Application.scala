package controllers


import play.api._
import play.api.mvc._
import models.website._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger
import play.api.libs.Files

import models.website._


object Application extends Controller with Secured{

	def home = IsAuthenticated {
		implicit request =>
		userId => 
    Ok(views.html.home("Home", User.dummyUser))
  }

  def homeGuest = Action {
    Ok(views.html.homeGuest("Home"))
  }


  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        JSONmaster.getSong,
        JSONmaster.getSongs,
        JSONmaster.saveSong,
        JSONmaster.getPlaybackSettings,
        JSONmaster.savePlaybackSettings,
        JSONmaster.formatText,
        JSONmaster.exportMusicXML,
        JSONmaster.download,
        JSONmaster.playback
      )
    ).as("text/javascript") 
  }


  

  





  
}