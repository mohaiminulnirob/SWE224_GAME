package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.AstroRunSavePlanet;

public class InstructionScreen implements Screen {
    private final AstroRunSavePlanet game;
    private Texture backgroundTexture;
    private BitmapFont font;
    private float timePassed = 0f;
    private String[] instructions;
    private int currentInstructionCount = 0;
    private Texture backButtonTexture;
    private Rectangle backButtonBounds;
    private OrthographicCamera camera;

    public InstructionScreen(AstroRunSavePlanet game) {
        this.game = game;
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/instructionScreen_background.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Indulta.otf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 25;
        font = generator.generateFont(parameter);
        generator.dispose();

        instructions = new String[] {
                "** Explore the galaxy and discover new planets.",
                "** Avoid obstacles and alien ships as you navigate.",
                "** Use your jetpack boost to escape danger quickly.",
                "** Collect coins and power-ups to increase your score.",
                "** Defeat alien forces to save the planet and move to the next level.",
                "** Pay attention to your health meter to avoid losing lives.",
                "** Complete missions to unlock new levels and challenges.",
                "** Upgrade your astronaut’s abilities for tougher enemies."
        };

        backButtonTexture = new Texture(Gdx.files.internal("buttons/back_button.png"));
        backButtonBounds = new Rectangle(Gdx.graphics.getWidth()/2-75,20,150, 50);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        timePassed += delta;

        if (currentInstructionCount < instructions.length && timePassed > 1f) {
            currentInstructionCount++;
            timePassed = 0f;
        }

        camera.update();

        SpriteBatch batch = game.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font.getData().setScale(2);
        font.setColor(Color.GREEN);
        font.draw(batch, "INSTRUCTIONS", Gdx.graphics.getWidth() / 2f - 150, Gdx.graphics.getHeight() - 50);

        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        for (int i = 0; i < currentInstructionCount; i++) {
            font.draw(batch, instructions[i], 100, Gdx.graphics.getHeight() - 150 - (i * 70));
        }

        batch.draw(backButtonTexture, backButtonBounds.x, backButtonBounds.y,backButtonBounds.width,backButtonBounds.height);

        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (backButtonBounds.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new StartScreen(game));
                dispose();
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
    public void dispose() {
        backgroundTexture.dispose();
        font.dispose();
        backButtonTexture.dispose();
    }
}
