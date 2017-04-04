package main;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.PatternProducer;
import org.jfugue.player.Player;
import org.jfugue.player.SynthesizerManager;
import org.jfugue.theory.Chord;
import org.jfugue.theory.ChordProgression;
import org.jfugue.theory.Intervals;
import org.jfugue.theory.Key;
import org.jfugue.theory.Note;
import org.jfugue.theory.Scale;
import org.staccato.DefaultNoteSettingsManager;

public class Generator {

	// give it random "rules" no using this timing sequence, none of this note,
	// none of this note difference,

	final static String[] majors = { "I", "ii", "iii", "IV", "V", "vi"        };
	final static String[] minors = { "i",       "III", "iv", "v", "VI", "VII" };
	final static String[] keys = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

	final static int repetitions = 6;
	final static int cpreps = 2;
	final static int totalChords = 16;
	final static int realTempo = 120;
	
	Random rand;
	String name, verseInfoString, debugStringSeeds;
	Player player = new Player();
	Pattern song;
	Pattern verse;
	Pattern chorus;
	boolean minor = false;
	double seventhChance = 0.2;
	Synthesizer synth;
	Soundbank soundbank;
	int tempoDisp = 0;
	int assignedTempo = 0;
	Key k;
	
//	 static double[] distribution_melodySubdivs = {0.5,0.4,0.08,0.019,0.001};
	static double[] distribution_melodySubdivs = { .5, .3, .2, 0, 0 };

	public String seven() {
		if (rand.nextFloat() < seventhChance)
			return "7";
		else
			return "";
	}
	
	public static double[] weight(double[] in, double power){
		double sum = 0;
		for(int i=0; i<in.length; i++){
			in[i] = Math.pow(in[i], power);
			sum+=in[i];
		}
		for(int i=0; i<in.length; i++){
			in[i]/=sum;
		}
		return in;
	}

	public String drumFill() {
		double[] distribution_percSounds = { 0.5, 0.1, 0.1, 0.03, 0.17, 0.1 };
		distribution_percSounds = weight(distribution_percSounds,2);
		String[] percSounds = { ".", "x", "X", "*", "+", "s" };
		String result = "";
		for (int i = 0; i < 64; i++) {
			String add = percSounds[outcome(distribution_percSounds)];
			if (rand.nextFloat() < 0.2 && i > 8)
				add = result.charAt(i - 8) + "";
			else if (rand.nextFloat() < 0.6 && i > 16)
				add = result.charAt(i - 16) + "";
			result += add;
		}
		return result;
	}
	
	public void loadFont(){
//		try {
//			synth = MidiSystem.getSynthesizer();
//			soundbank = MidiSystem.getSoundbank(new File("OmegaGMGS2.sf2"));
//			synth.open();
//			synth.loadAllInstruments(soundbank);
//			SynthesizerManager.getInstance().setSynthesizer(synth);
//			
//			String print = "";
//			for(int i=0; i<synth.getLoadedInstruments().length; i++){
//				print += (i+" "+synth.getLoadedInstruments()[i]+"\n");
//			}
//			System.out.println(print);
//				
//			
//		} catch (MidiUnavailableException e) {
//			e.printStackTrace();
//		} catch (InvalidMidiDataException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public Pattern getVerse(int instMel){
		Pattern[] p = new Pattern[8];
		String[] genChords = new String[totalChords];

		generateChords(genChords);
		
		ChordProgression keyed = new ChordProgression(genChords).setKey(k);
		Chord[] chords = keyed.getChords();
		for (int i = 0; i < genChords.length; i++) {
			Note n = chords[i].getNotes()[0];
			String s = Note.getToneString((byte) (n.getValue()-12)) + "w";
			genChords[i] = s;
		}
				
//		Rhythm rhythm = new Rhythm().addLayer("O...O...O...O...O...O...O...O...").addLayer(drumFill());
//		 'SsXx*+.^Oo
		int dms = outcome(distribution_melodySubdivs);
		p[0] = new Pattern().setInstrument(32); // chords 32 42
		p[1] = new Pattern().setInstrument(24); // melody
		p[2] = new Pattern().setInstrument(46); // melody
		p[3] = new Pattern().setInstrument(instMel); // melody
//		Note note = new Note("C5w");
//		p[0].add(note);
//		note.changeValue(1);
//		System.out.println(note.originalString);
//		System.out.println(note.getToneString(note.getValue()));
//		p[0].add(note);
		
//		p[5] = rhythm.getPattern();
		
		for (int i = 0; i < p.length; i++){
			if (p[i] != null){
				
				p[i].setVoice(i+10).setTempo(assignedTempo);
			}
		}
				
		Section[] s1 = new Section[genChords.length];
		Section[] s2 = new Section[genChords.length];
		Section[] s3 = new Section[genChords.length];
		
		int gl = genChords.length;
		for (int i = 0; i < genChords.length; i++) {
			p[0].add(new Note(genChords[i]).setOnVelocity((byte)50));
			Note[] notes = chords[i].getNotes();
			Section previous1 = null;
			Section previous2 = null;
			Section previous3 = null;
			if (i > 0){
				previous1 = s1[i - 1];
				previous2 = s2[i - 1];
				previous3 = s3[i - 1];
			}
			
			s1[i] = new Section(notes, this, previous1, dms, 0);
			s2[i] = new Section(notes, this, previous2, dms+rand.nextInt(2), 1);
			s3[i] = new Section(notes, this, previous3, dms+rand.nextInt(2), 2);
		}
		for (int i = 0; i < genChords.length; i++) {
			
			String copyIndicator1 = "";
			String copyIndicator3 = "";
			double[] distribution_copy = { 0.3, 0.7, 0.0, 0.0 };
			int outcome1 = outcome(distribution_copy);
			int outcome3 = outcome(distribution_copy);
			//if outcome is 0, do no copies.
			int g4 = (i - 4 + gl) % gl;
			int g2 = (i - 2 + gl) % gl;
			int g1 = (i - 1 + gl) % gl;
			if (outcome3 == 1) {
				s3[i].copy(s3[g4], 0.9f);
				copyIndicator3 = "(-4.)";
			}
			if (outcome3 == 2) {
				s3[i].copy(s3[g2], 0.9f);
				copyIndicator3 = "(-2.)";
			}
			if (outcome3 == 3) {
				s3[i].copy(s3[g1], 0.9f);
				copyIndicator3 = "(-1.)";
			}
			if (outcome1 == 1) {
				s1[i].copy(s1[g4], 0.9f);
				s2[i].copy(s2[g4], 0.9f);
				
				copyIndicator1 = "(-4)";
			}
			if (outcome1 == 2) {
				s1[i].copy(s1[g2], 0.9f);
				s2[i].copy(s2[g2], 0.9f);
				
				copyIndicator1 = "(-2)";
			}
			if (outcome1 == 3) {
				s1[i].copy(s1[g1], 0.9f);
				s2[i].copy(s2[g1], 0.9f);
				
				copyIndicator1 = "(-1)";
			}
			
			
			
			for (String s : s1[i].toArrayList())
				p[1].add(s);
			for (String s : s2[i].toArrayList())
				p[2].add(s);
			for (String s : s3[i].toArrayList())
				p[3].add(s);
			
			verseInfoString += genChords[i] + " " + "\n";
			verseInfoString += copyIndicator1 + s1[i].toString() + " " + "\n";
			verseInfoString += copyIndicator1 + s2[i].toString() + " " + "\n";
			verseInfoString += copyIndicator3 + s3[i].toString() + " " + "\n";
		}
		
		player = new Player();
		Pattern verse = new Pattern();
		verse.add(p[0]);
		verse.add(p[1]);
		verse.add(p[2]);
		verse.add(p[3]);
		
		System.out.println(p[0]);
		System.out.println(p[1]);
		System.out.println(p[2]);
		System.out.println(p[3]);
		
		return verse;
	}

