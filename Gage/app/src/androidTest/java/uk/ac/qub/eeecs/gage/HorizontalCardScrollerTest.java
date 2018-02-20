package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.engine.input.TouchHandler;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.HorizontalCardScroller;

import static junit.framework.Assert.assertEquals;

/**
 * HorizontalCardScroller Tests
 */

@RunWith(AndroidJUnit4.class)
public class HorizontalCardScrollerTest {

    private Context appContext;

    private FootballGame game;

    private HelpScreen gameScreen;

    private HorizontalCardScroller scroller;

    List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        gameScreen = new HelpScreen(game);

        scroller = new HorizontalCardScroller(0, 0, 500, 200, gameScreen);
    }

    @Test
    public void testConstructor_CheckPosition() {
        boolean result = scroller.position.x == 0 && scroller.position.y == 0 ? true : false;
        assertEquals(true, result);
    }

    @Test
    public void testConstructor_CheckWidth() {
        boolean result = scroller.getBound().getWidth() == 500 ? true : false;
        assertEquals(true, result);
    }

    @Test
    public void testConstructor_CheckHeight() {
        boolean result = scroller.getBound().getHeight() == 200 ? true : false;
        assertEquals(true, result);
    }

    @Test
    public void test_Constructor_InvalidData_CheckPosition() {
        float x = 0, y = 0, width = -98.23f, height = -11;
        scroller = new HorizontalCardScroller(x, y, width, height, gameScreen);

        boolean result = scroller.position.x == 0 && scroller.position.y == 0 ? true : false;
        assertEquals(true, result);
    }

    @Test
    public void test_Constructor_InvalidData_CheckWidth() {
        float x = 0, y = 0, width = -98.23f, height = -11;
        scroller = new HorizontalCardScroller(x, y, width, height, gameScreen);

        boolean result = scroller.getBound().getWidth() == 98.23f ? true : false;
        assertEquals(true, result);
    }

    @Test
    public void test_Constructor_InvalidData_CheckHeight() {
        float x = 0, y = 0, width = -98.23f, height = -11;
        scroller = new HorizontalCardScroller(x, y, width, height, gameScreen);

        boolean result = scroller.getBound().getHeight() == 11 ? true : false;
        assertEquals(true, result);
    }

    @Test
    public void test_addScrollerItem_OneItem_CheckItemCount() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        assertEquals(scroller.getItemCount(), 1);
    }

    @Test
    public void test_addScrollerItem_OneItem_CheckCurrentItemIndex() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        assertEquals(scroller.getCurrentItemIndex(), 0);
    }

    @Test
    public void test_addScrollerItem_TwoItems_CheckItemCount() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "2", 100));
        assertEquals(scroller.getItemCount(), 2);
    }

    @Test
    public void test_addScrollerItem_TwoItems_CheckCurrentItemIndex() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "2", 100));
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
        scroller.addTestData();
        assertEquals(scroller.getItemCount(), 11);
    }

    /**
     * Tests in single mode (default)
     */

    @Test
    public void test_clickLeftSideOfScroller_1Item() {
        // Create a fake input event and touch event array
        TouchEvent touchEvent = new TouchEvent();
        touchEvent.type = TouchEvent.TOUCH_UP;
        touchEvent.x = scroller.getBound().getLeft();
        touchEvent.y = scroller.getBound().getHeight() / 2;

        touchEvents.add(touchEvent);

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        // Simulates pushing the left side of the scroller
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));

        scroller.update(new ElapsedTime());
        assertEquals(scroller.isScrollAnimationTriggered(), false);
    }

    @Test
    public void test_clickRightSideOfScroller_1Item() {
        // Create a fake input event and touch event array
        TouchEvent touchEvent = new TouchEvent();
        touchEvent.type = TouchEvent.TOUCH_UP;
        touchEvent.x = scroller.getBound().getRight();
        touchEvent.y = scroller.getBound().getHeight() / 2;

        touchEvents.add(touchEvent);

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        // Simulates pushing the left side of the scroller
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));

        scroller.update(new ElapsedTime());
        assertEquals(scroller.isScrollAnimationTriggered(), false);
        assertEquals(scroller.getNextItemIndex(), -1);
    }

    /*@Test
    public void test_clickLeftSideOfScroller_2Items() {
        // Create a fake input event and touch event array
        TouchEvent touchEvent = new TouchEvent();
        touchEvent.type = TouchEvent.TOUCH_UP;
        touchEvent.x = scroller.getBound().getLeft();
        touchEvent.y = scroller.getBound().getHeight() / 2;

        touchEvents.add(touchEvent);

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        // Simulates pushing the left side of the scroller
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "2", 100));

        scroller.update(new ElapsedTime());
        assertEquals(scroller.isScrollAnimationTriggered(), true);
        assertEquals(scroller.getNextItemIndex(), 1);
    }

    @Test
    public void test_clickLeftSideOfScrollerTwice_2Items() {
        // Scroller moves to left from item 0 to item 1 and cycles back to item 1
    }

    @Test
    public void test_clickRightSideOfScroller_2Items() {
        // Create a fake input event and touch event array
        TouchEvent touchEvent = new TouchEvent();
        touchEvent.type = TouchEvent.TOUCH_UP;
        touchEvent.x = scroller.getBound().getRight();
        touchEvent.y = scroller.getBound().getHeight() / 2;

        touchEvents.add(touchEvent);

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        // Simulates pushing the left side of the scroller
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "2", 100));

        scroller.update(new ElapsedTime());
        assertEquals(scroller.isScrollAnimationTriggered(), true);
        assertEquals(scroller.getNextItemIndex(), 1);
    }*/

    @Test
    public void test_clickRightSideOfScrollerTwice_2Items() {
        // Scroller moves to right from item 0 to item 1 to item 0
    }

    /*@Test
    public void test_dragItem_SelectModeDisabled() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.setSelectMode(true);

        // Create a fake input event and touch event array
        BoundingBox selectDestination = new BoundingBox(1000, 1000, 50, 50);
        scroller.addSelectDestination(selectDestination);

        Vector2 touchVector = new Vector2();

        TouchEvent touchEvent = new TouchEvent();
        touchEvent.type = TouchEvent.TOUCH_DOWN;

        touchEvents.add(touchEvent);

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        Vector2 cardOldVector;
        while (true) {
            touchEvent.x = scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position.x + 5;
            touchEvent.y = scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position.y + 5;
            touchVector.set(touchEvent.x, touchEvent.y);
            touchEvents.clear();
            touchEvents.add(touchEvent);
            scroller.setSimulatedTouchEvents(touchEvents);
            cardOldVector = scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position;
            scroller.update(new ElapsedTime());
            if((scroller.position.x == touchEvent.x && scroller.position.y == touchEvent.y) || scroller.checkIfTouchInArea(touchVector, selectDestination) || cardOldVector == scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position) {
                break;
            }
        }

        assertEquals(scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position.x, scroller.position.x);
    }*/

    /*@Test
    public void test_dragItem_SelectModeEnabled() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.setSelectMode(true);

        // Create a fake input event and touch event array
        BoundingBox selectDestination = new BoundingBox(600, 600, 50, 50);
        scroller.addSelectDestination(selectDestination);

        Vector2 moveVector = new Vector2((selectDestination.x - scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position.x) * 0.4f, (selectDestination.y - scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position.x) * 0.4f);
        Vector2 touchVector = scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position;

        touchEvents.clear();
        TouchEvent touchEvent = new TouchEvent();
        touchEvent.type = TouchEvent.TOUCH_DOWN;
        touchEvent.x = touchVector.x;
        touchEvent.y = touchVector.y;

        touchEvents.add(touchEvent);

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setSimulatedTouchEvents(touchEvents);

        Vector2 cardOldVector;
        ElapsedTime elapsedTime = new ElapsedTime();
        scroller.update(elapsedTime);

        while (true) {
            touchEvents.clear();
            touchEvent.x = scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position.x += moveVector.x;
            touchEvent.y = scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position.y += moveVector.y;
            touchVector.set(touchEvent.x, touchEvent.y);
            touchEvent.type = TouchEvent.TOUCH_DRAGGED;
            touchEvents.add(touchEvent);

            scroller.setSimulatedTouchEvents(touchEvents);
            cardOldVector = scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position;
            scroller.update(elapsedTime);

            if(scroller.checkIfTouchInArea(touchVector, selectDestination) || cardOldVector == scroller.getCardScrollerItems().get(scroller.getCurrentItemIndex()).position) {
                touchEvents.clear();
                touchEvent.type = TouchEvent.TOUCH_UP;
                touchEvents.add(touchEvent);
                scroller.setSimulatedTouchEvents(touchEvents);
                scroller.update(elapsedTime);
                break;
            }
        }

        assertEquals(scroller.isRemovedCardReady(),true);
    }*/

    @Test
    public void test_dragItemToValidSelectDestination_SelectModeEnabled() {
        // Item moves up
    }

    /**
     * Multi Item Tests
     */
    @Test
    public void test_setMultiMode_0ItemsInScroller() {
        scroller.setMultiMode(true, 100);
        assertEquals(scroller.getMultiMode(), true);
    }

    @Test
    public void test_setMultiMode_True_1ItemInScroller() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.setMultiMode(true, 100);
        assertEquals(scroller.getMultiMode(), true);
    }

    @Test
    public void test_calculateMultiItemsDisplayed_MultipleItemsInScroller() {
        scroller.addTestData();
        // calculateMultiItemsDisplayed() is triggered by setMultiMode()
        scroller.setMultiMode(true, 100);
        assertEquals(scroller.getMaxDisplayedItems(),3);
    }

    @Test
    public void test_setMultiMode_True_False_1ItemInScroller_CheckPosition() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.setMultiMode(true, 100);
        scroller.setMultiMode(false, 100);
        assertEquals(scroller.getCardScrollerItems().get(0).position.x, scroller.position.x);
        assertEquals(scroller.getCardScrollerItems().get(0).position.y, scroller.position.y);
    }

    @Test
    public void test_setMultiMode_True_False_MultipleItemsInScroller_CheckPosition() {
        scroller.addTestData();
        scroller.setMultiMode(true, 100);
        scroller.setMultiMode(false, 100);
        assertEquals(scroller.getCardScrollerItems().get(0).position.x, scroller.position.x);
        assertEquals(scroller.getCardScrollerItems().get(0).position.y, scroller.position.y);
    }

    @Test
    public void test_calculateNextMultiVectors_1ItemInScroller() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals(scroller.getNextItemIndex(), -1);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionTrue_CheckNextItemIndex() {
        scroller.addTestData();
        scroller.setScrollDirection(true);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals(scroller.getNextItemIndex(), 9);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionTrue_CheckPosition_Item9() {
        scroller.addTestData();
        scroller.setScrollDirection(true);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals((scroller.getCardScrollerItems().get(9).position.x == -661.0f) && (scroller.getCardScrollerItems().get(9).position.y == 0.0), true);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionTrue_CheckPosition_Item10() {
        scroller.addTestData();
        scroller.setScrollDirection(true);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals((scroller.getCardScrollerItems().get(10).position.x == -501.0f) && (scroller.getCardScrollerItems().get(10).position.y == 0.0), true);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionFalse_CheckNextItemIndex() {
        scroller.addTestData();
        scroller.setScrollDirection(false);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals(scroller.getNextItemIndex(), 3);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionFalse_CheckPosition_Item3() {
        scroller.addTestData();
        scroller.setScrollDirection(false);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals((scroller.getCardScrollerItems().get(3).position.x == 339f) && (scroller.getCardScrollerItems().get(3).position.y == 0.0), true);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionFalse_CheckPosition_Item4() {
        scroller.addTestData();
        scroller.setScrollDirection(false);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals((scroller.getCardScrollerItems().get(4).position.x == 499f) && (scroller.getCardScrollerItems().get(4).position.y == 0.0), true);
    }

    @Test
    public void test_calculateNextMultiVectors_MultipleItemsInScroller_ScrollDirectionFalse_CheckPosition_Item5() {
        scroller.addTestData();
        scroller.setScrollDirection(false);
        scroller.setMultiMode(true, 100);
        scroller.calculateNextMultiVectors();
        assertEquals(scroller.getNextItemIndex(), 3);
        assertEquals((scroller.getCardScrollerItems().get(5).position.x == 659f) && (scroller.getCardScrollerItems().get(5).position.y == 0.0), true);
    }

    @Test
    public void test_addScrollerItem_NullCard() {
        scroller.addScrollerItem(null);
        assertEquals(scroller.getCardScrollerItems().size(), 0);
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
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "2", 100));
        scroller.clearScroller();
        assertEquals(scroller.getCardScrollerItems().size(), 0);
    }

    @Test
    public void test_clearScroller_NoItems() {
        scroller.clearScroller();
        assertEquals(scroller.getCardScrollerItems().size(), 0);
    }

    @Test
    public void test_isAnimating_NoAnimations() {
        assertEquals(scroller.isAnimating(), false);
    }

    @Test
    public void test_isAnimating_ScrollerAnimating() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "2", 100));

        scroller.setUseSimulatedTouchEvents(true);
        scroller.setPushButtonLeftPush(true);

        scroller.update(new ElapsedTime());

        assertEquals(scroller.isAnimating(), true);
    }

    @Test
    public void test_adjustPosition_ScrollPositionChanged() {
        Vector2 originalPosition = scroller.position;
        scroller.adjustPosition(10,10);
        originalPosition.add(10,10);
        assertEquals(scroller.position, originalPosition);
    }

    @Test
    public void test_adjustPosition_ButtonPositionsChanged_CheckPosition_ButtonLeft() {
        Vector2 originalPosition;
        originalPosition = scroller.getPushButtonLeft().position;
        scroller.adjustPosition(10,10);
        originalPosition.add(10,10);
        boolean result = scroller.getPushButtonLeft().position.x == originalPosition.x && scroller.getPushButtonLeft().position.y == originalPosition.y ? true : false;
        assertEquals(true, result);
    }

    @Test
    public void test_adjustPosition_ButtonPositionsChanged_CheckPosition_ButtonRight() {
        Vector2 originalPosition;
        originalPosition = scroller.getPushButtonRight().position;
        scroller.adjustPosition(10,10);
        originalPosition.add(10,10);
        boolean result = scroller.getPushButtonRight().position.x == originalPosition.x && scroller.getPushButtonRight().position.y == originalPosition.y ? true : false;
        assertEquals(true, result);
    }

    @Test
    public void test_adjustPosition_CardPositionsChanged() {
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "1", 100));
        scroller.addScrollerItem(new Card(500, 500,100, gameScreen, "2", 100));

        Vector2[] originalPositions = new Vector2[scroller.getCardScrollerItems().size()];
        for (int i = 0; i < scroller.getCardScrollerItems().size(); i++) {
            originalPositions[i] = scroller.getCardScrollerItems().get(i).position;
        }

        scroller.adjustPosition(10,10);

        boolean valuesCorrect = true;
        for (int i = 0; i < scroller.getCardScrollerItems().size(); i++) {
            originalPositions[i].add(10, 10);
            if(originalPositions[i] != scroller.getCardScrollerItems().get(i).position)
                valuesCorrect = false;
        }

        assertEquals(valuesCorrect, true);
    }

    @Test
    public void test_checkIfTouchInArea_InArea() {
        Vector2 touchLocation = new Vector2(50, 50);
        BoundingBox selectDestination = new BoundingBox(50, 50, 5, 5);
        assertEquals(scroller.checkIfTouchInArea(touchLocation, selectDestination), true);
    }

    @Test
    public void test_checkIfTouchInArea_OutsideArea() {
        Vector2 touchLocation = new Vector2(25, 25);
        BoundingBox selectDestination = new BoundingBox(50, 50, 5, 5);
        assertEquals(scroller.checkIfTouchInArea(touchLocation, selectDestination), false);
    }

}

