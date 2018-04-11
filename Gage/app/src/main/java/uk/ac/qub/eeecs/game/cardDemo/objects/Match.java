package uk.ac.qub.eeecs.game.cardDemo.objects;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;

/**
 * Created by Aedan on 24/01/2018.
 */
//This class stores the details of each match
public class Match extends GameObject {

    private int playerAScore;
    private int playerBScore;
    private GameState gameState;
    private Game mGame;
    private MatchEvent newEvent;
    private ArrayList<Card> AITeam, playerTeam;
    private int difficulty;
    private InfoBar infoBar;

    private final int totalGameTimeLength;
    private double currentGameTime, displayTime, timeSinceLastScenario;

    private boolean winnerDecided, scenarioActive, gameOver;
    public enum GameState{MIDFIELD, PLAYER_A_DANGEROUS_ATTACK, PLAYER_A_ATTACK, PLAYER_B_ATTACK, PLAYER_B_DANGEROUS_ATTACK };

    public Match(FootballGameScreen gameScreen, int difficulty, int gameLength, ArrayList<Card> playerTeam){
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
        playerTeam = gameScreen.getGame().getSquad();
        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, mGameScreen, "Test Player", "M A I N  M E N U", "0 | 0 | 0");
    }

    private void setPlayerAScore(int score){this.playerAScore = score;}

    private void setPlayerBScore(int score){this.playerBScore = score;}

    private void setGameState(GameState gameState) {this.gameState = gameState;}

    private GameState getGameState(){return gameState;}

    public int getPlayerBScore() {
        return playerBScore;
    }

    public int getPlayerAScore() {
        return playerAScore;
    }

    private void populateAITeam() {
        int minRating = 0, maxRating = 0;
        switch (difficulty) {
            case 0:
                minRating = 0;
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
            } else if (i < 5){
                position = "Defence";
            } else if (i < 8){
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

    private void makeScenario(){
        scenarioActive = true;
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
            scenarioActive = false;
        }
    }

    private void checkForWinner(){
        if (newEvent != null)
            if (newEvent.getWinner() != null){
                displayWinner();
                winnerDecided = true;
            }


    }

    private void checkIfGameOver(){
      if (currentGameTime >= totalGameTimeLength)
            gameOver = true;
    }
    /**
     * Updates properties of the InfoBar
     */
    private void updateInfoBar() {
        displayTime = currentGameTime / totalGameTimeLength * 90;
        if (gameOver)
            displayTime = 90.00;
        infoBar.setAreaOneText(String.format("Player %1$d | %2$d CPU", playerAScore, playerBScore));
        infoBar.setAreaTwoText(getGameState().name().replace("_", " ").replace("PLAYER A", "PLAYER").replace("PLAYER B", "CPU"));
        infoBar.setAreaThreeText(String.format("%2.2f", displayTime ));
    }

    private void checkForScenario(){
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
