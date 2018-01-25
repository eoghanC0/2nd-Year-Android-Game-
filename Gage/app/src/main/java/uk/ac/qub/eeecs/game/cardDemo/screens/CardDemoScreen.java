package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Color;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;

/**
 * Starter class for Card game stories in the 2nd sprint
 *
 * @version 1.0
 */
public class CardDemoScreen extends GameScreen {
    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the player's card
     */
    private Card mCard;

    /**
     * Define the info bar
     */
    private InfoBar infoBar;

    private LayerViewport mLayerViewport;
    private ScreenViewport mScreenViewport;
    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the Card game screen
     *
     * @param game Game to which this screen belongs
     */
    public CardDemoScreen(Game game) {
        super("CardScreen", game);

        // Create the player card
        //mCard = new Card(500, 500, this);
        // Create the view ports
        mLayerViewport = new LayerViewport(240, 160, 240, 160);
        mScreenViewport = new ScreenViewport();
        GraphicsHelper.create3To2AspectRatioScreenViewport(game, mScreenViewport);
        // Create info bar
        infoBar = new InfoBar(mLayerViewport.getWidth() / 2,mLayerViewport.getHeight() - mLayerViewport.getHeight() * 0.05f, mLayerViewport.getWidth(),mLayerViewport.getHeight() * 0.1f, this, "", "Test Player", "4 | 1 | 2", "100");
    }
    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the card demo screen
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        mCard.update(elapsedTime);

        infoBar.update(elapsedTime);
    }

    /**
     * Draw the card demo screen
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.WHITE);

        // Draw the card
        mCard.draw(elapsedTime, graphics2D);

        infoBar.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);
    }
}
