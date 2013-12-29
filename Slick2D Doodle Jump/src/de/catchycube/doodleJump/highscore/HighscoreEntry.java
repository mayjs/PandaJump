package de.catchycube.doodleJump.highscore;

public class HighscoreEntry implements Comparable<HighscoreEntry>{

	private int score;
	private String name;
	
	public HighscoreEntry(String name, int score){
		this.name = name;
		this.score = score;
	}
	
	@Override
	public int compareTo(HighscoreEntry o) {
		return o.getScore() - score;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name +":   " + score;
	}
}
