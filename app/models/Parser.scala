package models

import scala.util.matching.Regex
import play.api.Logger

case class Chord(root: String, suffix: String, bass: Option[String], beat: Int)
//case class MeasureString(measureString: String)

case class Measure(chords: List[Chord])
case class Element(text: String, element_type: Int)
case class Text(text: String)

abstract class Line
case class MusicLine(measures: List[Measure]) extends Line
case class TextLine(text: String) extends Line
case class EmptyLine() extends Line

case class AllLines(lines: List[Line])

object MusicMatch{
	def apply(measureStrings: List[String]) = measureStrings.mkString("|","|","|") 

	def unapply(input: String) = {
		val MusicbarPattern = new Regex("""(?<=\|)[^\n]*?(?=\|)""")
		val musicbars = MusicbarPattern.findAllIn(input).toList
		if (musicbars.isEmpty){
			None
		}else{
			Some(musicbars)
		}
	}
}

object TextMatch{
	def apply(text: String) = s"($text)"
	def unapply(input: String) = {
  	val TextPattern = new Regex("""(?<=\()[^\n]*?(?=\))""")
		TextPattern.findFirstIn(input)
	}
}

class ChordMatch(timeSig: Int){//Behaviour differs based on time signature
  def apply(chords: List[Chord]) = {
    def chordToString(chord: Chord) = {
      chord.root + chord.suffix + chord.bass.map(b => "/" + b).getOrElse("") + "." + chord.beat
    }
    chords.map(chord => chordToString(chord)).mkString("", " ", "")
  }

	def unapply(measureString: String): Option[List[Chord]] = {
		val potentialChords = measureString.split(""" +""")
		val validChordPattern = new Regex("""([a-gA-G][#b]?[#b]?)([^\n]*)""", "root", "appendix")

		val chordMatches = potentialChords.flatMap{
			c => validChordPattern findFirstMatchIn c
		}

		val chords = chordMatches.zipWithIndex.map{ case (chord, index) => 

			val root = chord.group("root")
			val appendix = chord.group("appendix")//everything after root

      val bassIndex = appendix.indexOf("/")
      val beatIndex = appendix.indexOf(".")

      def verifyBeat(s: String): Option[Int] = {
        Helper.stringToInt(s)
      }
      def verifyBass(s: String): Option[String] = {
        val bassPattern = new Regex("""[a-gA-G][#b]?[#b]?""")
        bassPattern.findFirstIn(s)
      }

      val (suffix: String, bass: Option[String], beat: Option[Int]) = (bassIndex, beatIndex) match {
        case (-1, -1) => (appendix, None, None) //No bass or beat modifier
        case (bassI, -1) => {
          val bass = verifyBass(appendix.substring(bassI+1, appendix.length))
          val suffix = appendix.substring(0, bassI)
          (suffix, bass, None)
        }
        case (-1, beatI) => {
          val beat = verifyBeat(appendix.substring(beatI+1, appendix.length))//returns Some(integer) if integer, None if not
          val suffix = appendix.substring(0, beatI)
          (suffix, None, beat)
        }
        case (bassI, beatI) => {
          val suffix = appendix.substring(0, bassI)
          val bass = verifyBass(appendix.substring(bassI+1, beatI))
          val beat = verifyBeat(appendix.substring(beatI+1, appendix.length))
          (suffix, bass, beat)
        }
      }

      //If no explicit beat specified, get default beat
      def defaultBeat(beat: Option[Int]): Int = {
        beat match {
          case Some(beat) => beat
          case None => {
            chordMatches.length match {
              case 1 => index match {
                case 0 => 1        
              }
              case 2 => index match {
                case 0 => 1          
                case 1 => timeSig match{
                  case 2 => 2 //In 2/2 bar, 2nd chord or second beat
                  case n => 3 //In any other bar, 2nd chord on third beat
                }          
              }
              case 3 => index match {
                case 0 => 1
                case 1 => timeSig match{
                  case 3 => 2 //3/4
                  case n => 3
                }
                case 2 => timeSig match{
                  case 3 => 3
                  case n => 4
                }
              }
              case 4 => index match {
                case 0 => 1
                case 1 => 2
                case 2 => 3
                case 3 => 4
              }
              case n => index + 1 //If more than 5 chords per measure, have each chord be one beat 
            }
          }
        } 
      }
      Chord(root, suffix, bass, defaultBeat(beat))
		}
    Some(chords.toList)
	}
}

