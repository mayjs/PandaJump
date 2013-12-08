package de.catchycube.doodleJump.game;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;

import de.catchycube.doodleJump.base.MainMenu;
import de.catchycube.doodleJump.loading.SpritesheetLoader;

public class InGameState extends BasicGameState{

	private static final int ID = 1;
	
	private Rectangle gameScreenBoundings;
	private float textureScaling = 2f, cameraHeight = 0f;
	private LinkedList<Platform> platforms = new LinkedList<Platform>();
	private SpriteSheet sheet;
	private Player player=new Player();
	private int score;
	private float scrollSpeed, constantScrollSpeed=7.5f;
	private int applyCounter, applyMax=15;
	private Font font;
	private float scoreFactor=0.1f;
	private Generator generator;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		gameScreenBoundings = new Rectangle(0, 0, container.getWidth(), container.getHeight());
		sheet = SpritesheetLoader.getInstance().getSpriteSheet("misc", 64, 64);
		font = new TrueTypeFont(new java.awt.Font("verdana", java.awt.Font.BOLD, 20), true);
		player.init(container, game, this);
		generator = new Generator(this);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		for(int i = platforms.size()-1; i >= 0; i--){
			if(gameScreenBoundings.getHeight() - platforms.get(i).getHitBounds().getY()+cameraHeight > gameScreenBoundings.getHeight()+2) break;
			platforms.get(i).draw(cameraHeight, this,g);
		}
		player.render(container, game, g);
		String pointString = "Punkte: " + score;
		font.drawString(gameScreenBoundings.getMaxX()-font.getWidth(pointString), 0, pointString);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		applyCounter+=delta;
		if(applyCounter > applyMax){
			applyCounter -= applyMax;
			
			generator.update();
			
			if(player.canExit()){
				game.enterState(MainMenu.ID,new FadeOutTransition(Color.black, 1800),null); //TODO: create game over screen
				return;
			}
			if(player.isAlive()){
				Rectangle pBounds = player.getBounds();
				if(pBounds.getY() * scoreFactor > score)
					score = (int)(pBounds.getY() * scoreFactor);
				Rectangle renderBounds = this.calcRenderRect(pBounds);
				if(renderBounds.getY() < gameScreenBoundings.getHeight()*0.2f){
					scrollSpeed = constantScrollSpeed;
				} else scrollSpeed = 0;
				cameraHeight+=scrollSpeed;
			}
			player.update(container, game, delta);
		}
	}
	
	public void initNewGame(){
		cameraHeight = 0f;
		platforms.clear();
		
		
		Image unflipped = sheet.getSprite(2, 1).getSubImage(0, 41, 64, 12);
		Image flipped = unflipped.getFlippedCopy(true, false);
		boolean flip = false;
		for(int i = 0; ; i++){
			platforms.add(new Platform(new Rectangle(i*64*textureScaling, 12*textureScaling, 64*textureScaling, 12*textureScaling), 
					flip?flipped:unflipped,this));
			if((i+1)*64*textureScaling > gameScreenBoundings.getWidth()) break;
			flip = !flip;
		}		
		player.initNewGame(gameScreenBoundings.getCenterX(),gameScreenBoundings.getCenterY());
	}

	@Override
	public int getID() {
		return ID;
	}

	public Rectangle getGameScreenBoundings(){
		return gameScreenBoundings;
	}
	
	public float getTextureScaling(){
		return textureScaling;
	}
	
	public List<Platform> getRelevantPlatforms(){
		LinkedList<Platform> ret = new LinkedList<Platform>();
		for(int i = platforms.size()-1; i >= 0; i--){
			if(gameScreenBoundings.getHeight() - platforms.get(i).getHitBounds().getY()+cameraHeight > gameScreenBoundings.getHeight()+2) break;
			ret.add(platforms.get(i));
		}
		return ret;
	}
	
	public float getCameraHeight(){
		return cameraHeight;
	}
	
	public Rectangle calcRenderRect(Rectangle modelRect){
		return new Rectangle(modelRect.getX(), getGameScreenBoundings().getHeight() - modelRect.getY() + getCameraHeight(),
				modelRect.getWidth(), modelRect.getHeight());
	}
	
	public LinkedList<Platform> getAllPlatforms(){
		return platforms;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public float getScoreFactor(){
		return scoreFactor;
	}
	
	public void setScoreFactor(float scoreFactor){
		this.scoreFactor = scoreFactor;
	}
	
	public void addPlatform(Platform p){
		platforms.add(p);
	}
}
