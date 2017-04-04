package main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.jfugue.theory.Key;
import org.jfugue.theory.Note;

public class Section {

	final static String[] timings = { "/1.00", "/0.50", "/0.25", "/0.125", "/0.0625", "/0.03125", "/0.015625",
			"/0.0078125" };
	final static String[] timings_legacy = { "w", "h", "q", "i", "s", "t", "x", "o" };

	ArrayList<String> content = new ArrayList<String>();
	int index = 0;
	Byte previousValue = 0;
	int subdivs = 0; // number of subdivisions. So at 0 it will do whole notes.
	int timesCopied = 0;
	int octaver = 0;
	ArrayList<Note> usableNotes;
	Generator generator;

	Key currentKey;

	// whqistxo
	
	public void setOctaver(int in){
		octaver = in;
	}

	public Section(Note[] notes, Generator parentIn, Section previous, int subdivsIn, int octaverIn) {
		setOctaver(octaverIn);
		generator = parentIn;
		if (previous != null)
			previousValue = previous.previousValue;
		usableNotes = new ArrayList<Note>(Arrays.asList(notes));
		currentKey = parentIn.k;
		subdivs = subdivsIn;
		for (int i = 0; i < Math.pow(2, subdivs); i++) {
			double[] dd = { 0.3, 0.3, 0.2, 0.1, 0.0 };
			double[] de = { 0.5, 0.5, 0.0, 0.0, 0.0 };
			
			int xx = generator.outcome(dd);
//			if(subdivs==3)xx = generator.outcome(de);
			
			if (xx == 0) {
				content.add(gen(subdivs));
			}
			if (xx == 1) {
				content.add(gen(subdivs + 1));
				content.add(gen(subdivs + 1));
			}
			if (xx == 2) {
				content.add(gen(subdivs + 2));
				content.add(gen(subdivs + 2));
				content.add(gen(subdivs + 1));
			}
			if (xx == 3) {
				content.add(gen(subdivs + 1));
				content.add(gen(subdivs + 2));
				content.add(gen(subdivs + 2));
			}
			if (xx == 4) {
				content.add(gen(subdivs));
			}
		}
	}

	public void copy(Section s, float retainChance) {
		// first completely become it, then take matching notes from this and
		// use them.
		// possible copy where timings are not copied.
		if(generator.rand.nextFloat()<0.2){
			content.clear();
			content.addAll(s.content);
		}
		else{
			copyIfMatching(s.content, content);
		}
		
		s.timesCopied++;
		timesCopied = s.timesCopied;
	}

	public ArrayList<BigDecimal> getTimingInstances(ArrayList<String> in) {
		ArrayList<BigDecimal> result = new ArrayList<BigDecimal>();
		for (int i = 0; i < in.size(); i++) {
			String[] s = new String(in.get(i) + "").split("/");
			if (s.length == 1) {
				for (int j = 0; j < 8; j++) {
					if (s[0].substring(s[0].length() - 1).equals(timings_legacy[j])) {
						result.add(new BigDecimal(timings[j].substring(1).trim()));
					}
				}
			} else
				result.add(new BigDecimal(s[1].trim()));
		}
		return result;
	}

	public ArrayList<BigDecimal> getTimingSums(ArrayList<String> in) {
		ArrayList<BigDecimal> result = getTimingInstances(in);
		for (int i = 1; i < result.size(); i++) {
			result.set(i, result.get(i).add(result.get(i - 1)));
		}
		return result;
	}

	public void copyIfMatching(ArrayList<String> copyFrom, ArrayList<String> copyTo) {
		// from a list of timings, copy if matching
		// generate a list of full timings from instanced timings.
		ArrayList<BigDecimal> cF = getTimingSums(copyFrom);
		ArrayList<BigDecimal> cT = getTimingSums(copyTo);
		ArrayList<BigDecimal> cTi = getTimingInstances(copyTo);

		Note n0 = new Note(new Note(copyFrom.get(0)).originalString);
		n0 = n0.setDuration(cT.get(0).doubleValue());
		copyTo.set(0, n0.toString());

		for (int i = 0; i < cT.size(); i++) {
			for (int j = 0; j < cF.size(); j++) {
				if (cT.get(i).compareTo(cF.get(j)) == 0) {
					Note n = new Note(new Note(copyFrom.get(j)).originalString);
					n = n.setDuration(cTi.get(i).doubleValue());
					copyTo.set(i, n.toString());
				}
			}
		}
	}

	public ArrayList<String> toArrayList() {
		return content;
	}

	public void setIndex(int in) {
		index = in;
	}

	public String gen(int halves) {
		ArrayList<Note> list = new ArrayList<Note>(currentKey.getScale().getIntervals().getNotes());
		Note resultNote = list.get(generator.randIndex(list.size()));
		if (resultNote.getValue() < previousValue) {
//			resultNote.changeValue(25);
//			resultNote = new Note(resultNote.getValue());
//			System.out.println(resultNote.getValue());
//			System.out.println(previousValue);
		}
		previousValue = resultNote.getValue();
		if(previousValue>110)previousValue=110;
		return "" + Note.getToneString((byte) (resultNote.getValue()+12*octaver)) + timings[halves] + " ";
//		return "" + resultNote.toString() + timings[halves] + " ";
	}

	public boolean equals(Section other) {
		return this.toArrayList().equals(other.toArrayList());
	}

	// whqistxo
	public String toString() {
		ArrayList<String> a = new ArrayList<String>(content);
		for (int i = 0; i < a.size(); i++) {
			// a.set(i,new Note(a.get(i)).getValue()-12*5+"");
			a.set(i, new Note(a.get(i)).toString() + "");
		}
		return a.toString();
	}
}