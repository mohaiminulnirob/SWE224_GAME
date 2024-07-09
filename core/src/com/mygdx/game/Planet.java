package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.Iterator;

public class Planet {
    private Vector2 position;
    private Texture alienTexture;
    private Texture savedTexture;
    private boolean isSaved;
    private Polygon collisionPolygon;
    private int hitCount;
    private ArrayList<Bullet> alienBullets;
    private float shootTimer;
    private float shootInterval;
    private float rotationAngle;
    private ParticleEffect hitEffect;

    private float effectTimer;
    private boolean showEffect;

    private static final float ALIEN_SCALE = 1.5f;
    private static final float SAVED_SCALE = .8f;

    public Planet(float x, float y, Texture alienTexture, Texture savedTexture) {
        position = new Vector2(x, y);
        this.alienTexture = alienTexture;
        this.savedTexture = savedTexture;
        this.isSaved = false;
        this.alienBullets = new ArrayList<>();
        this.shootTimer = 0f;
        this.shootInterval = 1.0f;
        this.hitCount = 0;
        this.rotationAngle = 0;

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
        hitEffect = new ParticleEffect();
        try {
            hitEffect.load(Gdx.files.internal("Particle Park Trail.p"), Gdx.files.internal(""));
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }

        hitEffect.scaleEffect(15f);

        effectTimer = 0f;
        showEffect = false;
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
        if(collisionPolygon.contains(bullet.getPosition().x + bullet.getWidth()/2, bullet.getPosition().y + bullet.getHeight() / 2))
            showHitEffect(bullet.getPosition().x + bullet.getWidth()/2,bullet.getPosition().y + bullet.getHeight() / 2);
        return collisionPolygon.contains(bullet.getPosition().x + bullet.getWidth()/2, bullet.getPosition().y + bullet.getHeight() / 2);
    }

    public void render(SpriteBatch batch, float deltaTime) {
        if (deltaTime > 0) {
            rotationAngle += 15 * deltaTime;
            if (rotationAngle > 360) rotationAngle -= 360;
            collisionPolygon.setRotation(rotationAngle);
        }

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

        for (Bullet bullet : alienBullets) {
            if(!collisionPolygon.contains(bullet.getPosition().x + bullet.getWidth()/2, bullet.getPosition().y + bullet.getHeight() / 2))
                bullet.render(batch);
        }
        if (showEffect) {
            hitEffect.draw(batch, deltaTime);
        }
    }

    public void update(float delta, Vector2 astronautPosition) {
        shootTimer += delta;
        if (shootTimer >= shootInterval && !isSaved) {
            generateAlienBullet(astronautPosition);
            shootTimer = 0f;
        }

        Iterator<Bullet> bulletIterator = alienBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update(delta);
            if (!bullet.isActive()) {
                bulletIterator.remove();
            }
        }
        if (showEffect) {
            hitEffect.update(delta);
            effectTimer += delta;
            if (effectTimer >= 1.0f) {
                hitEffect.reset();
                hitEffect.scaleEffect(10f);
                showEffect = false;
            }
        }
    }

    private void generateAlienBullet(Vector2 astronautPosition) {
        float bulletX = position.x + alienTexture.getWidth() * ALIEN_SCALE / 2;
        float bulletY = position.y + alienTexture.getHeight() * ALIEN_SCALE / 2;
        float directionX = astronautPosition.x - bulletX;
        float directionY = astronautPosition.y - bulletY;
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);
        directionX /= length;
        directionY /= length;
        Bullet alienBullet = new Bullet(bulletX, bulletY, directionX, directionY, 300, new Texture("alien_bullet.png"));
        if(astronautPosition.y>=0)
            alienBullets.add(alienBullet);
    }

    public ArrayList<Bullet> getAlienBullets() {
        return alienBullets;
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
    public void showHitEffect(float x,float y) {
        showEffect = true;
        hitEffect.setPosition(x, y);
        hitEffect.start();
        effectTimer = 0f;
    }

    public void dispose() {
        alienTexture.dispose();
        for (Bullet alienBullet : alienBullets) {
            alienBullet.dispose();
        }
        savedTexture.dispose();
        hitEffect.dispose();
    }
}
