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
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;

/**
 * Created by Eimhin Laverty on 08/12/2017.
 */

@RunWith(AndroidJUnit4.class)
public class InfoBarTest {

    private Context appContext;
    private Game game;
    private HelpScreen helpScreen;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new DemoGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        helpScreen = new HelpScreen(game);
    }

    @Test
    public void constructorA_ValidData() {
        float x = 0, y = 0, width = 1920, height = 1080;
        InfoBar infoBar = new InfoBar(0,0,1920, 1080, helpScreen);
        assert(infoBar.position.x == x && infoBar.position.y == y && infoBar.getBound().getWidth() == width && infoBar.getBound().getHeight() == height);
    }

    @Test
    public void constructorA_InvalidData() {

    }

    @Test
    public void constructorB_ValidData() {

    }

    @Test
    public void constructorB_InvalidData() {

    }

    @Test
    public void addNotification_ValidType0() {

    }

    @Test
    public void addNotification_ValidType1() {

    }

    @Test
    public void addNotification_ValidType2() {

    }

    @Test
    public void addNotification_Invalid() {

    }

    @Test
    public void checkNotifications_ChangingNotificationOnTime() {

    }

    @Test
    public void checkNotifications_ReturnToDefaultState() {

    }
}
