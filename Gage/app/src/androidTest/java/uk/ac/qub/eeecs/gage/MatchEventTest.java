package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;
import uk.ac.qub.eeecs.game.cardDemo.objects.Match;
import uk.ac.qub.eeecs.game.cardDemo.objects.MatchEvent;
import uk.ac.qub.eeecs.game.cardDemo.screens.PlayScreen;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Aedan on 26/01/2018.
 */
@RunWith(AndroidJUnit4.class)
public class MatchEventTest {


    private Context appContext;
    private FootballGame game;
    private PlayScreen playScreen;

    private MatchEvent testEvent;
    private Match testMatch;



    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();
        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        playScreen = new PlayScreen(game);
        testMatch = playScreen.currentMatch;
        testMatch.makeScenario();
        testEvent = testMatch.newEvent;


    }
    @Test
    public void checkContrsuctorAnimationCounter(){
        assertEquals(0, testEvent.animationCounter);
    }

    @Test
    public void checkConstructorHeight(){assertEquals(game.getScreenHeight() * 0.5f, testEvent.height);}

    @Test
    public void checkConstructorLeftAndRightPositions(){
        assertEquals(game.getScreenWidth() * 0.1f, testEvent.leftHolderPosition);
        assertEquals(game.getScreenWidth() * 0.9f, testEvent.rightHolderPosition);
    }

    @Test
    public void checkConstructorCardHolders(){
        assertEquals(true, testEvent.cardHolder1 != null);
        assertEquals(true, testEvent.cardHolder2 != null);
    }

    @Test
    public void checkConstrutorButton(){assertEquals(true, testEvent.confirmPlayer != null);}

    //check that when a correct scenario is generated when Match Event is called based on the state
    @Test
    public void scenarioMidfieldTest(){
        Match.GameState gameState = Match.GameState.MIDFIELD;
        testEvent.generateSccenario(gameState);
        String scenario = testEvent.chosenScenario;

        boolean success = false;
        if (scenario.equals("PAS PAS") || scenario.equals("DRI DRI") || scenario.equals("HEA HEA") || scenario.equals("PAC PAC")){
            success = true;

        }

        assertEquals(success, true);


    }

    @Test
    public void scenarioAttackTest(){
        Match.GameState gameState = Match.GameState.PLAYER_A_ATTACK;
        testEvent.generateSccenario(gameState);
        String scenario = testEvent.chosenScenario;

        boolean success = false;
        if (scenario.equals("PAS DEF") || scenario.equals("DRI DEF") || scenario.equals("HEA HEA") || scenario.equals("PAC PAC")){
            success = true;
        }

            assertEquals(success, true);


    }


    @Test
    public void scenarioDangerousAttackTest(){

        Match.GameState gameState = Match.GameState.PLAYER_A_DANGEROUS_ATTACK;
        testEvent.generateSccenario(gameState);
        String scenario = testEvent.chosenScenario;
        boolean success = false;
        if (scenario.equals("SHO GK") || scenario.equals("DRI DEF") || scenario.equals("HEA HEA") || scenario.equals("HEA GK")){
            success = true;
        }

            assertEquals(true, success);


    }


    @Test
    public void testWinnerGetter(){
        String testWinner = "Test";
        testEvent.winner = testWinner;
        assertEquals(testWinner, testEvent.getWinner());

    }

    @Test
    public void testScenarioWinner(){
        testEvent.winner = null;
        int testStat1 = 50, testStat2 = 50;
        testEvent.scenarioWinner(testStat1, testStat2);
        assertEquals(true, testEvent.getWinner() != null);
    }


    /**
     * Test that the scroller is created
     * Further tests on the functionality of the scroller is not required
     * as it has been tested before
     */
    @Test
    public void testScrollerIsCreated(){
        assertEquals(true, (testEvent.horizontalCardScroller != null));

    }



    //test that the SelectDestination array list for the scroller only contains the cardholder
    @Test
    public void testSelectDestinationsSize(){
        assertEquals(1, testEvent.horizontalCardScroller.getSelectDestinations().size());
    }

    public void testSelectDestinationIsCardHolder(){
        assertEquals(testEvent.cardHolder1.getBound(), testEvent.horizontalCardScroller.getSelectDestinations().get(0));
    }

    //Test that the card that is removed from the scroller is the card that is placed into the holder
    @Test
    public void testCorrectCardUsed(){
        testEvent.checkIfRemovedCardReady();
        Card removedCard = testEvent.horizontalCardScroller.getRemovedCard();
        assertEquals(removedCard, testEvent.cardHolder1.getCard());

    }
    /**
     * Test that when the card is removed from the scroller but not placed in the card holder it gets
     * added back to the scroller, and that when a card is moved to a different position in the scroller
     * it is not removed
     * However I am unsure of how to carry out these tests now and will carry them out in the next sprint


    @Test
    public void checkCardNotRemovedFromScrollerWhenDraggedOut(){

    }

    @Test
    public void checkCardNotRemovedFromScroller(){

    }
    */




    /**
     * test that when a card is dragged back to the scroller it gets added to the scroller
     */
   @Test
    public void checkAddedToScroller(){
       // Card cardInHolder = testEvent.cardHolder1.getCard();
       //testEvent.checkAndPerformDragCard();
       //assertEquals(testEvent.horizontalCardScroller.getCardScrollerItems().get(testEvent.horizontalCardScroller.getItemCount() - 1), testEvent.tempCard);

    }



    /**
     * Test that when swapping player A in the card holder with Player B in the scroller
     * that player A goes back into the scroller
     */
    @Test
    public void checkSwapPlayerBecomesTemp(){
        Card cardA = testEvent.cardHolder1.getCard();
        testEvent.checkIfRemovedCardReady();
        assertEquals(cardA, testEvent.tempCard);
    }
