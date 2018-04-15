package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.ScreenManager;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.screens.MenuScreen;
import uk.ac.qub.eeecs.game.screens.HelpScreen;

import static junit.framework.Assert.assertEquals;

/**
 * HelpScreenTests
 *
 */

@RunWith(AndroidJUnit4.class)
public class HelpScreenTest {

    private Context appContext;
    private FootballGame game;
    private HelpScreen helpScreen;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        helpScreen = new HelpScreen(game);
    }

    @Test
    public void test_CorrectScreenTransition() {
        MenuScreen menuScreen = new MenuScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(menuScreen);
        // Call test
        helpScreen.changeToScreen(menuScreen);

        // Check return
        assertEquals(game.getScreenManager().getCurrentScreen().getName(), menuScreen.getName());
    }

    @Test
    public void test_clickMenuButton() {
        // Returns to MenuScreen
        MenuScreen menuScreen = new MenuScreen(game);
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(menuScreen);

        ArrayList<TouchEvent> touchEvents = new ArrayList<>();
        TouchEvent touchEvent = new TouchEvent();

        helpScreen.menuScreenButton.setUseSimulatedTouchEvents(true);
        helpScreen.imageScroller.setUseSimulatedTouchEvents(true);

        touchEvents.clear();
        touchEvent.type = TouchEvent.TOUCH_DOWN;
        touchEvent.x = helpScreen.menuScreenButton.position.x;
        touchEvent.y = helpScreen.menuScreenButton.position.y;
        touchEvents.add(touchEvent);
        helpScreen.menuScreenButton.setSimulatedTouchEvents(touchEvents);

        helpScreen.update(new ElapsedTime());

        touchEvents.clear();
        touchEvent.type = TouchEvent.TOUCH_UP;
        touchEvent.x = helpScreen.menuScreenButton.position.x;
        touchEvent.y = helpScreen.menuScreenButton.position.y;
        touchEvents.add(touchEvent);
        helpScreen.menuScreenButton.setSimulatedTouchEvents(touchEvents);

        helpScreen.update(new ElapsedTime());

        assertEquals(game.getScreenManager().getCurrentScreen().getName(), menuScreen.getName());
    }
}

