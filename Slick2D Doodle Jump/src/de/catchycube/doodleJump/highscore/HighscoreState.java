package de.catchycube.doodleJump.highscore;


import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import de.catchycube.doodleJump.base.MainMenu;

public class HighscoreState extends BasicGameState{

	public static final int ID=4;
	
	private Highscore highscore;
	private int highlight=-1;
	private Color normalColor=Color.white,highlightColor=Color.red;
	private StateBasedGame game;
	private Font font;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		this.game = game;
		font = new TrueTypeFont(new java.awt.Font("verdana", java.awt.Font.BOLD, 20), true);
		highscore = new Highscore();
		highscore.setMaximalNumberOfEntries(3);
		
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		int x=container.getWidth()/2;
		int y=(container.getHeight() - (highscore.getNumberOfEntries() * font.getLineHeight()))/2;
		
		List<HighscoreEntry> entries = highscore.getEntries();
		for(int i = 0; i < entries.size(); i++){
			String s = entries.get(i).toString();
			font.drawString(x-font.getWidth(s)/2,y+i*font.getLineHeight(), s, i==highlight?highlightColor:normalColor);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		highlight = -1;
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if(key == Input.KEY_ESCAPE){
			game.enterState(MainMenu.ID);
		}
	}
	
	public int add(String name, int score){
		return highscore.add(name, score);
	}
	
	public void highlight(int i){
		highlight = i;
	}
}
