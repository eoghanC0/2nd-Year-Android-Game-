package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.objects.Card;
import uk.ac.qub.eeecs.game.screens.HelpScreen;
import uk.ac.qub.eeecs.game.ui.CardHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by stephenmcveigh on 16/02/2018.
 */

@RunWith(AndroidJUnit4.class)
public class CardHolderTest {
    Context appContext;
    FootballGame game;
    FootballGameScreen gameScreen;
    CardHolder testHolder;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        gameScreen = new HelpScreen(game);

        testHolder = new CardHolder(gameScreen);
    }

    //////////////////////////////////////////////
    //  Setters
    //////////////////////////////////////////////

    @Test
    public void test_setCard_notNull() {
        testHolder.card = null;
        Card testCard = new Card(500,500,300, gameScreen, "1", 100);
        testHolder.setCard(testCard);
        assertNotNull(testHolder.card);
    }

    @Test
    public void test_setCard_isExpectedCard() {
        testHolder.card = null;
        Card testCard = new Card(500,500,300, gameScreen, "1", 100);
        testHolder.setCard(testCard);
        assertEquals("1", testHolder.card.getPlayerID());
    }

    //////////////////////////////////////////////
    //  Getters
    //////////////////////////////////////////////

    @Test
    public void test_getCard_notNull() {
        Card testCard = new Card(500,500,300, gameScreen, "1", 100);
        testHolder.card = testCard;
        assertNotNull(testHolder.getCard());
    }

    @Test
    public void test_getCard_isExpectedCard() {
        Card testCard = new Card(500,500,300, gameScreen, "1", 100);
        testHolder.card = testCard;
        assertEquals("1", testHolder.getCard().getPlayerID());
    }

    //////////////////////////////////////////////
    //  Methods
    //////////////////////////////////////////////

    @Test
    public void test_setHeight_halfHeight() {
        testHolder.setHeight(710);
        assertEquals(355, testHolder.getBound().halfHeight, 0);
    }

    @Test
    public void test_setHeight_halfWidth() {
        testHolder.setHeight(710);
        assertEquals(225, testHolder.getBound().halfWidth, 0);
    }

}
