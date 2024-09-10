package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Screen;
import com.mygdx.game.AstroRunSavePlanet;

public class StartScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture startBackgroundTexture;
    private Texture startTexture;
    private Texture exitTexture;
    private Rectangle startButtonBounds;
    private Rectangle exitButtonBounds;
    private AstroRunSavePlanet game;
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private String[] textLines;
    private float[] lineTimers;
    private float letterInterval;
    private float totalTime;

    public StartScreen(AstroRunSavePlanet game) {
        this.game = game;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.batch = game.getBatch();

        startBackgroundTexture = new Texture(Gdx.files.internal("backgrounds/start_background.png"));
        startTexture = new Texture(Gdx.files.internal("buttons/start_button.png"));
        exitTexture = new Texture(Gdx.files.internal("buttons/exit_button.png"));

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

        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Zebulon Bold.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = 60;
        titleParameter.color = Color.WHITE;
        titleFont = titleGenerator.generateFont(titleParameter);
        titleGenerator.dispose();

        FreeTypeFontGenerator textGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Zebulon.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter textParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        textParameter.size = 20;
        textParameter.color = Color.WHITE;
        textFont = textGenerator.generateFont(textParameter);
        textGenerator.dispose();

        textLines = new String[] {"Explore the Space", "Battle Aliens", "Save Planets !"};
        lineTimers = new float[textLines.length];
        letterInterval = 0.1f;
        totalTime = 0f;
    }

    @Override
    public void render(float delta) {
        totalTime += delta;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(startBackgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(startTexture, startButtonBounds.x, startButtonBounds.y, startButtonBounds.width, startButtonBounds.height);
        batch.draw(exitTexture, exitButtonBounds.x, exitButtonBounds.y, exitButtonBounds.width, exitButtonBounds.height);

        titleFont.setColor(Color.MAGENTA);
        titleFont.draw(batch, "ASTRO RUN", 100, Gdx.graphics.getHeight() - 50);

        titleFont.setColor(Color.MAROON);
        titleFont.draw(batch, "SAVE PLANET", 100, Gdx.graphics.getHeight() - 120);

        drawTextLetterByLetter(batch, textLines, lineTimers, letterInterval, totalTime);

        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPos = camera.unproject(touchPos);
            if (startButtonBounds.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new MainGameScreen(game));
            } else if (exitButtonBounds.contains(touchPos.x, touchPos.y)) {
                Gdx.app.exit();
            }
        }
    }

    private void drawTextLetterByLetter(SpriteBatch batch, String[] lines, float[] timers, float interval, float totalTime) {
        float startX = Gdx.graphics.getWidth() - 450;
        float startY = Gdx.graphics.getHeight() - 100;
        float lineHeight = 30;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int letterCount = Math.min((int) (totalTime / interval) - (i * 5), line.length());
            if (letterCount > 0) {
                String subString = line.substring(0, letterCount);
                textFont.draw(batch, subString, startX, startY - i * lineHeight);
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
        titleFont.dispose();
        textFont.dispose();
    }
}
