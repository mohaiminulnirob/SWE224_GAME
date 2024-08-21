package com.mygdx.game;

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
import java.util.ArrayList;
import java.util.Iterator;

public class MainGameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture background;
    private Texture astronautTexture;
    private Texture bulletTexture;
    private Texture alienPlanetTexture;
    private Texture savedPlanetTexture;
    private Texture pauseButtonTexture;
    private Texture resumeButtonTexture;
    private Astronaut astronaut;
    private Array<Bullet> bullets;
    private Planet planet;
    private static int remainingBullets;
    private static int score;
    private BitmapFont BulletText;
    private BitmapFont scoreText;
    private boolean isPaused = false;
    private Rectangle pauseButtonBounds;
    private Rectangle resumeButtonBounds;
    private float backgroundX;

    public MainGameScreen(AstroRunSavePlanet game) {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = game.getBatch();

        background = new Texture(Gdx.files.internal("space_background.png"));
        astronautTexture = new Texture(Gdx.files.internal("aeroplane.png"));
        bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
        alienPlanetTexture = new Texture(Gdx.files.internal("alien_planet.png"));
        savedPlanetTexture = new Texture(Gdx.files.internal("saved_planet.png"));
        pauseButtonTexture = new Texture(Gdx.files.internal("pause_button.png"));
        resumeButtonTexture = new Texture(Gdx.files.internal("resume_button.png"));
        astronaut = new Astronaut(100, 300, astronautTexture);
        bullets = new Array<>();
        planet = new Planet(800, 300, alienPlanetTexture, savedPlanetTexture);

        remainingBullets = 10;
        score = 0;
        BulletText = new BitmapFont();

        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Zebulon.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = 22;
        titleParameter.color = Color.WHITE;
        scoreText = titleGenerator.generateFont(titleParameter);
        titleGenerator.dispose();

        float buttonWidth = 100;
        float buttonHeight = 50;
        float buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        float buttonY = Gdx.graphics.getHeight() - buttonHeight - 10;

        pauseButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        resumeButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);

        backgroundX = 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isPaused) {
            astronaut.handleInput();
            astronaut.update(delta);

            if (Gdx.input.isKeyJustPressed(Keys.ENTER) && remainingBullets > 0 && !astronaut.isDestroyed()) {
                float bulletX = astronaut.getPosition().x + astronautTexture.getWidth() * 0.5f;
                float bulletY = astronaut.getPosition().y + astronautTexture.getHeight() * 0.18f;
                bullets.add(new Bullet(bulletX, bulletY, 1, 0, 300, bulletTexture));
                remainingBullets--;
            }

            planet.update(delta, astronaut.getPosition());

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

                    astronaut.Collision();
                    alienBulletIterator.remove();
                }
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

        astronaut.render(batch);
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }

        planet.render(batch, isPaused ? 0 : delta);

        if (isPaused) {
            batch.draw(resumeButtonTexture, resumeButtonBounds.x, resumeButtonBounds.y, resumeButtonBounds.width, resumeButtonBounds.height);
        } else {
            batch.draw(pauseButtonTexture, pauseButtonBounds.x, pauseButtonBounds.y, pauseButtonBounds.width, pauseButtonBounds.height);
        }
        scoreText.setColor(Color.GREEN);
        scoreText.draw(batch, "Score: " + score,Gdx.graphics.getWidth() - 380 , Gdx.graphics.getHeight() - 17);
        scoreText.setColor(Color.MAGENTA);
        scoreText.draw(batch, "Bullets: " + remainingBullets, Gdx.graphics.getWidth() - 190, Gdx.graphics.getHeight() - 17);

//        BulletText.getData().setScale(2.0f);
//        BulletText.draw(batch, "Bullets: " + remainingBullets, Gdx.graphics.getWidth() - 170, Gdx.graphics.getHeight() - 17);
        batch.end();

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
    public static void UpdateRemBulletsScore(){
        remainingBullets+=10;
        score+=10;
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
        bulletTexture.dispose();
        alienPlanetTexture.dispose();
        savedPlanetTexture.dispose();
        astronaut.dispose();
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        planet.dispose();
        BulletText.dispose();
    }
}
