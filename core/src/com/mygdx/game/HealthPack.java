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
    private static final float PACKSHOW_INTERVAL = 7.0f;
    private  static final float PACKHIDE_INTERVAL=30.0f;
    private float packTimer;

    public HealthPack() {
        this.healthPackTexture = new Texture(Gdx.files.internal("Health_booster.png"));
        resetPosition(Gdx.graphics.getHeight() / 2, new Array<>());
        this.speed = MathUtils.random(180, 180);
        this.packTimer = 0f;
        this.effective = false;
        this.effectiveTimer = 0f;

        hitEffect = new ParticleEffect();
        try {
            hitEffect.load(Gdx.files.internal("particleEffects/Particle Park Explosion Small.p"), Gdx.files.internal("particleEffects/"));
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
        int maxRetries = 100;
        int retries = 0;
        float safeDistancePlanet = 150;
        float safeDistanceRock = 80;

        while (!is_valid && retries < maxRetries) {
            this.y = MathUtils.random(0, Gdx.graphics.getHeight() - healthPackTexture.getHeight() * scale);
            is_valid = true;
            if (y >= z - safeDistancePlanet && y <= z + safeDistancePlanet) {
                is_valid = false;
            }
            for (Rock rock : rocks) {
                if (y >= rock.getY() - safeDistanceRock && y <= rock.getY() + safeDistanceRock) {
                    is_valid = false;
                    break;
                }
            }

            retries++;
        }

        if (!is_valid) {
            this.y = MathUtils.random(0, Gdx.graphics.getHeight() - healthPackTexture.getHeight() * scale);
        }

        this.collisionCircle = new Circle(x, y, (healthPackTexture.getWidth() / 2) * scale);
    }

    public void update(float delta, Vector2 position, Array<Rock> rocks) {
        if(effective == false)
            effectiveTimer+=delta;
        if(effectiveTimer>=PACKHIDE_INTERVAL)
        {
            resetPosition(position.y, rocks);
            effective=true;
            effectiveTimer=0.0f;
        }
        if(effective==true)
           packTimer += delta;
        if (packTimer >= PACKSHOW_INTERVAL) {
            effective=false;
            //resetPosition(position.y, rocks);
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

//        if (!effective) {
//            effectiveTimer += delta;
//            if (effectiveTimer >= 2.0f) {
//                effective = true;
//            }
//        }
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

    public boolean isColliding(Bullet bullet) {
        return collisionCircle.contains(bullet.getPosition().x + bullet.getWidth() / 2, bullet.getPosition().y + bullet.getHeight() / 2);
    }

    public boolean isEffective() {
        return effective;
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
