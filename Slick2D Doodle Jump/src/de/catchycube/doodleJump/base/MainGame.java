package de.catchycube.doodleJump.base;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

import de.catchycube.doodleJump.game.InGameState;
import de.catchycube.doodleJump.loading.ImageLoader;

public class MainGame extends StateBasedGame{
	
	private static final int xRes=320;
	private static final float yScale=11f/6f;
	
	private GameState gameState, menuState;
	
	public MainGame(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}



	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		ImageLoader.getInstance().setImageBaseDir("Images");
		
		menuState = new MainMenu();
		addState(menuState);
		gameState = new InGameState();
		addState(gameState);
	}
	
	public static void main(String[] args) throws SlickException{
		AppGameContainer agc = new AppGameContainer(new MainGame("Doodle Jump"), xRes, (int)(yScale * xRes), false);
		System.out.println("Running at " + agc.getWidth() + "x" + agc.getHeight());
		
		agc.start();
		
	}
	
	public GameState getIngameState(){
		return gameState;
	}
}
