package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.screens.HelpScreen;
import uk.ac.qub.eeecs.game.ui.ImageScrollerItem;

import static junit.framework.Assert.assertEquals;

/**
 * Created by eimhin on 09/04/2018.
 *
 * ImageScrollerItem Tests
 */

@RunWith(AndroidJUnit4.class)
public class ImageScrollerItemTest {
    private Context appContext;
    private FootballGame game;
    // Testing of scroller requires a GameScreen, so I have randomly chosen HelpScreen
    private HelpScreen helpScreen;
    private ImageScrollerItem imageScrollerItem;
    private Random rand;
    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        rand = new Random();

        helpScreen = new HelpScreen(game);

        game.getAssetManager().loadAndAddBitmap("TestBitmap1", "img/card-0.png");
        game.getAssetManager().loadAndAddBitmap("TestBitmap2", "img/card-1.png");

        imageScrollerItem = new ImageScrollerItem(rand.nextFloat(),rand.nextFloat(),rand.nextFloat() + 1,rand.nextFloat() + 1, game.getAssetManager().getBitmap("TestBitmap1"), helpScreen);
    }

    @Test
    public void test_Constructor1() {
        imageScrollerItem = new ImageScrollerItem(helpScreen);

        assertEquals(imageScrollerItem.position.x, 0.0f);
        assertEquals(imageScrollerItem.position.y, 0.0f);
        assertEquals(imageScrollerItem.getBound().getWidth(), 2.0f);
        assertEquals(imageScrollerItem.getBound().getHeight(), 2.0f);
        assertEquals(imageScrollerItem.getBitmap(), game.getAssetManager().getBitmap("Empty"));
    }

    @Test
    public void test_Constructor2_ValidData() {
        float x = rand.nextFloat(), y = rand.nextFloat(), width = rand.nextFloat() + 1, height = rand.nextFloat() + 1;
        imageScrollerItem = new ImageScrollerItem(x, y, width, height, game.getAssetManager().getBitmap("TestBitmap1"), helpScreen);

        assertEquals(imageScrollerItem.position.x, x);
        assertEquals(imageScrollerItem.position.y, y);
        assertEquals(imageScrollerItem.getBound().getWidth(), width);
        assertEquals(imageScrollerItem.getBound().getHeight(), height);
        assertEquals(imageScrollerItem.getBitmap(), game.getAssetManager().getBitmap("TestBitmap1"));
    }

    @Test
    public void test_Constructor2_InvalidData() {
        float x = rand.nextFloat(), y = rand.nextFloat(), width = -(rand.nextFloat() + 1), height = -(rand.nextFloat() + 1);
        imageScrollerItem = new ImageScrollerItem(x, y, width, height, game.getAssetManager().getBitmap("TestBitmap12345"), helpScreen);

        assertEquals(x, imageScrollerItem.position.x);
        assertEquals(y, imageScrollerItem.position.y);
        assertEquals(-width, imageScrollerItem.getBound().getWidth());
        assertEquals(-height, imageScrollerItem.getBound().getHeight());
        assertEquals(game.getAssetManager().getBitmap("Empty"), imageScrollerItem.getBitmap());
    }

    @Test
    public void test_setWidthAndHeight() {
        float width = rand.nextFloat(), height = rand.nextFloat();
        imageScrollerItem.setWidthAndHeight(width,height);
        assertEquals(width, imageScrollerItem.getBound().getWidth());
        assertEquals(height, imageScrollerItem.getBound().getHeight());
    }

    @Test
    public void test_setBitmap_ValidBitmap() {
        imageScrollerItem.setBitmap(game.getAssetManager().getBitmap("TestBitmap2"));
        assertEquals(game.getAssetManager().getBitmap("TestBitmap2"), imageScrollerItem.getBitmap());
    }

    @Test
    public void test_setBitmap_InvalidBitmap() {
        imageScrollerItem.setBitmap(game.getAssetManager().getBitmap("TestBitmap12345"));
        // Retains original bitmap set in constructor
        assertEquals(game.getAssetManager().getBitmap("TestBitmap1"), imageScrollerItem.getBitmap());
    }
}
