package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ScreenManager;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.screens.SquadScreen;

import static junit.framework.Assert.assertEquals;


/**
 * Created by Iñaki McKearney on 11/12/2017.
 */

    @RunWith(AndroidJUnit4.class)
    public class SquadScreenTest {

        private Context appContext;
        private Game game;

        @Before
        public void setup() {
            appContext = InstrumentationRegistry.getTargetContext();

            game = new DemoGame();

            FileIO fileIO = new FileIO(appContext);
            game.mFileIO = fileIO;
            game.mAssetManager = new AssetStore(fileIO);
            game.mSharedPreferences = appContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        }

        @Test
        public void testCorrectMenuScreenTransition() {
            // Create test data
            SquadScreen squadScreen = new SquadScreen(game);
            game.mScreenManager = new ScreenManager();
            game.mScreenManager.addScreen(squadScreen);

            // Call test
            //squadScreen.changeToScreen(new MenuScreen(game));

            // Check return
            assertEquals(game.getScreenManager().getCurrentScreen().getName(), "MenuScreen");
        }

        @Test
        public void testCorrectPlayScreenTransition() {
            // Create test data
            SquadScreen squadScreen = new SquadScreen(game);
            game.mScreenManager = new ScreenManager();
            game.mScreenManager.addScreen(squadScreen);

            // Call test
            //squadScreen.changeToScreen(new PlayScreen(game));

            // Check return
            assertEquals(game.getScreenManager().getCurrentScreen().getName(), "PlayScreen");
        }
    }
