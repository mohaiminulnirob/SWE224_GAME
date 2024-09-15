package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Screen;
import com.mygdx.game.*;
import java.util.ArrayList;
import java.util.Iterator;

public class TrainingScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture background;
    private static GameSound sound;
    private Texture astronautTexture;
    private Texture bulletTexture;
    private Texture pauseButtonTexture;
    private Texture exitButtonTexture;
    private Texture resumeButtonTexture;
    private Texture yesButtonTexture;
    private Texture noButtonTexture;
    private Astronaut astronaut;
    private Array<Bullet> bullets;
    private Planet planet;
    public static boolean Successful,End;
    private static int remainingBullets;
    private static int score;
    private BitmapFont Text;
    private boolean isPaused = false;
    private boolean increase = true;
    private Rectangle pauseButtonBounds;
    private Rectangle resumeButtonBounds;
    private Rectangle exitButtonBounds;
    private Rectangle yesButtonBounds;
    private Rectangle noButtonBounds;
    private float backgroundX;
    private AstroRunSavePlanet game;

    public TrainingScreen(AstroRunSavePlanet game) {
        this.game = game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = game.getBatch();
        sound = new GameSound();
        background = new Texture(Gdx.files.internal("backgrounds/space_background.png"));
        astronautTexture = new Texture(Gdx.files.internal("astronaut/aeroplane.png"));
        bulletTexture = new Texture(Gdx.files.internal("bullets/bullet.png"));
        pauseButtonTexture = new Texture(Gdx.files.internal("buttons/pause_button.png"));
        resumeButtonTexture = new Texture(Gdx.files.internal("buttons/resume_button.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("buttons/exit_button.png"));
        yesButtonTexture = new Texture(Gdx.files.internal("buttons/yes_button.png"));
        noButtonTexture = new Texture(Gdx.files.internal("buttons/no_button.png"));
        astronaut = new Astronaut(100, 300, astronautTexture);
        bullets = new Array<>();
        planet = new Planet();
        remainingBullets = 10;
        score = 0;
        Successful=false;
        End =false;
        Text = new BitmapFont();

        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Indulta.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = 30;
        titleParameter.color = Color.WHITE;
        Text = titleGenerator.generateFont(titleParameter);
        titleGenerator.dispose();

        float buttonWidth = 100;
        float buttonHeight = 50;
        float buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        float buttonY = Gdx.graphics.getHeight() - buttonHeight - 10;

        pauseButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        resumeButtonBounds = new Rectangle(buttonX - buttonWidth - 10, buttonY, buttonWidth, buttonHeight);
        exitButtonBounds = new Rectangle(buttonX + buttonWidth + 10, buttonY, buttonWidth, buttonHeight);
        yesButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2 -105,
                Gdx.graphics.getHeight() / 2 - buttonHeight / 2 - 200,
                buttonWidth,
                buttonHeight
        );
        noButtonBounds = new Rectangle(
                Gdx.graphics.getWidth() / 2 + 5,
                Gdx.graphics.getHeight() / 2 - buttonHeight / 2 - 200,
                buttonWidth,
                buttonHeight
        );
        backgroundX = 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(score>0 && score%20==0 && increase){
            increase= false;
            float shootInterval=planet.getShootInterval();
            if(shootInterval>0.5f)
                planet.setShootInterval(-.1f);
            float planet_speed = planet.getPLANET_SPEED();
            if(planet_speed<=150f)
                planet.setPLANET_SPEED(10);

        }
        else if(score>=0 && score%20!=0)
            increase=true;
        if (!isPaused) {
            if(score==30 && End == false)
                Successful=true;
            astronaut.handleInput();
            astronaut.update(delta);

            if (Gdx.input.isKeyJustPressed(Keys.ENTER) && remainingBullets > 0 && !astronaut.isDestroyed()) {
                sound.playShoot();
                float bulletX = astronaut.getPosition().x + astronautTexture.getWidth() * 0.5f;
                float bulletY = astronaut.getPosition().y + astronautTexture.getHeight() * 0.18f;
                bullets.add(new Bullet(bulletX, bulletY, 1, 0, 300, bulletTexture));
                remainingBullets--;
            }

            planet.update(delta, astronaut.getPosition(), null);

            for (Bullet bullet : bullets) {
                bullet.update(delta);
            }

            Iterator<Bullet> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (!bullet.isActive()) {
                    bulletIterator.remove();
                } else if (planet.isColliding(bullet)) {
                    planet.hit();
                    bulletIterator.remove();
                }
            }

            ArrayList<Bullet> alienBullets = planet.getAlienBullets();
            Iterator<Bullet> alienBulletIterator = alienBullets.iterator();
            while (alienBulletIterator.hasNext()) {
                Bullet alienBullet = alienBulletIterator.next();
                if (!alienBullet.isActive() || alienBullet.getPosition().x < 0 || alienBullet.getPosition().x > Gdx.graphics.getWidth()
                        || alienBullet.getPosition().y < 0 || alienBullet.getPosition().y > Gdx.graphics.getHeight()) {
                    alienBulletIterator.remove();
                } else if (astronaut.isColliding(alienBullet)) {
                    sound.playExplosion();
                    astronaut.Collision();
                    alienBulletIterator.remove();
                }
            }

            if (astronaut.isCollidingWithPlanet(planet) && planet.effectiveness()) {
                sound.playCollision();
                planet.MakeInactive();
                astronaut.Collision();
            }

            backgroundX -= 200 * delta;
            if (backgroundX <= -Gdx.graphics.getWidth()) {
                backgroundX += Gdx.graphics.getWidth();
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, backgroundX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, backgroundX + Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }

        planet.render(batch, isPaused ? 0 : delta);
        astronaut.render(batch);

        if (isPaused) {
            batch.draw(resumeButtonTexture, resumeButtonBounds.x, resumeButtonBounds.y, resumeButtonBounds.width, resumeButtonBounds.height);
            batch.draw(exitButtonTexture, exitButtonBounds.x, exitButtonBounds.y, exitButtonBounds.width, exitButtonBounds.height);
        } else {
            batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y, pauseButtonBounds.width, pauseButtonBounds.height);
        }
        Text.setColor(Color.GREEN);
        Text.getData().setScale(1f);
        Text.draw(batch, "Score: " + score, Gdx.graphics.getWidth() - 380, Gdx.graphics.getHeight() - 17);
        Text.setColor(Color.MAGENTA);
        Text.draw(batch, "Bullets: " + remainingBullets, Gdx.graphics.getWidth() - 190, Gdx.graphics.getHeight() - 17);

        Text.setColor(Color.RED);
        Text.getData().setScale(2f);
            if (Successful) {
                sound.pauseGameBackground();
                Text.setColor(Color.BLUE);
                Text.draw(batch, "Training Successful!", Gdx.graphics.getWidth() / 2f - 200, Gdx.graphics.getHeight() / 2f + 25);
                End=true;
                Text.draw(batch,"Want to Continue?",Gdx.graphics.getWidth()/2f -200,Gdx.graphics.getHeight()/2f -100);
                batch.draw(yesButtonTexture, yesButtonBounds.x, yesButtonBounds.y, yesButtonBounds.width, yesButtonBounds.height);
                batch.draw(noButtonTexture, noButtonBounds.x, noButtonBounds.y, noButtonBounds.width, noButtonBounds.height);
                isPaused=true;
            }
        if (astronaut.isMoveOutOfScreen()) {
            Text.draw(batch,"Want to try Again?",Gdx.graphics.getWidth()/2f -200,Gdx.graphics.getHeight()/2f -100);
            batch.draw(yesButtonTexture, yesButtonBounds.x, yesButtonBounds.y, yesButtonBounds.width, yesButtonBounds.height);
            batch.draw(noButtonTexture, noButtonBounds.x, noButtonBounds.y, noButtonBounds.width, noButtonBounds.height);
        }
        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (!isPaused && pauseButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.pauseGameBackground();
                sound.playClick();
                isPaused = true;
            } else if (isPaused && resumeButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.resumeGameBackground();
                sound.playClick();
                isPaused = false;
            } else if (isPaused && exitButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                dispose();
                game.setScreen(new StartScreen(game));
            }else if (isPaused && yesButtonBounds.contains(touchPos.x, touchPos.y)) {
                isPaused = false;
                sound.playClick();
                if (Successful) {
                    sound.resumeGameBackground();
                    Successful=false;
                } else {
                    dispose();
                    game.setScreen(new TrainingScreen(game));
                }
            }
            else if (isPaused && noButtonBounds.contains(touchPos.x, touchPos.y)) {
                sound.playClick();
                dispose();
                game.setScreen(new StartScreen(game));
            }
        }

        if (astronaut.isDestroyed()) {
            sound.stopGameBackground();
            isPaused=true;
        }
    }

    public static void UpdateScore() {
        score += 10;
        UpdateRemBullets();
    }
    public static void UpdateRemBullets() {
        remainingBullets += 10;
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
        sound.stopGameBackground();
    }

    @Override
    public void show() {
        long backgroundSoundId = sound.playGameBackground();
    }

    @Override
    public void dispose() {
        background.dispose();
        astronautTexture.dispose();
        bulletTexture.dispose();
        astronaut.dispose();
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        planet.dispose();
        sound.stopGameBackground();
        sound.dispose();
        Text.dispose();
    }
}
