package de.catchycube.doodleJump.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

public class Platform {
	
	protected Rectangle hitBounds;
	protected Image sprite;	
	protected InGameState gameState;
	
	public Platform(Rectangle hitBounds, Image sprite, InGameState gameState) {
		this.hitBounds = hitBounds;
		this.sprite = sprite;
		this.gameState = gameState;
	}

	public void onHit(){

	}
	
	public void update(long delta){
		
	}
	
	public void draw(float offset, InGameState gameState,Graphics g){
////		float theoRenderPos = hitBounds.getY()+hitBounds.getHeight();
////		float realRenderPos = gameState.getGameScreenBoundings().getY() + gameState.getGameScreenBoundings().getHeight() - theoRenderPos + offset;
////		float realXPos = gameState.getGameScreenBoundings().getX() + hitBounds.getX();
//		sprite.draw(realXPos, realRenderPos, gameState.getTextureScaling());
		Rectangle drawBounds = gameState.calcRenderRect(hitBounds);
		sprite.draw(drawBounds.getX(), drawBounds.getY(), gameState.getTextureScaling());
	}
	
	public Rectangle getHitBounds(){
		return hitBounds;
	}
	
	public Rectangle getReCalculatedHitBounds(InGameState gameState){
		return getViewBounds(gameState);
	}
	
	public Rectangle getViewBounds(InGameState gameState){
		return gameState.calcRenderRect(hitBounds);
	}
}
