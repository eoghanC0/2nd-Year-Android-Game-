package uk.ac.qub.eeecs.game.cardDemo.objects;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.PlayScreen;

/**
 * Created by Aedan on 24/01/2018.
 */

public class MatchEvent {

    private Match.GameState gameState;
    GameScreen mGameScreen;

    public MatchEvent(Match.GameState gameState){
        gameState = gameState;
    }

    public String runScenario(){
        String chosenScenario = generateSccenario();
        Card[] selectedPlayers = selectPlayers(chosenScenario);
        int[] stats = getStats(chosenScenario, selectedPlayers[0], selectedPlayers[1]);
        String winner = (scenarioWinner(stats[0], stats[1]));

        return winner;

    }

    //method to generate a random scenario
    private String generateSccenario(){

        ArrayList<String> possibleScenarios = new ArrayList<String>();
        switch (gameState){
            case MIDFIELD:
                possibleScenarios.add("PAS PAS");
                possibleScenarios.add("PAC PAC");
                possibleScenarios.add("HEA HEA");
                possibleScenarios.add("DRI DRI");
                break;

            case PLAYERAATTACK:
                  possibleScenarios.add("PAC PAC");
                  possibleScenarios.add("HEA HEA");
                  possibleScenarios.add("DRI DEF");
                  possibleScenarios.add("PAS DEF");
                break;
            case PLAYERBATTACK:
                possibleScenarios.add("PAC PAC");
                possibleScenarios.add("HEA HEA");
                possibleScenarios.add("DEF DRI");
                possibleScenarios.add("DEF PAS");
                break;
            case PLAYERADANGEROUSATTACK:
                possibleScenarios.add("SHO GK");
                possibleScenarios.add("DRI DEF");
                possibleScenarios.add("HEA GK");
                possibleScenarios.add("HEA HEA");
                break;
            case PLAYERBDANGEROUSATTACK:
                possibleScenarios.add("GK SHO");
                possibleScenarios.add("DEF DRI");
                possibleScenarios.add("GK HEA");
                possibleScenarios.add("HEA HEA");
                break;
        }

        //choose the scenario
        Random rand = new Random();
        String chosenScenario = possibleScenarios.get(rand.nextInt(4)+1);


        return chosenScenario;

        //int[] stats = getStats(chosenScenario, playerA, playerB);




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
       for (int i = 0; i < 2; i++) {
           if (i == 1){
               currentPlayer = playerB;
           }


           switch (scenario[i]) {
               case "DEF":
                   stats[i] = currentPlayer.getDefending();
                   break;
               case "PAS":
                   stats[i] = currentPlayer.getPassing();
                   break;
               case "PAC":
                   stats[i] = currentPlayer.getPace();
                   break;
               case "DRI":
                   stats[i] = currentPlayer.getDribbling();
                   break;
               case "SHO":
                   stats[i] = currentPlayer.getShooting();
                   break;
               case "HEA":
                   stats[i] = currentPlayer.getHeading();
                   break;
               case "GK":
                   stats[i] = currentPlayer.getRating();
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
}
