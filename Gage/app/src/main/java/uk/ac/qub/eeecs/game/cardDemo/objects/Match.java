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
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;

/**
 * Created by Aedan on 24/01/2018.
 */
//This class stores the details of each match
public class Match extends GameObject {

    public int playerAScore;
    public int playerBScore;
    public int timeElapsed;
    public int totalTime;
    public GameState gameState;
    public Game mGame;
    public MatchEvent newEvent;
    public ArrayList<Card> AITeam, playerTeam;
    public int difficulty;
    public InfoBar infoBar;

    public final int totalGameTimeLength;
    public double currentGameTime, displayTime, timeSinceLastScenario;

    public boolean winnerDecided, scenarioActive, gameOver;
    public enum GameState{MIDFIELD, PLAYER_A_DANGEROUS_ATTACK, PLAYER_A_ATTACK, PLAYER_B_ATTACK, PLAYER_B_DANGEROUS_ATTACK };

    public Match(GameScreen gameScreen, int difficulty, int gameLength, ArrayList<Card> playerTeam){
        super(gameScreen);
        this.playerAScore = 0;
        this.playerBScore = 0;
        this.difficulty = difficulty;
        this.totalGameTimeLength = gameLength;
        currentGameTime = 0.0;
        this.playerTeam = playerTeam;
        this.gameState = gameState.MIDFIELD;
        AITeam = new ArrayList<>();
        populateAITeam();
        mGame = mGameScreen.getGame();
        this.gameOver = false;
        this.scenarioActive = false;
        timeSinceLastScenario = 0;


        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, mGameScreen, "", "Test Player", "M A I N  M E N U", "0 | 0 | 0");


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

    public void populateAITeam() {
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
    public boolean checkIfCardIsntDupe(Card newPlayer){
        for (int i = 0; i < AITeam.size(); i++) {
            if (newPlayer.getPlayerID().equals(AITeam.get(i).getPlayerID()))
                return false;
        }
        return true;
    }

    public void makeScenario(){
        scenarioActive = true;
        newEvent = new MatchEvent(mGameScreen, gameState, AITeam);
        winnerDecided = false;
    }

    public void displayWinner(){
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
            scenarioActive = false;
        }
    }

    public void checkForWinner(){
        if (newEvent != null)
            if (newEvent.getWinner() != null){
                displayWinner();
                winnerDecided = true;
            }


    }

    public void checkIfGameOver(){
      if (currentGameTime >= totalGameTimeLength)
            gameOver = true;
    }
    /**
     * Updates properties of the InfoBar
     */
    public void updateInfoBar() {
        displayTime = currentGameTime / totalGameTimeLength * 90;
        if (gameOver)
            displayTime = 90.00;
        infoBar.setAreaOneText(String.format("Player %1$d | %2$d CPU", playerAScore, playerBScore));
        infoBar.setAreaTwoText(getGameState().name().replace("_", " ").replace("PLAYER A", "PLAYER").replace("PLAYER B", "CPU"));
        infoBar.setAreaThreeText(String.format("%2.2f", displayTime ));
    }

    public void checkForScenario(){
        if (timeSinceLastScenario > 5){
            makeScenario();
            timeSinceLastScenario = 0;
        }
    }



    @Override
    public void update(ElapsedTime elapsedTime) {
        checkForScenario();
        checkIfGameOver();
        if (!(scenarioActive || gameOver)) {
            currentGameTime += elapsedTime.stepTime;
            timeSinceLastScenario += elapsedTime.stepTime;
        }


        if (newEvent != null){
            newEvent.update(elapsedTime);
            if (!winnerDecided)
                checkForWinner();
        }



        updateInfoBar();
     }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        if (newEvent != null)
            newEvent.draw(elapsedTime, graphics2D);


        infoBar.draw(elapsedTime, graphics2D);




    }



}
