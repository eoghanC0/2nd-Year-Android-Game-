package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.cardDemo.objects.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.ImageScroller;

import static junit.framework.Assert.assertEquals;

/**
 * ImageScroller Tests
 */

@RunWith(AndroidJUnit4.class)
public class ImageScrollerTest {

    private Context appContext;
    private FootballGame game;
    // Testing of scroller requires a GameScreen, so I have randomly chosen HelpScreen
    private HelpScreen helpScreen;
    private ImageScroller scroller;

    private Bitmap testBitmap;

    private TouchEvent touchEventDown;
    private TouchEvent touchEventFlingLeft;
    private TouchEvent touchEventFlingRight;
    private TouchEvent touchEventUp;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        helpScreen = new HelpScreen(game);

        scroller = new ImageScroller(0, 0, 500, 200, helpScreen);

        game.getAssetManager().loadAndAddBitmap("TestBitmap1", "img/card-0.png");
        game.getAssetManager().loadAndAddBitmap("TestBitmap2", "img/card-1.png");
        game.getAssetManager().loadAndAddBitmap("TestBitmap3", "img/his-background.png");

        testBitmap = game.getAssetManager().getBitmap("TestBitmap1");

        // Instantiate touch events
        touchEventDown = getTouchEventDown();
        touchEventFlingLeft = getTouchEventFling(false);
        touchEventFlingRight = getTouchEventFling(true);
        touchEventUp = getTouchEventUp();
    }

    private TouchEvent getTouchEventDown() {
        TouchEvent touch = new TouchEvent();
        touch.x = scroller.getBound().x;
        touch.y = scroller.getBound().y;
        touch.type = TouchEvent.TOUCH_DOWN;
        return touch;
    }

    // False = Left | True = Right
    private TouchEvent getTouchEventFling(boolean direction) {
        TouchEvent touch = new TouchEvent();
        if(direction) {
            touch.x = scroller.getBound().x + 10;
            touch.y = scroller.getBound().y + 10;
            touch.dx = 10;
            touch.dy = 10;
        } else {
            touch.x = scroller.getBound().x - 10;
            touch.y = scroller.getBound().y - 10;
            touch.dx = -10;
            touch.dy = -10;
        }

        touch.type = TouchEvent.TOUCH_FLING;
        return touch;
    }

    private TouchEvent getTouchEventUp() {
        TouchEvent touch = new TouchEvent();
        touch.x = scroller.getBound().x;
        touch.y = scroller.getBound().y;
        touch.type = TouchEvent.TOUCH_UP;
        return touch;
    }

    // False = Left | True = Right
    private void performSwipe(boolean direction) {
        ArrayList<TouchEvent> touchList = new ArrayList<TouchEvent>();
        scroller.setUseSimulatedTouchEvents(true);

        touchList.add(touchEventDown);
        scroller.setSimulatedTouchEvents(touchList);
        scroller.update(new ElapsedTime());
        touchList.clear();

        if(direction) touchList.add(touchEventFlingRight);
        else touchList.add(touchEventFlingLeft);

        scroller.setSimulatedTouchEvents(touchList);
        scroller.update(new ElapsedTime());
        touchList.clear();

        touchList.add(touchEventUp);
        scroller.setSimulatedTouchEvents(touchList);
        scroller.update(new ElapsedTime());
        touchList.clear();
    }

    @Test
    public void testConstructor_ValidData() {
        float x = 0, y = 0, width = 100, height = 50;
        scroller = new ImageScroller(x, y, width, height, helpScreen);

        assertEquals(scroller.position.x, x);
        assertEquals(scroller.position.y, y);
        assertEquals(scroller.getBound().getWidth(), width);
        assertEquals(scroller.getBound().getHeight(), height);
    }

    @Test
    public void test_ConstructorInvalidData() {
        float x = 0, y = 0, width = -98.23f, height = -11;
        scroller = new ImageScroller(x, y, width, height, helpScreen);

        assertEquals(scroller.position.x, x);
        assertEquals(scroller.position.y, y);
        assertEquals(scroller.getBound().getWidth(), 98.23f);
        assertEquals(scroller.getBound().getHeight(), 11.0f);
    }

    @Test
    public void test_addScrollerItem() {
        scroller.addScrollerItem(testBitmap);

        assertEquals(scroller.getItemCount(), 1);
        assertEquals(scroller.getCurrentItemIndex(), 0);
    }

    @Test
    public void test_addSecondScrollerItem() {
        scroller.addScrollerItem(testBitmap);
        scroller.addScrollerItem(testBitmap);

        assertEquals(scroller.getItemCount(), 2);
        assertEquals(scroller.getCurrentItemIndex(), 0);
    }

    @Test
    public void test_setBackground() {
        scroller.setBackground(game.getAssetManager().getBitmap("TestBitmap3"));

        assertEquals(scroller.getBitmap(), game.getAssetManager().getBitmap("TestBitmap3"));
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
    public void test_swipeLeft_1Item() {
        // Nothing happens
        scroller.addScrollerItem(testBitmap);

        performSwipe(false);

        assertEquals(0, scroller.getCurrentItemIndex());
        assertEquals(-1, scroller.getNextItemIndex());
    }

    @Test
    public void test_swipeRight_1Item() {
        // Nothing happens
        scroller.addScrollerItem(testBitmap);

        performSwipe(true);

        assertEquals(0, scroller.getCurrentItemIndex());
        assertEquals(-1, scroller.getNextItemIndex());
    }

    @Test
    public void test_swipeLeft_2Items() {
        // Scroller moves to left from item 0 to item 1
        scroller.addScrollerItem(testBitmap);
        scroller.addScrollerItem(testBitmap);

        performSwipe(false);

        assertEquals(1, scroller.getCurrentItemIndex());
    }

    @Test
    public void test_swipeLeftTwice_2Items() {
        // Scroller moves to left from item 0 to item 1 and cycles back to item 0
        scroller.addScrollerItem(testBitmap);
        scroller.addScrollerItem(testBitmap);

        performSwipe(false);
        performSwipe(false);

        assertEquals(0, scroller.getCurrentItemIndex());
    }

    @Test
    public void test_swipeRight_2Items() {
        // Scroller moves to left from item 0 to item 1
        scroller.addScrollerItem(testBitmap);
        scroller.addScrollerItem(testBitmap);

        performSwipe(true);

        assertEquals(1, scroller.getCurrentItemIndex());
    }

    @Test
    public void test_swipeRightTwice_2Items() {
        // Scroller moves to left from item 0 to item 1 and cycles back to item 0
        scroller.addScrollerItem(testBitmap);
        scroller.addScrollerItem(testBitmap);

        performSwipe(true);
        performSwipe(true);

        assertEquals(0, scroller.getCurrentItemIndex());
    }

    /**
     * Multi mode tests
     */
    @Test
    public void test_setMultiMode_0Items() {
        scroller.setMultiMode(true, 100);

        assertEquals(scroller.getMultiMode(), true);
    }

    @Test
    public void test_setMultiMode_True_1Item() {
        scroller.addScrollerItem(testBitmap);
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
        scroller.addScrollerItem(testBitmap);
        scroller.setMultiMode(true, 100);
        scroller.setMultiMode(false, 100);

        assertEquals(scroller.getScrollerItems().get(0).position.x, scroller.position.x);
        assertEquals(scroller.getScrollerItems().get(0).position.y, scroller.position.y);
    }

    @Test
    public void test_setMultiMode_True_False_MultipleItems() {
        scroller.addTestData();
        scroller.setMultiMode(true, 100);
        scroller.setMultiMode(false, 100);

        assertEquals(scroller.getScrollerItems().get(0).position.x, scroller.position.x);
        assertEquals(scroller.getScrollerItems().get(0).position.y, scroller.position.y);
    }

    @Test
    public void test_calculateNextMultiVectors_1Item() {
        scroller.addScrollerItem(testBitmap);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();

        assertEquals(scroller.getNextItemIndex(), -1);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItems_ScrollDirection_True() {
        scroller.addTestData();
        scroller.setScrollDirection(true);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();

        assertEquals(scroller.getNextItemIndex(), 6);
        assertEquals((scroller.getScrollerItems().get(6).position.x == -661.0f) && (scroller.getScrollerItems().get(6).position.y == 0.0), true);
        assertEquals((scroller.getScrollerItems().get(7).position.x == -501.0f) && (scroller.getScrollerItems().get(7).position.y == 0.0), true);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItems_ScrollDirection_False() {
        scroller.addTestData();
        scroller.setScrollDirection(false);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();

        assertEquals(scroller.getNextItemIndex(), 3);
        assertEquals((scroller.getScrollerItems().get(3).position.x == 339f) && (scroller.getScrollerItems().get(3).position.y == 0.0), true);
        assertEquals((scroller.getScrollerItems().get(4).position.x == 499f) && (scroller.getScrollerItems().get(4).position.y == 0.0), true);
        assertEquals((scroller.getScrollerItems().get(5).position.x == 659f) && (scroller.getScrollerItems().get(5).position.y == 0.0), true);
    }
}

