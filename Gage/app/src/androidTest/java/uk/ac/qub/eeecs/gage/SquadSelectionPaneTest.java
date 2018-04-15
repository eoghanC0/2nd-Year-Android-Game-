package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.objects.Card;
import uk.ac.qub.eeecs.game.screens.HelpScreen;
import uk.ac.qub.eeecs.game.ui.CardHolder;
import uk.ac.qub.eeecs.game.ui.SquadSelectionPane;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by stephenmcveigh on 01/03/2018.
 */

@RunWith(AndroidJUnit4.class)
public class SquadSelectionPaneTest {
    Context appContext;
    FootballGameScreen mockScreen;
    SquadSelectionPane testSelectionPane;
    FootballGame game;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        mockScreen = new HelpScreen(game);

        testSelectionPane = new SquadSelectionPane(mockScreen);
    }

    @Test
    public void test_Constructor_checkBitmap() {
        assertNotEquals(null, testSelectionPane.pitchStateBitmap);
    }

    @Test
    public void test_Constructor_checkOpenListBoxPosition() {
        assertEquals(testSelectionPane.position.y + testSelectionPane.getBound().halfHeight * 1/2, testSelectionPane.openListBoxPositionY, 0);
    }

    @Test
    public void test_initializeCardHolders_generalCase() {
        for (CardHolder holder : testSelectionPane.squadSelectionHolders) holder = null;
        testSelectionPane.initializeCardHolders();
        assertNotEquals(null, testSelectionPane.squadSelectionHolders[5]);
    }

    @Test
    public void test_initializeCardHolders_boundaryLow() {
        for (CardHolder holder : testSelectionPane.squadSelectionHolders) holder = null;
        testSelectionPane.initializeCardHolders();
        assertNotEquals(null, testSelectionPane.squadSelectionHolders[0]);
    }

    @Test
    public void test_initializeCardHolders_boundaryHigh() {
        for (CardHolder holder : testSelectionPane.squadSelectionHolders) holder = null;
        testSelectionPane.initializeCardHolders();
        assertNotEquals(null, testSelectionPane.squadSelectionHolders[10]);
    }

    @Test
    public void test_setUpFormationsListBox_checkPosition() {
        testSelectionPane.formationsListBox.position.x = testSelectionPane.formationsListBox.position.y = 100;
        testSelectionPane.formationsListBox.getBound().halfHeight = 100;
        testSelectionPane.setUpFormationsListBox();
        assertEquals(300, testSelectionPane.formationsListBox.position.y, 0);
    }

    @Test
    public void test_setUpFormationsListBox_checkItems() {
        testSelectionPane.formationsListBox.position.x = testSelectionPane.formationsListBox.position.y = 100;
        testSelectionPane.formationsListBox.getBound().halfHeight = 100;
        testSelectionPane.formationsListBox.clear();
        testSelectionPane.setUpFormationsListBox();
        assertEquals(6, testSelectionPane.formationsListBox.getItems().size());
    }

    @Test
    public void test_setCurrentBackground_outOfRange() {
        testSelectionPane.pitchStateBitmap = null;
        testSelectionPane.currentSelectionArea = -1;
        testSelectionPane.setCurrentBackground();
        assertEquals(null, testSelectionPane.pitchStateBitmap);
    }

    @Test
    public void test_setCurrentBackground_general() {
        testSelectionPane.pitchStateBitmap = null;
        testSelectionPane.currentSelectionArea = 2;
        testSelectionPane.setCurrentBackground();
        assertNotEquals(null, testSelectionPane.pitchStateBitmap);
    }

    @Test
    public void test_animateListBox_open() {
        testSelectionPane.showFormationsToggle.setToggled(true);
        testSelectionPane.formationsListBox.position.x = testSelectionPane.formationsListBox.position.y = 100;
        testSelectionPane.formationsListBox.getBound().halfHeight = 100;
        testSelectionPane.animateListBox();
        assertEquals(75, testSelectionPane.formationsListBox.position.y, 0);
    }

    @Test
    public void test_animateListBox_close() {
        testSelectionPane.showFormationsToggle.setToggled(false);
        testSelectionPane.formationsListBox.position.x = testSelectionPane.formationsListBox.position.y = 100;
        testSelectionPane.formationsListBox.getBound().halfHeight = 100;
        testSelectionPane.animateListBox();
        assertEquals(125, testSelectionPane.formationsListBox.position.y, 0);
    }

    @Test
    public void test_animateListBox_checkCounter() {
        testSelectionPane.listBoxAnimationCounter = 3;
        testSelectionPane.animateListBox();
        assertEquals(4, testSelectionPane.listBoxAnimationCounter);
    }

    @Test
    public void test_checkIfCoordsInArea_yes() {
        BoundingBox box = new BoundingBox(100,100,100,100);
        assertTrue(testSelectionPane.checkIfCoordsInArea(150,150, box));
    }

    @Test
    public void test_checkIfCoordsInArea_no() {
        BoundingBox box = new BoundingBox(100,100,100,100);
        assertFalse(testSelectionPane.checkIfCoordsInArea(500,500, box));
    }

    @Test
    public void test_checkTouchInCardAndTriggerDrag_checkTouchPosition_dragged() {
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 100;
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 100;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 100;
        assertTrue(testSelectionPane.checkTouchInCardAndTriggerDrag(touch,0));
    }

    @Test
    public void test_checkTouchInCardAndTriggerDrag_checkOriginalPos_dragged_x() {
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 100;
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 100;
        testSelectionPane.draggedCardOriginalPosition.x = testSelectionPane.draggedCardOriginalPosition.y = 0;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 100;
        testSelectionPane.checkTouchInCardAndTriggerDrag(touch,0);
        assertEquals(100, testSelectionPane.draggedCardOriginalPosition.x, 0);
    }

    @Test
    public void test_checkTouchInCardAndTriggerDrag_checkOriginalPos_dragged_y() {
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 100;
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 100;
        testSelectionPane.draggedCardOriginalPosition.x = testSelectionPane.draggedCardOriginalPosition.y = 0;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 100;
        testSelectionPane.checkTouchInCardAndTriggerDrag(touch,0);
        assertEquals(100, testSelectionPane.draggedCardOriginalPosition.y, 0);
    }

    @Test
    public void test_checkTouchInCardAndTriggerDrag_checkSelectedIndex_dragged() {
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 100;
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 100;
        testSelectionPane.selectedItemIndex = -1;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 100;
        testSelectionPane.checkTouchInCardAndTriggerDrag(touch,0);
        assertEquals(0, testSelectionPane.selectedItemIndex);
    }

    @Test
    public void test_checkTouchInCardAndTriggerDrag_checkTouchPosition_notDragged() {
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 100;
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 100;
        testSelectionPane.draggedCardOriginalPosition.x = testSelectionPane.draggedCardOriginalPosition.y = 0;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 300;
        assertFalse(testSelectionPane.checkTouchInCardAndTriggerDrag(touch,0));
    }

    @Test
    public void test_checkTouchInCardAndTriggerDrag_checkOriginalPos_notDragged_x() {
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 100;
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 100;
        testSelectionPane.draggedCardOriginalPosition.x = testSelectionPane.draggedCardOriginalPosition.y = 0;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 300;
        testSelectionPane.checkTouchInCardAndTriggerDrag(touch,0);
        assertEquals(0, testSelectionPane.draggedCardOriginalPosition.x, 0);
    }

    @Test
    public void test_checkTouchInCardAndTriggerDrag_checkOriginalPos_notDragged_y() {
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 100;
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 100;
        testSelectionPane.draggedCardOriginalPosition.x = testSelectionPane.draggedCardOriginalPosition.y = 0;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 300;
        testSelectionPane.checkTouchInCardAndTriggerDrag(touch,0);
        assertEquals(0, testSelectionPane.draggedCardOriginalPosition.y, 0);
    }

    @Test
    public void test_checkTouchInCardAndTriggerDrag_checkSelectedIndex_notDragged() {
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 100;
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 100;
        testSelectionPane.selectedItemIndex = -1;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 300;
        testSelectionPane.checkTouchInCardAndTriggerDrag(touch,0);
        assertEquals(-1, testSelectionPane.selectedItemIndex);
    }

    @Test
    public void test_performDrag_noneSelected() {
        testSelectionPane.selectedItemIndex = -1;
        TouchEvent touch = new TouchEvent();
        assertFalse(testSelectionPane.checkSelectedItemAndPerformDrag(touch));

        testSelectionPane.selectedItemIndex = 0;
        //TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 500;
        testSelectionPane.squadSelectionHolders[0].setCard(new Card(mockScreen,"1", 100));
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.checkSelectedItemAndPerformDrag(touch);
        assertEquals(500, testSelectionPane.squadSelectionHolders[0].position.x, 0);
        assertEquals(500, testSelectionPane.squadSelectionHolders[0].position.y, 0);
    }

    @Test
    public void test_performDrag_selectedNullHolder() {
        testSelectionPane.selectedItemIndex = 0;
        TouchEvent touch = new TouchEvent();
        testSelectionPane.squadSelectionHolders[0].setCard(null);
        assertFalse(testSelectionPane.checkSelectedItemAndPerformDrag(touch));
    }

    @Test
    public void test_performDrag_drag_x() {
        testSelectionPane.selectedItemIndex = 0;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 500;
        testSelectionPane.squadSelectionHolders[0].setCard(new Card(mockScreen,"1", 100));
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.checkSelectedItemAndPerformDrag(touch);
        assertEquals(500, testSelectionPane.squadSelectionHolders[0].position.x, 0);
    }

    @Test
    public void test_performDrag_drag_y() {
        testSelectionPane.selectedItemIndex = 0;
        TouchEvent touch = new TouchEvent();
        touch.x = touch.y = 500;
        testSelectionPane.squadSelectionHolders[0].setCard(new Card(mockScreen,"1", 100));
        testSelectionPane.squadSelectionHolders[0].setPosition(100,100);
        testSelectionPane.checkSelectedItemAndPerformDrag(touch);
        assertEquals(500, testSelectionPane.squadSelectionHolders[0].position.y, 0);
    }

    @Test
    public void test_dropIntoScroller_checkItemAddedToScroller() {
        testSelectionPane.cardScroller.setPosition(500,500);
        testSelectionPane.cardScroller.getBound().halfHeight = 200;
        testSelectionPane.cardScroller.getBound().halfWidth = 500;
        testSelectionPane.selectedItemIndex = 0;
        testSelectionPane.squadSelectionHolders[0].setCard(new Card(mockScreen, "1", 100));
        testSelectionPane.squadSelectionHolders[0].position.x = testSelectionPane.squadSelectionHolders[0].position.y = 500;
        int expectedNumOfCards = testSelectionPane.cardScroller.getItemCount() + 1;
        testSelectionPane.dropIntoScroller();
        assertEquals(expectedNumOfCards, testSelectionPane.cardScroller.getItemCount());
    }

    @Test
    public void test_dropIntoScroller_checkItemRemovedFromHolder() {
        testSelectionPane.cardScroller.setPosition(500,500);
        testSelectionPane.cardScroller.getBound().halfHeight = 200;
        testSelectionPane.cardScroller.getBound().halfWidth = 500;
        testSelectionPane.selectedItemIndex = 0;
        testSelectionPane.squadSelectionHolders[0].setCard(new Card(mockScreen, "1", 100));
        testSelectionPane.squadSelectionHolders[0].position.x = testSelectionPane.squadSelectionHolders[0].position.y = 500;
        testSelectionPane.dropIntoScroller();
        assertEquals(null, testSelectionPane.squadSelectionHolders[0].getCard());
    }

    @Test
    public void test_dropIntoScroller_checkDropFailedOutsideDropArea() {
        testSelectionPane.cardScroller.setPosition(500,500);
        testSelectionPane.cardScroller.getBound().halfHeight = 200;
        testSelectionPane.cardScroller.getBound().halfWidth = 500;
        testSelectionPane.selectedItemIndex = 0;
        testSelectionPane.squadSelectionHolders[0].position.x = testSelectionPane.squadSelectionHolders[0].position.y = 2000;
        assertFalse(testSelectionPane.dropIntoScroller());
    }

    @Test
    public void test_dropIntoHolder_checkAddCard() {
        testSelectionPane.squadSelectionHolders[0].setPosition(500,500);
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 200;
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 200;
        testSelectionPane.squadSelectionHolders[0].setCard(null);
        testSelectionPane.selectedItemIndex = 1;
        Card expectedCard = new Card(mockScreen, "1", 100);
        testSelectionPane.squadSelectionHolders[1].setCard(expectedCard);
        testSelectionPane.squadSelectionHolders[1].position.x = testSelectionPane.squadSelectionHolders[1].position.y = 500;
        testSelectionPane.dropIntoHolder(testSelectionPane.squadSelectionHolders[0].getBound());
        assertEquals(expectedCard, testSelectionPane.squadSelectionHolders[0].getCard());
    }

    @Test
    public void test_dropIntoHolder_checkRemoveCard() {
        testSelectionPane.squadSelectionHolders[0].setPosition(500,500);
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 200;
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 200;
        testSelectionPane.squadSelectionHolders[0].setCard(null);
        testSelectionPane.selectedItemIndex = 1;
        Card expectedCard = new Card(mockScreen, "1", 100);
        testSelectionPane.squadSelectionHolders[1].setCard(expectedCard);
        testSelectionPane.squadSelectionHolders[1].position.x = testSelectionPane.squadSelectionHolders[1].position.y = 500;
        testSelectionPane.dropIntoHolder(testSelectionPane.squadSelectionHolders[0].getBound());
        assertEquals(null, testSelectionPane.squadSelectionHolders[1].getCard());
    }

    @Test
    public void test_dropIntoHolder_checkDropFailedOutsideArea() {
        testSelectionPane.squadSelectionHolders[0].setPosition(500,500);
        testSelectionPane.squadSelectionHolders[0].getBound().halfHeight = 200;
        testSelectionPane.squadSelectionHolders[0].getBound().halfWidth = 200;
        testSelectionPane.squadSelectionHolders[0].setCard(null);
        testSelectionPane.selectedItemIndex = 1;
        Card expectedCard = new Card(mockScreen, "1", 100);
        testSelectionPane.squadSelectionHolders[1].setCard(expectedCard);
        testSelectionPane.squadSelectionHolders[1].position.x = testSelectionPane.squadSelectionHolders[0].position.y = 2000;
        assertFalse(testSelectionPane.dropIntoHolder(testSelectionPane.squadSelectionHolders[1].getBound()));
    }

    @Test
    public void test_performDrop_checkSelectedIndexReset() {
        testSelectionPane.selectedItemIndex = 0;
        testSelectionPane.performDrop();
        assertEquals(-1, testSelectionPane.selectedItemIndex);
    }

    @Test
    public void test_performDrop_checkSelectedIndexReset_checkHolderPositionReset_x() {
        testSelectionPane.selectedItemIndex = 0;
        testSelectionPane.performDrop();
        assertEquals(-1, testSelectionPane.selectedItemIndex);

        testSelectionPane.selectedItemIndex = 0;
        testSelectionPane.squadSelectionHolders[0].position = new Vector2(500,500);
        testSelectionPane.draggedCardOriginalPosition = new Vector2(100,100);
        testSelectionPane.performDrop();
        assertEquals(100, testSelectionPane.squadSelectionHolders[0].position.x,0);
    }

    @Test
    public void test_performDrop_checkSelectedIndexReset_checkHolderPositionReset_y() {
        testSelectionPane.selectedItemIndex = 0;
        testSelectionPane.squadSelectionHolders[0].position = new Vector2(500,500);
        testSelectionPane.draggedCardOriginalPosition = new Vector2(100,100);
        testSelectionPane.performDrop();
        assertEquals(100, testSelectionPane.squadSelectionHolders[0].position.y,0);
    }

    @Test
    public void test_assignCardsToLevels_level0() {
        testSelectionPane.formationString = "3-5-2";
        for (int i = 0; i < testSelectionPane.numberOfCardsOnLevel.length; i++)
            testSelectionPane.numberOfCardsOnLevel[i] = 0;
        testSelectionPane.assignCardsToLevels();
        assertEquals(2, testSelectionPane.numberOfCardsOnLevel[0]);
    }

    @Test
    public void test_assignCardsToLevels_level1() {
        testSelectionPane.formationString = "3-5-2";
        for (int i = 0; i < testSelectionPane.numberOfCardsOnLevel.length; i++)
            testSelectionPane.numberOfCardsOnLevel[i] = 0;
        testSelectionPane.assignCardsToLevels();
        assertEquals(5, testSelectionPane.numberOfCardsOnLevel[1]);
    }

    @Test
    public void test_assignCardsToLevels_level2() {
        testSelectionPane.formationString = "3-5-2";
        for (int i = 0; i < testSelectionPane.numberOfCardsOnLevel.length; i++)
            testSelectionPane.numberOfCardsOnLevel[i] = 0;
        testSelectionPane.assignCardsToLevels();
        assertEquals(3, testSelectionPane.numberOfCardsOnLevel[2]);
    }

