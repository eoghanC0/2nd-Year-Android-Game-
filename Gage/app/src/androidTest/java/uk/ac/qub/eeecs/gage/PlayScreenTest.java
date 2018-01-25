package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.DemoGame;
import uk.ac.qub.eeecs.game.cardDemo.screens.PlayScreen;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by stephenmcveigh on 10/12/2017.
 */

@RunWith(AndroidJUnit4.class)
public class PlayScreenTest {
    private Game game;

    @Before
    public void setup() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        game = new DemoGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);
        game.mSharedPreferences = appContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    @Test
    public void testConstructor() {
        PlayScreen testConstructor = new PlayScreen(game);

        ///////////////////////////////////////////////////
        // Check Results
        //////////////////////////////////////////////////

        //Check Screen name
        assertEquals(testConstructor.getName(), "PlayScreen");

        //Check game time and player scores
        assertEquals(testConstructor.currentGameTime, 0.0);
        assertEquals(testConstructor.CPUScore, 0);
        assertEquals(testConstructor.playerScore, 0);

        //Check background
        assertTrue(testConstructor.background != null);
    }
}

