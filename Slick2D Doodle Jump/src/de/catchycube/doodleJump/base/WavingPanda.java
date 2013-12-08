package de.catchycube.doodleJump.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

public class WavingPanda {
	private Image sheet;
	private int fw, fh, beginX, sheetY, endX,current,waitTime,timeCounter;
	private Rectangle bounds;
	
	public WavingPanda(Image sheet, int frameW, int frameH, int beginX, int endX, int sheetY,int waitTime, Rectangle boundings){
		this.sheet = sheet;
		fw = frameW;
		fh = frameH;
		this.beginX = beginX;
		this.endX = endX;
		this.sheetY = sheetY;
		bounds = boundings;
		this.waitTime = waitTime;
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g){
		sheet.draw(bounds.getX(), bounds.getY(), bounds.getMaxX(), bounds.getMaxY(), (beginX+current)*fw, sheetY*fh, (beginX+current+1)*fw, sheetY*fh+fh);
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta){
		timeCounter+=delta;
		if(timeCounter >= waitTime){
			current++;
			if(current >endX-beginX) current = 0;
			timeCounter -= waitTime;
		}
	}
}
