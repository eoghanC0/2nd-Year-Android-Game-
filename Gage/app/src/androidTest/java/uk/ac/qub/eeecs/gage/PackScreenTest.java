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
import uk.ac.qub.eeecs.game.SplashScreen1;
import uk.ac.qub.eeecs.game.cardDemo.screens.MenuScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.PackScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.SquadScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.*;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Eoghan on 26/01/2018.
 */

@RunWith(AndroidJUnit4.class)

public class PackScreenTest {

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
    public void testCorrectMenuScreenTransition() {
        //Create test data
        PackScreen packScreen = new PackScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(packScreen);
        MenuScreen menuScreen = new MenuScreen(game);

        // Call test
        packScreen.changeToScreen(menuScreen);

        // Check return
        assertEquals(game.getScreenManager().getCurrentScreen().getName(), menuScreen.getName());
    }

    @Test
    public void testCorrectSquadScreenTransition () {
        // Create test data
        PackScreen packScreen = new PackScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(packScreen);
        SquadScreen squadScreen = new SquadScreen(game);

        // Call test
        packScreen.changeToScreen(squadScreen);

        // Check return
        assertEquals(game.getScreenManager().getCurrentScreen().getName(), squadScreen.getName());
    }

    @Test
    public void testCorrect100PackEvent () {
        // Create test data
        PackScreen packScreen = new PackScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(packScreen);
        SplashScreen1 splashScreen = new SplashScreen1(game);

        // Call test
        packScreen.changeToScreen(splashScreen);

        // Check return
        assertEquals(game.getScreenManager().getCurrentScreen().getName(), splashScreen.getName());
    }

    @Test
    public void testCorrect300PackEvent () {
        // Create test data
        PackScreen packScreen = new PackScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(packScreen);
        SplashScreen1 splashScreen = new SplashScreen1(game);

        // Call test
        packScreen.changeToScreen(splashScreen);

        // Check return
        assertEquals(game.getScreenManager().getCurrentScreen().getName(), splashScreen.getName());
    }

    @Test
    public void testCorrect500PackEvent () {
        // Create test data
        PackScreen packScreen = new PackScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(packScreen);
        SplashScreen1 splashScreen = new SplashScreen1(game);

        // Call test
        packScreen.changeToScreen(splashScreen);

        // Check return
        assertEquals(game.getScreenManager().getCurrentScreen().getName(), splashScreen.getName());
    }

    @Test
    public void testCorrect1000PackEvent () {
        // Create test data
        PackScreen packScreen = new PackScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(packScreen);
        SplashScreen1 splashScreen = new SplashScreen1(game);

        // Call test
        packScreen.changeToScreen(splashScreen);

        // Check return
        assertEquals(game.getScreenManager().getCurrentScreen().getName(), splashScreen.getName());
    }

    @Test
    public void flickContentScrollRight_ContentOnRightAvailable() {
        // Create test data
        PackScreen packScreen = new PackScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(packScreen);
        HorizontalImageScroller horizontalImageScroller = new HorizontalImageScroller(game.getScreenWidth() / 2, (game.getScreenHeight()/8) * 2.8f, game.getScreenWidth(), game.getScreenHeight()/4, packScreen);

        //call test
//not sure how to test a push button
//horizontalImageScroller.getPushButtonRight().isPushed();

// Check return
        //assertEquals(horizontalImageScroller.getCurrentItemIndex(), 1);
    }

    @Test
    public void flickContentScrollLeft_ContentOnLeftAvailable() {
    // Create test data
    PackScreen packScreen = new PackScreen(game);
    game.mScreenManager = new ScreenManager();
    game.mScreenManager.addScreen(packScreen);
    HorizontalImageScroller horizontalImageScroller = new HorizontalImageScroller(game.getScreenWidth() / 2, (game.getScreenHeight()/8) * 2.8f, game.getScreenWidth(), game.getScreenHeight()/4, packScreen);

        //call test
//not sure how to test a push button
//horizontalImageScroller.getPushButtonLeft().isPushed();

    // Check return
    //assertEquals(horizontalImageScroller.getCurrentItemIndex(), -1);
    }
}
