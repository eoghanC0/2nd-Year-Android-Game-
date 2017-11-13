package uk.ac.qub.eeecs.game.cardDemo;


import android.graphics.Bitmap;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
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
    Bitmap frontBmp, backBmp;

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

    }

    // ///////////////////////////////////////////////////////////
    // Methods
    // ///////////////////////////////////////////////////////////
    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);

        //Get all inputs on the screen since the last update
        Input input = mGameScreen.getGame().getInput();

        //Get the coordinates of the bounds of the card
        BoundingBox bound = getBound();

        //Consider all buffered touch events
        for (int i = 0; i < input.getTouchEvents().size(); i++) {
            //Consider Touch events within the bounds of the card
            if (input.getTouchEvents().get(i).x <= bound.getRight() && input.getTouchEvents().get(i).x >= bound.getLeft() &&
                    input.getTouchEvents().get(i).y <= bound.getBottom() && input.getTouchEvents().get(i).y >= bound.getTop()) {
                //Consider TOUCH_DOWN events
                if (input.getTouchEvents().get(i).type == TOUCH_DOWN) {
                    // //////////////////////////////////////////////
                    // Perform Matrix Transformation here to shrink the bitmap width to 0
                    // //////////////////////////////////////////////
                    if (mBitmap == frontBmp) {
                        mBitmap = backBmp;
                    } else {
                        mBitmap = frontBmp;
                    }
                    // //////////////////////////////////////////////
                    // Perform Matrix Transformation here to grow the bitmap width back to the original
                    // //////////////////////////////////////////////
                }
            }
        }
    }

}
