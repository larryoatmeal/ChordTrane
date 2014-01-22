package models.website

trait DatabaseObject{
	import anorm._
	import anorm.RowParser
	import anorm.SqlParser._
	import play.api.Play.current
	import play.api.db.DB
	import play.api.Logger



	def getSingleWithID[T](parser: RowParser[T], id: Int, idColName: String, table: String) = DB.withConnection{
    implicit connection =>
    
    val sql = SQL(s"""SELECT * FROM $table WHERE $idColName = $id""")
    sql.as(parser *).head
  }

  def getSingleWithIDOption[T](parser: RowParser[T], id: Int, idColName: String, table: String) = DB.withConnection{
    implicit connection =>
    
    val sql = SQL(s"""SELECT * FROM $table WHERE $idColName = $id""")
    val result = sql.as(parser *)
    result match {
      case r if r.isEmpty => None
      case r => Some(r.head)
    }
  }

  def getAll[T](parser: RowParser[T], table: String) = DB.withConnection{
    implicit connection =>

    try {
      SQL(s"""SELECT * FROM $table""").as(parser *)
    }
    catch {
      case e: Throwable => {
        Logger.error(e.toString)
        List()
      }
    }
  }

  def getAllOfID[T](parser: RowParser[T], id: Int, idColName: String, table: String) = DB.withConnection{
    implicit connection =>

    try {
      SQL(s"""SELECT * FROM $table WHERE $idColName = $id""").as(parser *)
    }
    catch {
      case e: Throwable => {
        Logger.error(e.toString)
        List()
      }
    }
  }

  def deleteSingle(id: Int, idColName: String, table: String) = DB.withConnection{
    implicit connection =>
    SQL(s"""
      DELETE FROM $table WHERE $idColName = $id
      """).executeUpdate() == 1
  }






}