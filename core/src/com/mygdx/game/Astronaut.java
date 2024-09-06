package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import static sun.swing.MenuItemLayoutHelper.max;

public class Astronaut {
    private Vector2 position;
    private Texture texture;
    private boolean moveOutOfScreen = false;
    private boolean isDestroyed = false;
    private Polygon collisionPolygon;
    private ParticleEffect hitEffect;
    private int AstronautHealth, AstronautHealthInit;
    private boolean AstronautCollison;
    private float effectTimer;
    private float destroyTimer;
    private static final float SPEED = 150f;
    private static final float SCALE = 0.5f;
    private ShapeRenderer HealthRenderer;
    private BitmapFont HealthText;

    public Astronaut(float x, float y, Texture texture) {
        position = new Vector2(x, y);
        this.texture = texture;

        HealthRenderer = new ShapeRenderer();
        HealthText = new BitmapFont();
        float[] vertices = {
                10 * SCALE, 0,
                (texture.getWidth() - 10) * SCALE, 0,
                texture.getWidth() * SCALE, 10 * SCALE,
                texture.getWidth() * SCALE, (texture.getHeight() - 10) * SCALE,
                (texture.getWidth() - 10) * SCALE, texture.getHeight() * SCALE,
                10 * SCALE, texture.getHeight() * SCALE,
                0, (texture.getHeight() - 10) * SCALE,
                0, 10 * SCALE
        };
        collisionPolygon = new Polygon(vertices);
        collisionPolygon.setPosition(x, y);

        hitEffect = new ParticleEffect();
        try {
            hitEffect.load(Gdx.files.internal("Particle Park Thrust.p"), Gdx.files.internal(""));
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }
        hitEffect.scaleEffect(3f);
        effectTimer = 0f;
        destroyTimer = 0f;
        AstronautHealth = 10;
        AstronautHealthInit = AstronautHealth;
        AstronautCollison = false;

        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Zebulon.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = 20;
        titleParameter.color = Color.WHITE;
        HealthText = titleGenerator.generateFont(titleParameter);
        titleGenerator.dispose();
    }

    public void Collision() {
        AstronautHealth--;
        if (AstronautHealth == 0) {
            moveOutOfScreen = true;
            AstronautCollison = false;
        } else {
            AstronautCollison = true;
        }
    }

    public void update(float delta) {
        if (moveOutOfScreen) {
            if (destroyTimer < 0.5f)
                showHitEffect(position.x + 30, position.y + 30);
            hitEffect.update(delta);

            destroyTimer += delta;
            position.y -= 200 * delta;
            if (position.y + texture.getHeight() < 0) {
                isDestroyed = true;
            }
        } else if (AstronautCollison) {
            if (effectTimer < 0.5f)
                showHitEffect(position.x + 30, position.y + 30);
            hitEffect.update(delta);
            effectTimer += delta;
        }

        if (position.x < 0 && !moveOutOfScreen) {
            position.x = 0;
        } else if (position.x > Gdx.graphics.getWidth() - texture.getWidth() * SCALE) {
            position.x = Gdx.graphics.getWidth() - texture.getWidth() * SCALE;
        }
        if (position.y < 0 && !moveOutOfScreen) {
            position.y = 0;
        } else if (position.y > Gdx.graphics.getHeight() - texture.getHeight() * SCALE) {
            position.y = Gdx.graphics.getHeight() - texture.getHeight() * SCALE;
        }

        collisionPolygon.setPosition(position.x, position.y);
    }

    public void handleInput() {
        if (moveOutOfScreen) return;

        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            position.x -= SPEED * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            position.x += SPEED * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            position.y += SPEED * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            position.y -= SPEED * Gdx.graphics.getDeltaTime();
        }

        collisionPolygon.setPosition(position.x, position.y);
    }

    public boolean isColliding(Bullet bullet) {
        return collisionPolygon.contains(bullet.getPosition().x + bullet.getWidth(), bullet.getPosition().y + bullet.getHeight() / 2);
    }
    public boolean isCollidingWithRock(Rock rock) {
        return Intersector.overlaps(rock.getCollisionCircle(), collisionPolygon.getBoundingRectangle());
    }
    public boolean isCollidingWithHealthPack(HealthPack healthPack) {
        return Intersector.overlaps(healthPack.getCollisionCircle(), collisionPolygon.getBoundingRectangle());
    }
    public boolean isCollidingWithPlanet(Planet planet) {
        return Intersector.overlapConvexPolygons(this.getCollisionPolygon(), planet.getCollisionPolygon());
    }


    public void showHitEffect(float x, float y) {
        hitEffect.setPosition(x, y);
        hitEffect.start();
    }

    public void render(SpriteBatch batch) {
        if (moveOutOfScreen && destroyTimer < 1.0f) {
            AstronautCollison = false;
            effectTimer = 0f;
            hitEffect.draw(batch);
        } else if (AstronautCollison && effectTimer < 1.0f) {
            hitEffect.draw(batch);
        } else {
            hitEffect.reset();
            AstronautCollison = false;
            effectTimer = 0f;
        }

        if (!isDestroyed) {
            batch.draw(texture, position.x, position.y, texture.getWidth() * SCALE, texture.getHeight() * SCALE);
        }
        HealthText.draw(batch, "Health:", 35, Gdx.graphics.getHeight() - 17);


        batch.end();

        HealthRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float barX = 150;
        float barY = Gdx.graphics.getHeight() - 35;
        float barWidth = 200;
        float barHeight = 20;

        float healthPercentage = (float) AstronautHealth / AstronautHealthInit;
        if(moveOutOfScreen)
            healthPercentage=0f;

        HealthRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
        HealthRenderer.rect(barX, barY, barWidth, barHeight);

        HealthRenderer.setColor(0, 1, 0, 1);
        HealthRenderer.rect(barX, barY, barWidth * healthPercentage, barHeight);

        HealthRenderer.end();
        batch.begin();
    }

    public Polygon getCollisionPolygon() {
        return collisionPolygon;
    }

    public void dispose() {
        texture.dispose();
        hitEffect.dispose();
        HealthRenderer.dispose();
        HealthText.dispose();
    }
    public void consumeHealthpack(){
        int tempHealth= AstronautHealth+1;
        AstronautHealth= Math.min(AstronautHealthInit,tempHealth);
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public Vector2 getPosition() {
        return position;
    }
}