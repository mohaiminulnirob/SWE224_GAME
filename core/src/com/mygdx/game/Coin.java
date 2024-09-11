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

public class Coin {
    private Texture[] coinTextures;
    private Texture currentTexture;
    private float x, y;
    private float speed;
    private float scale;
    private Circle collisionCircle;
    private ParticleEffect hitEffect;
    private float effectTimer;
    private boolean showEffect;
    private boolean effective;
    private float effectiveTimer;
    private static final float SCALE_SIZE = .8f;
    private static final float COIN_SHOW_INTERVAL = 28.0f;
    private static final float COIN_HIDE_INTERVAL = 7.0f;
    private float coinTimer;

    public Coin() {
        coinTextures = new Texture[] {
                new Texture(Gdx.files.internal("coins/coin1.png")),
                new Texture(Gdx.files.internal("coins/coin2.png")),
                new Texture(Gdx.files.internal("coins/coin3.png"))
        };
        currentTexture = coinTextures[MathUtils.random(coinTextures.length - 1)];

        resetPosition(Gdx.graphics.getHeight() / 2, new Array<>());
        this.speed = MathUtils.random(180, 180);
        this.coinTimer = 0f;
        this.effective = true;
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

        currentTexture = coinTextures[MathUtils.random(coinTextures.length - 1)];

        while (!is_valid && retries < maxRetries) {
            this.y = MathUtils.random(0, Gdx.graphics.getHeight() - currentTexture.getHeight() * scale);
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
            this.y = MathUtils.random(0, Gdx.graphics.getHeight() - currentTexture.getHeight() * scale);
        }

        this.collisionCircle = new Circle(x, y, (currentTexture.getWidth() / 2) * scale);
    }

    public void update(float delta, Vector2 position, Array<Rock> rocks) {
        if(effective == false)
            effectiveTimer += delta;
        if(effectiveTimer >= COIN_HIDE_INTERVAL) {
            resetPosition(position.y, rocks);
            effective = true;
            effectiveTimer = 0.0f;
        }
        if(effective == true)
            coinTimer += delta;
        if (coinTimer >= COIN_SHOW_INTERVAL) {
            effective = false;
            coinTimer = 0f;
        }

        x -= speed * delta;
        collisionCircle.setPosition(x + (currentTexture.getWidth() / 2) * scale, y + (currentTexture.getHeight() / 2) * scale);

        if (x + currentTexture.getWidth() * scale < 0) {
            resetPosition(position.y, rocks);
        }

        if (showEffect) {
            hitEffect.update(delta);
            effectTimer += delta;
            if (effectTimer >= 1.0f) {
                hitEffect.reset();
                //hitEffect.scaleEffect(1f);
                showEffect = false;
            }
        }
    }

    public void showHitEffect(float x, float y) {
        showEffect = true;
        hitEffect.setPosition(x+10, y);
        hitEffect.start();
        effectTimer = 0f;
    }

    public void render(SpriteBatch batch, float deltaTime) {
        batch.draw(currentTexture, x, y, currentTexture.getWidth() * scale, currentTexture.getHeight() * scale);
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

    public void setCoinTimer(float coinTimer) {
        this.coinTimer = coinTimer;
    }

    public void dispose() {
        for (Texture texture : coinTextures) {
            texture.dispose();
        }
        hitEffect.dispose();
    }
}
