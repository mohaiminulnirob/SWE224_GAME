package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MainGameScreen implements com.badlogic.gdx.Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture background;
    private Texture astronautTexture;
    private Texture pauseButtonTexture;
    private Texture resumeButtonTexture;
    private com.mygdx.game.Astronaut astronaut;
    private boolean isPaused = false;
    private Rectangle pauseButtonBounds;
    private Rectangle resumeButtonBounds;

    public MainGameScreen(com.mygdx.game.AstroRunSavePlanet game) {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = game.getBatch();

        background = new Texture(Gdx.files.internal("space_background.png"));
        astronautTexture = new Texture(Gdx.files.internal("aeroplane.png"));
        pauseButtonTexture = new Texture(Gdx.files.internal("pause_button.png"));
        resumeButtonTexture = new Texture(Gdx.files.internal("resume_button.png"));
        astronaut = new com.mygdx.game.Astronaut(100, 300, astronautTexture);

        float buttonWidth = 100;
        float buttonHeight = 50;
        float buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        float buttonY = Gdx.graphics.getHeight() - buttonHeight - 10; // 10 pixels from the top

        pauseButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        resumeButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);

    }
    @Override
    public void render ( float delta){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isPaused) {
            astronaut.handleInput();
            astronaut.update(delta);
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        astronaut.render(batch);

        if (isPaused) {
            batch.draw(resumeButtonTexture, resumeButtonBounds.x, resumeButtonBounds.y, resumeButtonBounds.width, resumeButtonBounds.height);
        } else {
            batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y, pauseButtonBounds.width, pauseButtonBounds.height);
        }

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (pauseButtonBounds.contains(touchPos.x, touchPos.y) && !isPaused) {
                pause();
            } else if (resumeButtonBounds.contains(touchPos.x, touchPos.y) && isPaused) {
                resume();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }

    @Override
    public void dispose() {
        background.dispose();
        astronautTexture.dispose();
        astronaut.dispose();
    }

}