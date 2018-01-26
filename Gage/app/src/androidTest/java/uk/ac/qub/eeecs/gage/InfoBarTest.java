package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
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
    private Random rand;
    private InfoBar infoBar;
    private ElapsedTime elapsedTime;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new DemoGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        helpScreen = new HelpScreen(game);

        rand = new Random();
        elapsedTime = new ElapsedTime();
        elapsedTime.stepTime = 0.01;
        elapsedTime.totalTime = 1;
    }

    @Test
    public void constructorA_ValidData() {
        float width = rand.nextInt(1920);
        float height = rand.nextInt(1080);
        float x = width / 2, y = 0 + height;
        infoBar = new InfoBar(x,y,width, height, helpScreen);
        assert(infoBar.position.x == x && infoBar.position.y == y && infoBar.getBound().getWidth() == width && infoBar.getBound().getHeight() == height);
    }

    @Test
    public void constructorA_InvalidData() {
        float width = rand.nextInt(1920) * -1;
        float height = rand.nextInt(1) * -1;
        float x = width / 2, y = 0 + height;
        infoBar = new InfoBar(x,y,width, height, helpScreen);
        assert(infoBar.position.x == x && infoBar.position.y == y && infoBar.getBound().getWidth() == width && infoBar.getBound().getHeight() == height);
    }

    @Test
    public void constructorB_ValidData() {
        float width = rand.nextInt(1920);
        float height = rand.nextInt(1080);
        float x = width / 2, y = 0 + height;
        infoBar = new InfoBar(x,y,width, height, helpScreen, " img/Ball.png", "Player Name", "0-0-0", "100");
        assert(infoBar.position.x == x && infoBar.position.y == y && infoBar.getBound().getWidth() == width && infoBar.getBound().getHeight() == height
                && infoBar.getBitmap() != null && infoBar.getAreaOneText().equals("Player Name") && infoBar.getAreaTwoText().equals("0-0-0") && infoBar.getAreaThreeText().equals("100"));
    }

    @Test
    public void constructorB_InvalidData() {
        float width = rand.nextInt(1920) * -1;
        float height = rand.nextInt(1) * -1;
        float x = width / 2, y = 0 + height;
        infoBar = new InfoBar(x,y,width, height, helpScreen, " img/Ball.png", "Player Name", "0-0-0", "100");
        assert(infoBar.position.x == x && infoBar.position.y == y && infoBar.getBound().getWidth() == width && infoBar.getBound().getHeight() == height
                && infoBar.getBitmap() != null && infoBar.getAreaOneText().equals("Player Name") && infoBar.getAreaTwoText().equals("0-0-0") && infoBar.getAreaThreeText().equals("100"));
    }

    public void setupInfoBar() {
        float width = rand.nextInt(1920);
        float height = rand.nextInt(1080);
        float x = width / 2, y = 0 + height;
        infoBar = new InfoBar(x,y,width, height, helpScreen);
        assert(infoBar.position.x == x && infoBar.position.y == y && infoBar.getBound().getWidth() == width && infoBar.getBound().getHeight() == height);
    }

    @Test
    public void addNotification_ValidType0() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type 0", 0, 5);
        infoBar.update(elapsedTime);
        assert(infoBar.getCurrentNotification().getText() == "Type 0");
    }

    @Test
    public void addNotification_ValidType1() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type 1", 1, 5);
        infoBar.update(elapsedTime);
        assert(infoBar.getCurrentNotification().getText() == "Type 1");
    }

    @Test
    public void addNotification_ValidType2() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type 2", 2, 5);
        infoBar.update(elapsedTime);
        assert(infoBar.getCurrentNotification().getText() == "Type 2");
    }

    @Test
    public void addNotification_Invalid() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type -1", -1, 5);
        infoBar.update(elapsedTime);
        assert(infoBar.getCurrentNotification() == null);
    }

    // TODO: checkNotifications tests not working ignore for now
    @Test
    public void checkNotifications_ChangingNotificationOnTime() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Notification 1", 0, 4);
        infoBar.addNotification("Notification 2", 0, 5);
        infoBar.update(elapsedTime);
        infoBar.setCurrentNotificationDisplayTime((long) (System.nanoTime() + (4.1 * 1e+9)));
        infoBar.update(elapsedTime);
        assert(infoBar.getCurrentNotification().getText() == "Notification 2");
    }

    @Test
    public void checkNotifications_ReturnToDefaultState() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type 0", 0, 5);
        infoBar.update(elapsedTime);
        infoBar.setCurrentNotificationDisplayTime((long) (System.nanoTime() + (5 * 1e+9)));
        infoBar.update(elapsedTime);
        Log.d("TESTOUTPUT", infoBar.getCurrentNotification().toString());
        assert(infoBar.getNotificationDisplayed() == false);
    }
}
