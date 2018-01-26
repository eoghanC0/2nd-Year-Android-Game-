package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ScreenManager;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.DemoGame;
import uk.ac.qub.eeecs.game.cardDemo.screens.MenuScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.HorizontalImageScroller;
import uk.ac.qub.eeecs.game.cardDemo.ui.iHorizontalImageScroller;

import static junit.framework.Assert.assertEquals;

/**
 * HorizontalImageScroller Tests
 * TODO: Click event tests
 * TODO: Redesign tests involving addScrollerItem() after it has been changed
 */


@RunWith(AndroidJUnit4.class)
public class HorizontalImageScrollerTest {

    private Context appContext;
    private Game game;
    // Testing of scroller requires a GameScreen, so I have randomly chosen HelpScreen
    private HelpScreen helpScreen;
    private HorizontalImageScroller scroller;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new DemoGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        helpScreen = new HelpScreen(game);

        scroller = new HorizontalImageScroller(0, 0, 500, 200, helpScreen);
    }

    @Test
    public void testConstructor_ValidData() {
        float x = 0, y = 0, width = 100, height = 50;
        scroller = new HorizontalImageScroller(x, y, width, height, helpScreen);

        assertEquals(scroller.position.x, x);
        assertEquals(scroller.position.y, y);
        assertEquals(scroller.getBound().getWidth(), width);
        assertEquals(scroller.getBound().getHeight(), height);
    }

    @Test
    public void test_ConstructorInvalidData() {
        float x = 0, y = 0, width = -98.23f, height = -11;
        scroller = new HorizontalImageScroller(x, y, width, height, helpScreen);

        assertEquals(scroller.position.x, x);
        assertEquals(scroller.position.y, y);
        assertEquals(scroller.getBound().getWidth(), 98.23f);
        assertEquals(scroller.getBound().getHeight(), 11.0f);
    }

    @Test
    public void test_addScrollerItem() {
        game.getAssetManager().loadAndAddBitmap("TestBitmap1", "img/card-0.png");
        Bitmap testBitmap = game.getAssetManager().getBitmap("TestBitmap1");
        scroller.addScrollerItem(testBitmap);
        assertEquals(scroller.getItemCount(), 1);
        assertEquals(scroller.getCurrentItemIndex(), 0);
    }

    @Test
    public void test_addSecondScrollerItem() {
        game.getAssetManager().loadAndAddBitmap("TestBitmap2", "img/card-1.png");
        Bitmap testBitmap = game.getAssetManager().getBitmap("TestBitmap2");
        scroller.addScrollerItem(testBitmap);
        assertEquals(scroller.getItemCount(), 1);
        assertEquals(scroller.getCurrentItemIndex(), 0);
    }

    @Test
    public void test_setBackground() {
        game.getAssetManager().loadAndAddBitmap("TestBitmap3", "img/his-background.png");
        scroller.setBackground(game.getAssetManager().getBitmap("TestBitmap3"));
    }

    @Test
    public void test_setSelectMode() {
        boolean originalValue = scroller.getSelectMode();
        scroller.setSelectMode(true);
        assertEquals(scroller.getSelectMode(), !originalValue);
    }

    @Test
    public void test_addTestData() {
        scroller.addTestData();
        assertEquals(scroller.getItemCount(), 8);
    }

    /**
     * Tests in single mode (default)
     */

    @Test
    public void test_clickLeftSideOfScroller_1Item() {
        // Nothing happens

    }

    @Test
    public void test_clickRightSideOfScroller_1Item() {
        // Nothing happens
    }

    @Test
    public void test_clickLeftSideOfScroller_2Items() {
        // Scroller moves to left from item 0 to item 1
    }

    @Test
    public void test_clickLeftSideOfScrollerTwice_2Items() {
        // Scroller moves to left from item 0 to item 1 and cycles back to item 1
    }

    @Test
    public void test_clickRightSideOfScroller_2Items() {
        // Scroller moves to right from item 0 to item 1
    }

    @Test
    public void test_clickRightSideOfScrollerTwice_2Items() {
        // Scroller moves to right from item 0 to item 1 to item 0
    }

    @Test
    public void test_clickItem_SelectModeDisabled() {
        // Select mode disabled by default, so nothing happens
    }

    @Test
    public void test_clickItem_SelectModeEnabled() {
        // Item moves up
    }

    @Test
    public void test_clickItemTwice_SelectModeEnabled() {
        // Item moves up then down
    }

    /**
     * Multi Item Tests
     */
    @Test
    public void test_setMultiMode_0Items() {
        scroller.setMultiMode(true, 100);
        assertEquals(scroller.getMultiMode(), false);
    }

    @Test
    public void test_setMultiMode_True_1Item() {
        game.getAssetManager().loadAndAddBitmap("TestBitmap1", "img/card-0.png");
        scroller.addScrollerItem(game.getAssetManager().getBitmap("TestBitmap1"));
        scroller.setMultiMode(true, 100);
        assertEquals(scroller.getMultiMode(), true);
    }

    @Test
    public void test_calculateMultiItemsDisplayed_MultipleItems() {
        scroller.addTestData();
        // calculateMultiItemsDisplayed() is triggered by setMultiMode()
        scroller.setMultiMode(true, 100);
        assertEquals(scroller.getMaxDisplayedItems(),3);
    }

    @Test
    public void test_setMultiMode_True_False_1Item() {
        game.getAssetManager().loadAndAddBitmap("TestBitmap1", "img/card-0.png");
        scroller.addScrollerItem(game.getAssetManager().getBitmap("TestBitmap1"));
        scroller.setMultiMode(true, 100);
        scroller.setMultiMode(false, 100);
        assertEquals(scroller.getImageScrollerItems().get(0).position.x, scroller.position.x);
        assertEquals(scroller.getImageScrollerItems().get(0).position.y, scroller.position.y);
    }

    @Test
    public void test_setMultiMode_True_False_MultipleItems() {
        scroller.addTestData();
        scroller.setMultiMode(true, 100);
        scroller.setMultiMode(false, 100);
        assertEquals(scroller.getImageScrollerItems().get(0).position.x, scroller.position.x);
        assertEquals(scroller.getImageScrollerItems().get(0).position.y, scroller.position.y);
    }
}

