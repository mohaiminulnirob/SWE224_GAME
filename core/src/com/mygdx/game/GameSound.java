package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class GameSound {
    private Sound clickSound;
    private Sound coinSound;
    private Sound collisionSound;
    private Sound healthBoosterSound;
    private Sound shootSound;
    private Sound startBackgroundSound;
    private Sound gameBackgroundSound;
    private Sound collision2Sound;
    private Sound explosionSound;
    private Sound savedSound;

    public GameSound() {
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/click.wav"));
        coinSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin.wav"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/collision.wav"));
        healthBoosterSound = Gdx.audio.newSound(Gdx.files.internal("sounds/healthBooster.wav"));
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.wav"));
        startBackgroundSound = Gdx.audio.newSound(Gdx.files.internal("sounds/start_background.wav"));
        gameBackgroundSound = Gdx.audio.newSound(Gdx.files.internal("sounds/game_background.wav"));
        collision2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/collision2.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        savedSound = Gdx.audio.newSound(Gdx.files.internal("sounds/saved.wav"));
    }

    public void playClick() {
        clickSound.play();
    }

    public void playCoin() {
        coinSound.play();
    }

    public void playCollision() {
        collisionSound.play();
    }

    public void playHealthBooster() {
        healthBoosterSound.play();
    }

    public void playShoot() {
        shootSound.play();
    }

    public long playStartBackground() {
        return startBackgroundSound.loop();
    }
    public void stopStartBackground() {
        startBackgroundSound.stop();
    }
    public void pauseGameBackground() {
        gameBackgroundSound.pause();
    }

    public void resumeGameBackground() {
        gameBackgroundSound.resume();
    }

    public long playGameBackground() {
        return gameBackgroundSound.loop();
    }
    public void stopGameBackground() {
        gameBackgroundSound.stop();
    }

    public void playCollision2() {
        collision2Sound.play();
    }

    public void playExplosion() {
        explosionSound.play();
    }

    public void playSaved() {
        savedSound.play();
    }

    public void dispose() {
        clickSound.dispose();
        coinSound.dispose();
        collisionSound.dispose();
        healthBoosterSound.dispose();
        shootSound.dispose();
        startBackgroundSound.dispose();
        gameBackgroundSound.dispose();
        collision2Sound.dispose();
        explosionSound.dispose();
        savedSound.dispose();
    }
}
