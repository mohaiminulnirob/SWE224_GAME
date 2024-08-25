package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Rock {
    private Texture rockTexture;
    private float x, y;
    private float speed;
    private float scale;
    private Circle collisionCircle;
    private ParticleEffect hitEffect;
    private float effectTimer;
    private boolean showEffect;
    private boolean effective;
    private float  effectiveTimer;
    private static final float[] SCALE_SIZES = {0.34f,0.38f,0.3f};

    public Rock() {
        this.rockTexture = new Texture(Gdx.files.internal("rock.png"));
        resetPosition(Gdx.graphics.getHeight()/2);
        this.speed = MathUtils.random(50,100);
        hitEffect = new ParticleEffect();
        try {
            hitEffect.load(Gdx.files.internal("Particle Park Explosion Small.p"), Gdx.files.internal(""));
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }
        hitEffect.scaleEffect(1f);

        effectTimer = 0f;
        showEffect = false;
        effective=true;
        effectiveTimer= 0f;
    }
    private void resetPosition(float z) {
        this.scale = SCALE_SIZES[MathUtils.random(0, SCALE_SIZES.length - 1)];
        this.x = Gdx.graphics.getWidth() + 30;
        boolean is_valid = false;

        while (!is_valid) {
            this.y = MathUtils.random(0, Gdx.graphics.getHeight() - rockTexture.getHeight() * scale);
            if (y >= z - 150 && y <= z + 150) {
                is_valid = false;
            } else {
                is_valid = true;
            }
        }

        this.collisionCircle = new Circle(x, y, (rockTexture.getWidth() / 2) * scale);
    }

    public float getY() {
        return y;
    }

    public void update(float delta, Vector2 position) {
        x -= speed * delta;

        collisionCircle.setPosition(x + (rockTexture.getWidth() / 2) * scale, y + (rockTexture.getHeight() / 2) * scale);

        if (x + rockTexture.getWidth() * scale < 0) {
            resetPosition(position.y);
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
        if(effective==false){
            effectiveTimer+=delta;
            if (effectiveTimer >= 2.0f) {
                effective=true;
            }

        }
    }
    public void showHitEffect(float x, float y) {
        showEffect = true;
        hitEffect.setPosition(x+10, y);
        hitEffect.start();
        effectTimer = 0f;
    }

    public void render(SpriteBatch batch,float deltatime) {
        batch.draw(rockTexture, x, y, rockTexture.getWidth() * scale, rockTexture.getHeight() * scale);
        if (showEffect) {
            hitEffect.draw(batch, deltatime);
        }
    }
    public boolean isColliding(Bullet bullet) {
        //if(collisionCircle.contains(bullet.getPosition().x + bullet.getWidth()/2, bullet.getPosition().y + bullet.getHeight() / 2) && !isSaved)
            //showHitEffect(bullet.getPosition().x + bullet.getWidth()/2,bullet.getPosition().y + bullet.getHeight() / 2);
        return collisionCircle.contains(bullet.getPosition().x + bullet.getWidth()/2, bullet.getPosition().y + bullet.getHeight() / 2);
    }
    public boolean effectiveness(){
        return effective;
    }
    public void MakeInactive(){
        effectiveTimer=0f;
        effective=false;
    }

    public Circle getCollisionCircle() {
        return collisionCircle;
    }

    public void dispose() {
        rockTexture.dispose();
    }
}
