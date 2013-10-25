package models


case class WeightedEntry(rhythm: Int, count: Int)
case class RouletteEntry(rhythm: Int, roulette: Int)

object RhythmStyleDatabase{

}


trait RhythmStyle{
	import scala.io.Source
	import scala.annotation.tailrec
	import scala.util.Random
	import scala.util.Try
	import scala.util.Success

	def filePath: String
	def subdivisions: Int
	def timeSig: Int
	val ticks = subdivisions * timeSig


	//Read file---------------------------------------------------------------
	val rhythmDatabase = try{
		val text = Source.fromFile(filePath).mkString
		val takes = text.split("""\*""").toArray
		val takesWithRhythms = takes.map{
			take => {
				val lines = take.split("""\r?\n""").toArray
				val rhythmStrings = lines.flatMap(_.split(" "))
				val triedRhythms = rhythmStrings.map(s => Try(beatNumbersToInt(s.toInt, ticks)))
				triedRhythms.collect{case Success(n) => n}
			}
		}
		Some(takesWithRhythms)
	}catch{
		case e: Throwable => println(e); None
	}

	//Convert 17 to condensed integer representing rhythm with note on beat 1 and beat 3
	def beatNumbersToInt(number: Int, ticks: Int) = {
		val numberString = number.toString
		val beats = numberString.map(ticks - _.asDigit).toArray //"1" would become 2^7 for 8 divisions

		beats.foldLeft(0){
			(acc, beat) => {
				acc + (1 << beat)
			}
		}
	}

	val usable = !rhythmDatabase.isEmpty


	//Generate Rhythm database------------------------------------------------------------
	val randomGenerator = new Random

	val allRhythms: Array[Int] = rhythmDatabase match {
		case Some(r) => r.flatten
		case None => Array[Int]()
	}

	val numberOfRhythms = allRhythms.length

	val rhythmConnectionMap: Map[Int, Array[Int]] = rhythmDatabase match {
		case Some(r) => {
			val connections = r.flatMap{
				take => createConnection(take.toList)	
			}

			val uniqueRhythms = allRhythms.toSet
			val tuples = uniqueRhythms.map{
				rhythm => {
					// val followingRhythms = connections.filter(rhythm == _._1)
					// if (followingRhythms.isEmpty){
					// 	Tuple2(rhythm, Array[Int]())
					// }else{
					// 	Tuple2(rhythm, followingRhythms.map(_._2).toArray)
					// }
					Tuple2(rhythm, connections.filter(rhythm == _._1).map(_._2).toArray)
				}
			}
			tuples.toMap

		}
		case None => Map[Int, Array[Int]]()
	}
		
	def createConnection(l: List[Int]) = lookBehind[Int, Tuple2[Int, Int]](l, (current: Int, prev: Int) => Tuple2(current, prev))


	def lookBehind[T, U](l: List[T], f: (T, T) => U): List[U] = {
		def loop(unread: List[T], read: List[T], output: List[U]): List[U] = {
			if (unread.isEmpty) {output}
			else {
				val newValue = f(read.head, unread.head)
				loop(unread.tail, unread.head :: read, newValue :: output)
			}
		}
		loop(l.tail, List(l.head), List()).reverse
	}
	
	
	//Generate rhythms ---------------------------------------------------------------------
	def randomRhythm = {
		val randomIndex = randomGenerator.nextInt(numberOfRhythms)
		allRhythms(randomIndex)
	}

	def getNextRhythm(prev: Int, useConnection: Double, useDatabase: Double) = {
		if(Helper.rollDice(useConnection)){
			val options = rhythmConnectionMap.getOrElse(prev, Array[Int]())
			if (options.isEmpty){
				randomRhythm
			}else{
				Helper.getRandomFromArray[Int](options)
			}
		}else{
			if(Helper.rollDice(useDatabase)){
				randomRhythm
			}else{
				randomGenerator.nextInt((1 << ticks) - 1) //generate completely random rhythm
			}
		}
	}
	
	def generateRhythmTrack(numberOfMeasures: Int, useConnection: Double = 0.9, useDatabase: Double = 0.9) = {
		val firstRhythm = randomRhythm
		
		def loop(remainingMeasures: Int, output: Array[Int]): Array[Int] = {
			if (remainingMeasures == 0) output
			else{
				val newRhythm = getNextRhythm(output.head, useConnection, useDatabase)
				loop(remainingMeasures-1, newRhythm +: output)
			}
		}

		val rhythms = loop(numberOfMeasures-1, Array(firstRhythm)).reverse
		PianoPlayer.getRhythmStream(rhythms, ticks)
	}

	def test = {
		PianoPlayer.printRhythmStream(generateRhythmTrack(32), timeSig, subdivisions)
	}

}

object JazzSwing extends RhythmStyle{
	def filePath = "public/RhythmStyles/JazzSwing.txt"
	def subdivisions = 2
	def timeSig = 4
}



//Deprecated
/*
val rhythmEntries = List(WeightedEntry(78, 2), WeightedEntry(34, 5), WeightedEntry(22, 3))

	def toRoulette(weightedEntries: List[WeightedEntry]) = {
		
		val rhythms = weightedEntries.map(_.rhythm)
		val counts = weightedEntries.map(_.count)

		@tailrec def loop(weights: List[Int], rouletteValues: List[Int]): List[Int] = {
			if (weights.isEmpty) rouletteValues
			else{
				val nextRoulette = weights.head + rouletteValues.head
				loop(weights.tail, nextRoulette :: rouletteValues)
			}
		}

		val rouletteValues = loop(counts.tail, List(counts.head)).reverse//pass in head unaffected. Must reverse

		rhythms.zip(rouletteValues).map(t => RouletteEntry(t._1, t._2))
	}

	val rouletteRhythms = toRoulette(rhythmEntries)

	def rollRoulette(rouletteWheel: List[RouletteEntry]) = {
		val maxRoulette = rouletteWheel.last.roulette
		val rollNumber = randomGenerator.nextInt(maxRoulette)
		//println(rollNumber)

		def loop(remaining: List[RouletteEntry]): Int = {
			//println(remaining.head.roulette)
			if (rollNumber < remaining.head.roulette) remaining.head.rhythm
			else{
				loop(remaining.tail)
			}
		}

		loop(rouletteWheel)

	}

	
	def getRhythmRandom = rollRoulette(rouletteRhythms)




	def statTest(numberOfTests: Int) = {
		val results = (0 until numberOfTests).map(_ => getRhythmRandom)
		val categories = results.toSet

		categories.foldLeft("\n"){
			(acc, category) => {
				val percent = results.count(_ == category) / numberOfTests.toDouble
				val rhythmDisplay = PianoPlayer.printRhythm(PianoPlayer.intToRhythmPattern(category), 2)
				acc + s"$rhythmDisplay: $percent\n"
			}
		}
	}
	*/