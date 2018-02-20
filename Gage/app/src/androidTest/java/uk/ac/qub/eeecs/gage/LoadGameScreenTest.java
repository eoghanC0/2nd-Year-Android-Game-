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
import uk.ac.qub.eeecs.gage.engine.ScreenManager;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.LoadGameScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.MenuScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.SquadScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;
import uk.ac.qub.eeecs.game.cardDemo.ui.ListBox;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Iñaki McKearney on 15/02/2018.
 */

@RunWith(AndroidJUnit4.class)
public class LoadGameScreenTest {
    private Context appContext;
    private FootballGame game;
    private LoadGameScreen loadGameScreen;
    private ElapsedTime elapsedTime;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();
        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        loadGameScreen = new LoadGameScreen(game);
    }

    @Test
    public void testExampleSavesWereCreated() {
        // Create test data
        loadGameScreen.createTestSaves();

        // Call test
        game.loadGame(game.getSaveSlotMax());

        // Check return
        boolean created = false;
        if (game.getLastSaveDate()!= null)
            created = true;
        assertEquals(true, created);
    }

    @Test
    public void testSaveDelete1() {
        // Create test data
        loadGameScreen.createTestSaves();
        loadGameScreen.lbxGameSaves.selectedIndex = 0;

        // Call test
        loadGameScreen.deleteSave();
        game.loadGame(0);

        // Check return
        assertEquals(0, game.getWins());
    }

    @Test
    public void testSaveDelete2() {
        // Create test data
        loadGameScreen.createTestSaves();
        loadGameScreen.lbxGameSaves.selectedIndex = 1;

        // Call test
        loadGameScreen.deleteSave();
        game.loadGame(1);

        // Check return
        assertEquals(0, game.getWins());
    }

    @Test
    public void testSaveDelete3() {
        // Create test data
        loadGameScreen.createTestSaves();
        loadGameScreen.lbxGameSaves.selectedIndex = 2;

        // Call test
        loadGameScreen.deleteSave();
        game.loadGame(2);

        // Check return
        assertEquals(0, game.getWins());
    }

    @Test
    public void testEmptySaveNameText1() {
        // Create test data
        loadGameScreen.createTestSaves();
        loadGameScreen.lbxGameSaves.selectedIndex = 0;

        // Call test
        loadGameScreen.deleteSave();

        // Check return
        assertEquals("Save: 1 - Empty", loadGameScreen.lbxGameSaves.getItems().get(0));
    }

    @Test
    public void testEmptySaveNameText2() {
        // Create test data
        loadGameScreen.createTestSaves();
        loadGameScreen.lbxGameSaves.selectedIndex = 1;

        // Call test
        loadGameScreen.deleteSave();

        // Check return
        assertEquals("Save: 2 - Empty", loadGameScreen.lbxGameSaves.getItems().get(1));
    }

    @Test
    public void testEmptySaveNameText3() {
        // Create test data
        loadGameScreen.createTestSaves();
        loadGameScreen.lbxGameSaves.selectedIndex = 2;

        // Call test
        loadGameScreen.deleteSave();

        // Check return
        assertEquals("Save: 3 - Empty", loadGameScreen.lbxGameSaves.getItems().get(2));
    }

    @Test
    public void testListBoxItemsArentDuplicated(){
        // Create test data
        loadGameScreen.createTestSaves();

        //calling more than once has same effect as calling method just once
        loadGameScreen.setUpSaveData();
        loadGameScreen.setUpSaveData();

        // Check return
        assertEquals(3, loadGameScreen.lbxGameSaves.getItems().size());
    }

    @Test
    public void testCorrectMenuScreenTransition() {
        // Create test data
        game.mScreenManager = new ScreenManager();
        game.mScreenManager.addScreen(loadGameScreen);
        MenuScreen menuScreen = new MenuScreen(game);

        // Call test
        loadGameScreen.changeToScreen(menuScreen);

        // Check return
        assertEquals(game.getScreenManager().getCurrentScreen().getName(), menuScreen.getName());
    }
}