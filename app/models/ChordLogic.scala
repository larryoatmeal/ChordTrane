package models
import scala.util.matching.Regex
import models.ChordGenerator._






object ChordLogic{

	//kind ids
	val Maj = 1
	val Min = 2
	val Aug = 3
	val Dim = 4
	val Maj7 = 5
	val Min7 = 6
	val Dom7 = 7
	val Dim7 = 8
	val Hdim7 = 9
	val Maj9 = 10
	val Min9 = 11
	val Dom9 = 12
	val Maj11 = 13
	val Min11 = 14
	val Dom11 = 15
	val Maj13 = 16
	val Min13 = 17
	val Dom13 = 18
	val Maj6 = 19
	val Maj69 = 20
  val Min6 = 21
	val Sus2 = 22
	val Sus4 = 23
	val Sus7 = 24
  val Other = -1

  //Strings that correspond to each kind
  val kindDictionary = Array(
      ("M", Maj) , ("Maj", Maj) ,
      ("m", Min) , ("min", Min) , ("-", Min),
      ("aug", Aug), ("+", Aug),
      ("dim", Dim),
      ("Maj7", Maj7), ("M7", Maj7), ("maj7", Maj7),
      ("m7", Min7) , ("min7", Min7) , ("-7", Min7),
      ("7", Dom7), 
      ("dim7", Dim7), ("d7", Dim7),
      ("hdim7", Hdim7), ("m7b5", Hdim7),
      ("Maj9", Maj9), ("M9", Maj9), ("maj9", Maj9),
      ("m9", Min9) , ("min9", Min9) , ("-9", Min9),
      ("9", Dom9),
      ("Maj11", Maj11), ("M11", Maj11), ("maj11", Maj11),
      ("m11", Min11) , ("min11", Min11) , ("-11", Min11),
      ("11", Dom11),
      ("Maj13", Maj13), ("M13", Maj13), ("maj13", Maj13),
      ("m13", Min13) , ("min13", Min13) , ("-13", Min13),
      ("13", Dom13),
      ("M6", Maj6), ("Maj6", Maj6),
      ("69", Maj69),
      ("m6", Min6), ("-6", Min6), ("min6", Min6),
      ("sus2", Sus2),
      ("sus", Sus4), ("sus4", Sus4),
      ("sus7", Sus7)
  )

  //val kindMap = kindDictionary.toMap
  //Map generator to kind

  def extensionInterval(step: String) = {
		step match {
			case "1" => 0
			case "b2" | "#1" => 1
			case "2" => 2
			case "b3" | "#2" => 3
			case "3" | "b4"=> 4
			case "4" => 5
			case "#4" | "b5" =>  6
			case "5" => 7
			case "b6" | "#5" => 8
			case "6" => 9
			case "b7" | "#6" => 10
			case "7" => 11
			case "b9" => 13
			case "9" => 14
			case "#9" => 15
			case "b11" => 16
			case "11" => 17
			case "#11" => 18
			case "b13" => 20
			case "13" => 21
			case "#13" => 22
      case _ => 0
		}
	}



	def getChordGenerator(root: String, suffix: String) = {
		val (kindString, kindId) = suffix match {
			case "" => ("", Maj) //for major chords
			case s => {
				val matches = kindDictionary.filter{//matches(string, kindId)
          case (kind, id) => {
            //Make sure to escape all literals
            val kindPattern = new Regex("""^\Q""" + kind + """\E""")
            !(kindPattern findFirstIn s).isEmpty
          }
        }
        if (matches.isEmpty){
        	("", Other)
        }else{
        	matches.maxBy(_._2)
        }
			}
		}

		def getExtensions(extensionString: String) = {
			val extensionPattern = new Regex("""[b#]?[123456789][123456789]?""")
      val extensionStrings = extensionPattern findAllIn extensionString
      val extensions = extensionStrings.toList.map(extensionInterval(_))

      if (extensions.isEmpty){
      	None
      }else{
      	Some(extensions)
      }
		}

		val extensions = kindId match {
			case Other => None //If kind other, don't consider extensions
			case k => {
				val extensionString = suffix.drop(kindString.length)//extensions by themself
				getExtensions(extensionString)
			}
		}

		//println(kindId + ": " + kindString)
		//println(extensions)


		kindId match {
			case Maj| Maj7 | Maj9 | Maj11 | Maj13 | Maj69 => new SeventhGenerator(root, SeventhGenerator.Maj7)
			case Min| Min7 | Min9 | Min11 | Min13 => new SeventhGenerator(root, SeventhGenerator.Min7)
			case Dom7 | Dom9 | Dom11 | Dom13 => new SeventhGenerator(root, SeventhGenerator.Dom7)
			case Hdim7 => new SeventhGenerator(root, SeventhGenerator.Hdim7)
			case Dim| Dim7 => new SeventhGenerator(root, SeventhGenerator.Dim7)
			case Maj6 => new SeventhGenerator(root, SeventhGenerator.Maj6)
			case Min6 => new SeventhGenerator(root, SeventhGenerator.Min6)
			case _ => new SeventhGenerator(root, SeventhGenerator.Maj7)
		}

		

	}
	



}



//class ChordLogic(chord: Chord) = {
//
//
//
//
//
//
//
//
//
//}

