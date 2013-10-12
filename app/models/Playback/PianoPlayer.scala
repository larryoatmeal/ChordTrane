package models

import models.ChordGenerator._

case class MidiChord(chord: Array[Int], tick: Int)
case class CompingOptions(minRange: Int, maxRange: Int, closedness: Double, rangeCurvePower: Double)




object PianoPlayer{
	
	
	val JAZZ_SWING4 = 0
	val JAZZ_BALLAD4 = 1

	
	val DefaultCompingOptions = CompingOptions(Note.getMidiNote("C", 2), Note.getMidiNote("C", 5), 5.0, 5.0)

	def getCompingMidi(chordTemplate: Array[ComperTemplate], style: Int, numberOfMeasures: Int, compingOptions: CompingOptions = DefaultCompingOptions) = {

		val compingPattern = style match {
			case JAZZ_SWING4 => JazzSwing.generateRhythmTrack(numberOfMeasures)
			case _ => JazzSwing.generateRhythmTrack(numberOfMeasures)
		}

		val filledInChordTemplate = fillInCompingWithChords(chordTemplate, compingPattern)


		def loop(remaining: Array[ComperTemplate], out: Array[MidiChord]): Array[MidiChord] = {
			if(remaining.isEmpty){
				out
			}
			else{
				val nextChord = getNextChord(out.head.chord, remaining.head.chordGenerator, compingOptions)
				val newChord = MidiChord(nextChord, remaining.head.tick)
				loop(remaining.tail, newChord +: out)
			}
		}

		val startingChord = MidiChord(firstChord(filledInChordTemplate.head.chordGenerator), filledInChordTemplate.head.tick)
		loop(filledInChordTemplate.tail, Array(startingChord)).reverse

	}
	

	
	def getNextChord(prevChord: Array[Int], wantedChordType: ChordGenerator, compingOptions: CompingOptions) = {
		wantedChordType match {
			case s: SeventhGenerator => {
				val chords = s.all
				//ChordMatcher.nextChord(prevChord, chordOptions)
				val chordOptions = ChordMatcher.chordOptions(prevChord, chords)

				//Analyze position of preceding chord. Determine what to do
				val min = compingOptions.minRange
				val max = compingOptions.maxRange
				val center = (min + max) / 2
				val deviation = max - center

				//get Chord from list of chords with certain variance determined by probaility
				def randomizedVarianceChord(chords: Array[ChordOption]) = {
					def f = Helper.randomInRange(0, chords.length, compingOptions.closedness)
					val index = f(Helper.randomOneToZero)
					val sortedByVariance = chords.sortWith(_.variance < _.variance)
					sortedByVariance(index).chord
				}

				//probability you need to shift chord to keep in comfortable range
				def shiftNecessary(chord: Array[Int]) = {
					def shiftRangeProbability = Helper.powerProbabilityCurve(center, deviation, compingOptions.rangeCurvePower)
					val lowestNoteOfChord = chord.head
					val shiftProbability = shiftRangeProbability(lowestNoteOfChord)
					Helper.rollDice(shiftProbability)
				}

				//Mean of previous chord
				val meanPrevChord = ChordMatcher.chordMean(prevChord)

				if(shiftNecessary(prevChord)){//If in uncomfortable range
					if(prevChord.head > center){//If lowest not of chord is greater above center
						//Either choose a higher chord, or jump an octave
						//Choose only chord options that are higher than previous
						if(Helper.rollDice(0.5)){
							val higherChords = chordOptions.filter(_.mean > meanPrevChord)
							randomizedVarianceChord(higherChords)
						}
						//Find closest chord then jump an octave
						else{
							chordOptions.minBy(_.variance).chord.map(_ - 12)
						}
					}
					else{//Too low
						if(Helper.rollDice(0.5)){
							val lowerChords = chordOptions.filter(_.mean < meanPrevChord)
							randomizedVarianceChord(lowerChords)
						}
						else{
							chordOptions.minBy(_.variance).chord.map(_ + 12)
						}
					}
				}else{
					randomizedVarianceChord(chordOptions)
				}
			}
		}
	}


