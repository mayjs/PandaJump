package de.catchycube.doodleJump.highscore;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Highscore {
	
	private LinkedList<HighscoreEntry> entries = new LinkedList<HighscoreEntry>();
	private int maximalNumberOfEntries=10;
	
	public int add(String name, int score){ //Maybe need to reverse
		HighscoreEntry newEntry = new HighscoreEntry(name, score);
		entries.add(newEntry);
		Collections.sort(entries);
		while(entries.size() > maximalNumberOfEntries) entries.removeLast();
		return entries.indexOf(newEntry);
	}
	
	public int getMaximalNumberOfEntries() {
		return maximalNumberOfEntries;
	}
	public void setMaximalNumberOfEntries(int maximalNumberOfEntries) {
		this.maximalNumberOfEntries = maximalNumberOfEntries;
	}
	public List<HighscoreEntry> getEntries() {
		return entries;
	}
	public int getNumberOfEntries(){
		return entries.size();
	}
}
