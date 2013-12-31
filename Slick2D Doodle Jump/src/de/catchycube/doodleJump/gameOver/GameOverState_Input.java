package de.catchycube.doodleJump.gameOver;

import java.lang.reflect.Field;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import de.catchycube.doodleJump.base.MainMenu;
import de.catchycube.doodleJump.highscore.HighscoreState;
import de.catchycube.doodleJump.transition.FixedAlphaFadeOutTransition;

public class GameOverState_Input extends BasicGameState{
	public final static int ID = 3;

	private GameState counterState;
	private Font font;
	private String inputString;
	private Color color=Color.white;
	
	private StateBasedGame game;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		counterState = game.getState(GameOverState_Counter.ID);
		font = new TrueTypeFont(new java.awt.Font("verdana", java.awt.Font.BOLD, 20), true);
		inputString = "Spieler";
		this.game = game;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		counterState.render(container, game, g);
		
		String renderString = "Name: " + inputString;
		int y = (int)(0.45f * container.getHeight());
		int x = (int)((container.getWidth()-font.getWidth(renderString))/2f);
		font.drawString(x, y, renderString, color);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		return ID;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game){
		System.out.println("enter");
	}
	
	@Override
	public void keyPressed(int key, char c){
		switch (key){
		case Input.KEY_ENTER: confirm(); break;
		case Input.KEY_BACK: remove(); break;
		default: tryAppend(c); break;
		}
	}
	
	private void confirm(){
		
//		game.enterState(MainMenu.ID, new FixedAlphaFadeOutTransition(Color.black, GameOverState_Counter.COLOR_OVERLAY.a, 1, 180),new FadeInTransition());
		HighscoreState state = (HighscoreState)game.getState(HighscoreState.ID);
		int n = state.add(inputString, ((GameOverState_Counter) counterState).getTotalScore());
		state.highlight(n);
		if(n>=0)
			game.enterState(HighscoreState.ID);
		else
			game.enterState(MainMenu.ID);
	}
	
	private void remove(){
		if(inputString.length() > 1){
			inputString = inputString.substring(0, inputString.length()-1);
		} else if(inputString.length() == 1) inputString = new String();
	}
	
	private void tryAppend(char c){
		//validate the character
		int cAsNumber = (int)c;
			//48-57:numbers 65-90:uppercase characters 97-122:lowercase characters
		if(isBetween(cAsNumber,48,57) || isBetween(cAsNumber,65,90) || isBetween(cAsNumber,97,122)){
			//append valid characters
			inputString += c;
		}
	}
	
	private boolean isBetween(int toCheck, int lower, int upper){
		return toCheck>=lower && upper >= toCheck;
	}
}
