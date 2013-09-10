package controllers

import play.api._
import play.api.mvc._

object SongController extends Controller {
  
  def getSongJson = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
}