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
import com.mygdx.game.GameSound;

public class StartScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture startBackgroundTexture;
    private Texture startTexture;
    private Texture exitTexture;
    private Texture instructionTexture, introductionTexture;
    private Texture galacticTexture,trainingTexture;
    private Rectangle startButtonBounds;
    private Rectangle exitButtonBounds,instructionButtonBounds, introductionButtonBounds;
    private Rectangle galacticBounds, trainingBounds;
    private AstroRunSavePlanet game;
    private GameSound sound;
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private String[] textLines;
    private float[] lineTimers;
    private float letterInterval;
    private float totalTime;
    private long backgroundSoundId;
    boolean levelShow;

    public StartScreen(AstroRunSavePlanet game) {
        this.game = game;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.batch = game.getBatch();
        sound = new GameSound();
        startBackgroundTexture = new Texture(Gdx.files.internal("backgrounds/start_background.png"));
        startTexture = new Texture(Gdx.files.internal("buttons/start_button.png"));
        exitTexture = new Texture(Gdx.files.internal("buttons/exit_button.png"));
        instructionTexture= new Texture(Gdx.files.internal("buttons/instruction_button.png"));
        introductionTexture= new Texture(Gdx.files.internal("buttons/introduction_button.png"));
        galacticTexture = new Texture(Gdx.files.internal("buttons/galactic_button.png"));
        trainingTexture = new Texture(Gdx.files.internal("buttons/training_button.png"));
        float startButtonWidth = 300;
        float startButtonHeight = 80;
        float exitButtonWidth = 300;
        float exitButtonHeight = 80;
        float instructionButtonWidth = 200;
        float instructionButtonHeight = 80;
        float introductionButtonWidth = 200;
        float introductionButtonHeight = 80;
        float galacticButtonWidth = 230;
        float trainingButtonWidth = 230;
        float galacticButtonHeight = 80;
        float trainingButtonHeight = 80;
        levelShow = false;

        startButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2 - startButtonWidth / 2,
                Gdx.graphics.getHeight() / 2 - startButtonHeight / 2,
                startButtonWidth,
                startButtonHeight
        );

        exitButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2 - exitButtonWidth / 2,
                Gdx.graphics.getHeight() / 2 - startButtonHeight / 2 - 100,
                exitButtonWidth,
                exitButtonHeight
        );
        instructionButtonBounds = new Rectangle(
                Gdx.graphics.getWidth()-220,
                20,
                instructionButtonWidth,
                instructionButtonHeight
        );
        introductionButtonBounds = new Rectangle(
                20,
                20,
                introductionButtonWidth,
                introductionButtonHeight
        );
        galacticBounds = new Rectangle(
            (Gdx.graphics.getWidth() / 2f) + 160,
            (Gdx.graphics.getHeight() / 2f) - 85,
            galacticButtonWidth,
            galacticButtonHeight
        );
        trainingBounds = new Rectangle(
                (Gdx.graphics.getWidth() / 2f) + 160,
                (Gdx.graphics.getHeight() / 2f) + 5,
                trainingButtonWidth,
                trainingButtonHeight
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
        batch.draw(introductionTexture, introductionButtonBounds.x, introductionButtonBounds.y, introductionButtonBounds.width, introductionButtonBounds.height);
        batch.draw(instructionTexture, instructionButtonBounds.x, instructionButtonBounds.y, instructionButtonBounds.width, instructionButtonBounds.height);
        if(levelShow){
            batch.draw(trainingTexture, trainingBounds.x, trainingBounds.y, trainingBounds.width, trainingBounds.height);
            batch.draw(galacticTexture, galacticBounds.x, galacticBounds.y, galacticBounds.width, galacticBounds.height);
        }
        titleFont.setColor(Color.MAGENTA);
        titleFont.draw(batch, "ASTRO RUN", 100, Gdx.graphics.getHeight() - 50);

        titleFont.setColor(Color.MAROON);
        titleFont.draw(batch, "SAVE PLANET", 100, Gdx.graphics.getHeight() - 120);

        drawTextLetterByLetter(batch, textLines, lineTimers, letterInterval, totalTime);

        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPos = camera.unproject(touchPos);
            if (startButtonBounds.contains(touchPos.x, touchPos.y) && !levelShow) {
                sound.playClick();
                levelShow = true;
                //game.setScreen(new MainGameScreen(game));
            } else if (exitButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                Gdx.app.exit();
            }else if (instructionButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                game.setScreen(new InstructionScreen(game));
            } else if (introductionButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                game.setScreen(new IntroductionScreen(game));
            }
            else if (introductionButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                game.setScreen(new IntroductionScreen(game));
            } else if (galacticBounds.contains(touchPos.x, touchPos.y) && levelShow) {
                sound.playClick();
                game.setScreen(new MainGameScreen(game));
            } else if (trainingBounds.contains(touchPos.x, touchPos.y) && levelShow) {
                sound.playClick();
                //game.setScreen(new TrainingScreen(game));
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
        sound.stopStartBackground();
    }

    @Override
    public void show() {
        backgroundSoundId = sound.playStartBackground();
    }

    @Override
    public void dispose() {
        startBackgroundTexture.dispose();
        startTexture.dispose();
        exitTexture.dispose();
        instructionTexture.dispose();
        introductionTexture.dispose();
        trainingTexture.dispose();
        galacticTexture.dispose();
        titleFont.dispose();
        sound.stopStartBackground();
        sound.dispose();
        textFont.dispose();
    }
}
