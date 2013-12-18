package de.catchycube.doodleJump.game;

import java.util.LinkedList;
import java.util.Random;

import org.lwjgl.Sys;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import de.catchycube.doodleJump.loading.SpritesheetLoader;

public class Generator {
	private InGameState gameState;
	private static final float generationBorder=500f;
	private float platformThickness=12;
	private float platformWidth=64;
	
	private int possiblePlatformsInRow;
	private boolean[] possiblePlatformsFlags;
	
	private Random rnd;
	
	private Image solidPlatform, breakingPlatform;

	public Generator(InGameState state){
		gameState = state;
		platformThickness = 12 * state.getTextureScaling();
		platformWidth = 48 * state.getTextureScaling();
		
		possiblePlatformsInRow = (int)(state.getGameScreenBoundings().getWidth()/platformWidth);
		possiblePlatformsFlags = new boolean[possiblePlatformsInRow];
		
		SpriteSheet sheet = SpritesheetLoader.getInstance().getSpriteSheet("misc", 64, 64);
		solidPlatform = sheet.getSprite(2, 1).getSubImage(8, 41, 48, 12);
		breakingPlatform = sheet.getSprite(3,0).getSubImage(6, 42, 52, 10);
		
		rnd = new Random();
	}
	
	public Generator(long seed, InGameState state){
		this(state);
		rnd = new Random(seed);
	}
	
	public void update(){
		LinkedList<Platform> platforms = gameState.getAllPlatforms();
		Player player = gameState.getPlayer();
		float maximalJumpHeight = 0.5f * player.getMaxSpeed() * player.getMaxSpeed() / player.getGravitation();
		while(Math.abs(platforms.getLast().getHitBounds().getY() - player.getBounds().getY())<generationBorder){
			int maxPlatformScore = (int)(platforms.getLast().getHitBounds().getY()*gameState.getScoreFactor());
			
			float nextY = platforms.getLast().getHitBounds().getY()+getHeightOfNextPlatform(maximalJumpHeight, maxPlatformScore);
			
			int newPlatformScore = (int)(nextY*gameState.getScoreFactor());
			int numberOfPlatforms = getNumberOfPlatforms(newPlatformScore);
			
			LinkedList<Platform> generatedPlatforms = new LinkedList<Platform>();
			for(int i = 0; i < numberOfPlatforms; i++){
				Rectangle platformBoundings = new Rectangle(random(0, (int)(gameState.getGameScreenBoundings().getWidth()-platformWidth)), nextY, platformWidth, platformThickness);
				for(int failCount = 0; intersectsAny(generatedPlatforms, platformBoundings) && failCount < 100; failCount++){
					platformBoundings.setX(random(0, (int)(gameState.getGameScreenBoundings().getWidth()-platformWidth)));
				}
				
				Platform p = null;
				//Select platform type here
				if(rnd.nextBoolean())
					p = new Platform(platformBoundings, solidPlatform, gameState);
				else{
					platformBoundings.setWidth(platformWidth + 4 * gameState.getTextureScaling());
					platformBoundings.setX(platformBoundings.getX() - 2 * gameState.getTextureScaling());
					platformBoundings.setHeight(platformThickness - 2*gameState.getTextureScaling());
					platformBoundings.setY(platformBoundings.getY() + gameState.getTextureScaling());
					p = new BreakingPlatform(platformBoundings, breakingPlatform, gameState);
				}
				
				gameState.addPlatform(p);
				platforms = gameState.getAllPlatforms();
				generatedPlatforms.add(p);
			}
			for(int i = 0; i < possiblePlatformsInRow; i++){
				possiblePlatformsFlags[i] = false;
			}
		}
	}
	
	private boolean intersectsAny(LinkedList<Platform> all, Rectangle toCheck){
		for(Platform p : all){
			if(p.getHitBounds().intersects(toCheck)){
				return true;
			}
		}
		return false;
	}
	
	private int getMinPlatformInRowCount(int score){
		return 1;
	}
	
	private int getMaxPlatformInRowCount(int score){
		return 1;
	}
	
	private float getMinimalHeightDifference(float maxJmpHeight, int score){
		return maxJmpHeight*(score<100?0.2f:score<500?0.33f:score<800?0.43f:score<1000?0.53f:score<1200?0.63f:score<1500?0.73f:score<2000?0.83f:0.93f);
	}
	
	private float getMaximalHeightDifference(float maxJmpHeight, int score){
		return maxJmpHeight*(score<100?0.33f:score<500?0.43f:score<800?0.53f:score<1000?0.63f:score<1200?0.73f:score<1500?0.83f:score<2000?0.93f:1f);
	}
	
	private int getNumberOfPlatforms(int score){
		int min = getMinPlatformInRowCount(score);
		int max = getMaxPlatformInRowCount(score);
		
		return random(min, max);
	}
	
	private float getHeightOfNextPlatform(float maxJmpHeight, int score){
		int min = (int)(getMinimalHeightDifference(maxJmpHeight, score)/platformThickness);
		int max = (int)(getMaximalHeightDifference(maxJmpHeight, score)/platformThickness);
		
		return random(min,max) * platformThickness;
	}
	
	/**
	 * Can be used to generate a number in the intervall [min,max]
	 * @param min the inclusive minimal number
	 * @param max the inclusive maximal number
	 * @return a randum number from min to max
	 */
	private int random(int min, int max){
		return min + rnd.nextInt(max - min + 1);
	}
}
