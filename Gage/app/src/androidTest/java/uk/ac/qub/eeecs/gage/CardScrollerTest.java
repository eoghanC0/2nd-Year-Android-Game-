package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.objects.Card;
import uk.ac.qub.eeecs.game.screens.HelpScreen;
import uk.ac.qub.eeecs.game.ui.CardScroller;

import static junit.framework.Assert.assertEquals;


/**
 * CardScroller Tests
 */


@RunWith(AndroidJUnit4.class)
public class CardScrollerTest {

    private Context appContext;

    private FootballGame game;

    private HelpScreen gameScreen;

    private CardScroller scroller;

    private Random rand;

    List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();

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

        gameScreen = new HelpScreen(game);

        scroller = new CardScroller(0, 0, 500, 200, gameScreen);

        rand = new Random();

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

    public void addTestData() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen,"2", 100));
        scroller.addScrollerItem(new Card(gameScreen,"3", 100));
        scroller.addScrollerItem(new Card(gameScreen,"4", 100));
        scroller.addScrollerItem(new Card(gameScreen,"5", 100));
        scroller.addScrollerItem(new Card(gameScreen,"6", 100));
        scroller.addScrollerItem(new Card(gameScreen,"7", 100));
        scroller.addScrollerItem(new Card(gameScreen,"8", 100));
        scroller.addScrollerItem(new Card(gameScreen,"9", 100));
        scroller.addScrollerItem(new Card(gameScreen,"10", 100));
        scroller.addScrollerItem(new Card(gameScreen,"11", 100));
    }


    @Test
    public void testConstructor_CheckPosition() {
        boolean result = scroller.position.x == 0 && scroller.position.y == 0;
        assertEquals(true, result);
    }

    @Test
    public void testConstructor_CheckWidth() {
        boolean result = scroller.getBound().getWidth() == 500;
        assertEquals(true, result);
    }

    @Test
    public void testConstructor_CheckHeight() {
        boolean result = scroller.getBound().getHeight() == 200;
        assertEquals(true, result);
    }

    @Test
    public void test_Constructor_InvalidData_CheckPosition() {
        float x = 0, y = 0, width = -98.23f, height = -11;
        scroller = new CardScroller(x, y, width, height, gameScreen);

        boolean result = scroller.position.x == 0 && scroller.position.y == 0;
        assertEquals(true, result);
    }

    @Test
    public void test_Constructor_InvalidData_CheckWidth() {
        float x = 0, y = 0, width = -98.23f, height = -11;
        scroller = new CardScroller(x, y, width, height, gameScreen);

        boolean result = scroller.getBound().getWidth() == 98.23f;
        assertEquals(true, result);
    }

    @Test
    public void test_Constructor_InvalidData_CheckHeight() {
        float x = 0, y = 0, width = -98.23f, height = -11;
        scroller = new CardScroller(x, y, width, height, gameScreen);

        boolean result = scroller.getBound().getHeight() == 11;
        assertEquals(true, result);
    }

    @Test
    public void test_addScrollerItem_OneItem_CheckItemCount() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        assertEquals(scroller.getItemCount(), 1);
    }

    @Test
    public void test_addScrollerItem_OneItem_CheckCurrentItemIndex() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        assertEquals(scroller.getCurrentItemIndex(), 0);
    }

    @Test
    public void test_addScrollerItem_TwoItems_CheckItemCount() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "2", 100));
        assertEquals(scroller.getItemCount(), 2);
    }

    @Test
    public void test_addScrollerItem_TwoItems_CheckCurrentItemIndex() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "2", 100));
        assertEquals(scroller.getCurrentItemIndex(), 0);
    }

    @Test
    public void test_setBackground() {
        game.getAssetManager().loadAndAddBitmap("HCS-test_setBackground", "img/his-background.png");
        scroller.setBackground(game.getAssetManager().getBitmap("HCS-test_setBackground"));
    }

    @Test
    public void test_setSelectMode() {
        boolean originalValue = scroller.getSelectMode();
        scroller.setSelectMode(true);
        assertEquals(scroller.getSelectMode(), !originalValue);
    }

    @Test
    public void test_addTestData() {
        addTestData();
        assertEquals(scroller.getItemCount(), 11);
    }

    /**
     * Tests in single mode (default)
     */

    @Test
    public void test_swipeLeft_1Item() {
        // Nothing happens
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        performSwipe(false);

        assertEquals(0, scroller.getCurrentItemIndex());
        assertEquals(-1, scroller.getNextItemIndex());
    }

    @Test
    public void test_swipeRight_1Item() {
        // Nothing happens
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        performSwipe(true);

        assertEquals(0, scroller.getCurrentItemIndex());
        assertEquals(-1, scroller.getNextItemIndex());
    }

    @Test
    public void test_swipeLeft_2Items() {
        // Scroller moves to left from item 0 to item 1
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        performSwipe(false);

        assertEquals(1, scroller.getCurrentItemIndex());
    }

    @Test
    public void test_swipeLeftTwice_2Items() {
        // Scroller moves to left from item 0 to item 1 and cycles back to item 0
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        performSwipe(false);
        performSwipe(false);

        assertEquals(0, scroller.getCurrentItemIndex());
    }

    @Test
    public void test_swipeRight_2Items() {
        // Scroller moves to left from item 0 to item 1
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        performSwipe(true);

        assertEquals(1, scroller.getCurrentItemIndex());
    }

    @Test
    public void test_swipeRightTwice_2Items() {
        // Scroller moves to left from item 0 to item 1 and cycles back to item 0
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        performSwipe(true);
        performSwipe(true);

        assertEquals(0, scroller.getCurrentItemIndex());
    }

    @Test
    public void test_dragItemToSelectDestination_SelectModeDisabled() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 80);
        scroller.setSelectMode(false);
        float x = rand.nextInt(), y = rand.nextInt(), width = rand.nextFloat() + 2, height = rand.nextFloat() + 2;
        BoundingBox selectedDestination = new BoundingBox(x, y, width / 2.0f, height / 2.0f);
        scroller.addSelectDestination(selectedDestination);
        scroller.setUseSimulatedTouchEvents(true);

        // TouchDown on card
        touchEvents.clear();
        TouchEvent touchEvent = new TouchEvent();
        touchEvent.type = TouchEvent.TOUCH_DOWN;
        touchEvent.x = scroller.getScrollerItems().get(0).position.x;
        touchEvent.y = scroller.getScrollerItems().get(0).position.y;
        touchEvents.add(touchEvent);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        scroller.setTouchDownTime(System.nanoTime() - 300000001);

        touchEvents.clear();
        touchEvent.type = TouchEvent.TOUCH_DRAGGED;
        touchEvent.x = selectedDestination.x;
        touchEvent.y = selectedDestination.y;
        touchEvents.add(touchEvent);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        // TouchUp on card
        touchEvents.clear();
        touchEvent.type = TouchEvent.TOUCH_UP;
        touchEvent.x = scroller.getScrollerItems().get(0).position.x;
        touchEvent.y = scroller.getScrollerItems().get(0).position.y;
        touchEvents.add(touchEvent);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        assertEquals(null, scroller.getRemovedCard());
    }

    @Test
    public void test_dragItemToSelectDestination_SelectModeEnabled() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 80);
        scroller.setSelectMode(true);
        float x = rand.nextInt(), y = rand.nextInt(), width = rand.nextFloat(), height = rand.nextFloat();
        BoundingBox selectedDestination = new BoundingBox(1000, 1000, 50, 50);
        scroller.addSelectDestination(selectedDestination);
        scroller.setUseSimulatedTouchEvents(true);

        String selectedCardString = scroller.getScrollerItems().get(0).toString();

        // TouchDown on card
        touchEvents.clear();
        TouchEvent touchEvent = new TouchEvent();
        touchEvent.type = TouchEvent.TOUCH_DOWN;
        touchEvent.x = scroller.getScrollerItems().get(0).position.x;
        touchEvent.y = scroller.getScrollerItems().get(0).position.y;
        touchEvents.add(touchEvent);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        scroller.setTouchDownTime(System.nanoTime() - 300000001);

        touchEvents.clear();
        touchEvent.type = TouchEvent.TOUCH_DRAGGED;
        touchEvent.x = selectedDestination.x;
        touchEvent.y = selectedDestination.y;
        touchEvents.add(touchEvent);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        // TouchUp on card
        touchEvents.clear();
        touchEvent.type = TouchEvent.TOUCH_UP;
        touchEvent.x = scroller.getScrollerItems().get(0).position.x;
        touchEvent.y = scroller.getScrollerItems().get(0).position.y;
        touchEvents.add(touchEvent);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        Card removedCard = scroller.getRemovedCard();
        if(removedCard == null) {
            removedCard = new Card(gameScreen, "2", 100);
        }

        assertEquals(false, scroller.isAnimating());
        assertEquals(selectedCardString, removedCard.toString());
    }

    @Test
    public void test_dragItemToNonSelectDestination_SelectModeEnabled() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 80);
        scroller.setSelectMode(true);
        float x = rand.nextInt(), y = rand.nextInt(), width = rand.nextFloat() + 2, height = rand.nextFloat() + 2;
        BoundingBox selectedDestination = new BoundingBox(x, y, width / 2.0f, height / 2.0f);
        scroller.addSelectDestination(selectedDestination);
        scroller.setUseSimulatedTouchEvents(true);

        // TouchDown on card
        touchEvents.clear();
        TouchEvent touchEvent = new TouchEvent();
        touchEvent.type = TouchEvent.TOUCH_DOWN;
        touchEvent.x = scroller.getScrollerItems().get(0).position.x;
        touchEvent.y = scroller.getScrollerItems().get(0).position.y;
        touchEvents.add(touchEvent);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        scroller.setTouchDownTime(System.nanoTime() - 300000001);

        touchEvents.clear();
        touchEvent.type = TouchEvent.TOUCH_DRAGGED;
        touchEvent.x = 500f;
        touchEvent.y = 500f;
        touchEvents.add(touchEvent);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        // TouchUp on card
        touchEvents.clear();
        touchEvent.type = TouchEvent.TOUCH_UP;
        touchEvent.x = scroller.getScrollerItems().get(0).position.x;
        touchEvent.y = scroller.getScrollerItems().get(0).position.y;
        touchEvents.add(touchEvent);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        assertEquals(false, scroller.isAnimating());
        assertEquals(null, scroller.getRemovedCard());
    }


    /**
     * Multi Item Tests
    **/

    @Test
    public void test_setMultiMode_0ItemsInScroller() {
        scroller.setMultiMode(true, 100);
        assertEquals(scroller.getMultiMode(), true);
    }

    @Test
    public void test_setMultiMode_True_1ItemInScroller() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 100);
        assertEquals(scroller.getMultiMode(), true);
    }

    @Test
    public void test_calculateMultiItemsDisplayed_MultipleItemsInScroller() {
        addTestData();
        // calculateMultiItemsDisplayed() is triggered by setMultiMode()
        scroller.setMultiMode(true, 100);
        assertEquals(scroller.getMaxDisplayedItems(),3);
    }

    @Test
    public void test_setMultiMode_True_False_1ItemInScroller_CheckPosition() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 100);
        scroller.setMultiMode(false, 100);
        assertEquals(scroller.getScrollerItems().get(0).position.x, scroller.position.x);
        assertEquals(scroller.getScrollerItems().get(0).position.y, scroller.position.y);
    }

    @Test
    public void test_setMultiMode_True_False_MultipleItemsInScroller_CheckPosition() {
        addTestData();
        scroller.setMultiMode(true, 100);
        scroller.setMultiMode(false, 100);
        assertEquals(scroller.getScrollerItems().get(0).position.x, scroller.position.x);
        assertEquals(scroller.getScrollerItems().get(0).position.y, scroller.position.y);
    }

    @Test
    public void test_calculateNextMultiVectors_1ItemInScroller() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals(scroller.getNextItemIndex(), -1);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionTrue_CheckNextItemIndex() {
        addTestData();
        scroller.setScrollDirection(true);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals(scroller.getNextItemIndex(), 9);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionTrue_CheckPosition_Item9() {
        addTestData();
        scroller.setScrollDirection(true);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals((scroller.getScrollerItems().get(9).position.x == -661.0f) && (scroller.getScrollerItems().get(9).position.y == 0.0), true);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionTrue_CheckPosition_Item10() {
        addTestData();
        scroller.setScrollDirection(true);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals((scroller.getScrollerItems().get(10).position.x == -501.0f) && (scroller.getScrollerItems().get(10).position.y == 0.0), true);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionFalse_CheckNextItemIndex() {
        addTestData();
        scroller.setScrollDirection(false);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals(scroller.getNextItemIndex(), 3);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionFalse_CheckPosition_Item3() {
        addTestData();
        scroller.setScrollDirection(false);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals((scroller.getScrollerItems().get(3).position.x == 339f) && (scroller.getScrollerItems().get(3).position.y == 0.0), true);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionFalse_CheckPosition_Item4() {
        addTestData();
        scroller.setScrollDirection(false);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals((scroller.getScrollerItems().get(4).position.x == 499f) && (scroller.getScrollerItems().get(4).position.y == 0.0), true);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionFalse_CheckPosition_Item5() {
        addTestData();
        scroller.setScrollDirection(false);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals(scroller.getNextItemIndex(), 3);
        assertEquals((scroller.getScrollerItems().get(5).position.x == 659f) && (scroller.getScrollerItems().get(5).position.y == 0.0), true);
    }

    @Test
    public void test_addScrollerItem_NullCard() {
        scroller.addScrollerItem(null);
        assertEquals(scroller.getScrollerItems().size(), 0);
    }

    @Test
    public void test_addSelectDestination_ValidBoundingBox() {
        scroller.addSelectDestination(new BoundingBox(0,0,1,1));
        assertEquals(scroller.getSelectDestinations().size(), 1);
    }

    @Test
    public void test_addSelectDestination_InvalidBoundingBox() {
        scroller.addSelectDestination(null);
        assertEquals(scroller.getSelectDestinations().size(), 0);
    }

    @Test
    public void test_clearScroller_MultipleItems() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "2", 100));
        scroller.clearScroller();
        assertEquals(scroller.getScrollerItems().size(), 0);
    }

    @Test
    public void test_clearScroller_NoItems() {
        scroller.clearScroller();
        assertEquals(scroller.getScrollerItems().size(), 0);
    }

    @Test
    public void test_isAnimating_NoAnimations() {
        assertEquals(scroller.isAnimating(), false);
    }

    @Test
    public void test_adjustPosition_ScrollPositionChanged() {
        Vector2 originalPosition = scroller.position;
        scroller.adjustPosition(10,10);
        originalPosition.add(10,10);
        assertEquals(scroller.position, originalPosition);
    }

    @Test
    public void test_adjustPosition_CardPositionsChanged() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "2", 100));

        Vector2[] originalPositions = new Vector2[scroller.getScrollerItems().size()];
        for (int i = 0; i < scroller.getScrollerItems().size(); i++) {
            originalPositions[i] = scroller.getScrollerItems().get(i).position;
        }

        scroller.adjustPosition(10,10);

        boolean valuesCorrect = true;
        for (int i = 0; i < scroller.getScrollerItems().size(); i++) {
            originalPositions[i].add(10, 10);
            if(originalPositions[i] != scroller.getScrollerItems().get(i).position)
                valuesCorrect = false;
        }

        assertEquals(valuesCorrect, true);
    }

    @Test
    public void test_checkIfTouchInArea_InArea() {
        float x = rand.nextFloat(), y = rand.nextFloat(), width = (rand.nextFloat() + 1) % 25, height = (rand.nextFloat() + 1) % 25;
        Vector2 touchLocation = new Vector2(x, y);
        BoundingBox selectDestination = new BoundingBox(x, y, width / 2.0f, height / 2.0f);

        assertEquals(true, scroller.checkIfTouchInArea(touchLocation, selectDestination));
    }

    @Test
    public void test_checkIfTouchInArea_OutsideArea() {
        float x = rand.nextFloat(), y = rand.nextFloat(), width = (rand.nextFloat() + 1) % 25, height = (rand.nextFloat() + 1) % 25;
        Vector2 touchLocation = new Vector2(x + width, y + height);
        BoundingBox selectDestination = new BoundingBox(x, y, width / 2.0f, height / 2.0f);

        assertEquals(false, scroller.checkIfTouchInArea(touchLocation, selectDestination));
    }

    @Test
    public void test_checkFor1PageIcon() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        scroller.setMultiMode(true, 100);

        scroller.update(new ElapsedTime());

        assertEquals(1, scroller.getPageIconPositions().size());
    }

    @Test
    public void test_checkFor2PageIcons() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        scroller.setMultiMode(true, 80);

        scroller.update(new ElapsedTime());

        assertEquals(2, scroller.getPageIconPositions().size());
    }

    @Test
    public void test_checkForCorrectCurrentIndex_1Page() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        scroller.setMultiMode(true, 100);

        scroller.update(new ElapsedTime());

        assertEquals(0, scroller.getCurrentPageIndex());
    }

    @Test
    public void test_checkForCorrectCurrentIndex_2Pages() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        scroller.setMultiMode(true, 80);

        scroller.update(new ElapsedTime());

        assertEquals(0, scroller.getCurrentPageIndex());
    }

    @Test
    public void test_checkChangeToNextPageOnSwipeRight() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 80);

        // Create a fake input event and touch event array
        scroller.setUseSimulatedTouchEvents(true);

        touchEvents.clear();
        touchEvents.add(touchEventDown);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventFlingRight);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventUp);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        assertEquals(1, scroller.getCurrentPageIndex());
    }

    @Test
    public void test_checkChangeToNextPageOnSwipeLeft() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 80);

        // Create a fake input event and touch event array
        scroller.setUseSimulatedTouchEvents(true);

        touchEvents.clear();
        touchEvents.add(touchEventDown);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventFlingLeft);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventUp);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        assertEquals(1, scroller.getCurrentPageIndex());
    }

    @Test
    public void test_checkChangeToNextPageOnSwipeRightTwice() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 80);

        // Create a fake input event and touch event array
        scroller.setUseSimulatedTouchEvents(true);

        // First swipe
        touchEvents.clear();
        touchEvents.add(touchEventDown);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventFlingRight);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventUp);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        // Second swipe
        touchEvents.clear();
        touchEvents.add(touchEventDown);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventFlingRight);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventUp);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        assertEquals(0, scroller.getCurrentPageIndex());
    }

    @Test
    public void test_checkChangeToNextPageOnSwipeLeftTwice() {
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(gameScreen, "1", 100));
        scroller.setMultiMode(true, 80);

        // Create a fake input event and touch event array
        scroller.setUseSimulatedTouchEvents(true);

        // First swipe
        touchEvents.clear();
        touchEvents.add(touchEventDown);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventFlingLeft);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventUp);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        // Second swipe
        touchEvents.clear();
        touchEvents.add(touchEventDown);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventFlingLeft);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        touchEvents.clear();
        touchEvents.add(touchEventUp);
        scroller.setSimulatedTouchEvents(touchEvents);
        scroller.update(new ElapsedTime());

        assertEquals(0, scroller.getCurrentPageIndex());
    }

    @Test
    public void test_setPageIconUnselectedColour() {
        int colour = rand.nextInt();
        scroller.setPageIconUnselectedColour(colour);

        assertEquals(colour, scroller.getPageIconUnselectedColour());
    }

    @Test
    public void test_setPageIconSelectedColour() {
        int colour = rand.nextInt();
        scroller.setPageIconSelectedColour(colour);

        assertEquals(colour, scroller.getPageIconSelectedColour());
    }

    @Test
    public void test_setPageIconShadowColour() {
        int colour = rand.nextInt();
        scroller.setPageIconShadowColour(colour);

        assertEquals(colour, scroller.getPageIconShadowColour());
    }

    @Test
    public void test_setPageIconRelativePercentageYPos() {
        float percentage = Math.abs((rand.nextInt() % 20) / 10.0f);
        scroller.setPageIconRelativePercentageYPos(percentage);

        assertEquals(percentage, scroller.getPageIconRelativePercentageYPos());
    }

}