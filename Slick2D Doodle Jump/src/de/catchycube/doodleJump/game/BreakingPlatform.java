package de.catchycube.doodleJump.game;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

public class BreakingPlatform extends Platform{
	private boolean hitCounterEnabled=false;
	private int hitCounter;
	private static final int hitCounterMax=33;
	
	public BreakingPlatform(Rectangle hitBounds, Image sprite, InGameState gameState){
		super(hitBounds,sprite,gameState);
	}
	
	@Override
	public void onHit(Player p){
		super.onHit(p);
		hitCounterEnabled = true;
	}
	
	@Override
	public void update(){
		if(hitCounterEnabled){
			hitCounter++;
			if(hitCounter >= hitCounterMax){
				gameState.removePlatform(this);
				gameState.playAnimation(AnimationFactory.getInstance().getAnimation(AnimationFactory.BASE_BREAKINGPLATFORM), hitBounds.getX(), hitBounds.getY());
			}
		}
	}
}
