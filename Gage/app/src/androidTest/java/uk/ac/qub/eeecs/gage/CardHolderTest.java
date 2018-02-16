package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.CardHolder;

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
        assert (testHolder.card != null);
    }

    @Test
    public void test_setCard_isExpectedCard() {
        testHolder.card = null;
        Card testCard = new Card(500,500,300, gameScreen, "1", 100);
        testHolder.setCard(testCard);
        assert (testHolder.card.getPlayerID().equals("1"));
    }

    //////////////////////////////////////////////
    //  Getters
    //////////////////////////////////////////////

    @Test
    public void test_getCard_notNull() {
        Card testCard = new Card(500,500,300, gameScreen, "1", 100);
        testHolder.card = testCard;
        assert (testHolder.getCard() != null);
    }

    @Test
    public void test_getCard_isExpectedCard() {
        Card testCard = new Card(500,500,300, gameScreen, "1", 100);
        testHolder.card = testCard;
        assert (testHolder.getCard().getPlayerID().equals("1"));
    }

    //////////////////////////////////////////////
    //  Methods
    //////////////////////////////////////////////

    @Test
    public void test_setHeight_halfHeight() {
        testHolder.setHeight(710);
        assert(testHolder.getBound().halfHeight == 355);
    }

    @Test
    public void test_setHeight_halfWidth() {
        testHolder.setHeight(710);
        assert(testHolder.getBound().halfWidth == 112.5);
    }

}