	def firstChord(chordType: ChordGenerator, compingOptions: CompingOptions = DefaultCompingOptions) = {
		chordType match {
			case s: SeventhGenerator => {
				val chord = Helper.getRandomFromArray[Array[Int]](s.drop2s)
				val desiredHeight = Helper.getRandomWithinRange(compingOptions.minRange, compingOptions.maxRange)
				val octaveAdjust = Note.octaveAdjust(chord.head, desiredHeight)//compare to lowest note
				chord.map(_ + octaveAdjust)
			}
		}
	}


	//Connect chords with comping pattern
	def fillInCompingWithChords(template: Array[ComperTemplate], fill: Array[Int]) = {
		def loop(template: Array[ComperTemplate], fill: Array[Int], out: Array[ComperTemplate]): Array[ComperTemplate] = {

			if(template.isEmpty){
				out
			}
			else if(template.size == 1){//last chord
				val currentChord = template(0).chordGenerator
				val lastMeasure = fill.map(ComperTemplate(currentChord, _))
				loop(Array(), Array(), out ++ lastMeasure)

			}
			else{
				val currentChord = template(0).chordGenerator
				val nextTick = template(1).tick

				val (take, leave) = fill.partition(_ < nextTick)

				val newOuts = take.map(ComperTemplate(currentChord, _))

				loop(template.tail, leave, out ++ newOuts)

			}
		}
		loop(template, fill, Array[ComperTemplate]())
	}


	
	
	//Convert condensed integer into Array of beats
	def separateRhythms(rhythmBar: Int, ticks: Int) = {
		(0 until ticks).filter{
			tick => {
				val beatPosition = ticks - tick - 1 //0 => 2^7
				((rhythmBar >> beatPosition) & 1) == 1
			}
		}.toArray
	}

	//Get stream of ticks from Array of condensed integers
	def getRhythmStream(rhythms: Array[Int], ticks: Int) = {
		rhythms.zipWithIndex.flatMap{
			case (rhythmBar, index) => {
				separateRhythms(rhythmBar, ticks).map(_ + index * ticks)
			}
		}
	}
	
	def printRhythmStream(rhythmStream: Array[Int], timeSig: Int, subdivisions: Int) = {
		val ticks = timeSig * subdivisions
		val numberOfMeasures = rhythmStream.last / ticks + 1

		def loop(measureNumber: Int, feed: Array[Int], output: Array[Array[Int]]): Array[Array[Int]] = {
			if (measureNumber > numberOfMeasures) output
			else{
				val (take, leave) = feed.partition(rhythm => rhythm/ticks == (measureNumber - 1))
				loop(measureNumber + 1, leave, output :+ take)
			}
		}

		val measures = loop(1, rhythmStream, Array[Array[Int]]())

		measures.map{
			measure => {
				(0 until ticks).map{tick => 
					if(measure.exists( _ % ticks == tick)){"x"}
					else{"-"}
				}.mkString("","","")
			}
		}.mkString("","\n","")
	}

}



//Deprecated
//Pattern -
/*
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

	def printRhythms(rhythms: Array[Int], ticks: Int, subdivisions: Int) = {
		rhythms.map(rhythm => printRhythm(intToRhythmPattern(rhythm, ticks), subdivisions)).mkString("","\n", "")
	}


	def intToRhythmPattern(n: Int, ticks: Int) = {
		(0 until ticks).toArray.map{
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

	//Convert 17 to integer representing rhythm with note on beat 1 and beat 3
	def beatNumbersToInt(number: Int, ticks: Int) = {
		val numberString = number.toString
		val beats = numberString.map(ticks - _.asDigit).toArray //"1" would become 2^7 for 8 divisions

		beats.foldLeft(0){
			(acc, beat) => {
				acc + (1 << beat)
			}
		}
	}
	*/