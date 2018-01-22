package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.util.Vector2;
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
     * Vector positions to draw displayed bitmaps
     */
    private Vector2[] displayedBitmapVectors = new Vector2[5];
    /**
     * Prevents user from moving scroller
     */
    private boolean lockScroller = false;

    /**
     * Determines whether an animation is occuring
     */
    private boolean animationTriggered = false;

    /**
     * Direction of scroller movement
     * false = left
     * true = right
     */
    private boolean scrollDirection = false;

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

        bitmaps = new ArrayList<Bitmap>();
        mBitmap = assetManager.getBitmap("Test");

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));

        for (int i = 0; i < 5; i++) {
            displayedBitmapVectors[i] = new Vector2(0,0);
        }

        // TODO: Position to draw bitmap
        Vector2 vector = new Vector2(0,0);

        // Update positions of each bitmap
        // Calculate best positions based on parameters passed in constructor
        for (int i = 0; i < 5; i++) {
            if(displayedBitmapIndexes[i] != -1) {
                // TODO: Update vector for next bitmap
                if(i < 2)
                    vector.add(1,1);
                else if(i == 2)
                    vector.set(-2,-2);
                else
                    vector.add(1,1);
            }
        }

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
     * Should be called after animation moving images has completed
     * @param direction Specifies direction to move in (true = right false = left)
     */
    private void updateDisplayedBitmaps(boolean direction) {
        // Move everything in specified direction
        if(!bitmaps.isEmpty() && bitmaps.size() > 1) {
            int directionInt = direction ? 1 : -1;
            for (int i = 0; i < 5; i++) {
                displayedBitmapIndexes[i] = (displayedBitmapIndexes[i] + bitmaps.size() + directionInt) % bitmaps.size();
            }
        }
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        // TODO: Check for touch to change picture

        // Set animation in motion if a touch is detected
        if(/*Touch event triggered*/true && !animationTriggered) {
            animationTriggered = true;

            // Set direction to scroll images
            if(/*touch/ left side/swipe left*/true)
                scrollDirection = false;
            else
                scrollDirection = true;
        }

        // Perform animation
        if(animationTriggered) {
            // TODO: Animation

            if(/* Animation complete */ true) {
                animationTriggered = false;
                updateDisplayedBitmaps(scrollDirection);
            }
        }
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

        // TODO: Draw bitmaps
        for (int i = 0; i < 5; i++) {
            // Draw bitmap i.e. graphics2D.drawBitmap(bitmaps[displayedBitmapIndexes[i]], ...);
        }

        super.draw(elapsedTime, graphics2D);
    }


}