package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Planet {
    private Vector2 position;
    private Texture alienTexture;
    private Texture savedTexture;
    private boolean isSaved;
    private Polygon collisionPolygon;
    private int hitCount;
    private float rotationAngle; // Added for rotation

    private static final float ALIEN_SCALE = 0.6f;
    private static final float SAVED_SCALE = 0.58f;

    public Planet(float x, float y, Texture alienTexture, Texture savedTexture) {
        position = new Vector2(x, y);
        this.alienTexture = alienTexture;
        this.savedTexture = savedTexture;
        this.isSaved = false;
        this.hitCount = 0;
        this.rotationAngle = 0; // Initialize rotation angle

        float width = alienTexture.getWidth() * ALIEN_SCALE;
        float height = alienTexture.getHeight() * ALIEN_SCALE;
        float[] vertices = {
                0, 0,
                width, 0,
                width, height,
                0, height
        };
        collisionPolygon = new Polygon(vertices);
        collisionPolygon.setPosition(x, y);
        collisionPolygon.setOrigin(width / 2, height / 2); // Set the origin for rotation
    }

    public void hit() {
        if (!isSaved) {
            hitCount++;
            if (hitCount >= 5) {
                convertToSaved();
            }
        }
    }

    public boolean isColliding(Bullet bullet) {
        return collisionPolygon.contains(bullet.getPosition().x + bullet.getWidth(), bullet.getPosition().y + bullet.getHeight() / 2);
    }

    public void render(SpriteBatch batch, float deltaTime) {
        rotationAngle += 15 * deltaTime; // Adjust rotation speed as needed
        if (rotationAngle > 360) rotationAngle -= 360; // Keep angle within 0-360 degrees

        collisionPolygon.setRotation(rotationAngle);

        Texture texture = isSaved ? savedTexture : alienTexture;
        float scale = isSaved ? SAVED_SCALE : ALIEN_SCALE;

        batch.draw(texture,
                position.x,
                position.y,
                texture.getWidth() * scale / 2,
                texture.getHeight() * scale / 2,
                texture.getWidth() * scale,
                texture.getHeight() * scale,
                1,
                1,
                rotationAngle,
                0,
                0,
                texture.getWidth(),
                texture.getHeight(),
                false,
                false);
    }

    public Bullet shootAt(Astronaut astronaut) {
        if (!isSaved) {
            Vector2 astronautPosition = new Vector2(astronaut.getPosition()); // Create a copy of the astronaut's position
            Vector2 direction = astronautPosition.sub(position).nor();
            return new Bullet(position.x, position.y, direction.x, direction.y, 200, alienTexture); // Adjust speed and texture as necessary
        }
        return null;
    }

    public void convertToSaved() {
        isSaved = true;

        float width = savedTexture.getWidth() * SAVED_SCALE;
        float height = savedTexture.getHeight() * SAVED_SCALE;
        float[] vertices = {
                0, 0,
                width, 0,
                width, height,
                0, height
        };
        collisionPolygon = new Polygon(vertices);
        collisionPolygon.setPosition(position.x, position.y);
        collisionPolygon.setOrigin(width / 2, height / 2); // Set the origin for rotation
    }

    public void dispose() {
        alienTexture.dispose();
        savedTexture.dispose();
    }
}
