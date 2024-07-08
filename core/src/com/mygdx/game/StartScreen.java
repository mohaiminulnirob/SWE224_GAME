package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Screen;  // Import the Screen interface

public class StartScreen implements Screen {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture startBackgroundTexture;
    private Texture startTexture;
    private Texture exitTexture;
    private Rectangle startButtonBounds;
    private Rectangle exitButtonBounds;
    private AstroRunSavePlanet game;

    public StartScreen(AstroRunSavePlanet game) {
        this.game = game;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.batch = game.getBatch();

        startBackgroundTexture = new Texture(Gdx.files.internal("start_background.png"));
        startTexture = new Texture(Gdx.files.internal("start_button.png"));
        exitTexture = new Texture(Gdx.files.internal("exit_button.png"));

        // Define fixed bounds for the start and exit buttons
        float startButtonWidth = 400;
        float startButtonHeight = 100;
        float exitButtonWidth = 400;
        float exitButtonHeight = 100;

        startButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2 - startButtonWidth / 2,
                Gdx.graphics.getHeight() / 2 - startButtonHeight / 2,
                startButtonWidth,
                startButtonHeight
        );

        exitButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2 - exitButtonWidth / 2,
                Gdx.graphics.getHeight() / 2 - startButtonHeight / 2 - 150,
                exitButtonWidth,
                exitButtonHeight
        );
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw the start background image
        batch.draw(startBackgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw the start button within the fixed bounds
        batch.draw(startTexture, startButtonBounds.x, startButtonBounds.y, startButtonBounds.width, startButtonBounds.height);

        // Draw the exit button within the fixed bounds
        batch.draw(exitTexture, exitButtonBounds.x, exitButtonBounds.y, exitButtonBounds.width, exitButtonBounds.height);

        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPos = camera.unproject(touchPos); // Convert touch coordinates to screen coordinates
            if (startButtonBounds.contains(touchPos.x, touchPos.y)) {
                game.startMainGame(); // Transition to the main game screen
            } else if (exitButtonBounds.contains(touchPos.x, touchPos.y)) {
                Gdx.app.exit(); // Exit the game
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }

    @Override
    public void dispose() {
        startBackgroundTexture.dispose();
        startTexture.dispose();
        exitTexture.dispose();
    }
}
