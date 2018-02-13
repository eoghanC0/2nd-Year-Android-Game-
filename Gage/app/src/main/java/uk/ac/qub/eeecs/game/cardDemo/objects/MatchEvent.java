package uk.ac.qub.eeecs.game.cardDemo.objects;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.PlayScreen;

/**
 * Created by Aedan on 24/01/2018.
 */

public class MatchEvent extends GameObject{


    private Game mGame;
    private String chosenScenario, scenarioDescription;
    private int animationCounter;




    public MatchEvent(GameScreen gameScreen, Match.GameState gameState){
        super(gameScreen);
        mGame = mGameScreen.getGame();
        generateSccenario(gameState);
        animationCounter = 0;

    }

    public String runScenario(Match.GameState gameState){

        Card[] selectedPlayers = selectPlayers(chosenScenario);
        int[] stats = getStats(chosenScenario, selectedPlayers[0], selectedPlayers[1]);
        String winner = (scenarioWinner(stats[0], stats[1]));

        return winner;

    }

    //method to generate a random scenario
    private void generateSccenario(Match.GameState gameState){

        ArrayList<String> possibleScenarios = new ArrayList<String>();

        switch (gameState){
            case MIDFIELD:
                possibleScenarios.add("PAS PAS");
                possibleScenarios.add("PAC PAC");
                possibleScenarios.add("HEA HEA");
                possibleScenarios.add("DRI DRI");
                break;

            case PLAYER_A_ATTACK:
                  possibleScenarios.add("PAC PAC");
                  possibleScenarios.add("HEA HEA");
                  possibleScenarios.add("DRI DEF");
                  possibleScenarios.add("PAS DEF");
                break;
            case PLAYER_B_ATTACK:
                possibleScenarios.add("PAC PAC");
                possibleScenarios.add("HEA HEA");
                possibleScenarios.add("DEF DRI");
                possibleScenarios.add("DEF PAS");
                break;
            case PLAYER_A_DANGEROUS_ATTACK:
                possibleScenarios.add("SHO GK");
                possibleScenarios.add("DRI DEF");
                possibleScenarios.add("HEA GK");
                possibleScenarios.add("HEA HEA");
                break;
            case PLAYER_B_DANGEROUS_ATTACK:
                possibleScenarios.add("GK SHO");
                possibleScenarios.add("DEF DRI");
                possibleScenarios.add("GK HEA");
                possibleScenarios.add("HEA HEA");
                break;
        }

        //choose the scenario
        Random rand = new Random();
        chosenScenario = possibleScenarios.get(rand.nextInt(4));
        possibleScenarios.clear();




    }

    private Card[] selectPlayers(String chosenScenario){
        Card[] selectedPlayers = new Card[2];
        Card randomCardA = new Card(100f, 100f, 100f, mGameScreen, true, 80, 85 );
        Card randomCardB = new Card(100f, 100f, 100f, mGameScreen, true, 70, 75 );
        selectedPlayers[0] = randomCardA;
        selectedPlayers[1] = randomCardB;

        return selectedPlayers;
    }

    private int[] getStats(String chosenScenario, Card playerA, Card playerB){
        int[] stats = new int[2];
        String[] scenario = chosenScenario.split(" ");
        Card currentPlayer = playerA;
        //use the players stamina as a percentage to modify their stats by
        float stamina;
       for (int i = 0; i < 2; i++) {
           if (i == 1){
               currentPlayer = playerB;
           }

           stamina = currentPlayer.getFitness() / 100;
           switch (scenario[i]) {
               case "DEF":
                   stats[i] = (int)stamina * currentPlayer.getDefending();
                   break;
               case "PAS":
                   stats[i] = (int)stamina * currentPlayer.getPassing();
                   break;
               case "PAC":
                   stats[i] = (int)stamina * currentPlayer.getPace();
                   break;
               case "DRI":
                   stats[i] = (int)stamina * currentPlayer.getDribbling();
                   break;
               case "SHO":
                   stats[i] = (int)stamina *  currentPlayer.getShooting();
                   break;
               case "HEA":
                   stats[i] = (int)stamina * currentPlayer.getHeading();
                   break;
               case "GK":
                   stats[i] = (int)stamina * currentPlayer.getRating();
                   break;
             }
       }

        return stats;
    }

    //method to determine which player wins the scenario
    private String scenarioWinner(int playerAStat, int playerBStat){
        String winner = "PlayerA";
        //the max difference is the biggest difference between 2 stats in the game. This is used to
        //give the smaller stat a better chance of winning
        int maxDifference = 70;
        //difference between both stats
        int difference = 0;
        String smallestStat = "PlayerA";
        if (playerAStat > playerBStat)
            smallestStat = "PlayerB";
        difference = Math.abs(playerAStat - playerBStat);
        /*determine the probability of the smaller stat winning.
        Both stats will get a base probability of 0.5. The smaller stats probability
        will be lowered depending on the difference between the two stats.
         */
        double probability = 0.5 * (1 - (difference / maxDifference));
        //generate random number. If number is less than probability then smaller stat wins
        //else larger stat wins

        if (probability < Math.random()){
            //smaller stat wins
            if (smallestStat.equals("PlayerB"))winner = "PlayerB";
        } else{
            //larger stat wins
            if (smallestStat.equals("PlayerA"))winner = "PlayerB";
        }

        return winner;
    }

    @Override
    public void update(ElapsedTime elapsedTime) {

    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        Paint paint = mGame.getPaint();

        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        graphics2D.drawRect((int)(mGame.getScreenWidth() * 0.1f), (int)(mGame.getScreenHeight() * 0.15f),(int)(mGame.getScreenWidth() * 0.9f), (int)(mGame.getScreenHeight() * 0.9f), paint);

        paint.reset();
        paint.setColor(Color.GRAY);
        paint.setTextSize(100);
        graphics2D.drawRect((int)(mGame.getScreenWidth() * 0.1f), (int)(mGame.getScreenHeight() * 0.15f),(int)(mGame.getScreenWidth() * 0.9f), (int)(mGame.getScreenHeight() * 0.9f), paint);

        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        graphics2D.drawText(chosenScenario, 500.0f, 500.0f, paint);

    }
}
