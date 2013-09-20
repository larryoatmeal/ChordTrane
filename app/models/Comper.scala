package models

object Comper{
	
	//Pattern -
	def generateBasicRhythm(measures: List[Measure], timeSig: Int) = {
		measures.foldLeft(Array[Array[Boolean]]()){
			(acc, measure) => {
				def beatToEightBeat(beat: Int) = {
					beat * 2 - 1
				}
				val chordBeats = measure.chords.map(chord => beatToEightBeat(chord.beat))
				val beatMap: Array[Boolean] = (1 to timeSig * 2).foldLeft(Array[Boolean]()){//Eight notes
					(acc, eightBeat) => {
						acc :+  chordBeats.contains(eightBeat) //is there a chord on this beat?
					}
				}
				acc :+ beatMap
			}
		}
	}

	def printRhythm(rhythmPattern: Array[Boolean], subdivisions: Int) = {
		rhythmPattern.zipWithIndex.foldLeft(""){
			case (acc, (isBeat, tick)) => {
				val symbol = tick match {
					case t if {t % subdivisions == 0} => {//Separate beats with period
						"/" + {if(isBeat)("X")else{"-"}}
					}
					case _ => {
						if(isBeat)("X")else{"-"}
					}
				}
				acc + symbol
			}
		}
	}

	def printRhythms(rhythmMap: Array[Array[Boolean]], subdivisions: Int) = {
		rhythmMap.map(printRhythm(_, subdivisions)).mkString("","\n", "")
	}

	def intToRhythmPattern(n: Int, subdivisions: Int = 2, timeSig: Int = 4) = {
		(0 until timeSig * subdivisions).toArray.map{
			tick => ((n >> tick) & 1) == 1//shift bit right, test if equal to 1. 
		}.reverse //1st tick is last tick, so must reverse
	}

	def rhythmPatternToInt(rhythmMap: Array[Boolean]) = {
		rhythmMap.reverse.zipWithIndex.foldLeft(0){//must reverse so largest index corresponds to last beat
			case (acc, (isBeat, index)) => {
				val value = if(isBeat){1 << index}else{0}
				acc + value
			}
		}
	}

}