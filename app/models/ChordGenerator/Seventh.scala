package models.ChordGenerator

case class SeventhIntervals(third: Int, fifth: Int, seventh: Int)


object SeventhGenerator{

  //These are intervals
  import models.Interval._

  val Maj7 = SeventhIntervals(M3, P5, M7)
  val Min7 = SeventhIntervals(m3, P5, m7)
  val Dom7 = SeventhIntervals(M3, P5, m7)
  val Dim7 = SeventhIntervals(m3, d5, d7)
  val Hdim7 = SeventhIntervals(m3, d5, m7)

  //Inversions
  val Root = 0
  val First = 1
  val Second = 2
  val Third = 3

  val Inversions = Array(Root, First, Second, Third)
 
}

class SeventhGenerator(rootName:String, intervals: SeventhIntervals){

  import SeventhGenerator._
  import models.Helper._

  val root = models.Note.midify(rootName)

  val closedRoot = Array(root, 
    root+intervals.third,
    root+intervals.fifth,
    root+intervals.seventh)

  val closedInversions = Inversions.map(inversion => closedInversion(inversion))


  def closedInversion(inversion: Int) = {
    val (bottom, top) = closedRoot.splitAt(inversion)
    //transpose botom notes octave up
    top ++ bottom.map(_+12)
  }

  def drop2(inversion: Int) = {
    //Find closed inversion
    //Drop second highest note
    val closed = closedInversions(mod((inversion + 2),4))
    Array(closed(2) - 12, closed(0), closed(1), closed(3))
  }
  def drop3(inversion: Int) = {
    //Find closed inversion
    //Drop third highest note
    val closed = closedInversions(mod((inversion + 3),4))
    Array(closed(1) - 12, closed(0), closed(2), closed(3))
  }

  lazy val drop3s = Inversions.map(inversion => drop3(inversion))
  lazy val drop2s = Inversions.map(inversion => drop2(inversion))
}
