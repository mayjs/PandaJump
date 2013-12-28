package de.catchycube.doodleJump.transition;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.Transition;


public class FixedAlphaFadeOutTransition implements Transition{
	protected Color color;
	protected float targetAlpha, beginAlpha;
	protected int fadeTime;
	
	public FixedAlphaFadeOutTransition(Color color, int fadeTime){
		this.fadeTime = fadeTime;
		this.color = new Color(color);
		targetAlpha = color.a;
		this.color.a = 0;
	}
	
	public FixedAlphaFadeOutTransition(Color color, float beginAlpha, float endAlpha, int fadeTime){
		this(color,fadeTime);
		targetAlpha = endAlpha;
		this.beginAlpha = beginAlpha;
	}

	@Override
	public void init(GameState firstState, GameState secondState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isComplete() {
		return color.a >= targetAlpha;
	}

	@Override
	public void postRender(StateBasedGame game, GameContainer container,
			Graphics g) throws SlickException {
		Color old = g.getColor();
		g.setColor(color);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		g.setColor(old);
	}

	@Override
	public void preRender(StateBasedGame game, GameContainer container,
			Graphics g) throws SlickException {
	}

	@Override
	public void update(StateBasedGame game, GameContainer container, int delta)
			throws SlickException {
		color.a += delta * ((targetAlpha - beginAlpha) / fadeTime);
		if (color.a > targetAlpha) {
			color.a = targetAlpha;
		}
	}
}
