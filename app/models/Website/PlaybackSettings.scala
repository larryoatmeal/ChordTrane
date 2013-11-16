package models.website

import models._
import anorm._
import anorm.RowParser
import anorm.SqlParser._
import play.api.Play.current
import play.api.db.DB
import play.api.Logger


case class PlaybackSettings(songId: Int, bpm: Int, repeats: Int, pianoSettings: PianoSettings, bassSettings: BassSettings)


//case class CompingOptions(minRange: Int, maxRange: Int, closedness: Double, rangeCurvePower: Double)
//case class BassProperties(minRange: Int, maxRange: Int, connectedness: Double, rangeCurvePower: Double)

object PlaybackSettings extends DatabaseObject{

  val DefaultPlaybackSettings = PlaybackSettings(-1, 120, 3, PianoPlayer.DefaultPianoSettings, BassPlayer.DefaultBassSettings)

  val playbackSettingsParser: RowParser[PlaybackSettings] = {
    import anorm.~
    get[Int]("songId") ~ 
    get[Int]("bpm") ~ 
    get[Int]("repeats") ~                   
    get[Int]("pianoMinRange") ~     
    get[Int]("pianoMaxRange") ~     
    get[Double]("pianoStayInTessitura") ~ 
    get[Double]("pianoConnectivity") ~  
    get[Int]("bassMinRange") ~  
    get[Int]("bassMaxRange") ~      
    get[Double]("bassStayInTessitura") ~    
    get[Double]("bassConnectivity") map {
    case songId ~ bpm ~ repeats ~ pianoMinRange ~ pianoMaxRange ~ pianoStayInTessitura ~ pianoConnectivity    ~ bassMinRange ~ bassMaxRange   ~ bassStayInTessitura   ~ bassConnectivity =>
        PlaybackSettings(songId, bpm, repeats, 
            PianoSettings(pianoMinRange, pianoMaxRange, pianoStayInTessitura, pianoConnectivity),
            BassSettings(bassMinRange, bassMaxRange, bassStayInTessitura, bassConnectivity)
        )
    }
  }

  def getPlaybackSettings(songId: Int) = getSingleWithID[PlaybackSettings](playbackSettingsParser, songId, "songId", "playbackSettings")

  def updatePlaybackSettings(playbackSettings: PlaybackSettings) = DB.withConnection{
    implicit connection=>
    SQL("""
    UPDATE playbackSettings
    SET
    bpm = {bpm},
    repeats = {repeats},
    pianoMinRange = {pianoMinRange},
    pianoMaxRange = {pianoMaxRange},
    pianoStayInTessitura = {pianoStayInTessitura},
    pianoConnectivity = {pianoConnectivity},
    bassMinRange = {bassMinRange},
    bassMaxRange = {bassMaxRange},
    bassStayInTessitura = {bassStayInTessitura},
    bassConnectivity = {bassConnectivity}
    WHERE songId = {songId}
    """
    ).on(
    "songId" -> playbackSettings.songId,
    "bpm" -> playbackSettings.bpm,
    "repeats" -> playbackSettings.repeats,
    "pianoMinRange" -> playbackSettings.pianoSettings.lower,
    "pianoMaxRange" -> playbackSettings.pianoSettings.upper,
    "pianoStayInTessitura" -> playbackSettings.pianoSettings.stayInTessitura,
    "pianoConnectivity" -> playbackSettings.pianoSettings.connectivity,
    "bassMinRange" -> playbackSettings.bassSettings.lower,
    "bassMaxRange" -> playbackSettings.bassSettings.upper,
    "bassStayInTessitura" -> playbackSettings.bassSettings.stayInTessitura,
    "bassConnectivity" -> playbackSettings.bassSettings.connectivity
   ).executeUpdate()
  }

  def newPlaybackSettings(songId: Int) = DB.withConnection{
    implicit connection=>
    SQL("""
    INSERT INTO playbackSettings (bpm, repeats, pianoMinRange, pianoMaxRange, pianoStayInTessitura, pianoConnectivity, bassMinRange, bassMaxRange, bassStayInTessitura, bassConnectivity, songId)
    VALUES 
    ({bpm},
    {repeats},
    {pianoMinRange},
    {pianoMaxRange},
    {pianoStayInTessitura},
    {pianoConnectivity},
    {bassMinRange},
    {bassMaxRange},
    {bassStayInTessitura},
    {bassConnectivity},
    {songId})
    """
    ).on(
    "songId" -> songId,
    "bpm" -> DefaultPlaybackSettings.bpm,
    "repeats" -> DefaultPlaybackSettings.repeats,
    "pianoMinRange" -> DefaultPlaybackSettings.pianoSettings.lower,
    "pianoMaxRange" -> DefaultPlaybackSettings.pianoSettings.upper,
    "pianoStayInTessitura" -> DefaultPlaybackSettings.pianoSettings.stayInTessitura,
    "pianoConnectivity" -> DefaultPlaybackSettings.pianoSettings.connectivity,
    "bassMinRange" -> DefaultPlaybackSettings.bassSettings.lower,
    "bassMaxRange" -> DefaultPlaybackSettings.bassSettings.upper,
    "bassStayInTessitura" -> DefaultPlaybackSettings.bassSettings.stayInTessitura,
    "bassConnectivity" -> DefaultPlaybackSettings.bassSettings.connectivity
   ).executeUpdate()
  }

  def deletePlaybackSettings(songId: Int) = DB.withConnection{
    implicit connection =>
    SQL("""
      DELETE FROM playbackSettings WHERE songId = {songId}
      """).on(
        "songId" -> songId
      ).executeUpdate()
  }


}

  
  



	  

