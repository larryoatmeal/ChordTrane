package models


case class WeightedEntry(rhythm: Int, count: Int)
case class RouletteEntry(rhythm: Int, roulette: Int)

trait RhythmStyle{
	import scala.io.Source
	import scala.annotation.tailrec
	import scala.util.Random

	val filePath: String

	val rawFile = try{
		Some(Source.fromFile(filePath))
	}catch{
		case _: Throwable => None
	}

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
		val randomGenerator = new Random()
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
				val rhythmDisplay = Comper.printRhythm(Comper.intToRhythmPattern(category), 2)
				acc + s"$rhythmDisplay: $percent\n"
			}
		}
	}
}

object jazzSwing extends RhythmStyle{
	val filePath = "jazzSwing.txt"
}
