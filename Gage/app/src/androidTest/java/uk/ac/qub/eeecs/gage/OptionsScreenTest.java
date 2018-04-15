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
import uk.ac.qub.eeecs.game.screens.MenuScreen;
import uk.ac.qub.eeecs.game.screens.OptionsScreen;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Aedan on 10/12/2017.
 */

@RunWith(AndroidJUnit4.class)
public class OptionsScreenTest {
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
    public void testCorrectScreenTransition() {
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

    @Test
    public void testDefaultSharedPreferences(){
        //test that the default shared preferences are loaded
        //clear shared preferences first
        game.clearPreferences();
        assertEquals("Amateur", game.getStringPreference("Difficulty"));
        assertEquals(300, game.getIntPreference("GameLength"));
        assertEquals(1, game.getIntPreference("ScreenType"));

    }

    @Test
    public void testChangingSharedPreferences(){
        //test that the methods can change shared preferences
        //set up test data
        String tag1, tag2, tag3, value1;
        int value2;
        boolean value3;
        tag1 = "Tag 1";
        tag2 = "Tag 2";
        tag3 = "Tag 3";
        value1 = "Test data";
        value2 = 600;
        value3 = true;

        //call the methods
        game.setPreference(tag1, value1);
        game.setPreference(tag2, value2);
        game.setPreference(tag3, value3);

        //check if preferences have been set

        assertEquals(value1, game.getStringPreference(tag1));
        assertEquals(value2, game.getIntPreference(tag2));
        assertEquals(value3, game.getBooleanPreference(tag3));





    }


}
