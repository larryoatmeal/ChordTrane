package models





object AnormCodeGenerator{

	val String = "String"
	val Boolean = "Boolean"
	val Int = "Int"
	val Float = "Float"
	val Double = "Double"
	val DateTime = "DateTime"
	val Unknown = "UNKOWN"


	val TripleQuote = """*"""""  //*""

	val MysqlDataToScalaMap = Map(
		"varchar" -> "String",
		"text" -> "String",
		"int" -> "Int",
		"boolean" -> "Boolean",
		"datetime" -> "DateTime"
	)

	val DemoMysqlString = """
	|CREATE TABLE songs (
	|id 					int           	NOT NULL AUTO_INCREMENT,
	|rawText				TEXT			,
	|title				varchar(127)	NOT NULL,
	|composer			varchar(127)	,
	|dateCreated			varchar(127)	,
	|timeSig				int             NOT NULL,
	|userId				int             NOT NULL,
	|currentKey			varchar(127)	NOT NULL,
	|destinationKey		varchar(127)	NOT NULL,
	|transposeOn			boolean 		NOT NULL,
	|romanNumeral		boolean			NOT NULL,

	|PRIMARY KEY(id)
	|);
	""".stripMargin


	case class ColumnDefinition(name: String, datatype: String, notNull: Boolean, autoIncrement: Boolean = false)

	case class TableDefinition(tableName: String, columns: Array[ColumnDefinition])


	implicit class SuperString(val s: String){
		def splitIntoLines: Array[String] = s.split('\n').filter(!_.isEmpty)
		def splitIntoWords: Array[String] = s.replace('\t',' ').split(' ').filter(word => !word.isEmpty)
		def containsString(test: String): Boolean = s.indexOfSlice(test) != -1

		def stringInBetween(left: String, right: String, caseSensitive: Boolean = true): Option[String] = {
			val (leftCased, rightCased, sourceCased) = {
				if(caseSensitive) (left, right, s)
				else (left.toUpperCase, right.toUpperCase, s.toUpperCase)

			}

			// val (leftCased, rightCased, sourceCased) = caseSensitive match {
			// 	case true => (left, right, source)
			// 	case false => (left.toUpperCase, right.toUpperCase, source.toUpperCase)
			// }

			val leftIndex =	sourceCased.indexOfSlice(leftCased) 
			val rightIndex = sourceCased.indexOfSlice(rightCased)


			//CHECKPOINT
			if( (leftIndex == -1 || rightIndex == -1) || 
				(leftIndex >= rightIndex ) 
				){//if not in correct format, abort
				None
			}else{
				Some(s.substring(leftIndex + left.length, rightIndex).filter(_ != ' '))
			}
		}


		def tripleQuote: String = s.replace(TripleQuote, "\"\"\"")
	}


	//import models.AnormGenerator.SuperString

	def mysqlDataToScalaData(mysqlData: String) = MysqlDataToScalaMap.collectFirst({case (key, value) if mysqlData.toLowerCase.containsString(key) => value}).getOrElse(Unknown)

	def parseMysqlCreate(sql: String): TableDefinition = {
		val lines = sql.splitIntoLines


		//Test if first line CREATE TABLE _ ( format

		val firstLine = lines.head
		val tableName = firstLine.stringInBetween("CREATE TABLE", "(", caseSensitive = false)


		val columnWords = lines.tail.filter(!_.toUpperCase.containsString("PRIMARY KEY")).map(_.splitIntoWords.filter(_ != ","))

		val columnDefinitions = columnWords.map{
			c => 
			{
				c.length match {//SKETCHY
					case 5 => Some(ColumnDefinition(c(0), mysqlDataToScalaData(c(1)), true, true))//NOT NULL is two words. Auto_increment is one word
					case 4 => Some(ColumnDefinition(c(0), mysqlDataToScalaData(c(1)), true, false))
					case 2 => Some(ColumnDefinition(c(0), mysqlDataToScalaData(c(1)), false, false))
					case _ => None 
				}
			}
		}.flatten

		TableDefinition(tableName.getOrElse(Unknown), columnDefinitions)
	}


	//def columnDefinitionNamesNoAutoincrement(tableDefinition: TableDefinition) = tableDefinition.columns.collect{case c: ColumnDefinition if !c.autoIncrement=> c.name}


