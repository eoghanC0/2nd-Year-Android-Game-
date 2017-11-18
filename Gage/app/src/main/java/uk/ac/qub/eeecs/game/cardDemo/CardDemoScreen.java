package uk.ac.qub.eeecs.game.cardDemo;

import android.graphics.Color;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.world.GameScreen;

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
        mCard = new Card(500, 500, this);

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

        // Ensure the card cannot leave the confines of the screen
        BoundingBox playerBound = mCard.getBound();
        if (playerBound.getLeft() < 0)
            mCard.position.x -= playerBound.getLeft();
        else if (playerBound.getRight() >  mGame.getScreenWidth())
            mCard.position.x -= (playerBound.getRight() -  mGame.getScreenWidth());

        if (playerBound.getBottom() < 0)
            mCard.position.y -= playerBound.getBottom();
        else if (playerBound.getTop() >  mGame.getScreenHeight())
            mCard.position.y -= (playerBound.getTop() -  mGame.getScreenHeight());
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
    }
}
