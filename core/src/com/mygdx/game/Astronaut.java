package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
public class Astronaut {
    private Vector2 position;
    private Texture texture;
    private Polygon collisionPolygon;
    private static final float SPEED = 150f;
    private static final float SCALE = 0.3f; // Scale down the astronaut

    public Astronaut(float x, float y, Texture texture) {
        position = new Vector2(x, y);
        this.texture = texture;

        // Define the collision polygon (based on the astronaut's shape)
        float[] vertices = {
                10 * SCALE, 0, // bottom left
                (texture.getWidth() - 10) * SCALE, 0, // bottom right
                texture.getWidth() * SCALE, 10 * SCALE, // right middle
                texture.getWidth() * SCALE, (texture.getHeight() - 10) * SCALE, // top right
                (texture.getWidth() - 10) * SCALE, texture.getHeight() * SCALE, // top middle
                10 * SCALE, texture.getHeight() * SCALE, // top left
                0, (texture.getHeight() - 10) * SCALE, // left middle
                0, 10 * SCALE // left bottom
        };
        collisionPolygon = new Polygon(vertices);
        collisionPolygon.setPosition(x, y);
    }

    public void update(float delta) {
        // Ensure astronaut stays within screen bounds
        if (position.x < 0) {
            position.x = 0;
        } else if (position.x > Gdx.graphics.getWidth() - texture.getWidth() * SCALE) {
            position.x = Gdx.graphics.getWidth() - texture.getWidth() * SCALE;
        }
        if (position.y < 0) {
            position.y = 0;
        } else if (position.y > Gdx.graphics.getHeight() - texture.getHeight() * SCALE) {
            position.y = Gdx.graphics.getHeight() - texture.getHeight() * SCALE;
        }
        collisionPolygon.setPosition(position.x, position.y);
    }

    public void handleInput() {
        // Move astronaut based on arrow key input
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

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, texture.getWidth() * SCALE, texture.getHeight() * SCALE);
    }

    public void dispose() {
        texture.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }
}
