package de.catchycube.doodleJump.base;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

import de.catchycube.doodleJump.debug.DebugInfo;
import de.catchycube.doodleJump.game.InGameState;
import de.catchycube.doodleJump.gameOver.GameOverState_Counter;
import de.catchycube.doodleJump.gameOver.GameOverState_Input;
import de.catchycube.doodleJump.loading.ImageLoader;

public class MainGame extends StateBasedGame{
	
	private static final int xRes=320;
	private static final float yScale=11f/6f;
	
	private GameState gameState, menuState, gameOverState_Counter, gameOverState_Input;
	private DebugInfo info;
	
	public MainGame(String name) {
		super(name);
	}



	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		info = new DebugInfo(1, 20, 1, 1,this, container);
		try {
			info.set(0,0,new Object[]{"FPS: ", new Object[]{GameContainer.class.getMethod("getFPS"),container}});
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		ImageLoader.getInstance().setImageBaseDir("Images");
		
		menuState = new MainMenu();
		addState(menuState);
		gameState = new InGameState();
		addState(gameState);
		gameOverState_Counter = new GameOverState_Counter();
		addState(gameOverState_Counter);
		gameOverState_Input = new GameOverState_Input();
		addState(gameOverState_Input);
	}
	
	@Override
	protected void postRenderState(GameContainer container, Graphics g) throws SlickException{
		info.render(g);
	}
	
	public static void main(String[] args) throws SlickException{
		AppGameContainer agc = new AppGameContainer(new MainGame("Doodle Jump"), xRes, (int)(yScale * xRes), false);
		System.out.println("Running at " + agc.getWidth() + "x" + agc.getHeight());
		agc.setShowFPS(false);
		
		agc.start();
	}
	
	public GameState getIngameState(){
		return gameState;
	}
	
	public DebugInfo getDebugInfo(){
		return info;
	}
}
