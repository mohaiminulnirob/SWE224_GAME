package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Screens.StartScreen;

public class AstroRunSavePlanet extends Game {
	private StartScreen startScreen;
	private SpriteBatch batch;

	@Override
	public void create() {
		batch = new SpriteBatch();
		startScreen = new StartScreen(this);
		setScreen(startScreen);
	}

	@Override
	public void dispose() {
		batch.dispose();
		startScreen.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
