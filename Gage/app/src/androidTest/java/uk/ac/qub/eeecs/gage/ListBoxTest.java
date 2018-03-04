package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.ListBox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by stephenmcveigh on 24/01/2018.
 */
@RunWith(AndroidJUnit4.class)
public class ListBoxTest {
    Context appContext;
    FootballGameScreen mockScreen;
    ListBox testListBox;
    FootballGame game;
    Random rand;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        rand = new Random();

        mockScreen = new HelpScreen(game);

        testListBox = new ListBox(500, 500, 500, 567, mockScreen);
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.items.add("Test4");
        testListBox.items.add("Test5");
        testListBox.items.add("Test6");
    }

    //////////////////////////////////////////////////////
    //  Constructors
    //////////////////////////////////////////////////////

    @Test
    public void test_Constructor_height() {
        assertEquals(0, testListBox.getBound().getHeight() % 100, 0);
    }
    @Test
    public void test_Constructor_items() {
        assertNotNull(testListBox.items);
    }
    @Test
    public void test_Constructor_selectedIndex() {
        assertEquals(-1, testListBox.selectedIndex);
    }
    @Test
    public void test_Constructor_backColor() {
        assertEquals(Color.WHITE, testListBox.backColor);
    }
    @Test
    public void test_Constructor_borderColor() {
        assertEquals(Color.BLACK,testListBox.borderColor);
    }
    @Test
    public void test_Constructor_selectionColor() {
        assertEquals(Color.BLUE, testListBox.selectionColor);
    }
    @Test
    public void test_Constructor_textColor() {
        assertEquals(Color.BLACK, testListBox.textColor);
    }
    @Test
    public void test_Constructor_btnNextPage() {
        assertNotNull(testListBox.btnNextPage);
    }
    @Test
    public void test_Constructor_btnPreviousPage() {
        assertNotNull(testListBox.btnPreviousPage);
    }

    //////////////////////////////////////////////////////
    //  Getters
    //////////////////////////////////////////////////////
    @Test
    public void test_getSelectedIndex() {
        testListBox.selectedIndex = rand.nextInt(5);
        assertEquals(testListBox.selectedIndex, testListBox.getSelectedIndex());
    }

    @Test
    public void test_getSelectedItem() {
        testListBox.selectedIndex = rand.nextInt(5);
        assertEquals(testListBox.items.get(testListBox.selectedIndex), testListBox.getSelectedItem());
    }

    @Test
    public void test_getShowingPageNum() {
        testListBox.showingPageNum = rand.nextInt(1);
        assertEquals(testListBox.showingPageNum, testListBox.getShowingPageNum());
    }

    @Test
    public void test_getItems() {
        assertEquals(testListBox.items, testListBox.getItems());
    }

    //////////////////////////////////////////////////////
    //  Setters
    //////////////////////////////////////////////////////
    @Test
    public void test_setArray() {
        ArrayList<String> testArray = new ArrayList<>();
        testArray.add("TestData1");
        testArray.add("TestData2");
        testArray.add("TestData3");
        testArray.add("TestData4");
        testArray.add("TestData5");
        testArray.add("TestData6");
        testListBox.setArray(testArray);
        assertEquals(testArray, testListBox.items);
    }

    @Test
    public void test_setBackColor() {
        int red = rand.nextInt(255);
        int green = rand.nextInt(255);
        int blue = rand.nextInt(255);
        testListBox.setBackColor(Color.rgb(red,green,blue));
        assertEquals(Color.rgb(red,green,blue), testListBox.backColor);
    }

    @Test
    public void test_setBorderColor() {
        int red = rand.nextInt(255);
        int green = rand.nextInt(255);
        int blue = rand.nextInt(255);
        testListBox.setBorderColor(Color.rgb(red,green,blue));
        assertEquals(Color.rgb(red,green,blue), testListBox.borderColor);
    }

    @Test
    public void test_setSelectionColor() {
        int red = rand.nextInt(255);
        int green = rand.nextInt(255);
        int blue = rand.nextInt(255);
        testListBox.setSelectionColor(Color.rgb(red,green,blue));
        assertEquals(Color.rgb(red,green,blue), testListBox.selectionColor);
    }

    @Test
    public void test_setTextColor() {
        int red = rand.nextInt(255);
        int green = rand.nextInt(255);
        int blue = rand.nextInt(255);
        testListBox.setTextColor(Color.rgb(red,green,blue));
        assertEquals(Color.rgb(red,green,blue), testListBox.textColor);
    }

    //////////////////////////////////////////////////////
    //  Methods
    //////////////////////////////////////////////////////
    @Test
    public void test_setPosition_positionX() {
        float x = rand.nextFloat() * game.getScreenWidth();
        float y = rand.nextFloat() * game.getScreenHeight();
        testListBox.setPosition(x,y);
        assertEquals(x, testListBox.position.x, 0);
    }

    @Test
    public void test_setPosition_positionY() {
        float x = rand.nextFloat() * game.getScreenWidth();
        float y = rand.nextFloat() * game.getScreenHeight();
        testListBox.setPosition(x,y);
        assertEquals(y, testListBox.position.y, 0);
    }

    @Test
    public void test_setPosition_boundX() {
        float x = rand.nextFloat() * game.getScreenWidth();
        float y = rand.nextFloat() * game.getScreenHeight();
        testListBox.setPosition(x,y);
        assertEquals(x, testListBox.getBound().x,0);
    }

    @Test
    public void test_setPosition_boundY() {
        float x = rand.nextFloat() * game.getScreenWidth();
        float y = rand.nextFloat() * game.getScreenHeight();
        testListBox.setPosition(x,y);
        assertEquals(y, testListBox.getBound().y, 0);
    }

    @Test
    public void test_setButtonPositions_positionX() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        testListBox.setButtonPositions();
        assertEquals(590, testListBox.btnPreviousPage.position.x, 0);
    }

    @Test
    public void test_setButtonPositions_positionY() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        testListBox.setButtonPositions();
        assertEquals(250, testListBox.btnPreviousPage.position.y, 0);
    }

    @Test
    public void test_setButtonPositions_boundX() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        testListBox.setButtonPositions();
        assertEquals(590, testListBox.btnNextPage.position.x, 0);
    }

    @Test
    public void test_setButtonPositions_boundY() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        testListBox.setButtonPositions();
        assertEquals(750, testListBox.btnNextPage.position.y, 0);
    }

    @Test
    public void test_touchOccurredInSideBar_inside() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 590f;
        float y = 500f;
        assertTrue(testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_outside() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 500f;
        float y = 500f;
        assertFalse(testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_boundary_left() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 580f;
        float y = 500f;
        assertFalse(testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_boundary_top() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 590f;
        float y = 200f;
        assertFalse(testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_boundary_right() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 600f;
        float y = 500f;
        assertFalse(testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_boundary_bottom() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 590f;
        float y = 800f;
        assertFalse(testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_handleTouchEvents_firstPage_firstItem() {
        testListBox.position.x = 500f;
        testListBox.position.y = 500f;
        testListBox.getBound().halfWidth = 100f;
        testListBox.getBound().halfHeight = 100f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.items.add("Test4");
        testListBox.items.add("Test5");
        testListBox.items.add("Test6");

        testListBox.showingPageNum = 0;
        testListBox.selectedIndex = -1;
        float x = 500f;
        float y = 450f;
        testListBox.handleTouchEvents(x,y);
        assertEquals(0, testListBox.selectedIndex);
    }

    @Test
    public void test_handleTouchEvents_firstPage_secondItem() {
        testListBox.position.x = 500f;
        testListBox.position.y = 500f;
        testListBox.getBound().halfWidth = 100f;
        testListBox.getBound().halfHeight = 100f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.items.add("Test4");
        testListBox.items.add("Test5");
        testListBox.items.add("Test6");

        testListBox.showingPageNum = 0;
        testListBox.selectedIndex = -1;
        float x = 500f;
        float y = 550f;
        testListBox.handleTouchEvents(x,y);

        assertEquals(1, testListBox.selectedIndex);
    }

    @Test
    public void test_handleTouchEvents_higherPage() {
        testListBox.position.x = 500f;
        testListBox.position.y = 500f;
        testListBox.getBound().halfWidth = 100f;
        testListBox.getBound().halfHeight = 100f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.items.add("Test4");
        testListBox.items.add("Test5");
        testListBox.items.add("Test6");

        float x = 500f;
        float y = 450f;

        testListBox.showingPageNum = 2;
        testListBox.selectedIndex = -1;
        testListBox.handleTouchEvents(x,y);
        assertEquals(4, testListBox.selectedIndex);
    }

    @Test
    public void test_handleTouchEvents_NoItem() {
        testListBox.position.x = 500f;
        testListBox.position.y = 500f;
        testListBox.getBound().halfWidth = 100f;
        testListBox.getBound().halfHeight = 100f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.items.add("Test4");
        testListBox.items.add("Test5");

        testListBox.showingPageNum = 0;
        float x = 500f;
        float y = 550f;

        testListBox.showingPageNum = 2;
        testListBox.selectedIndex = -1;
        testListBox.handleTouchEvents(x,y);
        assertEquals(-1, testListBox.selectedIndex);
    }

    @Test
    public void test_handlePrevButtonTrigger_isPrevPage() {
        testListBox.showingPageNum = 2;
        testListBox.handlePrevButtonTrigger();
        assertEquals(1, testListBox.showingPageNum);
    }

    @Test
    public void test_handlePrevButtonTrigger_noPrevPage() {
        testListBox.showingPageNum = 0;
        testListBox.handlePrevButtonTrigger();
        assertEquals(0, testListBox.showingPageNum);
    }

    @Test
    public void test_getNumberOfItemsPerPage() {
        testListBox.getBound().halfHeight = 250f;
        assertEquals(5, testListBox.getNumberOfItemsPerPage());
    }

    @Test
    public void test_isNextButtonEnabled_noMoreItems() {
        testListBox.getBound().halfHeight = 300f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.showingPageNum = 0;
        assertFalse(testListBox.isNextButtonEnabled());
    }

    @Test
    public void test_isNextButtonEnabled_moreItems() {
        testListBox.getBound().halfHeight = 100f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.showingPageNum = 0;
        assertTrue(testListBox.isNextButtonEnabled());
    }

    @Test
    public void test_handleNextButtonTrigger_noMoreItems() {
        testListBox.getBound().halfHeight = 300f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.showingPageNum = 0;
        testListBox.handleNextButtonTrigger();
        assertEquals(0, testListBox.showingPageNum);
    }

    @Test
    public void test_handleNextButtonTrigger_moreItems() {
        testListBox.getBound().halfHeight = 100f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.showingPageNum = 0;
        testListBox.handleNextButtonTrigger();
        assertEquals(1, testListBox.showingPageNum);
    }

    @Test
    public void test_addItem() {
        testListBox.clear();
        testListBox.addItem("Test");
        assertEquals(1, testListBox.items.size());
    }

    @Test
    public void test_clear() {
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.clear();
        assertEquals(0, testListBox.items.size());
    }

    @Test
    public void test_RemoveItem_selectedIndex_newSelectedIndex() {
        testListBox.clear();
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.selectedIndex = 3;
        testListBox.removeItem(3);
        assertEquals(-1,testListBox.selectedIndex);
    }

    @Test
    public void test_RemoveItem_selectedIndex_newSize() {
        testListBox.clear();
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.selectedIndex = 3;
        testListBox.removeItem(3);
        assertEquals(3, testListBox.getItems().size());
    }

    @Test
    public void test_RemoveItem_notSelectedIndex_newSelectedIndex() {
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.selectedIndex = 1;
        testListBox.removeItem(3);
        assertEquals(1, testListBox.selectedIndex);
    }

    @Test
    public void test_RemoveItem_notSelectedIndex_newSize() {
        testListBox.clear();
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.selectedIndex = 1;
        testListBox.removeItem(3);
        assertEquals(3, testListBox.getItems().size());
    }
}

