package de.catchycube.doodleJump.game;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import de.catchycube.doodleJump.loading.SpritesheetLoader;

public class Player implements KeyListener{

	//Max Jump Height: 0.5*(maxSpeed)²/gravitation
	
	private InGameState gameState; GameContainer container;
	private boolean isAlive=true;
	private float ySpeed, gravitation=0.25f, movement, constantMovement=10f,
			maxSpeed=12.5f,minSpeed=-10f,
			ownScaling=1f;
	private float x, y, platformXOffset;
	private int jumpCounter, jumpAt=5;
	private Image spriteStanding, spriteSitting, spriteJumping, spriteDead;
	private boolean useOwnScaling=false,onPlatform=false, sitting=false, alive=false;
	private Platform platform;
	private Image currentSprite;
	private Rectangle bounds;
	
	private boolean canExit=false;
	
	private static final int SITTINGOFFSET=20;
	
	public void init(GameContainer container, StateBasedGame game, InGameState state){
		container.getInput().addKeyListener(this);
		gameState = state;
		this.container = container;
		
		SpriteSheet sheet = SpritesheetLoader.getInstance().getSpriteSheet("misc", 64, 64);
		spriteStanding = sheet.getSprite(0, 0).getSubImage(9, 0, 32, 59);
		spriteJumping = sheet.getSprite(0, 1).getSubImage(1, 0, 47, 54);
		spriteSitting = sheet.getSprite(1, 1).getSubImage(4, 0, 41, 53);
		try {
			Image img = new Image(41,spriteSitting.getHeight()+SITTINGOFFSET);
			Graphics imgGraphics = img.getGraphics();
			Graphics.setCurrent(imgGraphics);
			spriteSitting.draw(0, SITTINGOFFSET);
			imgGraphics.flush();
			spriteSitting = img;
		} catch (SlickException e) {
			e.printStackTrace();
		}
		spriteDead = sheet.getSprite(1, 0).getSubImage(3, 0, 47, 58);
		
		currentSprite = spriteStanding;
		bounds = new Rectangle(0, 0, spriteStanding.getWidth()*gameState.getTextureScaling(), spriteStanding.getHeight()*gameState.getTextureScaling());
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta){			
		if(ySpeed <= 5*maxSpeed/6f && ySpeed > 0 && alive){
			setCurrentSprite(spriteJumping);
		}
//		if(container.getInput().isKeyDown(Input.KEY_SPACE) && onPlatform) sitting = true;
		
		
		if(!onPlatform){
			y+=ySpeed;
			applyGravity();
			if(alive){
				x+=movement;
				applySideSwitch();
				applyCollision();
				checkDeath();
			}else{
				if(y < gameState.getCameraHeight()){
					canExit = true;
				}
			}
		} else if(onPlatform && alive){
			checkPlatformChanges();
			if(!sitting && !container.getInput().isKeyDown(Input.KEY_LCONTROL)){
				jumpCounter++;
				if(jumpCounter >= jumpAt){
					jumpCounter = 0;
					ySpeed = maxSpeed;
					onPlatform = false;
					setCurrentSprite(spriteStanding);
				}
			}
		}
	}
	
	private void checkPlatformChanges(){
		if(platform != null){
			if(!platform.applyPlayerCollision()){
				onPlatform = false;
				platform = null;
			} else if(platform.movePlayerWithPlatform()){ //TODO: Also move the y coordinate
				float newXOffset = calculateXOffset(platform);
				if(newXOffset != platformXOffset){
					x += newXOffset - platformXOffset;
					platformXOffset = newXOffset;
				}
			}
		}
	}
	
	private float calculateXOffset(Platform p){
		return p.getHitBounds().getX() - getBounds().getX();
	}
	
	private void checkDeath(){
		if(y < gameState.getCameraHeight()){
			 alive = false;
			 setCurrentSprite(spriteDead);
			 ySpeed = 15f;
		}
	}
	
	private void applyGravity(){
		ySpeed -= gravitation;
		if(ySpeed < minSpeed) ySpeed = minSpeed;
	}
	
	private void applyCollision(){
		List<Platform> platforms = gameState.getRelevantPlatforms();
		Rectangle myBounds = gameState.calcRenderRect(getBounds());
		myBounds.setY(myBounds.getY() + myBounds.getHeight()*0.9f);
		myBounds.setHeight(myBounds.getHeight()*0.1f);
		
		for(Platform p : platforms){
			if(p.applyPlayerCollision() && myBounds.intersects(p.getReCalculatedHitBounds(gameState)) && ySpeed < 0){
				ySpeed = 0;
				onPlatform = true;
				y = p.getHitBounds().getY() + getBounds().getHeight();
				setCurrentSprite(sitting?spriteSitting:spriteStanding);
				p.onHit(this);
				platform = p;
				platformXOffset = calculateXOffset(platform);
			}
		}
	}
	
