package uk.ac.qub.eeecs.game.cardDemo.objects;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.FootballGame;

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
    private Game mGame;
    private MatchEvent newEvent;
    public ArrayList<Card> AITeam;

    private boolean winnerDecided;
    public enum GameState{MIDFIELD, PLAYER_A_DANGEROUS_ATTACK, PLAYER_A_ATTACK, PLAYER_B_ATTACK, PLAYER_B_DANGEROUS_ATTACK };

    public Match(GameScreen gameScreen){
        super(gameScreen);
        this.playerAScore = 0;
        this.playerBScore = 0;
        this.gameState = gameState.MIDFIELD;
        mGame = mGameScreen.getGame();
        AITeam = new ArrayList<>();
        populateAITeam();

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

    private void populateAITeam() {
        int difficulty = 1;
        int minRating = 0, maxRating = 0;
        switch (difficulty) {
            case 0:
                maxRating = 65;
                break;
            case 1:
                minRating = 65;
                maxRating = 80;
                break;
            case 2:
                minRating = 80;
                maxRating = 99;
                break;
        }
        Card tempCard;
        String position = "";
        int i = 0;
        while(AITeam.size() < 11) {

            if (i == 0){
                position = "GoalKeeper";
            } else if (i < 4){
                position = "Defence";
            } else if (i < 7){
                position = "Midfield";
            } else {
                position = "Forward";
            }
            tempCard = new Card(mGameScreen, true, position, minRating, maxRating);
            if (checkIfCardIsntDupe(tempCard)) {
                AITeam.add(tempCard);
                i++;
            }

        }

    }
    private boolean checkIfCardIsntDupe(Card newPlayer){
        for (int i = 0; i < AITeam.size(); i++) {
            if (newPlayer.getPlayerID().equals(AITeam.get(i).getPlayerID()))
                return false;
        }
        return true;
    }

    public void makeScenario(){
        newEvent = new MatchEvent(mGameScreen, gameState, AITeam);
        winnerDecided = false;
    }

    private void displayWinner(){
        String winner = newEvent.getWinner();
         if (winner != null){

            if (winner.equals("Player")){
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
            } else if (winner.equals("CPU")) {
                if (gameState.equals(GameState.PLAYER_B_DANGEROUS_ATTACK)) {
                    setPlayerBScore(getPlayerBScore() + 1);
                    setGameState(gameState.MIDFIELD);
                } else {
                    switch (getGameState()) {
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

    private void checkForWinner(){
        if (newEvent != null)
            if (newEvent.getWinner() != null){
                displayWinner();
                winnerDecided = true;
            }


    }


    @Override
    public void update(ElapsedTime elapsedTime) {
        if (newEvent != null){
            newEvent.update(elapsedTime);
            if (!winnerDecided)
                checkForWinner();
        }
     }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        if (newEvent != null)
            newEvent.draw(elapsedTime, graphics2D);


    }



}
