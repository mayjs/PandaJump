package de.catchycube.doodleJump.game;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Animation;
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
	private LinkedList<Object[]> animations = new LinkedList<Object[]>();
	private SpriteSheet sheet;
	private Player player=new Player();
	private int score;
	private float scrollSpeed, constantScrollSpeed=7.5f;
	private int applyCounter, applyMax=15;
	private Font font;
	private float scoreFactor=0.1f;
	private Generator generator;
	private List<Platform> platformsToRemove = new LinkedList<Platform>();
	
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
		for(Object[] obj : animations){
			Animation anim = (Animation)obj[0];
			float x = calcRenderX((float)obj[1]);
			float y = calcRenderY((float)obj[2]);
			anim.draw(x, y,anim.getWidth() * textureScaling, anim.getHeight() * textureScaling);
		}
		player.render(container, game, g);
		String pointString = "Punkte: " + score;
		font.drawString(gameScreenBoundings.getMaxX()-font.getWidth(pointString), 0, pointString);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		for(Platform p : platformsToRemove){
			platforms.remove(p);
		}		
		platformsToRemove.clear();
		
		List<Object[]> toRemove = new LinkedList<Object[]>();
		for(Object[] obj : animations){
			Animation anim = (Animation)obj[0];
			anim.update(delta);
			if(anim.isStopped()){
				toRemove.add(obj);
			}
		} for(Object[] obj : toRemove){
			animations.remove(obj);
		}
		
		applyCounter+=delta;
		if(applyCounter > applyMax){			
			applyCounter -= applyMax;
			
			generator.update();
			for(Platform p : platforms){
				p.update();
			}
			
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
		generator.update(); //Pre generate a few platforms
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
		return new Rectangle(calcRenderX(modelRect.getX()), calcRenderY(modelRect.getY()),
				modelRect.getWidth(), modelRect.getHeight());
	}
	
	public float calcRenderY(float modelY){
		return gameScreenBoundings.getY() + getGameScreenBoundings().getHeight() - modelY + getCameraHeight();
	}
	
	public float calcRenderX(float modelX){
		return gameScreenBoundings.getX() + modelX;
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
	
	public void removePlatform(Platform p){
		platformsToRemove.add(p);
	}
	
	public void playAnimation(Animation animation, float x, float y){
		animations.add(new Object[]{animation,x,y});
	}
}
