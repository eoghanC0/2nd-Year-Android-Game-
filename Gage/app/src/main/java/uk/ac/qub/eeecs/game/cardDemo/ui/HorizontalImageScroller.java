package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

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
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;

/**
 * Created by eimhin on 11/01/2018.
 */

/**
 * This class allows you to create a horizontally moving image scroller
 *
 * Clicking the left side of the scroller moves the item(s) right, displaying the previous item
 * Clicking the right side of the scroller moves the item(s) left, displaying the next item
 *
 * Images are automatically scaled to fit within the scroller
 *
 * User can toggle between single item and multi item mode
 *
 * - Single item
 *      Items displayed one at a time
 *      Items are scaled to fit the total height of the scroller
 *      Scroller cycles in a loop
 * - Multi item
 *      Displays multiple items at a time by using the full width of the scroller
 *      User chooses the maximum item height, allowing for more/less item to be displayed
 *      Scroller cycles in a loop
 *
 * Default Settings:
 * - multiMode = false
 * - selectMode = false
 *
 * TODO: Create tests.
 * TODO: Animations not configured properly using frames, elapsedTime etc. Fix this.
 * TODO: Only draw items within the bounds of the scroller
 * TODO: Change addScrollerItem() to use an ImageScrollerItem as argument.
 */
