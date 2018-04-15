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
import uk.ac.qub.eeecs.game.DemoGame;
import uk.ac.qub.eeecs.game.screens.PackScreen;
import uk.ac.qub.eeecs.game.screens.MenuScreen;
import uk.ac.qub.eeecs.game.screens.HelpScreen;
import uk.ac.qub.eeecs.game.screens.OptionsScreen;
import uk.ac.qub.eeecs.game.screens.SquadScreen;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Eoghan on 07/12/2017.
 */


@RunWith(AndroidJUnit4.class)

public class MenuScreenTest {
    private Context appContext;
    private Game game;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new DemoGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);
    }

    @Test
    public void testCorrectHelpScreenTransition() {
        //Create test data
        MenuScreen menuScreen = new MenuScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(menuScreen);
        HelpScreen helpScreen = new HelpScreen(game);

        // Call test
        menuScreen.changeToScreen(helpScreen);

        // Check return
        assertEquals(game.getScreenManager().getCurrentScreen().getName(), helpScreen.getName());
        }

        @Test
        public void testCorrectPacksScreenTransition () {
            // Create test data
            MenuScreen menuScreen = new MenuScreen(game);
            game.mScreenManager = new ScreenManager();
            game.mScreenManager.addScreen(menuScreen);
            PackScreen packScreen = new PackScreen(game);

            // Call test
            menuScreen.changeToScreen(packScreen);

            // Check return
            assertEquals(game.getScreenManager().getCurrentScreen().getName(), packScreen.getName());
        }

        @Test
        public void testCorrectSquadsScreenTransition () {
            // Create test data
            MenuScreen menuScreen = new MenuScreen(game);
            game.mScreenManager = new ScreenManager();
            game.mScreenManager.addScreen(menuScreen);
            SquadScreen squadScreen = new SquadScreen(game);

            // Call test
            menuScreen.changeToScreen(squadScreen);

            // Check return
            assertEquals(game.getScreenManager().getCurrentScreen().getName(), squadScreen.getName());
        }

        @Test
        public void testCorrectOptionsScreenTransition() {
            // Create test data
            MenuScreen menuScreen = new MenuScreen(game);
            game.mScreenManager = new ScreenManager();
            game.mScreenManager.addScreen(menuScreen);
            OptionsScreen optionsScreen = new OptionsScreen(game);

            // Call test
            menuScreen.changeToScreen(optionsScreen);

            // Check return
            assertEquals(game.getScreenManager().getCurrentScreen().getName(), optionsScreen.getName());
        }



    }
