package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AstroRunSavePlanet extends Game {
	private StartScreen startScreen;
	private MainGameScreen mainGameScreen;
	private SpriteBatch batch;
	private boolean isPaused;

	@Override
	public void create() {
		batch = new SpriteBatch();
		startScreen = new StartScreen(this);
		mainGameScreen = new MainGameScreen(this);
		setScreen(startScreen);
		isPaused = false;
	}

	public void startMainGame() {
		setScreen(mainGameScreen);
	}

	public void pauseGame() {
		isPaused = true;
		mainGameScreen.pause();
	}

	public void resumeGame() {
		isPaused = false;
		mainGameScreen.resume();
	}

	public boolean isPaused() {
		return isPaused;
	}

	@Override
	public void dispose() {
		batch.dispose();
		startScreen.dispose();
		mainGameScreen.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
