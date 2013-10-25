package models

case class BassSettings(lower: Int, upper: Int, stayInTessitura: Double, connectivity: Double)
case class SingleNote(note: Int, tick: Int)


object BassPlayer{

	val DefaultProperties = BassSettings(Note.getMidiNote("G", 1), Note.getMidiNote("G", 3), 3, 2)
	val DefaultCenter = (DefaultProperties.lower + DefaultProperties.upper) / 2
	val DefaultDeviation = DefaultProperties.upper - DefaultCenter


	

	def bassRangeProbability = Helper.powerProbabilityCurve(DefaultCenter, DefaultDeviation, DefaultProperties.stayInTessitura)
	def octaveShiftNecessary(note: Int) = Helper.rollDice(bassRangeProbability(note))
	
	

	def bassSkeleton(chordTemplate: Array[ComperTemplate]) = {

		def loop(chordQueue: Array[ComperTemplate], out: Array[SingleNote]): Array[SingleNote] = {

			if(chordQueue.isEmpty){
				out
			}else{

				val previousNote = out.last.note

				val nextChordRoot = chordQueue.head.chordGenerator.rootMidi

				val nextNote = nextBassRoot(previousNote, nextChordRoot)

				println(nextNote)

				val finalOut = SingleNote(nextNote, chordQueue.head.tick)//keep same tick

				loop(chordQueue.tail, out :+ finalOut)

			}
		}

		val firstChord = chordTemplate.head

		val firstNote = SingleNote(initialNote(firstChord.chordGenerator.rootMidi), firstChord.tick)
		
		loop(chordTemplate, Array[SingleNote](firstNote))
	}

	def initialNote(note: Int) = {//Get first note
		val desiredHeight = Helper.getRandomWithinRange(DefaultProperties.lower, DefaultProperties.upper)
		val octaveAdjust = Note.octaveAdjust(note, desiredHeight)

		note + octaveAdjust
	}




	def nextBassRoot(previousRoot: Int, desiredRoot: Int) = {
		val closestInterval = Note.closestInterval(previousRoot, desiredRoot)

		val interval = closestInterval match {
			case 5 => {//Perfect 4th up
				if(Helper.rollDice(0.5)){//50% of time descend 5th instead of ascend 4th
					-Interval.P5
				}
				else{
					Interval.P4
				}
			}

			case -5 => {//Perfect 4th down
				if(Helper.rollDice(0.5)){//50% of time ascend 5th instead of descend 4th
					Interval.P5
				}
				else{
					-Interval.P4
				}
			}
			case 6 | -6 => {//Tritone
				if(Helper.rollDice(0.5)){//50% up, 50% down
					Interval.A4
				}
				else{
					-Interval.A4
				}
			}

			case i => i
		}


		val potentialNextNote = previousRoot + interval



		//If note out of range, make it in range. Based on probability
		if(potentialNextNote > DefaultCenter){//If higher than center
			if(octaveShiftNecessary(potentialNextNote)){
				potentialNextNote - 12
			}else{
				potentialNextNote
			}
		}
		else{//If lower than center
			if(octaveShiftNecessary(potentialNextNote)){
				potentialNextNote + 12
			}else{
				potentialNextNote
			}
		}
	}











}