package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Created by eimhin on 11/01/2018.
 */

/**
 * This class allows you to create a horizontally moving image scroller
 *
 * Clicking the left side of the scroller moves the image(s) right, displaying the previous image
 * Clicking the right side of the scroller moves the image(s) left, displaying the next image
 *
 * Images are automatically scaled to fit within the scroller
 *
 * User can toggle between single bitmap and multi-bitmap mode
 *
 * - Single bitmap
 *      Bitmaps displayed one at a time
 *      Bitmaps are scaled to fit the total height of the scroller
 *      Scroller cycles in a loop
 * - Multi-bitmap
 *      Displays multiple bitmaps at a time by using the full width of the scroller
 *      User chooses the maximum bitmap height, allowing for more/less bitmaps to be displayed
 *      Scroller cycles in a loop
 *
 * Default Settings:
 * - multiBitmapMode = false
 * - selectMode = false
 *
 * TODO: Create tests.
 * TODO: Noticed fan got pretty loud after having scroller open a while. Possible memory leak. Requires investigating.
 * TODO: Animations not configured properly using frames, elapsedTime etc. Fix this.
 * TODO: Only draw bitmaps within the bounds of the scroller
 * TODO: Refactor.
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
     * Determines whether a scroll animation is occuring
     */
    private boolean scrollAnimationTriggered = false;

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

    /**
     * MULTI-BITMAP VARIABLES
     */

    /**
     * Toggle to set multi bitmap mode
     * at a time
     * = = = = = = = = = = = = =
     *  * *  W A R N I N G  * *
     * = = = = = = = = = = = = =
     * This should only be used if the bitmaps all have the same dimensions
     */
    private boolean multiBitmapMode = false;

    /**
     * Maximum bitmaps that can be displayed
     */
    private int maxDisplayedBitmaps = 0;

    /**
     * Spacing between bitmaps
     */
    private int maxBitmapSpacing = 0;

    /**
     * Absolute maximum number of bitmaps that can be displayed at a time
     */
    private final int MAX_DISPLAYED_BITMAPS_ALLOWED = 15;

    /**
     * Dimensions of bitmaps
     */
    private Vector2 maxBitmapDimensions = new Vector2();

    /**
     * Indexes used to retrieve bitmaps from bitmaps ArrayList for currently displayed bitmaps
     */
    private int[] currentBitmapIndexes = new int[MAX_DISPLAYED_BITMAPS_ALLOWED];

    /**
     * Indexes used to retrieve bitmaps from bitmaps ArrayList for next displayed bitmaps
     */
    private int[] nextBitmapIndexes = new int[MAX_DISPLAYED_BITMAPS_ALLOWED];

    /**
     * Positions of currently displayed bitmaps
     */
    private Vector2[] currentBitmapVectors = new Vector2[MAX_DISPLAYED_BITMAPS_ALLOWED];

    /**
     * Positions of next displayed bitmaps
     */
    private Vector2[] nextBitmapVectors = new Vector2[MAX_DISPLAYED_BITMAPS_ALLOWED];

    /**
     * INTERACTIVITY VARIABLES
     */

    /**
     * Whether or not selecting is allowed
     */
    private boolean selectMode = true;

    /**
     * Tells whether a bitmap has been selected
     */
    private boolean bitmapSelected = false;

    /**
     * Stores the index of the bitmap currently selected
     * Index is used to get the bitmap from currentBitmap's
     */
    private int selectedBitmapIndex;

    /**
     * Determines whether a bitmap select animation is occuring
     */
    private boolean selectAnimationTriggered = false;

    /**
     * Direction in which bitmap moves for select animation
     */
    private boolean selectDirection = false;

    /**
     * Distance to move by
     */
    private float selectDistance  = 30;

    /**
     * Tracks the touch location
     */
    private Vector2 touchLocation = new Vector2();

    /**
     * Scroll click bound
     * Until swiping to scroll functions, a temporary bound has to be created which is contained
     * between the buttons else, selecting a button on top of a bitmap will scroll and select
     */
    private BoundingBox selectBound = new BoundingBox();

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

        for (int i = 0; i < MAX_DISPLAYED_BITMAPS_ALLOWED; i++) {
            currentBitmapVectors[i] = new Vector2();
            nextBitmapVectors[i] = new Vector2();
        }

        imageDistance = mBound.getWidth();

        // Buttons occupy whole scroller
        //pushButtonLeft = new PushButton((mBound.getLeft() + (mBound.getWidth() * 0.25f)), position.y, mBound.getWidth() / 2, mBound.getHeight(), "Empty", gameScreen);
        //pushButtonRight = new PushButton((mBound.getRight() - (mBound.getWidth() * 0.25f)), position.y, mBound.getWidth() / 2, mBound.getHeight(), "Empty", gameScreen);

        // Buttons occupy 10% of scroller (5% each side)
        pushButtonLeft = new PushButton((mBound.getLeft() + (mBound.getWidth() * 0.05f)), position.y, mBound.getWidth() * 0.1f, mBound.getHeight(), "Empty", gameScreen);
        pushButtonRight = new PushButton((mBound.getRight() - (mBound.getWidth() * 0.05f)), position.y, mBound.getWidth() * 0.1f, mBound.getHeight(), "Empty", gameScreen);

        selectBound = new BoundingBox();
        selectBound.x = position.x;
        selectBound.y = position.y;
        selectBound.halfWidth = mBound.getWidth() * 0.4f;
        selectBound.halfHeight = mBound.getHeight();

        // Test images to determine scroller functions as intended
        assetManager.loadAndAddBitmap("Card 0", "img/card-0.png");
        assetManager.loadAndAddBitmap("Card 1", "img/card-1.png");
        assetManager.loadAndAddBitmap("Card 2", "img/card-2.png");
        assetManager.loadAndAddBitmap("Card 3", "img/card-3.png");
        assetManager.loadAndAddBitmap("Card 4", "img/card-4.png");
        assetManager.loadAndAddBitmap("Card 5", "img/card-5.png");
        assetManager.loadAndAddBitmap("Card 6", "img/card-6.png");
        assetManager.loadAndAddBitmap("Card 7", "img/card-7.png");
        assetManager.loadAndAddBitmap("Green", "img/green.png");
        /*assetManager.loadAndAddBitmap("Blue", "img/blue.png");
        assetManager.loadAndAddBitmap("Vertical Test", "img/vertical-test.png");*/
        addBitmap(assetManager.getBitmap("Card 0"));
        addBitmap(assetManager.getBitmap("Card 1"));
        addBitmap(assetManager.getBitmap("Card 2"));
        addBitmap(assetManager.getBitmap("Card 3"));
        addBitmap(assetManager.getBitmap("Card 4"));
        addBitmap(assetManager.getBitmap("Card 5"));
        addBitmap(assetManager.getBitmap("Card 6"));
        addBitmap(assetManager.getBitmap("Card 7"));
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
     */
    private void getNextBitmap() {
        if(bitmaps.size() > 1) {
            int directionInt = scrollDirection ? -1 : 1;
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

    /**
     * Calculates the maximum number of bitmaps that can be displayed
     * @param templateBitmap Bitmap from which is used as the basis to perform all calculations
     * @param heightOccupyPercentage The percentage of the scrollers height the image should occupy
     */
    private void calculateMaxDisplayedBitmaps(Bitmap templateBitmap, float heightOccupyPercentage) {
        if(heightOccupyPercentage <= 0 || heightOccupyPercentage > 100)
            heightOccupyPercentage = 1;
        else
            heightOccupyPercentage /= 100;

        int maxHeight = (int) (mBound.getHeight() * heightOccupyPercentage);
        Vector2 scaledBitmapDimensions = getNewBitmapDimensions(templateBitmap, maxHeight, true);

        maxBitmapDimensions.x = scaledBitmapDimensions.x;
        maxBitmapDimensions.y = scaledBitmapDimensions.y;

        int maxImages = (int) (mBound.getWidth() / (scaledBitmapDimensions.x * 2));
        maxImages = maxImages > MAX_DISPLAYED_BITMAPS_ALLOWED ? MAX_DISPLAYED_BITMAPS_ALLOWED : maxImages;
        int remainder = (int) (mBound.getWidth() % (scaledBitmapDimensions.x * 2));
        int spacing = remainder <= 0 ? 0 : remainder / (maxImages + 1);

        maxDisplayedBitmaps = maxImages;
        maxBitmapSpacing = spacing;

        for (int i = 0; i < maxImages; i++) {
            if(i < bitmaps.size()) currentBitmapIndexes[i] = i;
        }

        calculateCurrentBitmapVectors();
    }

    /**
     * Calculates positions of current bitmaps
     */
    private void calculateCurrentBitmapVectors() {
        currentBitmapVectors[0] = new Vector2(mBound.getLeft() + maxBitmapSpacing + maxBitmapDimensions.x, position.y);

        int counter = maxDisplayedBitmaps > bitmaps.size() ? bitmaps.size() : maxDisplayedBitmaps;
        for(int i = 1; i < maxDisplayedBitmaps; i++) {
            currentBitmapVectors[i] = new Vector2(currentBitmapVectors[0].x + (i * (maxBitmapSpacing + (maxBitmapDimensions.x * 2))), position.y);
        }
    }

    /**
     * Toggles scroller from single bitmap to multi bitmap mode
     * @param value
     * @param heightOccupyPercentage
     */
    public void setDisplayMaxBitmaps(boolean value, int heightOccupyPercentage) {
        if(!(bitmaps.size() > 0) || !value) {
            Log.e("ERROR", "You cannot set multi-image mode unless there is at least 1 bitmap in bitmaps");
            return;
        }

        multiBitmapMode = true;
        calculateMaxDisplayedBitmaps(bitmaps.get(0), heightOccupyPercentage);
    }

    /**
     * Calculates the positions of the next bitmaps based on the direction the scroller
     * is being moved in
     */
    private void calculateNextBitmapVectors() {
        if(!multiBitmapMode) {
            if(scrollDirection) {
                nextBitmapVector = new Vector2(currentBitmapVector.x - imageDistance, currentBitmapVector.y);
            }
            else {
                nextBitmapVector = new Vector2(currentBitmapVector.x + imageDistance, currentBitmapVector.y);
            }

            getNextBitmap();
        } else {
            if(scrollDirection) {
                nextBitmapIndex = currentBitmapIndex - maxDisplayedBitmaps < 0 ? bitmaps.size() - (bitmaps.size() % maxDisplayedBitmaps) : currentBitmapIndex - maxDisplayedBitmaps;
                nextBitmapIndex = nextBitmapIndex == bitmaps.size() ? bitmaps.size() - maxDisplayedBitmaps : nextBitmapIndex;

                nextBitmapVectors[0] = new Vector2((mBound.getLeft() - mBound.getWidth()) + maxBitmapSpacing + maxBitmapDimensions.x, position.y);

                int counter = nextBitmapIndex + maxDisplayedBitmaps >= bitmaps.size() ? bitmaps.size() - nextBitmapIndex : MAX_DISPLAYED_BITMAPS_ALLOWED;

                for(int k = 1; k < counter; k++) {
                    nextBitmapVectors[k] = new Vector2(nextBitmapVectors[0].x + (k * (maxBitmapSpacing + (maxBitmapDimensions.x * 2))), position.y);
                }
            } else {
                nextBitmapIndex = currentBitmapIndex + maxDisplayedBitmaps > bitmaps.size() ? 0 : currentBitmapIndex + maxDisplayedBitmaps;
                nextBitmapIndex = nextBitmapIndex == bitmaps.size() ?  0 : nextBitmapIndex;

                nextBitmapVectors[0] = new Vector2(mBound.getRight() + maxBitmapSpacing + maxBitmapDimensions.x, position.y);

                int counter = nextBitmapIndex + maxDisplayedBitmaps >= bitmaps.size() ? bitmaps.size() - nextBitmapIndex : MAX_DISPLAYED_BITMAPS_ALLOWED;

                for(int k = 1; k < counter; k++) {
                    nextBitmapVectors[k] = new Vector2(nextBitmapVectors[0].x + (k * (maxBitmapSpacing + (maxBitmapDimensions.x * 2))), position.y);
                }
            }
        }
    }

    /**
     * Sets the value of selectMode
     * @param value
     */
    public void setSelectMode(boolean value) {
        selectMode = value;
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
        if((leftPushed || rightPushed) && !scrollAnimationTriggered && !bitmapSelected) {
            if(multiBitmapMode && maxDisplayedBitmaps >= bitmaps.size()) return;

            scrollAnimationTriggered = true;

            // Set direction to scroll images and vector of next bitmap
            if(leftPushed) {
                scrollDirection = true;
            }
            else {
                scrollDirection = false;
            }

            calculateNextBitmapVectors();

            // Cancel animation if there is no other bitmap in bitmaps
            if(nextBitmapIndex == -1) scrollAnimationTriggered = false;

            // Reset distance moved to base value of 0
            distanceMoved = 0;
        } else if (selectMode && (!scrollAnimationTriggered && !selectAnimationTriggered)){
            // Check for any other touch events on this button
            Input input = mGameScreen.getGame().getInput();
            boolean scrollerTouched = false;
            if (input.existsTouch(0)) {
                touchLocation = new Vector2(input.getTouchX(0), input.getTouchY(0));
                // Touch detected within the allowed bounds of the scroller
                if (selectBound.contains(touchLocation.x, touchLocation.y)) {
                    scrollerTouched = true;
                }
            }

            // Scroller was touched
            if(scrollerTouched) {
                Rect bitmapBound = new Rect();
                if(multiBitmapMode) {
                    // Check if a bitmap was touched by looping through each of the currentDisplayedBitmaps
                    int breaker = currentBitmapIndex + maxDisplayedBitmaps >= bitmaps.size() ? bitmaps.size() - currentBitmapIndex : maxDisplayedBitmaps;
                    for (int i = 0; i < breaker; i++) {
                        // Get bounds of currentBitmaps[i]
                        bitmapBound.set((int) (currentBitmapVectors[i].x - maxBitmapDimensions.x),
                                (int) (currentBitmapVectors[i].y - maxBitmapDimensions.y),
                                (int) (currentBitmapVectors[i].x + maxBitmapDimensions.x),
                                (int) (currentBitmapVectors[i].y + maxBitmapDimensions.y));

                        // Bitmap touched
                        if(bitmapBound.contains((int) touchLocation.x, (int) touchLocation.y)) {
                            /* If a bitmap has not been selected yet, set the selectedBitmapIndex to i
                               and start animation
                               else if a bitmap has been selected, the bitmap clicked here is the same as
                               the one selected, start animation */
                            if(!bitmapSelected) {
                                selectedBitmapIndex = i;
                                selectAnimationTriggered = true;
                                distanceMoved = 0;
                            }
                            else if (selectedBitmapIndex == i){
                                selectAnimationTriggered = true;
                                distanceMoved = 0;
                            }
                        }
                    }
                } else {
                    // Get bounds of current bitmap
                    Vector2 currentBitmapDimensions = getNewBitmapDimensions(bitmaps.get(currentBitmapIndex), (int) mBound.getHeight(), true);
                    bitmapBound.set((int) (currentBitmapVector.x - currentBitmapDimensions.x),
                            (int) (position.y - currentBitmapDimensions.y),
                            (int) (currentBitmapVector.x + currentBitmapDimensions.x),
                            (int) (position.y + currentBitmapDimensions.y));

                    // Start animation if click is within the bitmap's bounds
                    if(bitmapBound.contains((int) touchLocation.x, (int) touchLocation.y)) {
                        selectAnimationTriggered = true;
                        distanceMoved = 0;
                    }
                }
            }
        }

        // Perform animation if a scroll animation has been triggered
        if(scrollAnimationTriggered) {
            // TODO: Animation
            boolean isAnimationComplete = false;

            // Move current bitmap and next bitmap
            float moveBy = 0;
            if(!scrollDirection) moveBy = -1 * imageDistance * 0.05f;
            else moveBy = imageDistance * 0.05f;

            if(multiBitmapMode) {
                for (int i = 0; i < maxDisplayedBitmaps; i++) {
                    currentBitmapVectors[i].add(moveBy, 0);
                    nextBitmapVectors[i].add(moveBy, 0);
                }
            } else {
                currentBitmapVector.add(moveBy, 0);
                nextBitmapVector.add(moveBy, 0);
            }

            distanceMoved += Math.abs(moveBy);

            // If intended distance has been moved, end animation
            if(distanceMoved >= imageDistance) isAnimationComplete = true;

            if(isAnimationComplete) {
                scrollAnimationTriggered = false;
                if(multiBitmapMode) {
                    currentBitmapIndexes = nextBitmapIndexes;
                    currentBitmapIndex = nextBitmapIndex;
                    calculateCurrentBitmapVectors();
                } else {
                    currentBitmapIndex = nextBitmapIndex;
                    currentBitmapVector = new Vector2(position.x, position.y);
                }
                distanceMoved = 0;
            }
        }

        // Perform animation if select animation has been triggered
        if(selectAnimationTriggered) {
            boolean isAnimationComplete = false;

            // Move current bitmap and next bitmap
            float moveBy = 0;
            if(!selectDirection) moveBy = -1 * selectDistance * 0.4f;
            else moveBy = selectDistance * 0.4f;

            // Move currently selected bitmap
            if(multiBitmapMode) {
                currentBitmapVectors[selectedBitmapIndex].add(0, moveBy);

            } else {
                currentBitmapVector.add(0, moveBy);
            }


            distanceMoved += Math.abs(moveBy);

            // If intended distance has been moved, end animation
            if(distanceMoved >= selectDistance) isAnimationComplete = true;

            if(isAnimationComplete) {
                selectAnimationTriggered = false;
                bitmapSelected = !bitmapSelected;
                selectDirection = !selectDirection;
                distanceMoved = 0;
            }
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        // TODO: Get the damn thing to actually draw something
        // TODO: Draw bitmaps
        super.draw(elapsedTime, graphics2D);

        if(multiBitmapMode) {
            if(currentBitmapIndex == -1) return;

            // Draw current bitmaps
            int breaker = currentBitmapIndex + maxDisplayedBitmaps >= bitmaps.size() ? bitmaps.size() - currentBitmapIndex : maxDisplayedBitmaps;
            for(int i = 0; i < breaker; i++) {
                Rect bitmapRect = new Rect();
                bitmapRect.set((int) (currentBitmapVectors[i].x - maxBitmapDimensions.x),
                        (int) (currentBitmapVectors[i].y - maxBitmapDimensions.y),
                        (int) (currentBitmapVectors[i].x + maxBitmapDimensions.x),
                        (int) (currentBitmapVectors[i].y + maxBitmapDimensions.y));
                graphics2D.drawBitmap(bitmaps.get(currentBitmapIndex + i), null, bitmapRect, null);
            }

            // Draw next bitmaps if animation has been triggered
            if(!scrollAnimationTriggered) return;

            breaker = nextBitmapIndex + maxDisplayedBitmaps >= bitmaps.size() ? bitmaps.size() - nextBitmapIndex : maxDisplayedBitmaps;
            for(int i = 0; i < breaker; i++) {
                Rect bitmapRect = new Rect();
                bitmapRect.set((int) (nextBitmapVectors[i].x - maxBitmapDimensions.x),
                        (int) (position.y - maxBitmapDimensions.y),
                        (int) (nextBitmapVectors[i].x + maxBitmapDimensions.x),
                        (int) (position.y + maxBitmapDimensions.y));
                graphics2D.drawBitmap(bitmaps.get(nextBitmapIndex + i), null, bitmapRect, null);
            }

            // doot doot
        } else {
            // Get dimensions then draw current bitmap if it exists
            if(currentBitmapIndex == -1) return;
            Vector2 currentBitmapDimensions = getNewBitmapDimensions(bitmaps.get(currentBitmapIndex), (int) mBound.getHeight(), true);

            Rect currentBitmapRect = new Rect();
            currentBitmapRect.set((int) (currentBitmapVector.x - currentBitmapDimensions.x),
                    (int) (currentBitmapVector.y - currentBitmapDimensions.y),
                    (int) (currentBitmapVector.x + currentBitmapDimensions.x),
                    (int) (currentBitmapVector.y + currentBitmapDimensions.y));
            graphics2D.drawBitmap(bitmaps.get(currentBitmapIndex), null, currentBitmapRect, null);

            // Get dimensions then draw next bitmap if it exists and an animation has been triggered
            if(!scrollAnimationTriggered || nextBitmapIndex == -1) return;

            Vector2 nextBitmapDimensions = getNewBitmapDimensions(bitmaps.get(nextBitmapIndex), (int) mBound.getHeight(), true);

            Rect nextBitmapRect = new Rect();
            nextBitmapRect.set((int) (nextBitmapVector.x - nextBitmapDimensions.x),
                    (int) (position.y - nextBitmapDimensions.y),
                    (int) (nextBitmapVector.x + nextBitmapDimensions.x),
                    (int) (position.y + nextBitmapDimensions.y));
            graphics2D.drawBitmap(bitmaps.get(nextBitmapIndex), null, nextBitmapRect, null);
        }
    }
}