	def noAutoIncrement(columns: Array[ColumnDefinition]) = columns.filter(!_.autoIncrement)


	def columnDeclarations(tableDefinition: TableDefinition) = tableDefinition.columns.map(c => s"${c.name}: ${c.datatype}").mkString(", ")


	def caseClass(tableDefinition: TableDefinition, caseClassName: String) = s"""case class $caseClassName(${columnDeclarations(tableDefinition)})"""


	def create(tableDefinition: TableDefinition, scalaClassName: String, scalaParameterName: String) = {
		val columnNames = noAutoIncrement(tableDefinition.columns).map(_.name)
		val columnMappings = columnNames.map(name => s""""$name" -> $scalaParameterName.$name""")

		s"""
		|def create($scalaParameterName: $scalaClassName) = DB.withConnection{
		|	implicit connection =>
		|	SQL(*""INSERT INTO ${tableDefinition.tableName} ${columnNames.mkString("(", ", ", ")")}	
		|	VALUES (${columnNames.mkString("{", "}, {", "}")})	
		|	*"").on(
		|		${columnMappings.mkString(",\n\t\t")}
		|	).executeInsert().get.toInt
		|}
		""".stripMargin.tripleQuote
	}

	def update(tableDefinition: TableDefinition, scalaClassName: String, scalaParameterName: String) = {
		val columnNames = noAutoIncrement(tableDefinition.columns).map(_.name)//Does not include auto_increment column
		val columnEquals = columnNames.map(name => s"""$name = {$name}""")
		val idColumnName = tableDefinition.columns.collectFirst{case c if c.autoIncrement => c.name}.getOrElse(Unknown)//assuming there is only one auto_incrementing column, the id
		val columnMappings = tableDefinition.columns.map(_.name).map(name => s""""$name" -> $scalaParameterName.$name""")//Includes the auto_increment column

		s"""
		|def update($scalaParameterName: $scalaClassName) = DB.withConnection{
		|	implicit connection =>
		|	SQL(*""UPDATE ${tableDefinition.tableName}
		|	SET	
		|	${columnEquals.mkString("\n\t")}
		|	WHERE $idColumnName = {idColumnName}	
		|	*"").on(
		|		${columnMappings.mkString(",\n\t\t")}
		|	).executeUpdate() == 1
		|}
		""".stripMargin.tripleQuote
	}




	def parser(tableDefinition: TableDefinition, scalaClassName: String, parserName: String) = {

		def getFormatter(tableDefinition: TableDefinition) = tableDefinition.columns.map(c => 
			c.notNull match {
				case true => s"""get[${c.datatype}]("${c.name}")"""
				case false => s"""get[Option[${c.datatype}]]("${c.name}")"""
			}
		) 

		s"""
		|val $parserName: RowParser[$scalaClassName] = {
		|	import anorm.~
		|	${getFormatter(tableDefinition).mkString(" ~\n\t")} map {
		|		case ${tableDefinition.columns.map(_.name).mkString(" ~ ")} =>
		|		$scalaClassName(${tableDefinition.columns.map(_.name).mkString(", ")})
		|	}
		|}
		""".stripMargin.tripleQuote
	}

	def entireClass(mysqlString: String) = {
		val tableDefinition = parseMysqlCreate(mysqlString)

		val scalaClassName = tableDefinition.tableName.capitalize
		val scalaParameterName = tableDefinition.tableName.head.toLower + tableDefinition.tableName.tail

		val caseClassString = caseClass(tableDefinition, scalaClassName)
		val parserString = parser(tableDefinition, scalaClassName, s"${scalaParameterName}Parser")
		val createString = create(tableDefinition, scalaClassName, scalaParameterName)
		val updateString = create(tableDefinition, scalaClassName, scalaParameterName)


		s"""
		|package 
		|
		|import anorm._
		|import anorm.RowParser
		|import anorm.SqlParser._
		|import play.api.Play.current
		|import play.api.db.DB
		|
		|$caseClassString
		|
		|object $scalaClassName{
		|	$parserString
		|
		|	$createString
		|
		|	$updateString
		|}
		|
		""".stripMargin
		
	}














}