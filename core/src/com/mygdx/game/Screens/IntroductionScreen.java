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
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.AstroRunSavePlanet;
import com.mygdx.game.GameSound;

public class IntroductionScreen implements Screen {
    private final AstroRunSavePlanet game;
    private Texture backgroundTexture;
    private BitmapFont font;
    private String description;
    private Texture backButtonTexture;
    private Rectangle backButtonBounds;
    private GameSound sound;
    private OrthographicCamera camera;

    public IntroductionScreen(AstroRunSavePlanet game) {
        this.game = game;
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/introductionScreen_background.png"));
        sound = new GameSound();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Indulta.otf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 30;
        font = generator.generateFont(parameter);
        generator.dispose();
        backButtonTexture = new Texture(Gdx.files.internal("buttons/back_button.png"));
        backButtonBounds = new Rectangle(Gdx.graphics.getWidth()/2-75,20,150, 50);

        description = "Astro Run Save Planet is a 2D space adventure where you explore planets, battle aliens, " +
                "and collect power-ups to save the galaxy. Navigate through obstacles and complete missions " +
                "to unlock new levels and upgrades!";
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = game.getBatch();
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font.setColor(Color.MAGENTA);
        font.getData().setScale(1.2f);
        font.draw(batch, "WELCOME TO ASTRO RUN SAVE PLANET", Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() - 80);

        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        float descriptionWidth = Gdx.graphics.getWidth() - 200;
        font.draw(batch, description, 100, Gdx.graphics.getHeight() -150, descriptionWidth, Align.left, true);
        font.setColor(Color.PURPLE);
        font.draw(batch,"Developed by:  MOHAI MINUL ISLAM NIROB(2021831049)",100,170);
        font.draw(batch,"MD RAHIMUL HASSAN(2021831050)",330,120);
        batch.draw(backButtonTexture, backButtonBounds.x, backButtonBounds.y,backButtonBounds.width,backButtonBounds.height);
        batch.end();
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (backButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                game.setScreen(new StartScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
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
        sound.dispose();
    }
}
