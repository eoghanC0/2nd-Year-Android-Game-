package uk.ac.qub.eeecs.game.cardDemo;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameScreen;

import uk.ac.qub.eeecs.gage.world.Sprite;

import static uk.ac.qub.eeecs.gage.engine.input.TouchEvent.TOUCH_DOWN;

/**
 * Created by Inaki on 04/11/2017.
 * Refactored by Stephen on 13/11/2017.
 */


public class Card extends Sprite {

    // ////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////
    private Bitmap frontBmp, backBmp;

    /**
     * Dimensions of the screen
     */
    private Vector2 screenDimensions = new Vector2();

    /**
     * Centre of this game object
     */
    private Vector2 cardCentre = new Vector2();

    /**
     * Variables for touch handling
     */
    private boolean touchDown;

    /**
     * Properties for the card flip animation
     */
    private float flatCardBoundHalfWidth;
    private boolean isFlipping;
    private int flipFrameCounter = 1;
    private final int animationLength = 20;

    private final int defaultPlayerNameSize = 45;
    private final int defaultAttributeSize = 35;

    // /////////////////////////////////////////////////////
    // Constructor
    // /////////////////////////////////////////////////////
    public Card(float startX, float startY, GameScreen gameScreen) {
        super(startX, startY, 300f, 422f, null, gameScreen);

        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        if (assetManager.getBitmap("CardFront") == null) {assetManager.loadAndAddBitmap("CardFront", "img/CardFront.png");}
        if (assetManager.getBitmap("CardBack") == null) {assetManager.loadAndAddBitmap("CardBack", "img/CardBack.png");}
        frontBmp = assetManager.getBitmap("CardFront");
        backBmp = assetManager.getBitmap("CardBack");

        //Show the front of the card by default
        mBitmap = frontBmp;

        //TODO: Remove from constructor and avoid hardcoded numbers.
        screenDimensions.x = mGameScreen.getGame().getScreenWidth();
        screenDimensions.y = mGameScreen.getGame().getScreenHeight();
        cardCentre.x = 150f;
        cardCentre.y = 211f;

        //Set the default card half width to this initial value
        flatCardBoundHalfWidth = mBound.halfWidth;
    }

    // ///////////////////////////////////////////////////////////
    // Methods
    // ///////////////////////////////////////////////////////////

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);

        // Ensure the card cannot leave the confines of the screen
        BoundingBox cardBound = getBound();
        if (cardBound.getLeft() < 0)
            this.position.x -= cardBound.getLeft();
        else if (cardBound.getRight() >  mGameScreen.getGame().getScreenWidth())
            this.position.x -= (cardBound.getRight() -   mGameScreen.getGame().getScreenWidth());

        if (cardBound.getBottom() < 0)
            this.position.y -= cardBound.getBottom();
        else if (cardBound.getTop() >   mGameScreen.getGame().getScreenHeight())
            this.position.y -= (cardBound.getTop() -   mGameScreen.getGame().getScreenHeight());

        //Get all inputs on the screen since the last update
        Input input = mGameScreen.getGame().getInput();

        boolean touchOnCard = false;

        //Consider all buffered touch events
        for (TouchEvent t : input.getTouchEvents()) {

            //Consider Touch events within the area of the card
            if ((input.getTouchX(t.pointer) > position.x - cardCentre.x)
                    && (input.getTouchX(t.pointer) < position.x + cardCentre.x)
                    && (input.getTouchY(t.pointer) > position.y - cardCentre.y)
                    && (input.getTouchY(t.pointer) < position.y + cardCentre.y))
                touchOnCard = true;

            //Consider TOUCH_DOWN events
            if (t.type == TOUCH_DOWN && touchOnCard) {
                touchDown = true;
                Log.d("Card", "Down detected");

                //Card should flip on touching
                isFlipping = true;
            }

            //Consider TOUCH_DRAGGED events after TOUCH_DOWN event
            if (t.type == TouchEvent.TOUCH_DRAGGED && touchDown) {
                if (!Float.isNaN(input.getTouchX(t.pointer))) {
                    position.x = input.getTouchX(t.pointer);
                    position.y = input.getTouchY(t.pointer);
                    Log.d("Card", "Drag detected");
                }
            }

            //touch ends then change touchdown,activecard,doneMovement else check is dragged
            if (t.type == TouchEvent.TOUCH_UP) {
                touchDown = false;
                touchOnCard = false;
                Log.d("Card", "Up detected");
            }
        }

        //Show an animation if the card is currently being flipped
        if (isFlipping) {
            if (flipFrameCounter <= animationLength / 2) {
                mBound.halfWidth -= (mBound.halfWidth / (animationLength / 2));
            } else {
                mBound.halfWidth += (mBound.halfWidth / (animationLength / 2));
            }

            if (flipFrameCounter == animationLength / 2) {
                if (mBitmap == frontBmp) {
                    mBitmap = backBmp;
                } else {
                    mBitmap = frontBmp;
                }
            }

            if (flipFrameCounter == animationLength) {
                isFlipping = false;
                flipFrameCounter = 1;
            }

            flipFrameCounter++;
        } else {
            //Make sure the card is back to its usual size
            mBound.halfWidth = flatCardBoundHalfWidth;
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime,IGraphics2D graphics2D) {
        super.draw(elapsedTime, graphics2D);

        if (mBitmap == frontBmp) {
            //Draw on the card
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);

            int currentPlayerNameSize = defaultPlayerNameSize * ((int) mBound.halfWidth / (int) flatCardBoundHalfWidth);
            paint.setTextSize(currentPlayerNameSize);

            graphics2D.drawText("PLAYER NAME", mBound.getLeft(), mBound.getBottom() + 80, paint);
            int currentAttributeSize = defaultAttributeSize * ((int) mBound.halfWidth / (int) flatCardBoundHalfWidth);
            paint.setTextSize(currentAttributeSize);

            graphics2D.drawText("PAC", mBound.getLeft(), mBound.getBottom() + 250, paint);
            graphics2D.drawText("SHO", mBound.getLeft(), mBound.getBottom() + 300, paint);
            graphics2D.drawText("PAS", mBound.getLeft(), mBound.getBottom() + 350, paint);

            graphics2D.drawText("DRI", mBound.getRight() - 75, mBound.getBottom() + 250, paint);
            graphics2D.drawText("DEF", mBound.getRight() - 75, mBound.getBottom() + 300, paint);
            graphics2D.drawText("HEA", mBound.getRight() - 75, mBound.getBottom()+ 350, paint);
        }
    }
}
