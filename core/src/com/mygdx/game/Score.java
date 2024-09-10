package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.Screens.MainGameScreen;

public class Score {
    private int currentScore, highScore;
    private static final String HIGH_SCORE_FILE = "highScore.txt";

    public Score() {
        currentScore = MainGameScreen.getScore();
        highScore = loadHighScore();
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
        saveHighScore(highScore);
    }

    public int getHighScore() {
        return highScore;
    }

    public int getCurrentScore() {
        if (currentScore > highScore) {
            setHighScore(currentScore);
        }
        return currentScore;
    }

    private void saveHighScore(int highScore) {
        FileHandle file = Gdx.files.local(HIGH_SCORE_FILE);
        file.writeString(String.valueOf(highScore), false);  // Write the new high score, overwrite the file
    }

    private int loadHighScore() {
        FileHandle file = Gdx.files.local(HIGH_SCORE_FILE);
        if (file.exists()) {
            String highScoreString = file.readString();
            try {
                return Integer.parseInt(highScoreString);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
