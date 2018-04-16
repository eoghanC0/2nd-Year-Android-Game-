package uk.ac.qub.eeecs.game.ui;

import android.graphics.Color;
import android.graphics.Paint;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.objects.Card;

/**
 * Created by Steven McVeigh
 */

public class CardHolder extends GameObject {
    //////////////////////////////////////////////
    //  Properties
    //////////////////////////////////////////////
    /**
     * The card that is held within the holder
     */
    private Card card;

    //////////////////////////////////////////////
    //  Constructors
    //////////////////////////////////////////////
    public CardHolder(GameScreen gameScreen) {
        super(gameScreen);
    }

    public CardHolder(int height, float x, float y, GameScreen gameScreen){
        super(gameScreen);
        setPosition(x, y);
        setHeight(height);
    }

    //////////////////////////////////////////////
    //  Setters
    //////////////////////////////////////////////
    /**
     * Places the card in the placeholder. The card's position and height are set to that of the placeholder
     * @param card
     */
    public void setCard(Card card) {
        this.card = card;
        if(card != null) {
            card.position = position;
            card.setHeight((int) mBound.getHeight());
        }
    }

    //////////////////////////////////////////////
    //  Getters
    //////////////////////////////////////////////
    public Card getCard() {
        return card;
    }

    //////////////////////////////////////////////
    //  Methods
    //////////////////////////////////////////////

    /**
     * Sets the height of the placeholder. The width is always in line with a ratio of 225/355 with the height
     * @param height
     */
    public void setHeight(int height) {
        mBound.halfHeight = height / 2f;
        mBound.halfWidth = height * 225/355/2f;
    }


    /**
     * Updates the card
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        if (card != null)
            card.update(elapsedTime);
    }

    /**
     *  Draw the Placeholder outline if the Holder does not have a card
     *  Otherwise, draw the card
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        if (card == null) {
            Paint paint = mGameScreen.getGame().getPaint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.rgb(250, 250,250));
            paint.setStrokeWidth(10);
            graphics2D.drawRect(mBound.getLeft() + 3, mBound.getBottom() + 3, mBound.getRight() + 3, mBound.getTop() + 3, paint);
            paint.setColor(Color.rgb(4, 46,84));
            graphics2D.drawRect(mBound.getLeft(), mBound.getBottom(), mBound.getRight(), mBound.getTop(), paint);
        } else {
            card.draw(elapsedTime, graphics2D);
        }
    }

}
