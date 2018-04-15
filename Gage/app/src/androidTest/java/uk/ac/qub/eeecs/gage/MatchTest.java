package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.objects.Match;
import uk.ac.qub.eeecs.game.objects.MatchEvent;
import uk.ac.qub.eeecs.game.screens.PlayScreen;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Aedan on 10/03/2018.
 */

@RunWith(AndroidJUnit4.class)
public class MatchTest {


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
        game.setDifficulty(0);
        game.setGameLength(300);
        playScreen = new PlayScreen(game);

        testMatch = playScreen.currentMatch;

    }

    @Test
    public void testConstructorPlayerScore(){assertEquals(0, testMatch.playerAScore);}

    @Test
    public void testConstructorCPUScore(){assertEquals(0, testMatch.playerBScore);}

    @Test
    public void testConstructorDifficulty(){assertEquals(0, testMatch.difficulty);}

    @Test
    public void testConstructorGameLength(){assertEquals(300, testMatch.totalGameTimeLength);}

    @Test
    public void testConstructorPlayerTeam(){assertEquals(game.getSquad(), testMatch.playerTeam);}

    @Test
    public void testConstructorInfoBar(){assertEquals(true, testMatch.infoBar != null);}

    @Test
    public void testPlayerScoreGetter(){assertEquals(testMatch.getPlayerAScore(), testMatch.playerAScore);}

    @Test
    public void testCPUScoreGetter(){assertEquals(testMatch.getPlayerBScore(), testMatch.playerBScore);}

    @Test
    public void testPopulateAITeam(){
        testMatch.AITeam.clear();
        testMatch.populateAITeam();
        assertEquals(11, testMatch.AITeam.size());
    }

    @Test
    public void testAITeamRatingsDifficulty0(){
        testMatch.AITeam.clear();
        game.setDifficulty(0);
        testMatch.populateAITeam();
        int max = 65;
        for (int i = 0; i < testMatch.AITeam.size(); i++)
            if (testMatch.AITeam.get(i).getRating() > max)
                max = testMatch.AITeam.get(i).getRating();

        assertEquals(65, max);
    }

    @Test
    public void testAITeamRatingsDifficulty1(){
        testMatch.difficulty = 1;
        testMatch.AITeam.clear();
        testMatch.populateAITeam();
        int min = 65;
        int max = 80;
        for (int i = 0; i < testMatch.AITeam.size(); i++) {
            if (testMatch.AITeam.get(i).getRating() < min)
                min = testMatch.AITeam.get(i).getRating();
            if (testMatch.AITeam.get(i).getRating() > max)
                max = testMatch.AITeam.get(i).getRating();
        }
        assertEquals(65, min);
        assertEquals(80, max);
    }

    @Test
    public void testAITeamRatingsDifficulty2(){
        testMatch.difficulty = 2;
        testMatch.AITeam.clear();
        testMatch.populateAITeam();
        int min = 80;
        int max = 99;
        for (int i = 0; i < testMatch.AITeam.size(); i++) {
            if (testMatch.AITeam.get(i).getRating() < min)
                min = testMatch.AITeam.get(i).getRating();
            if (testMatch.AITeam.get(i).getRating() > max)
                max = testMatch.AITeam.get(i).getRating();
        }
        assertEquals(80, min);
        assertEquals(99, max);
    }

    @Test
    public void testNoDuplicates(){
        boolean dupe = false;
        for (int i = 0; i < 5; i++) {
            testMatch.AITeam.clear();
            testMatch.populateAITeam();
            for (int j = 0; j < testMatch.AITeam.size(); j++)
                for (int k = 0; k < testMatch.AITeam.size(); k++) {
                    if (k != j)
                        if (testMatch.AITeam.get(j).getPlayerID().equals(testMatch.AITeam.get(k).getPlayerID()))
                            dupe = true;
                }

        }
        assertEquals(false, dupe);
    }

    @Test
    public void testGameOver(){
        testMatch.currentGameTime = testMatch.totalGameTimeLength;
        testMatch.checkIfGameOver();
        assertEquals(true, testMatch.gameOver);
    }

    @Test
    public void testCheckForScenario(){
        testMatch.timeSinceLastScenario = 6;
        testMatch.checkForScenario();
        assertEquals(true, testMatch.newEvent != null);
    }



}
