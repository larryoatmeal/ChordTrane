// package models





// case class ChordType(third: Int, fifth: Int, seventh: Some[Int], extensions: List[Int])


// case class TriadSet(third: Int, fifth: Int)
// case class TriadConcrete(root: Int, third: Int, fifth: Int)
// case class TriadTheoretical(root: Int, triadSet: TriadSet)



// import Interval._

// object ChordCreator{

//   //TriadSets
//   val Major = TriadSet(M3, P5)



//   //Inversion constants
//   val Root = 0
//   val First = 1
//   val Second = 2
//   val Third = 3

//   //Number of notes, closed/open, 
//   def createTriad(root: Int, triadSet: TriadSet, inversion: Int, closed: Boolean) = {
//     TriadConcrete(root, root + triadSet.third, root + triadSet.fifth)
//   }

//   def simpleMajor(root: Int) = createTriad(root, Major, Root, closed = true)



// }




// object ChordTheory{
//   //Find best match
//   def closestTriad(initial: TriadConcrete, destination: TriadTheoretical){
        

//   }
// }



// abstract class ChordType{
//   def chordTones: Set[Int]
//   override def toString = {
//     chordTones.mkString("Notes:", "," ,"")
//   }




// }


// class Triad(third: Int, fifth: Int) extends ChordType{
//   def chordTones = Set(third, fifth)


//   def rootPositionClosed = Set(0, third, fifth)



// }

// // class Seventh(_third: Int, _fifth: Int, _seventh: Int) extends ChordType{
// //   def chordTones = Set(_third, _fifth, _seventh)

// //   def third = _third
// //   def fifth = _fifth
// //   def seventh = _seventh
// // }




// //Models the actual chord being played
// class Chord(root: Int, chordType: ChordType){

//   chordType match {
//     case triad: Triad => {


//     }




//   }

// }


// class Seventh(third: Int, fifth: Int, seventh: Int) extends Triad{
//   def chordTones = Set(third, fifth, seventh)
// }






//case class ChordSet(third: Int, fifth: Int, seventh: Option[Int], extensions: List[Int])







// object ChordTheory{

//   import Interval._
//   //Chord mappings
//   val Major = ChordSet(M3, P5 ,None, Nil)
//   val Minor = ChordSet(m3, P5, None, Nil)
//   val Augmented = ChordSet(M3, A5, None, Nil)
//   val Diminished = ChordSet(m3, d5, None, Nil)
//   val Maj7 = ChordSet(M3, P5, M7, Nil)
//   val Min7 = ChordSet(m3, P5, m7, Nil)
//   val Dom7 = ChordSet(M3, P5, m7, Nil)
//   val Dim7 = ChordSet(m3, d5, d7, Nil)
//   val Hdim7 = ChordSet(m3, d5, m7, Nil)


//   def chordTones(root: Int, chordSet: Set[Int]) = {
    
//     chordSet.map(
//       interval => root + interval
//     ) + root


//   }

// }

// class Chord(root: Int, chordSet: Set[Int]){

//   //val noteSet = chordTones(root, chordSet)


// }










