package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.DemoGame;
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.ListBox;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by stephenmcveigh on 24/01/2018.
 */
@RunWith(AndroidJUnit4.class)
public class ListBoxTest {
    Context appContext;
    GameScreen mockScreen;
    ListBox testListBox;
    Game game;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new DemoGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        mockScreen = new HelpScreen(game);
        testListBox = new ListBox(100,100,100,400, mockScreen);
    }


    @Test
    public void test_setArray() {
        ArrayList<String> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        testListBox.setArray(list);
        assertEquals(testListBox.items,list);
    }

    @Test
    public void test_setBackColor() {
        testListBox.setBackColor(Color.RED);
        assertEquals(testListBox.backColor, Color.RED);
    }

    @Test
    public void test_setBorderColor() {
        testListBox.setBorderColor(Color.RED);
        assertEquals(testListBox.borderColor, Color.RED);
    }

    @Test
    public void test_setSelectionColor() {
        testListBox.setSelectionColor(Color.RED);
        assertEquals(testListBox.selectionColor, Color.RED);
    }

    @Test
    public void test_setTextColor() {
        testListBox.setTextColor(Color.RED);
        assertEquals(testListBox.textColor, Color.RED);
    }

    @Test
    public void test_getSelectedIndex() {
        assertEquals(testListBox.getSelectedIndex(), testListBox.selectedIndex);
    }

    @Test
    public void test_getShowingNum() {
        assertEquals(testListBox.getShowingPageNum(), testListBox.showingPageNum);
    }

    @Test
    public void test_getItems() {
        assertEquals(testListBox.getItems(), testListBox.items);
    }

    /*
    Test the touch event handler method.
    This method will only be called when the touch event occurs within the bounds of the listbox
     */

    @Test
    public void test_handleTouchEvents_valid_NoItems() {
        Float y = testListBox.getBound().getBottom() + testListBox.ITEM_HEIGHT * 1.5f;
        testListBox.handleTouchEvents(y);
        assertEquals(testListBox.selectedIndex, -1);
    }

    @Test
    public void test_handleTouchEvents_valid_HasItems() {
        testListBox.addItem("item1");
        testListBox.addItem("item2");
        testListBox.addItem("item3");
        Float y = testListBox.getBound().getBottom() + testListBox.ITEM_HEIGHT * 1.5f;
        testListBox.handleTouchEvents(y);
        assertEquals(testListBox.selectedIndex, 1);
    }

    @Test
    public void test_handleTouchEvents_HasItems_TouchBorderBetweenItems() {
        testListBox.addItem("item1");
        testListBox.addItem("item2");
        testListBox.addItem("item3");
        Float y = testListBox.getBound().getBottom() + testListBox.ITEM_HEIGHT * 2.0f;
        testListBox.handleTouchEvents(y);
        assertEquals(testListBox.selectedIndex, 2);
    }

    @Test
    public void test_handleTouchEvents_HasItems_TouchBorderTop() {
        testListBox.addItem("item1");
        testListBox.addItem("item2");
        testListBox.addItem("item3");
        Float y = testListBox.getBound().getBottom();
        testListBox.handleTouchEvents(y);
        assertEquals(testListBox.selectedIndex, 0);
    }

    @Test
    public void test_handleTouchEvents_HasItems_TouchBorderBottom() {
        testListBox.addItem("item1");
        testListBox.addItem("item2");
        testListBox.addItem("item3");
        testListBox.addItem("item4");
        Float y = testListBox.getBound().getTop();
        testListBox.handleTouchEvents(y);
        assertEquals(testListBox.selectedIndex, -1);
    }

    /*
    The showingPageNum can never be less than 0 since the trigger method only runs the decrement line when the current page number is > 0
    Therefore no need to test this case
     */
    @Test
    public void test_handlePrevButtonTrigger_Equals_Zero() {
        testListBox.showingPageNum = 0;
        testListBox.handlePrevButtonTrigger();
        assertEquals(testListBox.showingPageNum, 0);
    }

    @Test
    public void test_handlePrevButtonTrigger_More_Than_Zero() {
        testListBox.showingPageNum = 2;
        testListBox.handlePrevButtonTrigger();
        assertEquals(testListBox.showingPageNum, 1);
    }

    @Test
    public void test_handleNextButtonTrigger_Next_Page_Exists() {
        //Add enough items for the listbox to run over 1 page long
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");

        testListBox.showingPageNum = 0;
        testListBox.handleNextButtonTrigger();
        assertEquals(testListBox.showingPageNum, 1);
    }

    @Test
    public void test_handleNextButtonTrigger_No_Next_Page() {
        //Empty listbox, so next page does not exist
        testListBox.showingPageNum = 0;
        testListBox.handleNextButtonTrigger();
        assertEquals(testListBox.showingPageNum, 0);
    }

    @Test
    public void test_getNumberOfItemsPerPage() {
        assertEquals(testListBox.getNumberOfItemsPerPage(), 4);
    }

    @Test
    public void test_isNextButtonEnabled_yes() {
        //Add enough items for the listbox to run over 1 page long
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        assertTrue(testListBox.isNextButtonEnabled());
    }

    @Test
    public void test_isNextButtonEnabled_no() {
        //Empty Listbox
        testListBox.showingPageNum = 0;
        assertFalse(testListBox.isNextButtonEnabled());
    }

    @Test
    public void test_RemoveItem_Selected_Index() {
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.selectedIndex = 3;
        testListBox.removeItem(3);
        assertEquals(testListBox.selectedIndex, -1);
        assertEquals(testListBox.getItems().size(), 3);
    }

    @Test
    public void test_RemoveItem_Not_Selected_Index() {
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.addItem("test");
        testListBox.selectedIndex = 1;
        testListBox.removeItem(3);
        assertEquals(testListBox.selectedIndex, 1);
        assertEquals(testListBox.getItems().size(), 3);
    }
}

