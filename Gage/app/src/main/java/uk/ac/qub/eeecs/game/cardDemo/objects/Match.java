package uk.ac.qub.eeecs.game.cardDemo.objects;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Created by Aedan on 24/01/2018.
 */
//This class stores the details of each match
public class Match {

    private int playerAScore;
    private int playerBScore;
    private int timeElapsed;
    private int totalTime;
    private GameState gameState;

    public enum GameState{MIDFIELD, PLAYERADANGEROUSATTACK, PLAYERAATTACK, PLAYERBATTACK, PLAYERBDANGEROUSATTACK };

    public Match(){
        this.playerAScore = 0;
        this.playerBScore = 0;
        this.gameState = gameState.MIDFIELD;

    }

    public void setPlayerAScore(int score){
        this.playerAScore = score;
    }

    public void setPlayerBScore(int score){
        this.playerAScore = score;
    }

    public void setGameState(GameState gameState) {this.gameState = gameState;}

    public int getPlayerBScore() {
        return playerBScore;
    }

    public int getPlayerAScore() {
        return playerAScore;
    }

    public GameState getGameState() {return gameState;}



}
