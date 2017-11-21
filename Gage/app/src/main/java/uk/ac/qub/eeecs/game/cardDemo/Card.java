package uk.ac.qub.eeecs.game.cardDemo;


import android.graphics.Bitmap;
import android.util.Log;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.gage.world.Sprite;

import static uk.ac.qub.eeecs.gage.engine.input.TouchEvent.TOUCH_DOWN;
import static uk.ac.qub.eeecs.gage.engine.input.TouchEvent.TOUCH_DRAGGED;

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

    private boolean flipFlag;

    private int flipFrameCounter = 1;

    private final int animationLength = 30;

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
    }

    // ///////////////////////////////////////////////////////////
    // Methods
    // ///////////////////////////////////////////////////////////

    // swaps image bitmap
//    private void flipCard(){
//        // //////////////////////////////////////////////
//        //TODO: Perform Matrix Transformation here to shrink the bitmap width to 0
//        // //////////////////////////////////////////////
//        flipFlag = true;
//        if (mBitmap == frontBmp) {
//            mBitmap = backBmp;
//        } else {
//            mBitmap = frontBmp;
//        }
//        // //////////////////////////////////////////////
//        //TODO: Perform Matrix Transformation here to grow the bitmap width back to the original
//        // //////////////////////////////////////////////
//    }
    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);

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
                flipFlag = true;
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

        if (flipFlag) {

            if (flipFrameCounter <= animationLength / 2) {
                mBound.halfWidth -= (mBound.halfWidth / (animationLength /2));
            } else {
                mBound.halfWidth += (mBound.halfWidth / (animationLength /2));
            }

            if (flipFrameCounter == animationLength / 2) {
                if (mBitmap == frontBmp) {
                    mBitmap = backBmp;
                } else {
                    mBitmap = frontBmp;
                }
            }

            if (flipFrameCounter == animationLength) {
                flipFlag = false;
                flipFrameCounter = 1;
            }

            flipFrameCounter++;
        }
    }

//    @Override
//    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
//        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
//
//    }
}
