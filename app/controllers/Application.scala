package controllers


import play.api._
import play.api.mvc._
import models.website._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger
import play.api.libs.Files
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints

import models.website._


object Application extends Controller with Secured{

	def home = IsAuthenticated {
    userId => 
		implicit request =>
    Ok(views.html.home("Home", User.getUser(userId.toInt)))
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
        JSONmaster.getSongsFull,
        JSONmaster.saveSong,
        JSONmaster.getPlaybackSettings,
        JSONmaster.savePlaybackSettings,
        JSONmaster.formatText,
        JSONmaster.exportMusicXML,
        JSONmaster.download,
        JSONmaster.playback,
        JSONmaster.newSong,
        JSONmaster.deleteSong,
        JSONmaster.midiAsBase64,
        JSONmaster.midiString64,
        JSONmaster.playbackGuestAccount
      )
    ).as("text/javascript") 
  }



  





  
}