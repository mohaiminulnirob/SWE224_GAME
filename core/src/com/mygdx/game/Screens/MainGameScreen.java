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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Screen;
import com.mygdx.game.*;

import java.util.ArrayList;
import java.util.Iterator;

public class MainGameScreen implements Screen{
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture background;
    private Texture astronautTexture;
    private Texture bulletTexture;
    private Texture pauseButtonTexture;
    private Texture exitButtonTexture;
    private Texture resumeButtonTexture;
    private Astronaut astronaut;
    private Array<Bullet> bullets;
    private Planet planet;
    private Array<Rock> rocks;
    private HealthPack healthPack;
    private Coin coin;
    private static int remainingBullets;
    private static int score;
    private BitmapFont Text;
    private boolean isPaused = false;
    private Rectangle pauseButtonBounds;
    private Rectangle resumeButtonBounds;
    private Rectangle exitButtonBounds;
    private float backgroundX;
    private AstroRunSavePlanet game;

    public MainGameScreen(AstroRunSavePlanet game) {
        this.game = game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = game.getBatch();

        background = new Texture(Gdx.files.internal("backgrounds/space_background.png"));
        astronautTexture = new Texture(Gdx.files.internal("astronaut/aeroplane.png"));
        bulletTexture = new Texture(Gdx.files.internal("bullets/bullet.png"));
        pauseButtonTexture = new Texture(Gdx.files.internal("buttons/pause_button.png"));
        resumeButtonTexture = new Texture(Gdx.files.internal("buttons/resume_button.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("buttons/exit_button.png"));
        astronaut = new Astronaut(100, 300, astronautTexture);
        bullets = new Array<>();
        planet = new Planet();
        rocks = new Array<>();
        for (int i = 0; i < 3; i++) {
            rocks.add(new Rock());
        }
        healthPack = new HealthPack();
        coin = new Coin();
        remainingBullets = 10;
        score = 0;
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
        resumeButtonBounds = new Rectangle(buttonX - buttonWidth - 10, buttonY, buttonWidth, buttonHeight); // Adjust resume button position
        exitButtonBounds = new Rectangle(buttonX + buttonWidth + 10, buttonY, buttonWidth, buttonHeight); // Position exit button beside resume button
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

            planet.update(delta, astronaut.getPosition(),rocks);

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
                }else if(healthPack.isColliding(bullet) && healthPack.isEffective()){
                    Vector2 position = bullet.getPosition();
                    healthPack.showHitEffect(position.x,position.y);
                    healthPack.setPackTimer(6.9f);
                    bulletIterator.remove();
                }
                else if(coin.isColliding(bullet) && coin.isEffective()){
                    Vector2 position = bullet.getPosition();
                    coin.showHitEffect(position.x,position.y);
                    coin.setCoinTimer(27.9f);
                    bulletIterator.remove();
                }
                else {
                    for (Rock rock : rocks) {
                        if (rock.isColliding(bullet)) {
                            Vector2 position = bullet.getPosition();
                            rock.showHitEffect(position.x,position.y);

                            bulletIterator.remove();
                        }
                    }
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
                else if(healthPack.isColliding(alienBullet) && healthPack.isEffective()){
                    Vector2 position = alienBullet.getPosition();
                    healthPack.showHitEffect(position.x,position.y);
                    healthPack.setPackTimer(6.9f);
                    alienBulletIterator.remove();
                }
                else if(coin.isColliding(alienBullet) && coin.isEffective()){
                    Vector2 position = alienBullet.getPosition();
                    coin.showHitEffect(position.x,position.y);
                    coin.setCoinTimer(27.9f);
                    alienBulletIterator.remove();
                }
                else{
                    for(Rock rock: rocks){
                        if(rock.isColliding(alienBullet)) {

                            Vector2 position = alienBullet.getPosition();
                            rock.showHitEffect(position.x,position.y);
                            alienBulletIterator.remove();
                        }

                    }
                }
            }
            if(astronaut.isCollidingWithHealthPack(healthPack) && healthPack.isEffective()) {
                healthPack.setPackTimer(7.0f);
                astronaut.consumeHealthpack();
            }
            if(astronaut.isCollidingWithCoin(coin) && coin.isEffective()) {
                coin.setCoinTimer(28.0f);
                astronaut.consumeCoin();
            }

            for( Rock rock:rocks){
                if(astronaut.isCollidingWithRock(rock) && rock.effectiveness()) {
                    rock.MakeInactive();
                    astronaut.Collision();
                }

            }
            if(astronaut.isCollidingWithPlanet(planet) && planet.effectiveness()){
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
        for (Rock rock : rocks) {
            rock.update(isPaused ? 0 : delta,
                    planet.getPosition());
            rock.render(batch,isPaused ? 0 : delta);
        }
        healthPack.update(isPaused ? 0 : delta,
                planet.getPosition(),rocks);
        if(healthPack.isEffective())
            healthPack.render(batch,isPaused ? 0 : delta);
        coin.update(isPaused ? 0 : delta,
                planet.getPosition(),rocks);
        if(coin.isEffective())
            coin.render(batch,isPaused ? 0 : delta);
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
        Text.draw(batch, "Score: " + score,Gdx.graphics.getWidth() - 380 , Gdx.graphics.getHeight() - 17);
        Text.setColor(Color.MAGENTA);
        Text.draw(batch, "Bullets: " + remainingBullets, Gdx.graphics.getWidth() - 190, Gdx.graphics.getHeight() - 17);
        Text.setColor(Color.RED);
        Text.getData().setScale(2f);
        if(astronaut.isMoveOutOfScreen())
            Text.draw(batch,"Game Over!",Gdx.graphics.getWidth()/2f-200,Gdx.graphics.getHeight()/2f + 25);
        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (!isPaused && pauseButtonBounds.contains(touchPos.x, touchPos.y)) {
                isPaused = true;
            } else if (isPaused && resumeButtonBounds.contains(touchPos.x, touchPos.y)) {
                isPaused = false;
            } else if (isPaused && exitButtonBounds.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new StartScreen(game));
            }
        }
        if(astronaut.isDestroyed()){
            game.setScreen(new GameOverScreen(game));
        }
    }
    public static void UpdateScore(){
        //remainingBullets+=10;
        score+=10;
    }
    public static void UpdateRemBullets(){
        remainingBullets+=10;
        //score+=10;
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

    public static int getScore() {
        return score;
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
        astronaut.dispose();
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        planet.dispose();
        for (Rock rock : rocks) {
            rock.dispose();
        }
        pauseButtonTexture.dispose();
        resumeButtonTexture.dispose();
        exitButtonTexture.dispose();
        Text.dispose();
        healthPack.dispose();
        coin.dispose();
    }
}