//    @Test
//    public void test_setFormationFromListBox() {
//        testSelectionPane.formationsListBox.addItem("4-3-3");
//        testSelectionPane.formationsListBox.selectedIndex = testSelectionPane.formationsListBox.getItems().size() - 1;
//        testSelectionPane.setFormationFromListBox();
//        assertEquals("4-3-3", testSelectionPane.formationString);
//    }

    @Test
    public void test_squadIsFull_yes() {
        for (CardHolder holder : testSelectionPane.squadSelectionHolders)
            holder.setCard(new Card(mockScreen,true,null, 30,100));
        assertTrue(testSelectionPane.squadIsFull());
    }

    @Test
    public void test_squadIsFull_no() {
        for (CardHolder holder : testSelectionPane.squadSelectionHolders)
            holder.setCard(null);
        assertFalse(testSelectionPane.squadIsFull());
    }

    @Test
    public void test_calculateHolderPositions_x() {
        testSelectionPane.formationString = "4-4-2";
        testSelectionPane.assignCardsToLevels();
        testSelectionPane.position = new Vector2(500,500);
        testSelectionPane.getBound().halfWidth = 500;
        testSelectionPane.getBound().halfHeight = 200;
        testSelectionPane.calculateHolderPositions();
        assertEquals(375, testSelectionPane.squadSelectionHolders[3].position.x, 0);
    }

    @Test
    public void test_calculateHolderPositions_y() {
        testSelectionPane.formationString = "4-4-2";
        testSelectionPane.assignCardsToLevels();
        testSelectionPane.position = new Vector2(500,500);
        testSelectionPane.getBound().halfWidth = 500;
        testSelectionPane.getBound().halfHeight = 200;
        testSelectionPane.calculateHolderPositions();
        assertEquals(600, testSelectionPane.squadSelectionHolders[3].position.y, 0);
    }

    @Test
    public void test_calculateShownPlaceholderIndices_start() {
        testSelectionPane.formationString = "4-4-2";
        testSelectionPane.assignCardsToLevels();
        testSelectionPane.currentSelectionArea = 1;
        testSelectionPane.calculateShownPlaceholderIndices();
        assertEquals(2, testSelectionPane.shownPlaceholdersStartIndex);
    }

    @Test
    public void test_calculateShownPlaceholderIndices_end() {
        testSelectionPane.formationString = "4-4-2";
        testSelectionPane.assignCardsToLevels();
        testSelectionPane.currentSelectionArea = 1;
        testSelectionPane.calculateShownPlaceholderIndices();
        assertEquals(6, testSelectionPane.shownPlaceholdersEndIndex);
    }

    @Test
    public void test_calculateAvailableHolders() {
        testSelectionPane.availableDropAreas.clear();
        for (int i = 0; i < testSelectionPane.squadSelectionHolders.length; i++) {
            testSelectionPane.squadSelectionHolders[i].setCard(null);
        }
        Card testCard = new Card(mockScreen, "1",100);
        testSelectionPane.shownPlaceholdersStartIndex = 2;
        testSelectionPane.shownPlaceholdersEndIndex = 5;
        testSelectionPane.squadSelectionHolders[3].setCard(testCard);
        testSelectionPane.squadSelectionHolders[4].setCard(testCard);
        testSelectionPane.calculateAvailableHolders();
        assertEquals(1, testSelectionPane.availableDropAreas.size());
    }

    @Test
    public void test_handleNextAreaButtonTrigger() {
        testSelectionPane.currentSelectionArea = 0;
        testSelectionPane.handleNextAreaButtonTrigger();
        assertEquals(1, testSelectionPane.currentSelectionArea);
    }

    @Test
    public void test_handlePreviousAreaButtonTrigger() {
        testSelectionPane.currentSelectionArea = 2;
        testSelectionPane.handlePreviousAreaButtonTrigger();
        assertEquals(1, testSelectionPane.currentSelectionArea);
    }

    @Test
    public void test_checkToggleAndTriggerListBoxAnimation_correctPos_closed() {
        testSelectionPane.listBoxAnimationCounter = 8;
        testSelectionPane.formationsListBox.position.y = testSelectionPane.openListBoxPositionY + 500;
        testSelectionPane.showFormationsToggle.setToggled(false);
        testSelectionPane.checkToggleAndTriggerListBoxAnimation();
        assertEquals(testSelectionPane.LISTBOX_ANIMATION_LENGTH,testSelectionPane.listBoxAnimationCounter);
    }

    @Test
    public void test_checkToggleAndTriggerListBoxAnimation_correctPos_open() {
        testSelectionPane.listBoxAnimationCounter = 8;
        testSelectionPane.formationsListBox.position.y = testSelectionPane.openListBoxPositionY;
        testSelectionPane.showFormationsToggle.setToggled(true);
        testSelectionPane.checkToggleAndTriggerListBoxAnimation();
        assertEquals(testSelectionPane.LISTBOX_ANIMATION_LENGTH,testSelectionPane.listBoxAnimationCounter);
    }

    @Test
    public void test_checkToggleAndTriggerListBoxAnimation_wrongPos_closed() {
        testSelectionPane.listBoxAnimationCounter = 8;
        testSelectionPane.formationsListBox.position.y = testSelectionPane.openListBoxPositionY + 500;
        testSelectionPane.showFormationsToggle.setToggled(true);
        testSelectionPane.checkToggleAndTriggerListBoxAnimation();
        assertEquals(0,testSelectionPane.listBoxAnimationCounter);
    }

    @Test
    public void test_checkToggleAndTriggerListBoxAnimation_wrongPos_open() {
        testSelectionPane.listBoxAnimationCounter = 8;
        testSelectionPane.formationsListBox.position.y = testSelectionPane.openListBoxPositionY;
        testSelectionPane.showFormationsToggle.setToggled(false);
        testSelectionPane.checkToggleAndTriggerListBoxAnimation();
        assertEquals(0,testSelectionPane.listBoxAnimationCounter);
    }
}
