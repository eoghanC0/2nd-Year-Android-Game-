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
        testListBox = new ListBox(100,100,100,456, mockScreen);
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
    public void test_Constructor() {
        assert(testListBox.getBound().getHeight() % 100 == 0);
        assert(testListBox.items != null);
        assert(testListBox.selectedIndex == -1);
        assert(testListBox.backColor == Color.WHITE);
        assert(testListBox.borderColor == Color.BLACK);
        assert(testListBox.selectionColor == Color.BLUE);
        assert(testListBox.textColor == Color.BLACK);
        assert(testListBox.btnNextPage != null);
        assert(testListBox.btnPreviousPage != null);
    }

    //////////////////////////////////////////////////////
    //  Getters
    //////////////////////////////////////////////////////
    @Test
    public void test_getSelectedIndex() {
        testListBox.selectedIndex = rand.nextInt(5);
        assert(testListBox.getSelectedIndex() == testListBox.selectedIndex);
    }

    @Test
    public void test_getSelectedItem() {
        testListBox.selectedIndex = rand.nextInt(5);
        assert(testListBox.getSelectedItem() == testListBox.items.get(testListBox.selectedIndex));
    }

    @Test
    public void test_getShowingPageNum() {
        testListBox.showingPageNum = rand.nextInt(1);
        assert(testListBox.getShowingPageNum() == testListBox.showingPageNum);
    }

    @Test
    public void test_getItems() {
        assert(testListBox.getItems() == testListBox.items);
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
        assert(testListBox.items == testArray);
    }

    @Test
    public void test_setBackColor() {
        int red = rand.nextInt(255);
        int green = rand.nextInt(255);
        int blue = rand.nextInt(255);
        testListBox.setBackColor(Color.rgb(red,green,blue));
        assert(testListBox.backColor == Color.rgb(red,green,blue));
    }

    @Test
    public void test_setBorderColor() {
        int red = rand.nextInt(255);
        int green = rand.nextInt(255);
        int blue = rand.nextInt(255);
        testListBox.setBorderColor(Color.rgb(red,green,blue));
        assert(testListBox.borderColor == Color.rgb(red,green,blue));
    }

    @Test
    public void test_setSelectionColor() {
        int red = rand.nextInt(255);
        int green = rand.nextInt(255);
        int blue = rand.nextInt(255);
        testListBox.setSelectionColor(Color.rgb(red,green,blue));
        assert(testListBox.selectionColor == Color.rgb(red,green,blue));
    }

    @Test
    public void test_setTextColor() {
        int red = rand.nextInt(255);
        int green = rand.nextInt(255);
        int blue = rand.nextInt(255);
        testListBox.setTextColor(Color.rgb(red,green,blue));
        assert(testListBox.textColor == Color.rgb(red,green,blue));
    }

    //////////////////////////////////////////////////////
    //  Methods
    //////////////////////////////////////////////////////
    @Test
    public void test_setPosition() {
        float x = rand.nextFloat() * game.getScreenWidth();
        float y = rand.nextFloat() * game.getScreenHeight();
        testListBox.setPosition(x,y);
        assert(testListBox.position.x == x);
        assert(testListBox.position.y == y);
        assert(testListBox.getBound().x == x);
        assert(testListBox.getBound().y == y);
    }

    @Test
    public void test_setButtonPositions() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        testListBox.setButtonPositions();
        assert(testListBox.btnPreviousPage.position.x == 590);
        assert(testListBox.btnPreviousPage.position.y == 250);
        assert(testListBox.btnNextPage.position.x == 590);
        assert(testListBox.btnNextPage.position.y == 750);
    }

    @Test
    public void test_touchOccurredInSideBar_inside() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 590f;
        float y = 500f;
        assert(testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_outside() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 500f;
        float y = 500f;
        assert(!testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_boundary_left() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 580f;
        float y = 500f;
        assert(!testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_boundary_top() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 590f;
        float y = 200f;
        assert(!testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_boundary_right() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 600f;
        float y = 500f;
        assert(!testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_touchOccurredInSideBar_boundary_bottom() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 300;
        float x = 590f;
        float y = 800f;
        assert(!testListBox.touchOccurredInSideBar(x,y));
    }

    @Test
    public void test_handleTouchEvents() {
        testListBox.position.x = 500;
        testListBox.position.y = 500;
        testListBox.getBound().halfWidth = 100;
        testListBox.getBound().halfHeight = 100;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.items.add("Test4");
        testListBox.items.add("Test5");

        //Test general case of a touch on different levels of the first page
        testListBox.showingPageNum = 0;
        float x = 500f;
        float y = 550f;
        testListBox.handleTouchEvents(x,y);
        assert(testListBox.selectedIndex == 0);
        y = 650f;
        testListBox.handleTouchEvents(x,y);
        assert(testListBox.selectedIndex == 1);

        //Test a touch on a higher page
        testListBox.showingPageNum = 2;
        y = 550f;
        testListBox.handleTouchEvents(x,y);
        assert(testListBox.selectedIndex == 4);

        //Test a touch out of the bounds of the items array
        y = 650f;
        testListBox.handleTouchEvents(x,y);
        assert(testListBox.selectedIndex == -1);
    }

    @Test
    public void test_handlePrevButtonTrigger() {
        testListBox.showingPageNum = 2;
        testListBox.handlePrevButtonTrigger();
        assert(testListBox.showingPageNum == 1);
        testListBox.showingPageNum = 0;
        testListBox.handlePrevButtonTrigger();
        assert(testListBox.showingPageNum == 0);
    }

    @Test
    public void test_getNumberOfItemsPerPage() {
        testListBox.getBound().halfHeight = 225f;
        assert(testListBox.getNumberOfItemsPerPage() == 5);
    }

    @Test
    public void test_isNextButtonEnabled() {
        //Test case of no more items on next page
        testListBox.getBound().halfHeight = 300f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.showingPageNum = 0;
        assert(!testListBox.isNextButtonEnabled());

        //Test case of more items on next page
        testListBox.getBound().halfHeight = 100f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.showingPageNum = 0;
        assert(testListBox.isNextButtonEnabled());
    }

    @Test
    public void test_handleNextButtonTrigger() {
        //Test case of no more items on next page
        testListBox.getBound().halfHeight = 300f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.showingPageNum = 0;
        testListBox.handleNextButtonTrigger();
        assert(testListBox.showingPageNum == 0);

        //Test case of more items on next page
        testListBox.getBound().halfHeight = 100f;
        testListBox.items.clear();
        testListBox.items.add("Test1");
        testListBox.items.add("Test2");
        testListBox.items.add("Test3");
        testListBox.showingPageNum = 0;
        testListBox.handleNextButtonTrigger();
        assert(testListBox.showingPageNum == 1);
    }

    @Test
    public void test_addItem() {
        testListBox.clear();
        testListBox.addItem("Test");
        assert(testListBox.items.size() == 1);
    }

    @Test
    public void test_clear() {
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.clear();
        assert(testListBox.items.size() == 1);
    }

    @Test
    public void test_RemoveItem_Selected_Index() {
        testListBox.clear();
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.selectedIndex = 3;
        testListBox.removeItem(3);
        assert(testListBox.selectedIndex == -1);
        assert(testListBox.getItems().size() == 3);
    }

    @Test
    public void test_RemoveItem_Not_Selected_Index() {
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.items.add("Test");
        testListBox.selectedIndex = 1;
        testListBox.removeItem(3);
        assert(testListBox.selectedIndex == 1);
        assert(testListBox.getItems().size() == 3);
    }
}

