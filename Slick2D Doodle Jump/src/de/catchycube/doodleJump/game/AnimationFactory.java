package de.catchycube.doodleJump.game;

import java.util.HashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import de.catchycube.doodleJump.loading.SpritesheetLoader;

public class AnimationFactory {
	public final static String BASE_BREAKINGPLATFORM="baseAnimation_breakingPlatform";
	
	private HashMap<String, Animation> animations;
	
	private AnimationFactory(){init();}
	private static AnimationFactory instance;
	public static AnimationFactory getInstance(){
		if(instance==null) instance = new AnimationFactory();
		return instance;
	}
	
	private void init(){
		animations = new HashMap<String,Animation>();
		
		//Load breaking platform animation
		SpriteSheet sheet = SpritesheetLoader.getInstance().getSpriteSheet("misc", 64, 64);
		Image[] frames = new Image[]{sheet.getSprite(4, 0).getSubImage(0, 42, 64, 22),
				sheet.getSprite(5, 0).getSubImage(0, 42, 64, 22),
				sheet.getSprite(6, 0).getSubImage(0, 42, 64, 22)};
		Animation a = new Animation(frames, 200);
		a.setLooping(false);
		animations.put(BASE_BREAKINGPLATFORM, a);
	}
	
	public Animation getAnimation(String name){
		return animations.get(name).copy();
	}
	
	public void addAnimation(Animation a, String n){
		animations.put(n, a);
	}
}
