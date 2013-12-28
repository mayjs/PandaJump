package de.catchycube.doodleJump.gameOver;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import de.catchycube.doodleJump.game.InGameState;

public class GameOverState_Counter extends BasicGameState{

	public static final int ID = 2;
	
	public static Color COLOR_OVERLAY= new Color(0, 0, 0, 0.7f);
	
	private float scoreFactor = 0.1f;
	private int updateCounter, updateAt=40;
	private int scoreToRender, totalScore, scoreCounterTime=3400, scoreStep;
	private InGameState state;
	private Font font;
	private Color staticTextColor=Color.white;
	
	private StateBasedGame game;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		state = (InGameState)game.getState(InGameState.ID);
		font = new TrueTypeFont(new java.awt.Font("verdana", java.awt.Font.BOLD, 20), true);
		this.game = game;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		state.render(container, game, g);
		
		g.setColor(COLOR_OVERLAY);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		
		String pointString = "Punkte: " + scoreToRender;
		font.drawString((container.getWidth() - font.getWidth(pointString))/2, container.getHeight()/4, pointString, staticTextColor);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		for(updateCounter += delta; updateCounter >= updateAt && stillCounting(); updateCounter -= updateAt){
			scoreToRender+=scoreStep;
			if(scoreToRender >= totalScore){
				scoreToRender = totalScore;
				game.enterState(GameOverState_Input.ID);
			}
		}
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game){
		updateCounter = 0;
		scoreToRender=0;
		totalScore = state.getAmplifiedScore();
		scoreStep=totalScore*updateAt/scoreCounterTime;
		if(scoreStep==0)scoreStep=1;
	}
	
	@Override
	public void keyPressed(int key, char c){
		if(key == Input.KEY_ENTER){
			scoreToRender = totalScore;
			game.enterState(GameOverState_Input.ID);
		}
	}
	
	private boolean stillCounting(){
		return scoreToRender < totalScore;
	}
}