/*
    @Test
    public void checkTempCardAddedToScroller(){
        testEvent.checkIfTempCardReady();
        assertEquals(testEvent.horizontalCardScroller.getCardScrollerItems().get(testEvent.horizontalCardScroller.getItemCount() - 1), testEvent.tempCard);

    }
    */

    @Test
    public void testTeamSorted(){
        testEvent.bubbleSort("PAC");
        for (int i = 1; i < testEvent.AITeam.size(); i++) {
            assertEquals(true, testEvent.AITeam.get(i).getPace() >= testEvent.AITeam.get(i - 1).getPace());
        }
    }

    @Test
    public void testGetStatsOutfield(){
        Card testPlayer = new Card(playScreen, true, "Forward", 80, 90);
        assertEquals(testPlayer.getPace(), testEvent.getStats(testPlayer, "PAC"));

    }

    @Test
    public void testGetStatsGoalkeeper(){
        Card testPlayer = new Card(playScreen, true, "GoalKeeper", 80, 90);
        assertEquals(testPlayer.getRating(), testEvent.getStats(testPlayer, "GK"));

    }
    @Test
    public void testGetStatsOutfieldAsKeeper(){
        Card testPlayer = new Card(playScreen, true, "Forward", 80, 90);
        assertEquals((int)(testPlayer.getRating()/2), testEvent.getStats(testPlayer, "GK"));

    }

    @Test
    public void testGetStatsGoalkeeperOutfield(){
        Card testPlayer = new Card(playScreen, true, "GoalKeeper", 80, 90);
        assertEquals(testPlayer.getPace(), testEvent.getStats(testPlayer, "PAC"));

    }

    @Test
    public void testFitnessReduced(){
        Card testPlayer = new Card(playScreen, true, "Forward", 80, 90);
        int originalStam = testPlayer.getFitness();
        testEvent.getStats(testPlayer, "SHO");
        assertEquals(true, originalStam > testPlayer.getFitness());
    }
















}
