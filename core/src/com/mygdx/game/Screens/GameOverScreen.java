package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.AstroRunSavePlanet;
import com.mygdx.game.GameSound;
import com.mygdx.game.Score;

public class GameOverScreen implements Screen {
    private final AstroRunSavePlanet game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture gameOverBackground;
    private Texture retryButtonTexture;
    private Texture exitButtonTexture;
    private Texture menuButtonTexture;
    private Texture victoryPicTexture;
    private GameSound sound;
    private Rectangle retryButtonBounds;
    private Rectangle exitButtonBounds;
    private Rectangle menuButtonBounds;
    private BitmapFont Font;
    private Score score;

    public GameOverScreen(AstroRunSavePlanet game) {
        this.game = game;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.batch = game.getBatch();
        sound = new GameSound();
        gameOverBackground = new Texture(Gdx.files.internal("backgrounds/gameOver_background.png"));
        retryButtonTexture = new Texture(Gdx.files.internal("buttons/retry_button.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("buttons/gameOver_exit_button.png"));
        menuButtonTexture = new Texture(Gdx.files.internal("buttons/menu_button.png"));
        victoryPicTexture = new Texture(Gdx.files.internal("astronaut/victory_pic.png"));

        float buttonWidth = 200;
        float buttonHeight = 80;
        retryButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2 - buttonWidth / 2,
                Gdx.graphics.getHeight() / 2 - buttonHeight / 2 - 100,
                buttonWidth,
                buttonHeight
        );
        menuButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2 - buttonWidth / 2,
                Gdx.graphics.getHeight() / 2 - buttonHeight / 2 - 200,
                buttonWidth,
                buttonHeight
        );
        exitButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2 - buttonWidth / 2,
                Gdx.graphics.getHeight() / 2 - buttonHeight / 2 - 300,
                buttonWidth,
                buttonHeight
        );
        score = new Score();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Zebulon Bold.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.WHITE;
        Font = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(gameOverBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.draw(retryButtonTexture, retryButtonBounds.x, retryButtonBounds.y, retryButtonBounds.width, retryButtonBounds.height);
        batch.draw(exitButtonTexture, exitButtonBounds.x, exitButtonBounds.y, exitButtonBounds.width, exitButtonBounds.height);
        batch.draw(menuButtonTexture, menuButtonBounds.x, menuButtonBounds.y, menuButtonBounds.width, menuButtonBounds.height);

        String currentScoreText = "Score: " + score.getCurrentScore();
        String highScoreText = "High Score: " + score.getHighScore();

        float currentScoreX = 100;
        float currentScoreY = Gdx.graphics.getHeight()/2f + 150;

        float highScoreX = Gdx.graphics.getWidth() - 400;
        float highScoreY = Gdx.graphics.getHeight()/2f + 150;
        Font.getData().setScale(2f);
        Font.setColor(Color.RED);
        Font.draw(batch,"GAME OVER!",Gdx.graphics.getWidth()/2f-250,Gdx.graphics.getHeight()-100);
        Font.getData().setScale(1f);
        Font.setColor(Color.WHITE);
        Font.draw(batch, currentScoreText, currentScoreX, currentScoreY);
        Font.draw(batch, highScoreText, highScoreX, highScoreY);

        if (score.getCurrentScore() >= score.getHighScore()) {
            Font.draw(batch, "HURRAY! HIGH SCORE!", 100, Gdx.graphics.getHeight() / 2f + 50);
            batch.draw(victoryPicTexture, 150, Gdx.graphics.getHeight() / 2f -270, 250, 250);
        }

        batch.end();
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPos = camera.unproject(touchPos);

            if (retryButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                game.setScreen(new MainGameScreen(game));
            } else if (menuButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                game.setScreen(new StartScreen(game));
            } else if (exitButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        gameOverBackground.dispose();
        retryButtonTexture.dispose();
        exitButtonTexture.dispose();
        sound.dispose();
        victoryPicTexture.dispose();
    }
}
