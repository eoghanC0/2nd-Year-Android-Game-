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
import uk.ac.qub.eeecs.game.cardDemo.screens.MenuScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;

import static junit.framework.Assert.assertEquals;

/**
 * HelpScreenTests
 *
 */

@RunWith(AndroidJUnit4.class)
public class HelpScreenTest {

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
    public void testCorrectScreenTransition() {
        // Create test data
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
    public void clickMenuButton() {
        // Returns to MenuScreen
    }

    @Test
    public void flickContentScrollRight_ContentOnRightAvailable() {
        // Moves to content on right
    }

    @Test
    public void flickContentScrollRight_ContentOnRightNotAvailable() {
        // Does not move
    }

    @Test
    public void flickContentScrollLeft_ContentOnLeftAvailable() {
        // Moves to content on left
    }

    @Test
    public void flickContentScrollLeft_ContentOnLeftNotAvailable() {
        // Moves to content on right
    }
}

