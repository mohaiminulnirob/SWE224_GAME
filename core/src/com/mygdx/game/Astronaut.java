package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Astronaut {
    private Vector2 position;
    private Texture texture;
    private boolean moveOutOfScreen = false;
    private boolean isDestroyed = false;
    private Polygon collisionPolygon;
    private ParticleEffect hitEffect;

    private float effectTimer;
    private boolean showEffect;
    private static final float SPEED = 150f;
    private static final float SCALE = 0.5f;

    public Astronaut(float x, float y, Texture texture) {
        position = new Vector2(x, y);
        this.texture = texture;

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
        //hitEffect.setPosition(x, y);
        hitEffect.scaleEffect(1f);

        effectTimer = 0f;
        showEffect = false;
    }

    public void update(float delta) {
        if (moveOutOfScreen) {
            if(effectTimer<0.5f)
                showHitEffect(position.x, position.y);
            hitEffect.update(delta);

            effectTimer+=delta;
            position.y -= 200 * delta;
            if (position.y + texture.getHeight() < 0) {
                isDestroyed = true;
                //texture.dispose();
            }
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
        if(moveOutOfScreen)
            return;

        if (Gdx.input.isKeyPressed(Keys.LEFT) ) {
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
    public void moveDownAndOut(float deltaTime) {
        moveOutOfScreen = true;
    }
    public void showHitEffect(float x,float y) {
        //showEffect = true;
        hitEffect.setPosition(x+20, y);
        //hitEffect.scaleEffect(10f);
        hitEffect.start();
        //effectTimer = 0f;
    }

    public void render(SpriteBatch batch) {
        if(moveOutOfScreen && effectTimer<1.0f)
            hitEffect.draw(batch);
        if(!isDestroyed)
            batch.draw(texture, position.x, position.y, texture.getWidth() * SCALE, texture.getHeight() * SCALE);
    }

    public void dispose() {
        texture.dispose();
    }
    public boolean isDestroyed() {
        return isDestroyed;
    }

    public Vector2 getPosition() {
        return position;
    }
}