	private void applySideSwitch(){
		if(x >= gameState.getGameScreenBoundings().getWidth() && movement > 0){
			x -= gameState.getGameScreenBoundings().getWidth();
			x -= this.getBounds().getWidth();
			x += 5;
		} else if(x+getBounds().getWidth() <= 0 && movement < 0){
			x = gameState.getGameScreenBoundings().getWidth();
			x -= 5;
		}
	}
	
	public void initNewGame(float x, float y){
		this.x = x;
		this.y = y;
		alive = true;
		canExit = false;
	}
	
	public void setCurrentSprite(Image sprite){
//		x -= (sprite.getWidth() - currentSprite.getWidth())/2f;
//		y += sprite.getHeight() - currentSprite.getHeight();
		currentSprite = sprite;
		
	}
	
	public Rectangle getBounds(){
		bounds.setX(x);
		bounds.setY(y);
		bounds.setWidth(spriteStanding.getWidth()*gameState.getTextureScaling());
		bounds.setHeight(spriteStanding.getHeight()*gameState.getTextureScaling());
		return bounds;
		//		return new Rectangle(x, y, currentSprite.getWidth() * getScaling(), currentSprite.getHeight()*getScaling());
	}
	
	private float getScaling(){
		return useOwnScaling?ownScaling:gameState.getTextureScaling();
	}
	
	private Rectangle drawBounds = new Rectangle(0, 0, 1, 1);
	public void render(GameContainer container, StateBasedGame game, Graphics g){
		drawBounds = gameState.calcRenderRect(getBounds());
		float rx = drawBounds.getX();
		float ry = drawBounds.getY();
		rx -= (currentSprite.getWidth()*getScaling() - drawBounds.getWidth())/2;
		ry -= (currentSprite.getHeight()*getScaling() - drawBounds.getHeight())/2;
		
		
//		currentSprite.draw(drawBounds.getX(), gameState.getGameScreenBoundings().getHeight() - drawBounds.getY() + gameState.getCameraHeight(), gameState.getTextureScaling());
		currentSprite.draw(rx,ry,gameState.getTextureScaling());
		
		//DEBUG
//		Rectangle r = gameState.calcRenderRect(getBounds());
//		g.setColor(Color.red);
//		g.drawRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public void inputEnded() {
	}

	@Override
	public void inputStarted() {

	}

	@Override
	public boolean isAcceptingInput() {
		return gameState.isAcceptingInput() && isAlive;
	}

	@Override
	public void setInput(Input input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(int key, char c) {
		if(Input.KEY_DOWN == key){
			sitting = true;
			if(onPlatform){
				setCurrentSprite(spriteSitting);
				jumpCounter = 0;
			}
		} else if(Input.KEY_RIGHT == key){
			movement = constantMovement;
		} else if(Input.KEY_LEFT == key){
			movement = -constantMovement;
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		if(Input.KEY_DOWN == key){
			sitting = false;
		}
		else if((Input.KEY_RIGHT == key && movement == constantMovement)||
				(Input.KEY_LEFT == key && movement == -constantMovement)){
			movement = 0;
			if(container.getInput().isKeyDown(Input.KEY_LEFT)) movement = -constantMovement;
			if(container.getInput().isKeyDown(Input.KEY_RIGHT)) movement = constantMovement;
		}
	}
	
	public boolean canExit(){
		return canExit;
	}
	
	public boolean isAlive(){
		return alive;
	}

	public float getGravitation() {
		return gravitation;
	}

	public void setGravitation(float gravitation) {
		this.gravitation = gravitation;
	}

	public float getConstantMovement() {
		return constantMovement;
	}

	public void setConstantMovement(float constantMovement) {
		this.constantMovement = constantMovement;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public float getMinSpeed() {
		return minSpeed;
	}

	public void setMinSpeed(float minSpeed) {
		this.minSpeed = minSpeed;
	}

	public float getOwnScaling() {
		return ownScaling;
	}

	public void setOwnScaling(float ownScaling) {
		this.ownScaling = ownScaling;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public boolean isUseOwnScaling() {
		return useOwnScaling;
	}

	public void setUseOwnScaling(boolean useOwnScaling) {
		this.useOwnScaling = useOwnScaling;
	}

	public boolean isOnPlatform() {
		return onPlatform;
	}

	public boolean isSitting() {
		return sitting;
	}
	

}
