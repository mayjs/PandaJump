package de.catchycube.doodleJump.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import de.catchycube.doodleJump.game.InGameState;
import de.catchycube.doodleJump.highscore.HighscoreState;
import de.catchycube.doodleJump.loading.SpritesheetLoader;
import de.catchycube.doodleJump.particles.custom.OneTimeEmitter;
import de.catchycube.doodleJump.particles.custom.OneTimeEmitterIO;

public class MainMenu extends BasicGameState{

	public final static int ID = 0;
	public final static String CMD_EXIT = "command_exit_full",
								CMD_NEW_GAME="command_new_game",
								CMD_HIGHSCORE="command_highscore";
	
	private StateBasedGame game;
	private GameContainer container;
	
	private List<MainMenuButton> buttons;
	
	private SpriteSheet sheet;
	private int frameWidth=64, frameHeight=64;
	private Animation wavingPanda;
	
	private ParticleSystem system;
	private OneTimeEmitter emitter;
		
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		sheet = SpritesheetLoader.getInstance().getSpriteSheet("misc", frameWidth, frameHeight);
		
		this.game = game;
		this.container = container;
		this.buttons = new LinkedList<MainMenuButton>();
		
		system = new ParticleSystem(sheet.getSprite(3, 1));
		system.setUsePoints(true);
		
		try {
			emitter = OneTimeEmitterIO.loadEmitter(new FileInputStream("Particles\\emitter_fallingLeaves.xml"));
			emitter.setPosition(50, 50);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buttons.add(new MainMenuButton(this, "New Game", CMD_NEW_GAME, container.getWidth()/2, container.getHeight() * 0.1f, container, emitter));
		buttons.add(new MainMenuButton(this, "High Score", CMD_HIGHSCORE, buttons.get(0), 0, 5, container, emitter));		
		buttons.add(new MainMenuButton(this, "Quit",CMD_EXIT, buttons.get(1) ,0,5, container, emitter));
	
		//panda = new WavingPanda(sheet, frameWidth, frameHeight, 4, 6,1, 200, new Rectangle(0,0,256,256));
		wavingPanda = new Animation(sheet, new int[]{4,1,5,1,6,1}, new int[]{400,200,400});
		wavingPanda.setPingPong(true);
		
		container.getGraphics().setBackground(Color.cyan);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		system.render();
		wavingPanda.draw(container.getWidth()/2-100, container.getHeight()/2-64, 256, 256);
		for(MainMenuButton btn : buttons){
			btn.render(container,game,g);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		system.update(delta);
		wavingPanda.update(delta);
	}

	@Override
	public int getID() {
		return ID;
	}
	
	@Override
	public boolean isAcceptingInput() {
		return game.getCurrentStateID() == ID;
	}
	
	public void execute(String command){
//		System.out.println("Executing " + command);
		if(command.equals(CMD_EXIT)){
			container.exit();
		} else if(command.equals(CMD_NEW_GAME)){
			((InGameState)((MainGame)game).getIngameState()).initNewGame();
			game.enterState(((MainGame)game).getIngameState().getID(), new FadeOutTransition(), new FadeInTransition());
		} else if(command.equals(CMD_HIGHSCORE)){
			game.enterState(HighscoreState.ID);
		}
	}
	
	public ParticleSystem getParticleSystem(){
		return system;
	}
}
