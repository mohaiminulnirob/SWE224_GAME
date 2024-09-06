package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class HealthPack {
    private Texture healthPackTexture;
    private float x, y;
    private float speed;
    private float scale;
    private Circle collisionCircle;
    private ParticleEffect hitEffect;
    private float effectTimer;
    private boolean showEffect;
    private boolean effective;
    private float effectiveTimer;
    private static final float SCALE_SIZE = 1f;
    private static final float PACK_INTERVAL = 7.0f;
    private float packTimer;

    public HealthPack() {
        this.healthPackTexture = new Texture(Gdx.files.internal("Health_booster.png"));
        resetPosition(Gdx.graphics.getHeight() / 2, new Array<>());
        this.speed = MathUtils.random(180, 180);
        this.packTimer = 0f;
        this.effective = true;
        this.effectiveTimer = 0f;

        hitEffect = new ParticleEffect();
        try {
            hitEffect.load(Gdx.files.internal("Particle Park Explosion Small.p"), Gdx.files.internal(""));
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }
        hitEffect.scaleEffect(1f);

        effectTimer = 0f;
        showEffect = false;
    }

    private void resetPosition(float z, Array<Rock> rocks) {
        this.scale = SCALE_SIZE;
        this.x = Gdx.graphics.getWidth() + 30;
        boolean is_valid = false;
        int maxRetries = 100;  // Limit the number of retries to avoid endless loop
        int retries = 0;
        float safeDistancePlanet = 150;// Distance buffer to avoid collisions
        float safeDistanceRock = 80;

        while (!is_valid && retries < maxRetries) {
            this.y = MathUtils.random(0, Gdx.graphics.getHeight() - healthPackTexture.getHeight() * scale);
            is_valid = true;

            // Check collision with planet
            if (y >= z - safeDistancePlanet && y <= z + safeDistancePlanet) {
                is_valid = false;
            }

            // Check collision with rocks
            for (Rock rock : rocks) {
                if (y >= rock.getY() - safeDistanceRock && y <= rock.getY() + safeDistanceRock) {
                    is_valid = false;
                    break;
                }
            }

            retries++;
        }

        if (!is_valid) {
            // If we couldn't find a valid position, just place it somewhere without checking.
            this.y = MathUtils.random(0, Gdx.graphics.getHeight() - healthPackTexture.getHeight() * scale);
        }

        this.collisionCircle = new Circle(x, y, (healthPackTexture.getWidth() / 2) * scale);
    }

    public void update(float delta, Vector2 position, Array<Rock> rocks) {
        packTimer += delta;
        if (packTimer >= PACK_INTERVAL) {
            resetPosition(position.y, rocks);
            packTimer = 0f;
        }

        x -= speed * delta;
        collisionCircle.setPosition(x + (healthPackTexture.getWidth() / 2) * scale, y + (healthPackTexture.getHeight() / 2) * scale);

        if (x + healthPackTexture.getWidth() * scale < 0) {
            resetPosition(position.y, rocks);
        }

        if (showEffect) {
            hitEffect.update(delta);
            effectTimer += delta;
            if (effectTimer >= 1.0f) {
                hitEffect.reset();
                showEffect = false;
            }
        }

        if (!effective) {
            effectiveTimer += delta;
            if (effectiveTimer >= 2.0f) {
                effective = true;
            }
        }
    }

    public void showHitEffect(float x, float y) {
        showEffect = true;
        hitEffect.setPosition(x + 10, y);
        hitEffect.start();
        effectTimer = 0f;
    }

    public void render(SpriteBatch batch, float deltaTime) {
        batch.draw(healthPackTexture, x, y, healthPackTexture.getWidth() * scale, healthPackTexture.getHeight() * scale);
        if (showEffect) {
            hitEffect.draw(batch, deltaTime);
        }
    }

//    public boolean isColliding(Astronaut astronaut) {
//        return collisionCircle.overlaps(astronaut.getCollisionCircle());
//    }

    public boolean isColliding(Bullet bullet) {
        return collisionCircle.contains(bullet.getPosition().x + bullet.getWidth() / 2, bullet.getPosition().y + bullet.getHeight() / 2);
    }

    public boolean isEffective() {
        return effective;
    }

    public void makeInactive() {
        effectiveTimer = 0f;
        effective = false;
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public void setPackTimer(float packTimer) {
        this.packTimer = packTimer;
    }

    public void dispose() {
        healthPackTexture.dispose();
        hitEffect.dispose();
    }
}