	public void getSong() {
		
		
		verseInfoString = "";
		name = FrameGUI.tf1.getText();
		debugStringSeeds += ("|" + name);
		rand = new Random(name.hashCode());
		
		
		minor = rand.nextBoolean() && !FrameGUI.cMaj.isSelected();
		DefaultNoteSettingsManager.getInstance().setDefaultOctave((byte)(3+rand.nextInt(2)));
		Scale sc = new Scale(new Intervals("1 2 b3 4 5 b6 b7"));
		sc = new Scale(Scale.MAJOR.getIntervals());
		if (minor) sc = new Scale(Scale.MINOR.getIntervals());
		System.out.println(sc);
		
		k = new Key(new Note(keys[randIndex(keys.length)] + ""), sc);
		if (FrameGUI.cMaj.isSelected())k = new Key(new Note(keys[0] + ""), sc);
		
		tempoDisp = rand.nextInt(30);
		assignedTempo = realTempo + tempoDisp;

		song = new Pattern();
		verse = getVerse(54); //0 54
		FrameGUI.taVerse.setText(verseInfoString);
		verseInfoString="";
		chorus = getVerse(79);
		
		FrameGUI.taChorus.setText(verseInfoString);
		FrameGUI.taDebug.setText("Tempo: " + this.assignedTempo + "\n"
				+ "Scale: " + k.toString() + " " + sc.toString() + "\n"
						+ "Tested Seeds: " + debugStringSeeds);
		
		song.add(verse);
		song.add(chorus);
		song.add(verse);
		
		player.delayPlay(0, song);
		
	}
	
	public void export(){
		try {
			 MidiFileManager.savePatternToMidi((PatternProducer) song, new File("harpGen"+name+".mid"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void generateChords(String[] genChords) {
		String[]  chordList = Arrays.copyOf(majors, majors.length);
		if (minor)chordList = Arrays.copyOf(minors, minors.length);
		for (int i = 0; i < genChords.length; i++) {
			int randChordindex = randIndex(chordList.length);
			String generatedChord = chordList[randChordindex];
			genChords[i] = generatedChord;
			genChords[i] += seven();
		}
		for (int i = 0; i < genChords.length / cpreps; i++) {
			
			for (int j = 1; j < cpreps; j++) {
				genChords[i + j * genChords.length / cpreps] = genChords[i];
			}
			
		}
		if(rand.nextFloat()<0.1)
		for (int i = 0; i < genChords.length; i++) {
			genChords[i] = genChords[i-i%4];
		}
		if(rand.nextFloat()<0.4)
		for (int i = 0; i < genChords.length; i++) {
			genChords[i] = genChords[i-i%2];
		}
	}

	public int outcome(double[] chance) {
		ArrayList<BigDecimal> bi = new ArrayList<BigDecimal>();
		for (int i = 0; i < chance.length; i++) {
			BigDecimal bd = new BigDecimal(chance[i] + "");
			if (i > 0)
				bd = bd.add(bi.get(bi.size() - 1));
			bi.add(bd);
		}
		bi.set(bi.size() - 1, new BigDecimal("1.00"));
		float f = rand.nextFloat();

		if (f < bi.get(0).floatValue())
			return 0;
		for (int i = 1; i < bi.size(); i++) {
			if (f < bi.get(i).floatValue())
				return i;
		}
		return 0;
	}

	public int randIndex(int in) {
		return (int) (in * rand.nextFloat());
	}
}
