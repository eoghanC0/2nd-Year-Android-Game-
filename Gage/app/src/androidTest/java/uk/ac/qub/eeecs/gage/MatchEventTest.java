package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.DemoGame;
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
    private Game game;
    private PlayScreen playScreen;

    private MatchEvent testEvent;



    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();
        game = new DemoGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        playScreen = new PlayScreen(game);
        testEvent = new MatchEvent(playScreen);


    }

    //check that when a correct scenario is generated when Match Event is called based on the state
    @Test
    public void scenarioMidfieldTest(){
        Match.GameState gameState = Match.GameState.MIDFIELD;
        String scenario = testEvent.generateSccenario(gameState);

        boolean success = false;
        if (scenario.equals("PAS PAS") || scenario.equals("DRI DRI") || scenario.equals("HEA HEA") || scenario.equals("PAC PAC")){
            success = true;

        }

        assertEquals(success, true);


    }

    @Test
    public void scenarioAttackTest(){
        Match.GameState gameState = Match.GameState.PLAYERAATTACK;
        String scenario = testEvent.generateSccenario(gameState);

        boolean success = false;
        if (scenario.equals("PAS DEF") || scenario.equals("DRI DEF") || scenario.equals("HEA HEA") || scenario.equals("PAC PAC")){
            success = true;
        }

            assertEquals(success, true);


    }


    @Test
    public void scenarioDangerousAttackTest(){
        Match.GameState gameState = Match.GameState.PLAYERADANGEROUSATTACK;
        String scenario = testEvent.generateSccenario(gameState);

        boolean success = false;
        if (scenario.equals("SHO GK") || scenario.equals("DRI DEF") || scenario.equals("HEA HEA") || scenario.equals("HEA GK")){
            success = true;
        }

            assertEquals(success, true);


    }

    //check correct stats are collected
    @Test
    public void testGetStats(){
        Card randomCardA = new Card(100f, 100f, 100f, playScreen, true, 80, 85 );
        Card randomCardB = new Card(100f, 100f, 100f, playScreen, true, 70, 75 );
        int[] stats = testEvent.getStats("PAC PAC", randomCardA, randomCardB);

        assertEquals(randomCardA.getPace(), stats[0]);


    }

    @Test
    public void testStats2(){
        Card randomCardA = new Card(100f, 100f, 100f, playScreen, true, 80, 85 );
        Card randomCardB = new Card(100f, 100f, 100f, playScreen, true, 70, 75 );
        int[] stats = testEvent.getStats("SHO GK", randomCardA, randomCardB);

        assertEquals(randomCardB.getRating(), stats[1]);

    }




}
