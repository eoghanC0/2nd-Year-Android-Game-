package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.screens.HelpScreen;
import uk.ac.qub.eeecs.game.ui.InfoBar;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Eimhin Laverty on 08/12/2017.
 */

@RunWith(AndroidJUnit4.class)
public class InfoBarTest {

    private Context appContext;
    private FootballGame game;
    private HelpScreen helpScreen;
    private Random rand;
    private InfoBar infoBar;
    private ElapsedTime elapsedTime;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

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

        assertEquals(x, infoBar.position.x);
        assertEquals(y, infoBar.position.y);
        assertEquals(width, infoBar.getBound().getWidth());
        assertEquals(height, infoBar.getBound().getHeight());
    }

    @Test
    public void constructorA_InvalidData() {
        float width = -(rand.nextInt(1920) + 1);
        float height = -(rand.nextInt(1080) + 1);
        float x = width / 2, y = 0 + height;
        infoBar = new InfoBar(x,y,width, height, helpScreen);

        assertEquals(x, infoBar.position.x);
        assertEquals(y, infoBar.position.y);
        assertEquals(-width, infoBar.getBound().getWidth());
        assertEquals(-height, infoBar.getBound().getHeight());
    }

    @Test
    public void constructorB_ValidData() {
        float width = rand.nextInt(1920) + 1;
        float height = rand.nextInt(1080) + 1;
        float x = width / 2, y = 0 + height;
        infoBar = new InfoBar(x,y,width, height, helpScreen, "Player Name", "0-0-0", "100");

        assertEquals(x, infoBar.position.x);
        assertEquals(y, infoBar.position.y);
        assertEquals(width, infoBar.getBound().getWidth());
        assertEquals("Player Name", infoBar.getAreaOneText());
        assertEquals("0-0-0", infoBar.getAreaTwoText());
        assertEquals("100", infoBar.getAreaThreeText());
    }

    @Test
    public void constructorB_InvalidData() {
        float width = -(rand.nextInt(1920) + 1);
        float height = -(rand.nextInt(1080) + 1);
        float x = width / 2, y = 0 + height;
        infoBar = new InfoBar(x,y,width, height, helpScreen, "Player Name", "0-0-0", "100");

        assertEquals(x, infoBar.position.x);
        assertEquals(y, infoBar.position.y);
        assertEquals(-width, infoBar.getBound().getWidth());
        assertEquals(-height, infoBar.getBound().getHeight());
        assertEquals("Player Name", infoBar.getAreaOneText());
        assertEquals("0-0-0", infoBar.getAreaTwoText());
        assertEquals("100", infoBar.getAreaThreeText());
    }

    public void setupInfoBar() {
        float width = rand.nextInt(1920);
        float height = rand.nextInt(1080);
        float x = width / 2, y = 0 + height;
        infoBar = new InfoBar(x,y,width, height, helpScreen);
        boolean valid = infoBar.position.x == x && infoBar.position.y == y && infoBar.getBound().getWidth() == width && infoBar.getBound().getHeight() == height;

        assertEquals(true, valid);
    }

    @Test
    public void addNotification_ValidType0() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type 0", 0, rand.nextFloat() % 60);
        infoBar.update(elapsedTime);

        assertEquals("Type 0", infoBar.getCurrentNotification().getText());
    }

    @Test
    public void addNotification_ValidType1() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type 1", 1, rand.nextFloat() % 60);
        infoBar.update(elapsedTime);

        assertEquals("Type 1", infoBar.getCurrentNotification().getText());
    }

    @Test
    public void addNotification_ValidType2() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type 2", 2, rand.nextFloat() % 60);
        infoBar.update(elapsedTime);

        assertEquals("Type 2", infoBar.getCurrentNotification().getText());
    }

    @Test
    public void addNotification_Invalid() {
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type -1", -1, rand.nextFloat() % 60);
        infoBar.update(elapsedTime);

        assertEquals(null, infoBar.getCurrentNotification());
    }

    @Test
    public void checkNotifications_ChangingNotificationOnTime() {
        float notificationDisplayTime = rand.nextFloat() % 60;
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Notification 1", 0, notificationDisplayTime);
        infoBar.addNotification("Notification 2", 0, notificationDisplayTime);
        infoBar.update(elapsedTime);
        infoBar.setCurrentNotificationDisplayTime((long) (System.nanoTime() - (notificationDisplayTime * 1e+9)));
        infoBar.update(elapsedTime);

        assertEquals("Notification 2", infoBar.getCurrentNotification().getText());
    }

    @Test
    public void checkNotifications_ReturnToDefaultState() {
        float notificationDisplayTime = rand.nextFloat() % 60;
        setupInfoBar();
        infoBar.clearNotifications();
        infoBar.addNotification("Type 0", 0, notificationDisplayTime);
        infoBar.update(elapsedTime);
        infoBar.setCurrentNotificationDisplayTime((long) (System.nanoTime() - (notificationDisplayTime * 1e+9)));
        infoBar.update(elapsedTime);

        assertEquals(false, infoBar.getNotificationDisplayed());
    }
}
