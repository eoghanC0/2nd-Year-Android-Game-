package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Created by eimhin on 11/01/2018.
 */

public class HorizontalImageScroller extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * All the images to be displayed
     */
    private ArrayList<Bitmap> bitmaps;

    /**
     * Images currently displayed along with hidden images for one the scroller is activated
     */
    private int[] displayedBitmapIndexes = {-1,-1,-1,-1,-1};

    /**
     * Internal matrix use to support draw requests
     */
    protected Matrix drawMatrix = new Matrix();

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Main constructor
     * @param x
     * @param y
     * @param width
     * @param height
     * @param gameScreen
     */
    public HorizontalImageScroller(float x, float y, float width, float height, GameScreen gameScreen) {
        super(x, y, width > 0 ? width : -width, height > 0 ? height : -height, null, gameScreen);
        if(width < 0) width = 100;
        if(height < 0) height = 100;
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("Test","img/help-image-test.png");

        mBitmap = assetManager.getBitmap("Test");

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));


    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Adds bitmap to bitmaps ArrayList
     * @param bitmap
     */
    public void addBitmap(Bitmap bitmap) {
        if(bitmap != null) {
            bitmaps.add(bitmap);

            for (int i = 0; i < 5; i++) {
                if(displayedBitmapIndexes[i] == -1) displayedBitmapIndexes[i] = bitmaps.size() - 1;
                break;
            }
        }
    }

    /**
     * Updates the currently displayed bitmaps and hidden bitmaps
     * TODO: Animation
     * @param direction Specifies whether direction is positive or negative (right or left)
     */
    private void updateDisplayedBitmaps(boolean direction) {
        // Move everything in direction
        if(!bitmaps.isEmpty() && bitmaps.size() > 1) {
            int directionInt = direction ? 1 : -1;
            for (int i = 0; i < 5; i++) {
                displayedBitmapIndexes[i] = (displayedBitmapIndexes[i] + bitmaps.size() + directionInt) % bitmaps.size();
            }
        }
        // Update displayedBitmaps


    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        // TODO: Check for touch to change picture
        // TODO: Animation
        super.update(elapsedTime);

    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        // TODO: Get the damn thing to actually draw something
        /*if (GraphicsHelper.getClippedSourceAndScreenRect(this, layerViewport, screenViewport, drawSourceRect, drawScreenRect)) {

            float scaleX = (float) drawScreenRect.width() / (float) drawSourceRect.width();
            float scaleY = (float) drawScreenRect.height() / (float) drawSourceRect.height();

            // Build an appropriate transformation matrix
            drawMatrix.reset();
            drawMatrix.postScale(scaleX, scaleY);
            drawMatrix.postRotate(0, scaleX * mBitmap.getWidth()
                    / 2.0f, scaleY * mBitmap.getHeight() / 2.0f);
            //drawMatrix.postTranslate(100, drawScreenRect.top);
            drawMatrix.setScale(1f, 1f);
            // Draw the image
            graphics2D.drawBitmap(mBitmap, drawMatrix, null);
        }*/

        super.draw(elapsedTime, graphics2D);
    }


}