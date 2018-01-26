package uk.ac.qub.eeecs.game.cardDemo.objects;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Created by Aedan on 24/01/2018.
 */
//This class stores the details of each match
public class Match extends GameObject {

    private int playerAScore;
    private int playerBScore;
    private int timeElapsed;
    private int totalTime;
    private GameState gameState;

    public enum GameState{MIDFIELD, PLAYER_A_DANGEROUS_ATTACK, PLAYER_A_ATTACK, PLAYER_B_ATTACK, PLAYER_B_DANGEROUS_ATTACK };

    public Match(GameScreen gameScreen){
        super(gameScreen);
        this.playerAScore = 0;
        this.playerBScore = 0;
        this.gameState = gameState.MIDFIELD;

    }

    public void setPlayerAScore(int score){
        this.playerAScore = score;
    }

    public void setPlayerBScore(int score){
        this.playerBScore = score;
    }

    public void setGameState(GameState gameState) {this.gameState = gameState;}

    public int getPlayerBScore() {
        return playerBScore;
    }

    public int getPlayerAScore() {
        return playerAScore;
    }

    public GameState getGameState() {return gameState;}

    public void scenario(){
        MatchEvent newEvent = new MatchEvent(mGameScreen);
        String winner = newEvent.runScenario(gameState);
        if (winner.equals("PlayerA")){
            if (gameState.equals(GameState.PLAYER_A_DANGEROUS_ATTACK)){
                setPlayerAScore(getPlayerAScore() + 1);
                setGameState(gameState.MIDFIELD);
            } else{
                switch (getGameState()){
                    case MIDFIELD:
                        setGameState(gameState.PLAYER_A_ATTACK);
                        break;
                    case PLAYER_A_ATTACK:
                        setGameState(gameState.PLAYER_A_DANGEROUS_ATTACK);
                        break;
                    case PLAYER_B_ATTACK:
                        setGameState(gameState.MIDFIELD);
                        break;
                    case PLAYER_B_DANGEROUS_ATTACK:
                        setGameState(gameState.PLAYER_B_ATTACK);
                        break;
                }
            }
        } else if (winner.equals("PlayerB")){
            if (gameState.equals(GameState.PLAYER_B_DANGEROUS_ATTACK)){
                setPlayerBScore(getPlayerBScore() + 1);
                setGameState(gameState.MIDFIELD);
            } else{
                switch (getGameState()){
                    case MIDFIELD:
                        setGameState(gameState.PLAYER_B_ATTACK);
                        break;
                    case PLAYER_B_ATTACK:
                        setGameState(gameState.PLAYER_B_DANGEROUS_ATTACK);
                        break;
                    case PLAYER_A_ATTACK:
                        setGameState(gameState.MIDFIELD);
                        break;
                    case PLAYER_A_DANGEROUS_ATTACK:
                        setGameState(gameState.PLAYER_A_ATTACK);
                        break;
                }
            }
        }

    }



}
