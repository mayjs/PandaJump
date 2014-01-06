package de.catchycube.doodleJump.game.generator;

import java.util.Random;

import de.catchycube.doodleJump.game.InGameState;
import de.catchycube.doodleJump.game.Platform;

public class BreakablePlatformsBiome extends Biome{

	private int platformCounter;
	private float minHeightDifferenceFac=0.3f, maxHeightDifferenceFac=0.5f;
	private static float chance=0.9f;
	private static int cooldown;
	
	public BreakablePlatformsBiome(InGameState state, Generator generator,
			Random rnd) {
		super(state, generator, rnd);
	}

	public BreakablePlatformsBiome(InGameState state, Generator generator,
			Random rnd, int platforms) {
		this(state,generator,rnd);
		this.platformCounter = platforms;
	}
	
	@Override
	public boolean checkEnterConditions(int score) {
		return score > 540;
	}

	@Override
	public boolean shouldEnter(int score) {
		if(checkEnterConditions(score)) cooldown--;
		return checkEnterConditions(score) && rnd.nextFloat() > chance && cooldown <= 0;
	}

	@Override
	public Biome createGeneratorInstance(int score) {
		cooldown = 20;
		return new BreakablePlatformsBiome(state, generator, rnd, generator.random(20, score>1000?30:25));
	}

	@Override
	public void generate(float playerJumpHeight, int maxPlatformScore) {
		float nextHeight = (minHeightDifferenceFac + ((maxHeightDifferenceFac-minHeightDifferenceFac)*rnd.nextFloat()))*playerJumpHeight;
		float nextY = state.getAllPlatforms().getLast().getHitBounds().getY() + nextHeight;
		float nextX = generator.random(0, (int)(state.getGameScreenBoundings().getWidth()-generator.platformWidth));
		Platform p = generator.createBreakingPlatform(nextX, nextY);
		state.addPlatform(p);
		platformCounter--;
	}

	@Override
	public boolean isBiomeFinished() {
		return platformCounter <= 0;
	}
	
	@Override
	public String toString(){
		return "Breakable platforms biome with " + platformCounter + " platforms left";
	}
}
