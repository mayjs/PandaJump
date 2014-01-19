package de.catchycube.doodleJump.game.generator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import de.catchycube.doodleJump.game.BreakingPlatform;
import de.catchycube.doodleJump.game.InGameState;
import de.catchycube.doodleJump.game.MovingPlatform;
import de.catchycube.doodleJump.game.Platform;
import de.catchycube.doodleJump.game.Player;
import de.catchycube.doodleJump.game.Spring;
import de.catchycube.doodleJump.loading.SpritesheetLoader;

public class Generator {
	private InGameState gameState;
	private static final float generationBorder=500f;
	protected float platformThickness=12;
	protected float platformWidth=64;
	
	private int possiblePlatformsInRow;
	private List<Biome> biomes;
	private Biome currentBiome, baseBiome;
	private Random rnd;
	
	protected static Image solidPlatform, breakingPlatform, movingPlatform; //platforms
	protected static Image spring; //sprites

	public Generator(InGameState state){
		gameState = state;
		platformThickness = 12 * state.getTextureScaling();
		platformWidth = 48 * state.getTextureScaling();
		
		possiblePlatformsInRow = (int)(state.getGameScreenBoundings().getWidth()/platformWidth);
		
		SpriteSheet sheet = SpritesheetLoader.getInstance().getSpriteSheet("misc", 64, 64);
		solidPlatform = sheet.getSprite(2, 1).getSubImage(8, 41, 48, 12);
		breakingPlatform = sheet.getSprite(3,0).getSubImage(6, 42, 52, 10);
		movingPlatform = sheet.getSprite(2, 0).getSubImage(0, 41, 64, 12);
		
		SpriteSheet spriteSheet = SpritesheetLoader.getInstance().getSpriteSheet("items", 64, 64);
		spring = spriteSheet.getSprite(2, 0).getSubImage(5, 27, 40, 32).getScaledCopy(0.5f);
		
		rnd = new Random();
		
		baseBiome = new NormalBiome(state, this, rnd);
		biomes = new LinkedList<Biome>();
		
		biomes.add(new BreakablePlatformsBiome(gameState, this, rnd));
		biomes.add(new MovingPlatformBiome(state, this, rnd));
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
			
			if(currentBiome == null || currentBiome instanceof NormalBiome){
				Collections.shuffle(biomes);
				for(Biome b : biomes){
					if(b.shouldEnter(maxPlatformScore)){
						currentBiome = b.createGeneratorInstance(maxPlatformScore);
						System.out.println("Entered " + currentBiome.toString());
						break;
					}
				}
				if(currentBiome == null){
					currentBiome = baseBiome.createGeneratorInstance(maxPlatformScore);
				}
			}
			
			
			
			currentBiome.generate(maximalJumpHeight, maxPlatformScore);
			
			if(currentBiome.isBiomeFinished()){
				currentBiome = null;
			}
		}
	}
	
	protected boolean intersectsAny(LinkedList<Platform> all, Rectangle toCheck){
		for(Platform p : all){
			if(p.getHitBounds().intersects(toCheck)){
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * Can be used to generate a number in the intervall [min,max]
	 * @param min the inclusive minimal number
	 * @param max the inclusive maximal number
	 * @return a randum number from min to max
	 */
	public int random(int min, int max){
		return min + rnd.nextInt(max - min + 1);
	}
	
	protected Platform createDirtPlatform(float x, float y){
		Rectangle platformBoundings = new Rectangle(x,y,platformWidth,platformThickness);
		return new Platform(platformBoundings, solidPlatform, gameState);
	}
	
	protected Platform createBreakingPlatform(float x, float y){
		Rectangle platformBoundings = new Rectangle(x, y, 0, 0);
		platformBoundings.setWidth(platformWidth + 4 * gameState.getTextureScaling());
		platformBoundings.setX(platformBoundings.getX() - 2 * gameState.getTextureScaling());
		platformBoundings.setHeight(platformThickness - 2*gameState.getTextureScaling());
		platformBoundings.setY(platformBoundings.getY() + gameState.getTextureScaling());
		return new BreakingPlatform(platformBoundings,breakingPlatform,gameState);
	}
	
	protected Platform createMovingPlatform(float x, float y, float minX, float maxX, float speed){
		Rectangle platformBoundings = new Rectangle(x, y, 0, 0);
		platformBoundings.setWidth(movingPlatform.getWidth() * gameState.getTextureScaling());
		platformBoundings.setHeight(movingPlatform.getHeight() * gameState.getTextureScaling());
		
		return new MovingPlatform(platformBoundings, movingPlatform, gameState, minX, maxX, speed);
	}
	
	protected Spring createSpring(float x, float y){
		Spring s = new Spring(spring, new Rectangle(x, y, spring.getWidth(), spring.getHeight()), gameState, null);
		
		return s;
	}
	
	protected int getSpringWidth(){
		return spring.getWidth();
	}
}