object Parser{
  //Element types
  val Musicbar = 1
  val Text = 2
  val Keysig = 3

  def splitIntoLines(raw: String) ={
    raw.split("""\r?\n""").toList
  }

  def parse(raw: String, timeSig: Int) = {
    def parseLine(line: String): Line = {
      line match {
        case TextMatch(text) => {
          TextLine(text)
        }
        case MusicMatch(measureStrings) => {
          MusicLine(measureStrings.map(parseMeasure(_)))
        }
        case _ => {
          EmptyLine()
        }

      }
    }
    def parseMeasure(measureString: String) = {
      val c = new ChordMatch(timeSig)
      measureString match {
        case c(chords) => {
          Measure(chords)
        }
      }
    }

		val lines = splitIntoLines(raw) //split into lines of text
    println("LINES" + lines.toString)

    AllLines(lines.map(parseLine(_)))

    
  }
}


















//DEPRECATED **********************************************************************



  // //Recursively parses elements and adds them to a list
  // def getElements(input: String, listOfElements: List[Element]): List[Element] = {
  //   //First determine which element is next and what index it is at
  //   val (index, element_type) = {
  //     val parensIndex = (input.indexOf('('), Text)
  //     val pipeIndex = (input.indexOf('|'), Musicbar)
  //     val angleIndex = (input.indexOf('<'), Keysig)

  //     //Get index and element_type with lowest index
  //     List(parensIndex, pipeIndex, angleIndex).foldLeft((input.length-1, -1)){
  //       (minimum, current) => {
  //         if (current._1 == -1){//If index not found
  //           minimum
  //         }else if (current._1 < minimum._1) {
  //           current
  //         }
  //         else {
  //           minimum
  //         }
  //       } 
  //     }
  //   }

  //   //Logger.debug(s"Step 1: Index = $index Element = $element_type")

  //   //Remove anything before index where element begins (makes things easier)
  //   val input2 = input.drop(index)

  //   //Logger.debug(s"Input 2: $input2")

  //   //Parse appropriate bar
  //   element_type match {
  //     case Musicbar => {
  //       //Logger.debug("Musicbar:")
  //       MusicbarPattern findFirstIn input2 match {
  //         case Some(found) => {
  //           //store element, remove from input
  //           //tail.indexOf gives you second occurence as | must be at the beginning of the string
  //           val newInput = input2.drop(input2.tail.indexOf('|')+1) //Must compensate for +1 because tail reduces original string length by 1
  //           val newElement = Element(found, Musicbar)
  //           getElements(newInput, newElement :: listOfElements)
  //         } 
  //         case None => listOfElements.reverse
  //       }
  //     }
  //     case Text => {
  //       //Logger.debug("Musicbar:")
  //       TextPattern findFirstIn input2 match {
  //         case Some(found) => {
  //           val newInput = input2.drop(input2.indexOf(')')+1) //Don't keep )
  //           val newElement = Element(found, Text)
  //           getElements(newInput, newElement :: listOfElements)
  //         } 
  //         case None => {
  //           listOfElements.reverse
  //         }
  //       }
  //     }
  //     // case Keysig => {
  //     //   //Logger.debug("Musicbar:")
  //     //   KeysigPattern findFirstIn input2 match {
  //     //     case Some(found) => {
  //     //       //store element, remove from input
  //     //       //tail.indexOf gives you second occurence as | must be at the beginning of the string
  //     //       val newInput = input2.drop(input2.indexOf('>')+1) //don't keep > 
  //     //       val newElement = Element(found, Keysig) //don't store | in element
  //     //       getElements(newInput, newElement :: listOfElements)
  //     //     } 
  //     //     case None => listOfElements.reverse
  //     //   }
  //     // }
  //     case _ => {
  //       listOfElements.reverse
  //     }
  //   }  
  // }

  // //Recursively parse chords








  // val sample = """
  // (hello) | a | b | c | 
  // <e>| d | f | d |
  // | d | s | f |
  // | a | b|
  // | d |
  // | d |
  // """

  // //Time signature bar
  // //Key sig bar

  // //| (Comment) |
  // //| <4/4> |
  // //| [1] |






  // val barPattern = new Regex("""(?<=\|)[^\n]*?(?=\|)""")

  // def getBars(input: String) = {

  //   /*
  //   Ex: | a | b | c | d
  //   a, b, c will be taken, but not d
  //   */
  //   barPattern.findAllIn(input)
  // }

