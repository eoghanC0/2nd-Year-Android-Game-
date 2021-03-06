package uk.ac.qub.eeecs.game.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Created by eimhin on 28/01/2018.
 */

/**
 * Abstract class for Scroller's
 *
 * Swiping left-right on the scroller moves the item(s) right, displaying the previous item(s)
 * Swiping right-left on the scroller moves the item(s) left, displaying the next item(s)
 *
 * Items are automatically scaled to fit within the scroller
 *
 * User can toggle between single item and multi item mode
 *
 * - Single item
 *      Items displayed one at a time
 *      Items are scaled to fit the total height of the scroller
 *      Scroller cycles in a loop
 * - Multi card
 *      Requires all items to have same dimensions to behave properly
 *      Displays multiple items at a time by using the full width of the scroller
 *      User chooses the maximum item height, allowing for more/less items to be displayed
 *      Scroller cycles in a loop
 *
 * Default Settings:
 * - multiMode = false
 * - selectMode = false
 *
 */
public abstract class Scroller<T extends GameObject> extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * ArrayList containing all the scroller's items
     */
    protected ArrayList<T> scrollerItems;

    /**
     * Index of currently displayed item
     */
    protected int currentItemIndex = -1;

    /**
     * Index of next item to be displayed
     */
    protected int nextItemIndex = -1;

    /**
     * Vector position of current item
     */
    protected Vector2 currentItemPosition;

    /**
     * Vector position of current item
     */
    protected Vector2 nextItemPosition;

    /**
     * Distance between moving batches of items. Used to calculate how far to move
     */
    protected float itemDistance = 0;

    /**
     * Distance moved by items
     */
    protected float distanceMoved = 0;

    /**
     * Determines whether a scroll animation is occuring
     */
    protected boolean scrollAnimationTriggered = false;

    /**
     * Direction of scroller movement
     * false = left
     * true = right
     */
    protected boolean scrollDirection = false;

    /**
     * Added for sub classes to enable/disable scroller
     */
    protected boolean scrollEnabled = true;

    /**
     * Declaration for touchEvents
     */
    List<TouchEvent> touchEvents;

    /************************
     * Multi Mode Properties
     ************************/

    /**
     * Toggle to set multi bitmap mode
     * at a time
     * ! * ! * ! * ! * ! * ! * !
     *  ! *  W A R N I N G  * !
     * ! * ! * ! * ! * ! * ! * !
     * This should only be used if the bitmaps all have the same dimensions
     */
    protected boolean multiMode = false;

    /**
     * Base bitmap used to determine dimensions of cards
     * Uses img/CardFront.png
     */
    protected Bitmap baseBitmap;

    /**
     * Maximum bitmaps that can be displayed
     */
    protected int maxDisplayedItems = 0;

    /**
     * Spacing between bitmaps
     */
    protected int maxItemSpacing = 0;

    /**
     * Absolute maximum number of bitmaps that can be displayed at a time
     */
    protected final int MAX_DISPLAYED_ITEMS_ALLOWED = 15;

    /**
     * User defined maximum number of items allowed in scroller
     */
    protected int maxScrollerItems = 100;

    /**
     * Dimensions of bitmaps
     */
    protected Vector2 maxItemDimensions = new Vector2();

    /**
     * Positions of currently displayed bitmaps
     */
    protected Vector2[] currentItemPositions = new Vector2[MAX_DISPLAYED_ITEMS_ALLOWED];

    /***********************
     * Page Icon Properties
     ***********************/

    /**
     * Locations of page icons
     */
    protected ArrayList<RectF> pageIconPositions = new ArrayList<RectF>();

    /**
     * Locations of page shadow icons
     */
    protected ArrayList<RectF> pageIconShadowPositions = new ArrayList<RectF>();

    /**
     * Offset of shadow icons
     */
    protected int pageIconShadowOffset = 2;

    /**
     * Index of current page in pageIconPositions ArrayList
     */
    protected int currentPageIndex = 0;

    /**
     * Flag for when a page check is required
     */
    protected boolean checkPageChange = true;

    /**
     * Additional flag for when a scroller triggers a page change
     */
    protected boolean pageScroll = false;

    /**
     * Used to draw icons
     */
    protected Paint paint = new Paint();

    /**
     * Colour of page icon unselected
     */
    protected int pageIconUnselectedColour;

    /**
     * Colour of page icon unselected
     */
    protected int pageIconSelectedColour;

    /**
     * Colour of page icon shadow
     */
    protected int pageIconShadowColour;

    /**
     * Used when calculating the Y position of page icons
     * The percentage is the percentage height of the scroller, to which the icons will be positioned
     * Default is 0.8
     */
    protected float pageIconRelativePercentageYPos = 0.8f;

    /**
     * Determines whether a touch down event is present
     */
    private boolean touchDown = false;

    /**
     * Stores the time of the last TOUCH_DOWN event
     */
    private long touchDownTime = 0;

    /********************
     * Testing Properties
     ********************/

    /**
     * Determines whether to use simulated touch events or touch events from device
     */
    protected boolean useSimulatedTouchEvents = false;

    /**
     * Contains the simulated touch events
     */
    protected List<TouchEvent> simulatedTouchEvents = new ArrayList<TouchEvent>();

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Main constructor
     *
     * @param x x-position
     * @param y y-position
     * @param width width of scroller
     * @param height height of scroller
     * @param gameScreen associated GameScreen
     */
    public Scroller(float x, float y, float width, float height, GameScreen gameScreen) {
        super(x, y, width > 0 ? width : -width, height > 0 ? height : -height, null, gameScreen);
        if(width < 0) width = 100;
        if(height < 0) height = 100;
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("Empty", "img/empty.png");
        assetManager.loadAndAddBitmap("Test","img/help-image-test.png");
        mBitmap = assetManager.getBitmap("Empty");

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));

        currentItemPosition = new Vector2(position.x,position.y);
        calculateNextSingleVector();

        itemDistance = mBound.getWidth();

        paint.setStrokeWidth(5);
        paint.setColor(Color.CYAN);

        pageIconUnselectedColour = Color.rgb(250, 250, 250);
        pageIconSelectedColour = Color.rgb(4, 46, 84);
        pageIconShadowColour = Color.rgb(1, 32, 61);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Sets the background bitmap of the scroller
     *
     * @param bitmap Bitmap of background
     */
    public void setBackground(Bitmap bitmap) {
        if(bitmap == null) return;
        mBitmap = bitmap;
    }

    /**
     * Gets the scaled dimensions of a bitmap based on a maximum height
     * If the height of the bitmap is less than maxHeight, original dimensions are returned
     *
     * @param maxHeight Maximum height allowed for new bitmaps
     * @return Vector2 containing the half width and half height of the bitmap
     */
    protected Vector2 getNewBitmapDimensions(Bitmap bitmap, int maxHeight, boolean occupyFullHeight) {
        if(bitmap == null) return new Vector2(2,2);
        if(maxHeight == 0 || (bitmap.getHeight() < maxHeight && !occupyFullHeight)) return new Vector2(bitmap.getWidth() / 2, bitmap.getHeight() / 2);

        float scaleFactor = (float) maxHeight / bitmap.getHeight();
        int newWidth = (int) (bitmap.getWidth() * scaleFactor);
        int newHeight = (int) (bitmap.getHeight() * scaleFactor);

        return new Vector2(newWidth / 2, newHeight / 2);
    }

    /**
     * Adds item to scroller
     *
     * @param gameObject GameObject to add to scroller
     */
    public abstract void addScrollerItem(GameObject gameObject);

    /**
     * Checks if nextIndex exists
     *
     * @return boolean as true if there is more than one scrollerItem, else false
     */
    private boolean checkDoesNextExist() {
        if(scrollerItems.size() > 1) return true;
        else return false;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Single Mode Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Calculates position of next vector for single image mode
     */
    protected void calculateNextSingleVector() {
        if(nextItemIndex == -1) return;
        if(scrollDirection) {
            nextItemPosition = new Vector2(currentItemPosition.x - itemDistance, currentItemPosition.y);
        }
        else {
            nextItemPosition = new Vector2(currentItemPosition.x + itemDistance, currentItemPosition.y);
        }

        scrollerItems.get(nextItemIndex).position = nextItemPosition;
    }

    /**
     * Gets the next imageScrollerItem from ArrayList based on the direction
     */
    private void calculateNextSingleIndex() {
        if(scrollerItems.size() > 1) {
            int directionInt = scrollDirection ? -1 : 1;
            nextItemIndex = (currentItemIndex + scrollerItems.size() + directionInt) % scrollerItems.size();
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Multi Mode Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Toggles scroller from single card to multi card mode
     *
     * @param value boolean to toggle multi mode
     * @param heightOccupyPercentage The percentage of the scroller's height that is allowed to display
     *                               the scroller items
     */
    public void setMultiMode(boolean value, int heightOccupyPercentage) {
        if(!value) {
            multiMode = false;
            currentItemIndex = 0;

            scrollerItems.get(currentItemIndex).position = new Vector2(position);
            for (GameObject i : scrollerItems) {
                Vector2 dimensions = getNewBitmapDimensions(i.getBitmap(), (int) mBound.getHeight(), true);
                i.setScaledHeight((int) dimensions.y * 2);
            }
            calculateNextSingleVector();
        } else {
            multiMode = true;
            calculateMultiItemsDisplayed(heightOccupyPercentage);
        }
    }

    /**
     * Calculates the next multi based index
     */
    private void calculateNextMultiIndex() {
        if(scrollDirection) {
            nextItemIndex = currentItemIndex - maxDisplayedItems < 0 ? scrollerItems.size() - (scrollerItems.size() % maxDisplayedItems) : currentItemIndex - maxDisplayedItems;
            nextItemIndex = nextItemIndex == scrollerItems.size() ? scrollerItems.size() - maxDisplayedItems : nextItemIndex;
        } else {
            nextItemIndex = currentItemIndex + maxDisplayedItems > scrollerItems.size() ? 0 : currentItemIndex + maxDisplayedItems;
            nextItemIndex = nextItemIndex == scrollerItems.size() ?  0 : nextItemIndex;
        }
    }

    /**
     * Calculates the number of cards that can be displayed along with their height and width
     *
     * @param heightOccupyPercentage The percentage of the scroller's height the image should occupy
     */
    public void calculateMultiItemsDisplayed(float heightOccupyPercentage) {
        if(!multiMode) return;
        // Ensure heightOccupyPercentage is within bounds then divide by 100, else set to 1 (100%)
        if(heightOccupyPercentage <= 0 || heightOccupyPercentage > 100)
            heightOccupyPercentage = 1;
        else
            heightOccupyPercentage /= 100;

        // Find the maximum height allowed for the card
        int maxHeight = (int) (mBound.getHeight() * heightOccupyPercentage);
        // Rescale card dimensions using the maxHeight
        if(baseBitmap == null) baseBitmap = mGameScreen.getGame().getAssetManager().getBitmap("Empty");

        Vector2 scaledBitmapDimensions = getNewBitmapDimensions(baseBitmap, maxHeight, true);

        // Set maxItemDimensions
        maxItemDimensions.x = scaledBitmapDimensions.x;
        maxItemDimensions.y = scaledBitmapDimensions.y;

        // Find the max images that can be displayed
        int maxImages = (int) (mBound.getWidth() / (scaledBitmapDimensions.x * 2));
        maxImages = maxImages > MAX_DISPLAYED_ITEMS_ALLOWED ? MAX_DISPLAYED_ITEMS_ALLOWED : maxImages;
        // Find the remaining screen space
        int remainder = (int) (mBound.getWidth() % (scaledBitmapDimensions.x * 2));
        // Use the remainder to determine the spacing between displayed items
        int spacing = remainder <= 0 ? 0 : remainder / (maxImages + 1);

        // Set the maxDisplayedItems and maxItemSpacing
        maxDisplayedItems = maxImages;
        maxItemSpacing = spacing;

        // Set dimensions of all items to be the same
        for (GameObject i : scrollerItems) {
            i.setScaledHeight((int) scaledBitmapDimensions.y * 2);
        }

        calculateCurrentMultiVectors();
    }

    /**
     * Gets vectors for currently displayed items
     */
    protected void calculateCurrentMultiVectors() {
        // If a current item exists, draw any current items else return
        if(!multiMode || currentItemIndex == -1 || scrollerItems.size() < 1) return;

        // Set position of current item
        scrollerItems.get(currentItemIndex).position = new Vector2(mBound.getLeft() + maxItemSpacing + scrollerItems.get(0).getBound().halfWidth, position.y);

        // Set positions of any other current items
        int breaker = currentItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - currentItemIndex : maxDisplayedItems;
        for(int i = 0; i < breaker; i++)
            scrollerItems.get(currentItemIndex + i).position = new Vector2(scrollerItems.get(currentItemIndex).position.x + (i * (maxItemSpacing + (maxItemDimensions.x * 2))), position.y);

    }

    /**
     * Calculates the positions of the next cards based on the direction the scroller
     * is being moved in
     */
    public void calculateNextMultiVectors() {
        if(!multiMode || scrollerItems.size() <= 1) return;
        // Get starting position of next items based on the direction the scroller is going to move
        float startPosition = 0;
        startPosition = scrollDirection ? mBound.getLeft() - mBound.getWidth() : mBound.getRight();

        // Set the new item index
        calculateNextMultiIndex();

        // Set  position of first next item
        scrollerItems.get(nextItemIndex).position = new Vector2(startPosition + maxItemSpacing + maxItemDimensions.x, position.y);

        // Set positions of any other next items
        int breaker = nextItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - nextItemIndex : maxDisplayedItems;
        for(int i = 1; i < breaker; i++) {
            scrollerItems.get(nextItemIndex + i).position = new Vector2(scrollerItems.get(nextItemIndex).position.x + (i * (maxItemSpacing + (maxItemDimensions.x * 2))), position.y);
        }
    }

    /**
     * Adds moveBy to distanceMoved then checks if distanceMoved
     * has reached the item distance
     *
     * @param moveBy The distance which has been moved since the last update
     * @return boolean as true if distanceMoved is greater than or equal to itemDistance,
     *         else false
     */
    private boolean addAndCheckScrollerDistanceMoved(float moveBy) {
        // Add to distance moved
        distanceMoved += Math.abs(moveBy);

        float distance = itemDistance;

        // If intended distance has been moved, end animation
        if(distanceMoved >= distance) return true;
        else return false;
    }

    /**
     * Executes the updating of positions of items for scroll animation if
     * scrollAnimationTriggered is true
     */
    private void checkAndPerformScrollAnimation() {
        if(!scrollAnimationTriggered || (multiMode && scrollerItems.size() <= maxDisplayedItems)) return;

        // Move current card and next card
        float moveBy = 0;
        if(!scrollDirection) moveBy = -1 * itemDistance * 0.05f;
        else moveBy = itemDistance * 0.05f;

        if(useSimulatedTouchEvents) moveBy = scrollDirection ? itemDistance : -1 * itemDistance;

        // Branch based on whether multi mode or single mode is enabled
        if(multiMode) {
            // Move any currently displayed items
            int breaker = currentItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - currentItemIndex : maxDisplayedItems;

            for (int i = 0; i < breaker; i++) {
                scrollerItems.get(i + currentItemIndex).position.add(moveBy, 0);
            }

            // Move any next items
            breaker = nextItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - nextItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                scrollerItems.get(nextItemIndex + i).position.add(moveBy, 0);
            }
        } else {
            // Draw current item
            scrollerItems.get(currentItemIndex).position.add(moveBy, 0);
            if(checkDoesNextExist())
                scrollerItems.get(nextItemIndex).position.add(moveBy, 0);
        }
        // Carry out completing steps if the move distance has been reached
        if(addAndCheckScrollerDistanceMoved(moveBy)) {
            // Stop animation
            scrollAnimationTriggered = false;

            // Set current index to the next index, as next index is now at the position
            // current index was at originally before the animation
            currentItemIndex = nextItemIndex;

            // Branch based on whether multi mode or single mode is enabled
            if(multiMode) {
                // Calculate the vectors for the new current items
                calculateCurrentMultiVectors();
            } else {
                // Set position of new current item to scroller position
                scrollerItems.get(currentItemIndex).position = new Vector2(position);
            }

            // Reset distance moved
            distanceMoved = 0;

            // Trigger flag for page icon changed
            checkPageChange = true;
            pageScroll = true;
        }
    }

    /**
     * Checks for a touch event on the scroller and carries out
     * necessary actions depending on what is touched
     */
    private void checkForTouchEvent() {
        if(isAnimating()) return;

        if(!useSimulatedTouchEvents) touchEvents = mGameScreen.getGame().getInput().getTouchEvents();
        else touchEvents = simulatedTouchEvents;

        Vector2 touchVector = new Vector2();

        // Check touch events for a swipe gesture
        for (TouchEvent t : touchEvents) {
            touchVector.set(t.x, t.y);
            if(!checkIfTouchInArea(touchVector, mBound)) continue;

            if(!touchDown) {
                if(t.type == TouchEvent.TOUCH_DOWN) {
                    touchDown = true;
                    touchDownTime = System.nanoTime();
                }
            }

            if(t.type == TouchEvent.TOUCH_FLING && scrollEnabled) {
                if (t.dx > 0) scrollDirection = true;
                else scrollDirection = false;
            }

            if(t.type == TouchEvent.TOUCH_UP) {
                if(System.nanoTime() - touchDownTime < 300000000 && scrollerItems.size() > 1) {
                    scrollAnimationTriggered = true;
                }
                touchDown = false;
                touchDownTime = 0;
            }
        }

        // Check for input to determine if animation should be triggered to move the items
        if((scrollAnimationTriggered) && (nextItemIndex != -1)) {
            if(multiMode && maxDisplayedItems >= scrollerItems.size()) return;

            // Branch based on multi mode or single mode is enabled
            if(multiMode) {
                // Calculates next item's vectors
                calculateNextMultiVectors();
            } else {
                // Calculates next vector and index
                calculateNextSingleIndex();
                calculateNextSingleVector();
            }

            // Reset distance moved to base value of 0
            distanceMoved = 0;
        }
    }

    /**
     * Check if a touch is within the general area of a certain location
     *
     * @param userTouchLocation Vector2 of the location touched
     * @return boolean as true if touch is in the touchDestination, else false
     */
    public boolean checkIfTouchInArea(Vector2 userTouchLocation, BoundingBox touchDestination) {
        if(userTouchLocation == null || touchDestination == null) return false;

        if(touchDestination.contains(userTouchLocation.x, userTouchLocation.y)) return true;

        return false;
    }

    /**
     * Check if a touch is within the general area of a certain location
     *
     * @param userTouchLocation Vector2 of the locationTouched
     * @param touchLocation Vector2 of the allowed touch area
     * @param deviation Used to generate a bounding box of height (deviation * 2) and width (deviation * 2)
     *                  to improve the reliability of the userTouchLocation
     * @return boolean as true if the userTouchLocation is within the area of the touchLocation else false
     */
    public boolean checkIfTouchInArea(Vector2 userTouchLocation, Vector2 touchLocation, float deviation) {
        if(userTouchLocation == null || touchLocation == null) return false;

        BoundingBox tempBound = new BoundingBox();
        tempBound.x = touchLocation.x;
        tempBound.y = touchLocation.y;
        tempBound.halfWidth = deviation;
        tempBound.halfHeight = deviation;

        if(tempBound.contains(userTouchLocation.x, userTouchLocation.y)) return true;

        return false;
    }

    /**
     * Used to move scroller and all contents
     *
     * @param x amount to move x position by
     * @param y amount to move y position by
     */
    public void adjustPosition(float x, float y) {
        position.add(x, y);
        for (GameObject item : scrollerItems) {
            item.position.add(x, y);
        }
        for (RectF pageIconRectF : pageIconPositions) {
            pageIconRectF.offset(x, y);
        }
        getBound();
    }

    /**
     * Returns whether an animation is occuring
     *
     * @return boolean as true if scrollAnimationTriggered is true, else false
     */
    public boolean isAnimating() {
        return scrollAnimationTriggered;
    }

    /**
     * Clears items in the scroller
     */
    public void clearScroller() {
        scrollerItems.clear();
    }

    /**
     * Checks if the current page icon should be changed
     */
    private void checkChangeCurrentPage() {
        // Page icon should be changed
        if(checkPageChange) {
            calculateCurrentPageIndex();
            checkPageChange = false;
        }
    }

    /**
     * Calculates positions of page icons
     */
    public void calculateCurrentPageIndex() {
        if(scrollerItems.size() == 0 || maxDisplayedItems <= 0) pageIconPositions.clear();

        // Calculate number of pages needed
        int pages = 0;
        if(multiMode)
            pages = (int) Math.ceil((double) scrollerItems.size() / maxDisplayedItems);
        else
            pages = scrollerItems.size();

        // Change current page index if a page scroll was triggered
        if(pageScroll) {
            int moveDirection = scrollDirection ? -1 : 1;
            currentPageIndex = (currentPageIndex + moveDirection) % pages;
            currentPageIndex = currentPageIndex < 0 ? pages - 1 : currentPageIndex;
            pageScroll = false;
        }

        // If there has been no changes in the number of changes return
        if(pages == pageIconPositions.size()) return;

        // Calculate position of each icon starting with the first as a basis
        pageIconPositions.clear();
        pageIconShadowPositions.clear();
        Vector2 firstIconPos = new Vector2((getBound().getWidth() - (pages * getBound().getHeight() * 0.05f) - ((pages - 1) * getBound().getHeight() * 0.1f)) / 2, position.y + getBound().halfHeight * pageIconRelativePercentageYPos);
        pageIconPositions.add(new RectF(firstIconPos.x, firstIconPos.y, firstIconPos.x + getBound().getHeight() * 0.05f, firstIconPos.y + getBound().getHeight() * 0.05f));
        pageIconShadowPositions.add(new RectF(firstIconPos.x + pageIconShadowOffset, firstIconPos.y + pageIconShadowOffset, firstIconPos.x + getBound().getHeight() * 0.05f + pageIconShadowOffset, firstIconPos.y + getBound().getHeight() * 0.05f + pageIconShadowOffset));

        for (int i = 1; i < pages; i++) {
            float x = firstIconPos.x + (i * (getBound().getHeight() * 0.15f));
            pageIconPositions.add(new RectF(x, firstIconPos.y, x + getBound().getHeight() * 0.05f, firstIconPos.y + getBound().getHeight() * 0.05f));
            pageIconShadowPositions.add(new RectF(x + pageIconShadowOffset, firstIconPos.y + pageIconShadowOffset, x + getBound().getHeight() * 0.05f + pageIconShadowOffset, firstIconPos.y + getBound().getHeight() * 0.05f + pageIconShadowOffset));
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Update and Draw
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);

        // Updates Cards on scroller
        for (GameObject item : scrollerItems)
            item.update(elapsedTime);

        if(scrollerItems.isEmpty()) return;

        // Checks if scroller has been touched and will carry out any
        // necessary actions depending on where is touched
        checkForTouchEvent();

        // Checks if scroll animation has been triggered and performs animation if so
        checkAndPerformScrollAnimation();

        // Checks if the current page icon should be changed
        checkChangeCurrentPage();
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        super.draw(elapsedTime, graphics2D);
    }

    /**
     * Draw page icons
     * @param graphics2D
     */
    protected void drawPageIcons(IGraphics2D graphics2D) {
        for (int i = 0; i < pageIconPositions.size(); i++) {
            paint.setColor(pageIconShadowColour);
            graphics2D.drawArc(pageIconShadowPositions.get(i), 0,360, true, paint);
            if(i == currentPageIndex) {
                paint.setColor(pageIconSelectedColour);
                graphics2D.drawArc(pageIconPositions.get(i), 0,360, true, paint);
            } else {
                paint.setColor(pageIconUnselectedColour);
                graphics2D.drawArc(pageIconPositions.get(i), 0,360, true, paint);
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Testing
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Sets the simulatedTouchEvents used for simulated input
     *
     * @param simulatedTouchEvents
     */
    public void setSimulatedTouchEvents(List<TouchEvent> simulatedTouchEvents) {
        this.simulatedTouchEvents = simulatedTouchEvents;
    }

    /**
     * Sets whether or not the scroller should use simulated touch events passed in (for testing)
     * or standard input settings
     *
     * @param useSimulatedTouchEvents
     */
    public void setUseSimulatedTouchEvents(boolean useSimulatedTouchEvents) {
        if(useSimulatedTouchEvents) {
            this.useSimulatedTouchEvents = true;
        } else {
            this.useSimulatedTouchEvents = false;
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Getters
    // /////////////////////////////////////////////////////////////////////////

    public int getItemCount() {
        return scrollerItems.size();
    }

    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    public int getNextItemIndex() {
        return nextItemIndex;
    }

    public boolean getMultiMode() {
        return multiMode;
    }

    public Vector2 getCurrentItemPosition() {
        return currentItemPosition;
    }

    public Vector2 getNextItemPosition() {
        return nextItemPosition;
    }

    public float getItemDistance() {
        return itemDistance;
    }

    public float getDistanceMoved() {
        return distanceMoved;
    }

    public int getMaxDisplayedItems() {
        return maxDisplayedItems;
    }

    public int getMaxItemSpacing() {
        return maxItemSpacing;
    }

    public int getMAX_DISPLAYED_ITEMS_ALLOWED() {
        return MAX_DISPLAYED_ITEMS_ALLOWED;
    }

    public Vector2 getMaxItemDimensions() {
        return maxItemDimensions;
    }

    public Vector2[] getCurrentItemPositions() {
        return currentItemPositions;
    }

    public ArrayList<T> getScrollerItems() {
        return scrollerItems;
    }

    public void setScrollDirection(boolean scrollDirection) { this.scrollDirection = scrollDirection; }

    public int getMaxScrollerItems() { return maxScrollerItems; }

    public ArrayList<RectF> getPageIconPositions() {
        return pageIconPositions;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public int getPageIconUnselectedColour() { return pageIconUnselectedColour; }

    public int getPageIconSelectedColour() { return pageIconSelectedColour; }

    public int getPageIconShadowColour() { return pageIconShadowColour; }

    public float getPageIconRelativePercentageYPos() { return pageIconRelativePercentageYPos; }

    public boolean isScrollAnimationTriggered() {
        return scrollAnimationTriggered;
    }

    public boolean isScrollDirection() {
        return scrollDirection;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Setters
    // /////////////////////////////////////////////////////////////////////////

    public void setMaxScrollerItems(int maxScrollerItems) { this.maxScrollerItems = maxScrollerItems > 0 ? maxScrollerItems : 25; }

    public void setBaseBitmap(Bitmap baseBitmap) {
        this.baseBitmap = baseBitmap;
    }

    public void setPageScroll(boolean pageScroll) {
        this.pageScroll = pageScroll;
    }

    public void setPageIconUnselectedColour(int pageIconUnselectedColour) { this.pageIconUnselectedColour = pageIconUnselectedColour; }

    public void setPageIconSelectedColour(int pageIconSelectedColour) { this.pageIconSelectedColour = pageIconSelectedColour; }

    public void setPageIconShadowColour(int pageIconShadowColour) { this.pageIconShadowColour = pageIconShadowColour; }

    public void setPageIconRelativePercentageYPos(float pageIconRelativePercentageYPos) {
        this.pageIconRelativePercentageYPos = pageIconRelativePercentageYPos >= 0.0f ? pageIconRelativePercentageYPos : this.pageIconRelativePercentageYPos;
    }
}