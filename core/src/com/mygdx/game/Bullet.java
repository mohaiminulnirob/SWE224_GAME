package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private Vector2 position;
    private Vector2 direction;
    private float speed;
    private Texture texture;
    private boolean active;

    private static final float SCALE = 0.2f;

    public Bullet(float x, float y, float directionX, float directionY, float speed, Texture texture) {
        position = new Vector2(x, y);

        direction = new Vector2(directionX, directionY).nor();
        this.speed = speed;
        this.texture = texture;
        this.active = true;
    }

    public void update(float delta) {
        position.x += direction.x * speed * delta;
        position.y += direction.y * speed * delta;

        if (position.x < 0 || position.x > Gdx.graphics.getWidth() || position.y < 0 || position.y > Gdx.graphics.getHeight()) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            float width = texture.getWidth() * SCALE;
            float height = texture.getHeight() * SCALE;
            batch.draw(texture, position.x, position.y, width, height);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void dispose() {
        texture.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getWidth() {
        return texture.getWidth() * SCALE;
    }

    public float getHeight() {
        return texture.getHeight() * SCALE;
    }
}
