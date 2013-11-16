package models


case class ComperTemplate(chordGenerator: ChordGenerator.ChordGenerator, tick: Int)


object RhythmSectionCoordinator{

	//Chords with their literal positions in music
	def generateChordTemplate(measures: List[Measure], timeSig: Int, subdivisions: Int) = {
		val ticksPerMeasure = subdivisions * timeSig

		//Convert beat to linear time position
		def beatToTick(beat: Int, measureIndex: Int) = {
			(beat -1) * subdivisions + ticksPerMeasure * measureIndex
		}

		measures.zipWithIndex.flatMap{
			case (measure, index) => {
				measure.chords.map(chord => ComperTemplate(ChordLogic.getChordGenerator(chord),beatToTick(chord.beat, index)))
			}
		}.toArray
	}

	val dummyMeasures = Helper.measuresOnly(Master.parseSong(Master.DummySong))

	//val dummyRhythmSection = new RhythmSectionCoordinator(dummyMeasures, 4, 2, PianoPlayer.JAZZ_SWING4)

	


}

class RhythmSectionCoordinator(measures: List[Measure], timeSig: Int, subdivisions: Int, style: Int, pianoSettings: PianoSettings, bassSettings: BassSettings, bpm: Int, repeats:Int){

	def repeat(measures: List[Measure]) = {
		if(repeats < 2) measures
		else{
			(1 until repeats).foldLeft(measures){
				(acc, repeatNumber) => {
					acc ::: measures 
				}
			}
		}
	}

	val chordTemplate = RhythmSectionCoordinator.generateChordTemplate(repeat(measures), timeSig, subdivisions)

	def piano = PianoPlayer.getCompingMidi(chordTemplate, style, measures.length * repeats, pianoSettings)

	def bass = BassPlayer.getBass(chordTemplate, bassSettings)

	def drums = Drums.SwingDrumBasic(measures.length * repeats)

	def midi(path: String){
		val master = new MidiCreator
		
		val tempoEvent = MidiCreator.tempoEvent(bpm)


		//Piano
		val pianoMidiData = (MidiCreator.PianoProgram(MidiCreator.PianoChannel) +: MidiCreator.midiChordsToMidiEvent(piano, 3, MidiCreator.PianoChannel)) //piano triplet feel. Resolve later
		master.createTrack(tempoEvent +: pianoMidiData)

		//Bass 
		val bassMidiData = MidiCreator.BassProgram(MidiCreator.BassChannel) +: MidiCreator.singleNotesToMidiEvent(bass, subdivisions, MidiCreator.BassChannel) 
		master.createTrack(bassMidiData)

		//Drums 
		val drumMidiData = MidiCreator.DrumProgram(MidiCreator.DrumChannel) +: MidiCreator.singleNotesToMidiEvent(drums, 3, MidiCreator.DrumChannel) 
		master.createTrack(drumMidiData)

		master.createMidi(path)
	}


}