public class HorizontalImageScroller extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////


    /**
     * ArrayList containing all the scroller's items
     */
    private ArrayList<ImageScrollerItem> imageScrollerItems = new ArrayList<ImageScrollerItem>();

    /**
     * Index of currently displayed item
     */
    private int currentItemIndex = -1;

    /**
     * Index of next item to be displayed
     */
    private int nextItemIndex = -1;

    /**
     * Vector position of current item
     */
    private Vector2 currentItemPosition;

    /**
     * Vector position of current item
     */
    private Vector2 nextItemPosition;

    /**
     * Distance between moving batches of items. Used to calculate how far to move
     */
    private float itemDistance = 0;

    /**
     * Distance moved by items
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
     * Push buttons to activate scroller
     */
    private PushButton pushButtonLeft;
    private PushButton pushButtonRight;

    /**
     * MULTI MODE VARIABLES
     */

    /**
     * Toggle to set multi item mode
     * at a time
     * = = = = = = = = = = = = =
     *  * *  W A R N I N G  * *
     * = = = = = = = = = = = = =
     * This should only be used if the items all have the same dimensions
     */
    private boolean multiMode = false;

    /**
     * Maximum items that can be displayed
     */
    private int maxDisplayedItems = 0;

    /**
     * Spacing between items
     */
    private int maxItemSpacing = 0;

    /**
     * Absolute maximum number of items that can be displayed at a time
     */
    private final int MAX_DISPLAYED_ITEMS_ALLOWED = 15;

    /**
     * User defined maximum number of items allowed in scroller
     */
    private int maxScrollerItems = 25;

    /**
     * Dimensions of items
     */
    private Vector2 maxItemDimensions = new Vector2();

    /**
     * Positions of currently displayed items
     */
    private Vector2[] currentItemPositions = new Vector2[MAX_DISPLAYED_ITEMS_ALLOWED];

    /**
     * INTERACTIVITY VARIABLES
     */

    /**
     * Whether or not selecting is allowed
     */
    private boolean selectMode = false;

    /**
     * Tells whether a item has been selected
     */
    private boolean itemSelected = false;

    /**
     * Stores the index of the item currently selected
     */
    private int selectedItemIndex;

    /**
     * Determines whether a item select animation is occuring
     */
    private boolean selectAnimationTriggered = false;

    /**
     * Direction in which item moves for select animation
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
     * between the buttons else, selecting a button on top of a item will scroll and select
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
        mBitmap = assetManager.getBitmap("Empty");

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));

        currentItemPosition = new Vector2(position.x,position.y);
        calculateNextSingleVector();

        itemDistance = mBound.getWidth();

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
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Test items
     */
    public void addTestData() {
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();

        assetManager.loadAndAddBitmap("Card 0", "img/card-0.png");
        assetManager.loadAndAddBitmap("Card 1", "img/card-1.png");
        assetManager.loadAndAddBitmap("Card 2", "img/card-2.png");
        assetManager.loadAndAddBitmap("Card 3", "img/card-3.png");
        assetManager.loadAndAddBitmap("Card 4", "img/card-4.png");
        assetManager.loadAndAddBitmap("Card 5", "img/card-5.png");
        assetManager.loadAndAddBitmap("Card 6", "img/card-6.png");
        assetManager.loadAndAddBitmap("Card 7", "img/card-7.png");
        assetManager.loadAndAddBitmap("Green", "img/green.png");

        addScrollerItem(assetManager.getBitmap("Card 0"));
        addScrollerItem(assetManager.getBitmap("Card 1"));
        addScrollerItem(assetManager.getBitmap("Card 2"));
        addScrollerItem(assetManager.getBitmap("Card 3"));
        addScrollerItem(assetManager.getBitmap("Card 4"));
        addScrollerItem(assetManager.getBitmap("Card 5"));
        addScrollerItem(assetManager.getBitmap("Card 6"));
        addScrollerItem(assetManager.getBitmap("Card 7"));
    }

    /**
     * Sets the background bitmap of the scroller
     * @param bitmap
     */
    public void setBackground(Bitmap bitmap) {
        if(!(mBitmap == null)) return;
        mBitmap = bitmap;
    }

    /**
     * Sets the value of selectMode
     * @param value
     */
    public void setSelectMode(boolean value) {
        selectMode = value;
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
     * Adds item to scroller
     * @param bitmap
     */
    public void addScrollerItem(Bitmap bitmap) {
        if(bitmap != null || imageScrollerItems.size() <= maxScrollerItems) {
            if(imageScrollerItems.size() == 0) currentItemIndex = 0;
            else if(imageScrollerItems.size() == 1) nextItemIndex = 1;

            Vector2 dimensions = getNewBitmapDimensions(bitmap, (int) mBound.getHeight(), true);
            imageScrollerItems.add(new ImageScrollerItem(position.x, position.y, dimensions.x * 2, dimensions.y * 2, bitmap, mGameScreen));
        }
    }

    /**
     * Checks if nextIndex exists
     * @return
     */
    private boolean checkDoesNextExist() {
        if(imageScrollerItems.size() > 1) return true;
        else return false;
    }

    /**
     * * * * * * * * * * *
     * SINGLE MODE METHODS
     * * * * * * * * * * *
     */

    /**
     * Calculates position of next vector for single image mode
     */
    private void calculateNextSingleVector() {
        if(nextItemIndex == -1) return;
        if(scrollDirection) {
            nextItemPosition = new Vector2(currentItemPosition.x - itemDistance, currentItemPosition.y);
        }
        else {
            nextItemPosition = new Vector2(currentItemPosition.x + itemDistance, currentItemPosition.y);
        }

        imageScrollerItems.get(nextItemIndex).position = nextItemPosition;
    }

    /**
     * Gets the next imageScrollerItem from ArrayList based on the direction
     */
    private void calculateNextSingleIndex() {
        if(imageScrollerItems.size() > 1) {
            int directionInt = scrollDirection ? -1 : 1;
            nextItemIndex = (currentItemIndex + imageScrollerItems.size() + directionInt) % imageScrollerItems.size();
        }
    }

    /**
     * * * * * * * * * * *
     * MULTI MODE METHODS
     * * * * * * * * * * *
     */

    /**
     * Toggles scroller to or from single item to multi item mode
     * @param value
     * @param heightOccupyPercentage
     */
    public void setMultiMode(boolean value, int heightOccupyPercentage) {
        if(!(imageScrollerItems.size() > 0)) {
            Log.e("ERROR", "You cannot set multi item mode unless there is at least 1 item in scroller");
        } else if(!value) {
            multiMode = false;
            currentItemIndex = 0;

            imageScrollerItems.get(currentItemIndex).position = new Vector2(position);
            for (ImageScrollerItem i : imageScrollerItems) {
                Vector2 dimensions = getNewBitmapDimensions(i.getBitmap(), (int) mBound.getHeight(), true);
                i.setWidthAndHeight(dimensions.x * 2, dimensions.y * 2);
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
            nextItemIndex = currentItemIndex - maxDisplayedItems < 0 ? imageScrollerItems.size() - (imageScrollerItems.size() % maxDisplayedItems) : currentItemIndex - maxDisplayedItems;
            nextItemIndex = nextItemIndex == imageScrollerItems.size() ? imageScrollerItems.size() - maxDisplayedItems : nextItemIndex;
        } else {
            nextItemIndex = currentItemIndex + maxDisplayedItems > imageScrollerItems.size() ? 0 : currentItemIndex + maxDisplayedItems;
            nextItemIndex = nextItemIndex == imageScrollerItems.size() ?  0 : nextItemIndex;
        }
    }

    /**
     * Calculates thenumber of items that can be displayed
     * @param heightOccupyPercentage The percentage of the scroller's height the item should occupy
     */
    public void calculateMultiItemsDisplayed(float heightOccupyPercentage) {
        if(!multiMode || !(imageScrollerItems.size() > 0)) return;
        // Ensure heightOccupyPercentage is within bounds then divide by 100, else set to 1 (100%)
        if(heightOccupyPercentage <= 0 || heightOccupyPercentage > 100)
            heightOccupyPercentage = 1;
        else
            heightOccupyPercentage /= 100;

        // Find the maximum height allowed for the item
        int maxHeight = (int) (mBound.getHeight() * heightOccupyPercentage);
        // Rescale item dimensions using the maxHeight
        Vector2 scaledBitmapDimensions = getNewBitmapDimensions(imageScrollerItems.get(0).getBitmap(), maxHeight, true);

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
        for (ImageScrollerItem i : imageScrollerItems) {
            i.setWidthAndHeight(scaledBitmapDimensions.x * 2, scaledBitmapDimensions.y * 2);
        }

        calculateCurrentMultiVectors();
    }

    /**
     * Gets vectors for currently displayed items
     */
    private void calculateCurrentMultiVectors() {
        // If a current item exists, draw any current items else return
        if(!multiMode || currentItemIndex == -1) return;

        // Set position of current item
        imageScrollerItems.get(currentItemIndex).position = new Vector2(mBound.getLeft() + maxItemSpacing + imageScrollerItems.get(0).getBound().halfWidth, position.y);

        // Set positions of any other current items
        int breaker = currentItemIndex + maxDisplayedItems >= imageScrollerItems.size() ? imageScrollerItems.size() - currentItemIndex : maxDisplayedItems;
        for(int i = 0; i < breaker; i++) {
            imageScrollerItems.get(currentItemIndex + i).position = new Vector2(imageScrollerItems.get(currentItemIndex).position.x + (i * (maxItemSpacing + imageScrollerItems.get(currentItemIndex + i).getBound().getWidth())), position.y);
        }
    }

    /**
     * Calculates the positions of the next items based on the direction the scroller
     * is being moved in
     */
    public void calculateNextMultiVectors() {
        if(!multiMode || imageScrollerItems.size() <= 1) return;
        // Get starting position of next items based on the direction the scroller is going to move
        float startPosition = 0;
        startPosition = scrollDirection ? mBound.getLeft() - mBound.getWidth() : mBound.getRight();

        // Set the new item index
        calculateNextMultiIndex();

        // Set  position of first next item
        imageScrollerItems.get(nextItemIndex).position = new Vector2(startPosition + maxItemSpacing + maxItemDimensions.x, position.y);

        // Set positions of any other next items
        int breaker = nextItemIndex + maxDisplayedItems >= imageScrollerItems.size() ? imageScrollerItems.size() - nextItemIndex : maxDisplayedItems;
        for(int i = 1; i < breaker; i++) {
            imageScrollerItems.get(nextItemIndex + i).position = new Vector2(imageScrollerItems.get(nextItemIndex).position.x + (i * (maxItemSpacing + (maxItemDimensions.x * 2))), position.y);
        }
    }

    /**
     * Adds moveBy to distanceMoved then checks if distanceMoved
     * has reached the item distance
     * @param moveBy
     * @param animation false = scroll true = select
     * @return
     */
    private boolean addAndCheckDistanceMoved(float moveBy, boolean animation) {
        // Add to distance moved
        distanceMoved += Math.abs(moveBy);

        float distance = animation == false ? itemDistance : selectDistance;

        // If intended distance has been moved, end animation
        if(distanceMoved >= distance) return true;
        else return false;
    }

    /**
     * Executes the updating of positions of items for scroll animation if
     * scrollAnimationTriggered is true
     */
    private void checkAndPerformScrollAnimation() {
        if(!scrollAnimationTriggered) return;

        // Move current item and next item
        float moveBy = 0;
        if(!scrollDirection) moveBy = -1 * itemDistance * 0.05f;
        else moveBy = itemDistance * 0.05f;

        // Branch based on whether multi mode or single mode is enabled
        if(multiMode) {
            // Move any currently displayed items
            int breaker = currentItemIndex + maxDisplayedItems >= imageScrollerItems.size() ? imageScrollerItems.size() - currentItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                imageScrollerItems.get(i + currentItemIndex).position.add(moveBy, 0);
            }

            // Move any next items
            breaker = nextItemIndex + maxDisplayedItems >= imageScrollerItems.size() ? imageScrollerItems.size() - nextItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                imageScrollerItems.get(nextItemIndex + i).position.add(moveBy, 0);
            }
        } else {
            // Draw current item
            imageScrollerItems.get(currentItemIndex).position.add(moveBy, 0);
            if(checkDoesNextExist())
                imageScrollerItems.get(nextItemIndex).position.add(moveBy, 0);
        }

        // Carry out completing steps if the move distance has been reached
        if(addAndCheckDistanceMoved(moveBy, false)) {
            // Stop animation
            scrollAnimationTriggered = false;

            // Branch based on whether multi mode or single mode is enabled
            if(multiMode) {
                // Calculate the vectors for the new current items
                calculateCurrentMultiVectors();
            } else {
                // Set position of new current item to scroller position
                imageScrollerItems.get(currentItemIndex).position = new Vector2(position);
            }
            // Set current index to the next index, as next index is now at the position
            // current index was at originally before the animation
            currentItemIndex = nextItemIndex;
            // Reset distance moved
            distanceMoved = 0;
        }
    }

    /**
     * Executes the updating of positions of items for select animation if
     * selectAnimationTriggered is true and select mode is enabled
     */
    private void checkAndPerformSelectAnimation() {
        if(!selectAnimationTriggered || !selectMode) return;

        // Move current item and next item
        float moveBy = 0;
        if(!selectDirection) moveBy = -1 * selectDistance * 0.4f;
        else moveBy = selectDistance * 0.4f;

        imageScrollerItems.get(selectedItemIndex).position.add(0, moveBy);

        // Carry out completing steps if the move distance has been reached
        if(addAndCheckDistanceMoved(moveBy, true)) {
            // Stop animation
            selectAnimationTriggered = false;
            // Toggle item selected and selectDirection
            itemSelected = !itemSelected;
            selectDirection = !selectDirection;
            // Reset distance moved
            distanceMoved = 0;
        }
    }

    /**
     * Checks if an item is touched by the user
     * If an item is touched, the select animation will be triggered
     */
    private void checkForItemTouch() {
        // Check for any touch events on scroller
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
            if(multiMode) {
                // Check if an item was touched by looping through each of the currently displayed item
                int breaker = currentItemIndex + maxDisplayedItems >= imageScrollerItems.size() ? imageScrollerItems.size() - currentItemIndex : maxDisplayedItems;
                for (int i = 0; i < breaker; i++) {
                    // Item touched
                    if(imageScrollerItems.get(currentItemIndex + i).getBound().contains((int) touchLocation.x, (int) touchLocation.y)) {
                        /* If a item has not been selected yet, set the selectedItemIndex to i
                               and start animation
                               else if a item has been selected, the item clicked here is the same as
                               the one selected, start animation */
                        if(!itemSelected) {
                            selectedItemIndex = currentItemIndex + i;
                            selectAnimationTriggered = true;
                            distanceMoved = 0;
                        }
                        else if (selectedItemIndex == currentItemIndex + i){
                            selectAnimationTriggered = true;
                            distanceMoved = 0;
                        }
                    }
                }
            } else {
                // Start animation if click is within the item's bounds
                if(imageScrollerItems.get(currentItemIndex).getBound().contains((int) touchLocation.x, (int) touchLocation.y)) {
                    selectedItemIndex = currentItemIndex;
                    selectAnimationTriggered = true;
                    distanceMoved = 0;
                }
            }
        }
    }

    /**
     * Checks for a touch event on the scroller and carries out
     * necessary actions depending on what is touched
     */
    private void checkForTouchEvent() {
        boolean leftPushed = pushButtonLeft.isPushTriggered();
        boolean rightPushed = pushButtonRight.isPushTriggered();

        // Check for input to determine if animation should be triggered to move the items
        if((leftPushed || rightPushed) && !scrollAnimationTriggered && !itemSelected && (nextItemIndex != -1)) {
            if(multiMode && maxDisplayedItems >= imageScrollerItems.size()) return;

            scrollAnimationTriggered = true;

            // Set direction to scroll images and find position of next item(s)
            if(leftPushed) {
                scrollDirection = true;
            }
            else {
                scrollDirection = false;
            }

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
        } else if (selectMode && (!scrollAnimationTriggered && !selectAnimationTriggered)){
            checkForItemTouch();
        }
    }

    /**
     * Used to move scroller and all contents
     * @param x amount to move x position by
     * @param y amount to move y position by
     */
    public void adjustPosition(float x, float y) {
        position.add(x, y);
        for (ImageScrollerItem imageScrollerItem : imageScrollerItems) {
            imageScrollerItem.position.add(x, y);
        }
        pushButtonLeft.position.add(x, y);
        pushButtonRight.position.add(x, y);
        selectBound.x += x;
        selectBound.y += y;
    }

    /**
     * Returns whether an animation is occuring
     * @return
     */
    public boolean isAnimating() {
        return scrollAnimationTriggered || selectAnimationTriggered;
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
        pushButtonLeft.update(elapsedTime);
        pushButtonRight.update(elapsedTime);

        if(imageScrollerItems.isEmpty()) return;

        // Checks if scroller has been touched and will carried out any
        // necessary actions depending on where is touched
        checkForTouchEvent();

        // Checks if scroll animation has been triggered and performs animation if so
        checkAndPerformScrollAnimation();

        // Checks if select animation has been triggered and performs animation if so
        checkAndPerformSelectAnimation();
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        super.draw(elapsedTime, graphics2D);

        if(multiMode) {
            // If a current item exists, draw any current items else return
            if(currentItemIndex == -1) return;

            // Determine how many current items to draw, then draw
            int breaker = currentItemIndex + maxDisplayedItems >= imageScrollerItems.size() ? imageScrollerItems.size() - currentItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                imageScrollerItems.get(currentItemIndex + i).draw(elapsedTime, graphics2D);
            }

            // Continue if scroll animation has been triggered else return
            if(!scrollAnimationTriggered) return;

            // Determine how many next items to draw, then draw
            breaker = nextItemIndex + maxDisplayedItems >= imageScrollerItems.size() ? imageScrollerItems.size() - nextItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                imageScrollerItems.get(nextItemIndex + i).draw(elapsedTime, graphics2D);
            }
        } else {
            // If current item exists draw else return
            if(currentItemIndex == -1) return;
            imageScrollerItems.get(currentItemIndex).draw(elapsedTime, graphics2D);

            // If a scroll animation has been triggered, draw next item
            if(!scrollAnimationTriggered) return;
            imageScrollerItems.get(nextItemIndex).draw(elapsedTime, graphics2D);
        }
    }

    /**
     * GETTERS AND SETTERS
     */

    public int getItemCount() {
        return imageScrollerItems.size();
    }

    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    public int getNextItemIndex() {
        return nextItemIndex;
    }

    public boolean getSelectMode() {
        return selectMode;
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

    public boolean isScrollAnimationTriggered() {
        return scrollAnimationTriggered;
    }

    public boolean isScrollDirection() {
        return scrollDirection;
    }

    public PushButton getPushButtonLeft() {
        return pushButtonLeft;
    }

    public PushButton getPushButtonRight() {
        return pushButtonRight;
    }

    public boolean isMultiMode() {
        return multiMode;
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

    public boolean isSelectMode() {
        return selectMode;
    }

    public boolean isItemSelected() {
        return itemSelected;
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public boolean isSelectAnimationTriggered() {
        return selectAnimationTriggered;
    }

    public boolean isSelectDirection() {
        return selectDirection;
    }

    public float getSelectDistance() {
        return selectDistance;
    }

    public Vector2 getTouchLocation() {
        return touchLocation;
    }

    public BoundingBox getSelectBound() {
        return selectBound;
    }

    public ArrayList<ImageScrollerItem> getImageScrollerItems() {
        return imageScrollerItems;
    }

    public void setScrollDirection(boolean scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    public int getMaxScrollerItems() { return maxScrollerItems; }

    public void setMaxScrollerItems(int maxScrollerItems) { this.maxScrollerItems = maxScrollerItems > 0 ? maxScrollerItems : 25; }
}