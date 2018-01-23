package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
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
     * Index of currently displayed bitmap
     */
    private int currentBitmapIndex = -1;

    /**
     * Index of next bitmap to be displayed
     */
    private int nextBitmapIndex = -1;

    /**
     * Vector position of current bitmap
     */
    private Vector2 currentBitmapVector;

    /**
     * Vector position of current bitmap
     */
    private Vector2 nextBitmapVector;

    /**
     * Distance between bitmaps. Used to calculate how far to move
     */
    private float imageDistance = 0;

    /**
     * Distance moved by bitmaps
     */
    private float distanceMoved = 0;

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
     * Push buttons to change the bitmap displayed
     */
    private PushButton pushButtonLeft;
    private PushButton pushButtonRight;

    /**
     * Paint object for drawing bitmaps
     */
    private Paint mPaint = new Paint();

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
        assetManager.loadAndAddBitmap("Empty", "img/empty.png");
        assetManager.loadAndAddBitmap("Test","img/help-image-test.png");
        bitmaps = new ArrayList<Bitmap>();
        mBitmap = assetManager.getBitmap("Empty");

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));

        currentBitmapVector = new Vector2(position.x,position.y);
        nextBitmapVector = new Vector2(position.x + imageDistance,position.y);

        imageDistance = mBound.getWidth();

        pushButtonLeft = new PushButton((mBound.getLeft() + (mBound.getWidth() * 0.25f)), position.y, mBound.getWidth() / 2, mBound.getHeight(), "Empty", gameScreen);
        pushButtonRight = new PushButton((mBound.getRight() - (mBound.getWidth() * 0.25f)), position.y, mBound.getWidth() / 2, mBound.getHeight(), "Empty", gameScreen);

        // Test images to determine scroller functions as intended
        assetManager.loadAndAddBitmap("Red", "img/help-image-test.png");
        assetManager.loadAndAddBitmap("Green", "img/green.png");
        assetManager.loadAndAddBitmap("Blue", "img/blue.png");
        addBitmap(assetManager.getBitmap("Red"));
        addBitmap(assetManager.getBitmap("Green"));
        addBitmap(assetManager.getBitmap("Blue"));
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
            if(bitmaps.size() == 0) currentBitmapIndex = 0;
            bitmaps.add(bitmap);
        }
    }

    /**
     * Gets the next bitmap in the bitmaps ArrayList based on the direction
     * @param direction
     */
    private void getNextBitmap(boolean direction) {
        if(bitmaps.size() > 1) {
            int directionInt = direction ? 1 : -1;
            nextBitmapIndex = (currentBitmapIndex + bitmaps.size() + directionInt) % bitmaps.size();
        }
    }

    /**
     * Gets the scaled dimensions of a bitmap based on a maximum height
     * If the height of the bitmap is less than maxHeight, original dimensions are returned
     * @param maxHeight
     * @return Vector2 containing the half width and half height of the bitmap
     */
    private Vector2 getNewBitmapDimensions(Bitmap bitmap, int maxHeight, boolean occupyFullHeight) {
        if(maxHeight == 0 || (bitmap.getHeight() < maxHeight && !occupyFullHeight)) return new Vector2(bitmap.getWidth() / 2, bitmap.getHeight() / 2);

        float scaleFactor = (float) maxHeight / bitmap.getHeight();
        int newWidth = (int) (bitmap.getWidth() * scaleFactor);
        int newHeight = (int) (bitmap.getHeight() * scaleFactor);

        return new Vector2(newWidth / 2, newHeight / 2);
    }

    /**
     * Sets the background bitmap of the scroller
     * @param bitmap
     */
    private void setBackground(Bitmap bitmap) {
        if(!(mBitmap == null)) return;
        mBitmap = bitmap;
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
        pushButtonLeft.update(elapsedTime);
        pushButtonRight.update(elapsedTime);
        boolean leftPushed = pushButtonLeft.isPushTriggered();
        boolean rightPushed = pushButtonRight.isPushTriggered();

        if(bitmaps == null) return;

        // Check for input to determine if animation should be triggered to move the bitmaps
        if((leftPushed || rightPushed) && !animationTriggered) {
            animationTriggered = true;

            // Set direction to scroll images and vector of next bitmap
            if(leftPushed) {
                scrollDirection = true;
                nextBitmapVector = new Vector2(currentBitmapVector.x - imageDistance, currentBitmapVector.y);
            }
            else {
                scrollDirection = false;
                nextBitmapVector = new Vector2(currentBitmapVector.x + imageDistance, currentBitmapVector.y);
            }

            getNextBitmap(scrollDirection);

            // Cancel animation if there is no other bitmap in bitmaps
            if(nextBitmapIndex == -1) animationTriggered = false;

            // Reset distance moved to base value of 0
            distanceMoved = 0;
        }

        // Perform animation if an animation has been triggered
        if(animationTriggered) {
            // TODO: Animation
            boolean isAnimationComplete = false;

            // Move current bitmap and next bitmap
            float moveBy = 0;
            if(!scrollDirection) moveBy = -1 * imageDistance * 0.05f;
            else moveBy = imageDistance * 0.05f;

            currentBitmapVector.add(moveBy, 0);
            nextBitmapVector.add(moveBy, 0);

            distanceMoved += Math.abs(moveBy);

            if(distanceMoved >= imageDistance) isAnimationComplete = true;

            if(isAnimationComplete) {
                animationTriggered = false;
                currentBitmapIndex = nextBitmapIndex;
                currentBitmapVector = new Vector2(position.x, position.y);
                distanceMoved = 0;
            }
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        // TODO: Get the damn thing to actually draw something
        // TODO: Draw bitmaps
        super.draw(elapsedTime, graphics2D);

        if(currentBitmapIndex == -1) return;
        Vector2 currentBitmapDimensions = getNewBitmapDimensions(bitmaps.get(currentBitmapIndex), (int) mBound.getHeight(), true);

        Rect currentBitmapRect = new Rect();
        currentBitmapRect.set((int) (currentBitmapVector.x - currentBitmapDimensions.x),
                (int) (position.y - currentBitmapDimensions.y),
                (int) (currentBitmapVector.x + currentBitmapDimensions.x),
                (int) (position.y + currentBitmapDimensions.y));
        graphics2D.drawBitmap(bitmaps.get(currentBitmapIndex), null, currentBitmapRect, null);

        if(nextBitmapIndex == -1) return;
        Vector2 nextBitmapDimensions = getNewBitmapDimensions(bitmaps.get(nextBitmapIndex), (int) mBound.getHeight(), true);

        Rect nextBitmapRect = new Rect();
        nextBitmapRect.set((int) (nextBitmapVector.x - nextBitmapDimensions.x),
                (int) (position.y - nextBitmapDimensions.y),
                (int) (nextBitmapVector.x + nextBitmapDimensions.x),
                (int) (position.y + nextBitmapDimensions.y));
        graphics2D.drawBitmap(bitmaps.get(nextBitmapIndex), null, nextBitmapRect, null);

    }


}