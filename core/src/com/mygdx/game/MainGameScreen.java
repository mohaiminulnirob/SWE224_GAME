package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.Keys;
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
    private int remainingBullets;
    private BitmapFont font;
    private boolean isPaused = false;

    private Rectangle pauseButtonBounds;
    private Rectangle resumeButtonBounds;

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
        font = new BitmapFont();


        float buttonWidth = 100;
        float buttonHeight = 50;
        float buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        float buttonY = Gdx.graphics.getHeight() - buttonHeight - 10;

        pauseButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        resumeButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
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
                    //planet.showHitEffect();
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
                    astronaut.moveDownAndOut(delta);
                    alienBulletIterator.remove();
                    //isPaused = true;
                }
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

        font.draw(batch, "Bullets: " + remainingBullets, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 10);
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
        font.dispose();
    }
}
