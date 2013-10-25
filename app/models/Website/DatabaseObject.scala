package models.website

trait DatabaseObject{
	import anorm._
	import anorm.RowParser
	import anorm.SqlParser._
	import play.api.Play.current
	import play.api.db.DB
	import play.api.Logger


  val TableName = "ChordTrane"

	def getSingleWithID[T](parser: RowParser[T], id: Int, idColName: String, table: String = TableName) = DB.withConnection{
    implicit connection =>
    
    val sql = SQL(s"""SELECT * FROM $table WHERE $idColName = $id""")
    sql.as(parser *).head
  }

  def getAll[T](parser: RowParser[T], table: String = TableName) = DB.withConnection{
    implicit connection =>

    try {
      SQL(s"""SELECT * FROM $table""").as(parser *)
    }
    catch {
      case e => {
        Logger.error(e.toString)
        List()
      }
    }
  }

  def getAllOfID[T](parser: RowParser[T], id: Int, idColName: String, table: String = TableName) = DB.withConnection{
    implicit connection =>

    try {
      SQL(s"""SELECT * FROM $table WHERE $idColName = $id""").as(parser *)
    }
    catch {
      case e => {
        Logger.error(e.toString)
        List()
      }
    }
  }




}