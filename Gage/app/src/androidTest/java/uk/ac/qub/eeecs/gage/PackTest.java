package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.objects.FootballGameScreen;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.objects.Card;
import uk.ac.qub.eeecs.game.objects.Pack;
import uk.ac.qub.eeecs.game.screens.HelpScreen;

import static org.junit.Assert.assertEquals;

/**
 * Created by stephenmcveigh on 09/03/2018.
 */

@RunWith(AndroidJUnit4.class)
public class PackTest {
    Context appContext;
    FootballGameScreen mockScreen;
    Pack testPack;
    FootballGame game;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        mockScreen = new HelpScreen(game);

        testPack = new Pack(mockScreen, 11,5,0,100);
    }

    @Test
    public void test_createCards() {
        testPack.cardScroller.getCardScrollerItems().clear();
        testPack.createCards(5, true, 0,100);
        assertEquals(5, testPack.cardScroller.getCardScrollerItems().size());
    }

    @Test
    public void test_createPack_countRares() {
        testPack.cardScroller.getCardScrollerItems().clear();
        testPack.createPack(5, 3, 0,100);
        int rareCounter = 0;
        for(Card card : testPack.cardScroller.getCardScrollerItems())
            if(card.isRare())
                rareCounter++;
        assertEquals(3, rareCounter);
    }

    @Test
    public void test_createPack_countNonRares() {
        testPack.cardScroller.getCardScrollerItems().clear();
        testPack.createPack(5, 3, 0,100);
        int nonRareCounter = 0;
        for(Card card : testPack.cardScroller.getCardScrollerItems())
            if(!card.isRare())
                nonRareCounter++;
        assertEquals(2, nonRareCounter);
    }

    @Test
    public void test_getBestCard() {
        testPack.cardScroller.getCardScrollerItems().clear();
        testPack.cardScroller.getCardScrollerItems().add(new Card(mockScreen, true, null,91,91));
        testPack.cardScroller.getCardScrollerItems().add(new Card(mockScreen, true, null,80,80));
        testPack.cardScroller.getCardScrollerItems().add(new Card(mockScreen, true, null,75,75));
        testPack.cardScroller.getCardScrollerItems().add(new Card(mockScreen, true, null,63,63));
        testPack.cardScroller.getCardScrollerItems().add(new Card(mockScreen, true, null,59,59));
        assertEquals(91, testPack.getBestCard().getRating());
        Collections.shuffle(testPack.cardScroller.getCardScrollerItems()); //shuffle and test again to prove not a fluke
        assertEquals(91, testPack.getBestCard().getRating());
    }

    @Test
    public void test_getValueFromQuadraticCurve_bottom() {
        assertEquals(0,testPack.getValueFromQuadraticCurve(0, 10, 200));
    }

    @Test
    public void test_getValueFromQuadraticCurve_top() {
        assertEquals(0,testPack.getValueFromQuadraticCurve(10, 10, 200));
    }

    @Test
    public void test_getValueFromQuadraticCurve_middle() {
        assertEquals(200,testPack.getValueFromQuadraticCurve(5, 10, 200));
    }
}
