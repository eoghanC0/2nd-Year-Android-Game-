package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
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
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;

/**
 * Created by eimhin on 28/01/2018.
 */

/**
 * This class allows you to create a horizontally moving card scroller
 *
 * Clicking the left side of the scroller moves the card(s) right, displaying the previous card
 * Clicking the right side of the scroller moves the card(s) left, displaying the next card
 *
 * Images are automatically scaled to fit within the scroller
 *
 * User can toggle between single card and multi card mode
 *
 * - Single card
 *      Cards displayed one at a time
 *      Cards are scaled to fit the total height of the scroller
 *      Scroller cycles in a loop
 * - Multi card
 *      Displays multiple card at a time by using the full width of the scroller
 *      User chooses the maximum card height, allowing for more/less cards to be displayed
 *      Scroller cycles in a loop
 *
 * Default Settings:
 * - multiMode = false
 * - selectMode = false
 *
 * TODO: Create tests.
 * TODO: Animations not configured properly using frames, elapsedTime etc. Fix this.
 * TODO: Only draw cards within the bounds of the scroller
 */
public class HorizontalCardScroller extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * ArrayList containing all the scroller's items
     */
    private ArrayList<Card> cardScrollerItems = new ArrayList<Card>();

    /**
     * Base bitmap used to determine dimensions of cards
     * Uses img/CardFront.png
     */
    private Bitmap baseBitmap;

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
     * Booleans used for artificial trigger of push buttons
     */
    private boolean pushButtonLeftPush = false;
    private boolean pushButtonRightPush = false;

    /**
     * MULTI MODE VARIABLES
     */

    /**
     * Toggle to set multi bitmap mode
     * at a time
     * = = = = = = = = = = = = =
     *  * *  W A R N I N G  * *
     * = = = = = = = = = = = = =
     * This should only be used if the bitmaps all have the same dimensions
     */
    private boolean multiMode = false;

    /**
     * Maximum bitmaps that can be displayed
     */
    private int maxDisplayedItems = 0;

    /**
     * Spacing between bitmaps
     */
    private int maxItemSpacing = 0;

    /**
     * Absolute maximum number of bitmaps that can be displayed at a time
     */
    private final int MAX_DISPLAYED_ITEMS_ALLOWED = 15;

    /**
     * Dimensions of bitmaps
     */
    private Vector2 maxItemDimensions = new Vector2();

    /**
     * Positions of currently displayed bitmaps
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
     * Tells whether a card has been selected
     */
    private boolean itemSelected = false;

    /**
     * Stores the index of the item currently selected
     */
    private int selectedItemIndex;

    /**
     * Determines whether a card select animation is occuring
     */
    private boolean selectAnimationTriggered = false;

    /**
     * Direction in which card moves for select animation
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
     * Use to determine where the user is allowed to click on screen to move a card
     * that has been selected
     */
    private Vector2 selectDestination;

    /**
     * Determines whether an item move animation is occuring
     */
    private boolean cardMoveAnimationTriggered = false;

    /**
     * Scroll click bound
     * Until swiping to scroll functions, a temporary bound has to be created which is contained
     * between the buttons else, selecting a button on top of a bitmap will scroll and select
     */
    private BoundingBox selectBound = new BoundingBox();

    /**
     * Original position of moved card
     */
    private Vector2 movedCardOriginalPosition = new Vector2();

    /**
     * Determines whether a new item move animation is occuring
     */
    private boolean newCardMoveAnimationTriggered = false;

    /**
     * Distance to move new card
     */
    private float newMoveDistance = 0;

    /**
     * Determines whether to skip the new card move animation
     * for the particular chosen card
     */
    private boolean skipMoveNewCardAnimation = false;

    /**
     * Determines whether to reduce the current index
     * Used by new card move
     */
    private boolean reduceCurrentIndexAfterMoveNewCard = false;

    /**
     * Determines whether to automatically move scroller
     * Used by new card move
     */
    private boolean autoScroll = false;

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
    public HorizontalCardScroller(float x, float y, float width, float height, GameScreen gameScreen) {
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

        assetManager.loadAndAddBitmap("BaseBitmap", "img/CardFront.png");
        baseBitmap = assetManager.getBitmap("BaseBitmap");
        if(baseBitmap == null)
            Log.d("DEBUG", "HorizontalCardScroller: NO BASE BITMAP");

        selectDestination = new Vector2(100,50);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Test items
     */
    public void addTestData() {
        addScrollerItem("0", 100);
        addScrollerItem("1", 100);
        addScrollerItem("2", 100);
        addScrollerItem("3", 100);
        addScrollerItem("4", 100);
        addScrollerItem("5", 100);
        addScrollerItem("6", 100);
        addScrollerItem("7", 100);
        addScrollerItem("8", 100);
        addScrollerItem("9", 100);
        addScrollerItem("10", 100);
        addScrollerItem("11", 100);
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
        if(multiMode)
            selectMode = value;
        else
            Log.d("ERROR", "* * * Multi mode must be enabled to enable select mode * * *");
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
     * @param playerID
     * @param fitness
     */
    public void addScrollerItem(String playerID, int fitness) {
        if(playerID != null) {
            if(cardScrollerItems.size() == 0) currentItemIndex = 0;
            else if(cardScrollerItems.size() == 1) nextItemIndex = 1;

            Vector2 dimensions = getNewBitmapDimensions(baseBitmap, (int) mBound.getHeight(), true);
            cardScrollerItems.add(new Card(position.x, position.y,dimensions.y * 2, mGameScreen, playerID, fitness));
        }
    }

    /**
     * Checks if nextIndex exists
     * @return
     */
    private boolean checkDoesNextExist() {
        if(cardScrollerItems.size() > 1) return true;
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

        cardScrollerItems.get(nextItemIndex).position = nextItemPosition;
    }

    /**
     * Gets the next imageScrollerItem from ArrayList based on the direction
     */
    private void calculateNextSingleIndex() {
        if(cardScrollerItems.size() > 1) {
            int directionInt = scrollDirection ? -1 : 1;
            nextItemIndex = (currentItemIndex + cardScrollerItems.size() + directionInt) % cardScrollerItems.size();
        }
    }

    /**
     * * * * * * * * * * *
     * MULTI MODE METHODS
     * * * * * * * * * * *
     */

    /**
     * Toggles scroller from single card to multi card mode
     * @param value
     * @param heightOccupyPercentage
     */
    public void setMultiMode(boolean value, int heightOccupyPercentage) {
        if(!(cardScrollerItems.size() > 0)) {
            Log.e("ERROR", "* * * You cannot set multi card mode unless there is at least 1 card in scroller * * *");
        } else if(!value) {
            multiMode = false;
            currentItemIndex = 0;

            cardScrollerItems.get(currentItemIndex).position = new Vector2(position);
            for (Card i : cardScrollerItems) {
                Vector2 dimensions = getNewBitmapDimensions(i.getBitmap(), (int) mBound.getHeight(), true);
                //i.setWidthAndHeight(dimensions.x * 2, dimensions.y * 2);
                i.setHeight((int) dimensions.y * 2);
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
            nextItemIndex = currentItemIndex - maxDisplayedItems < 0 ? cardScrollerItems.size() - (cardScrollerItems.size() % maxDisplayedItems) : currentItemIndex - maxDisplayedItems;
            nextItemIndex = nextItemIndex == cardScrollerItems.size() ? cardScrollerItems.size() - maxDisplayedItems : nextItemIndex;
        } else {
            nextItemIndex = currentItemIndex + maxDisplayedItems > cardScrollerItems.size() ? 0 : currentItemIndex + maxDisplayedItems;
            nextItemIndex = nextItemIndex == cardScrollerItems.size() ?  0 : nextItemIndex;
        }
    }

    /**
     * Calculates the number of cards that can be displayed
     * @param heightOccupyPercentage The percentage of the scrollers height the image should occupy
     */
    public void calculateMultiItemsDisplayed(float heightOccupyPercentage) {
        if(!multiMode || !(cardScrollerItems.size() > 0)) return;
        // Ensure heightOccupyPercentage is within bounds then divide by 100, else set to 1 (100%)
        if(heightOccupyPercentage <= 0 || heightOccupyPercentage > 100)
            heightOccupyPercentage = 1;
        else
            heightOccupyPercentage /= 100;

        // Find the maximum height allowed for the card
        int maxHeight = (int) (mBound.getHeight() * heightOccupyPercentage);
        // Rescale card dimensions using the maxHeight
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
        for (Card i : cardScrollerItems) {
            i.setHeight((int) scaledBitmapDimensions.y * 2);
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
        cardScrollerItems.get(currentItemIndex).position = new Vector2(mBound.getLeft() + maxItemSpacing + cardScrollerItems.get(0).getBound().halfWidth, position.y);

        // Set positions of any other current items
        int breaker = currentItemIndex + maxDisplayedItems >= cardScrollerItems.size() ? cardScrollerItems.size() - currentItemIndex : maxDisplayedItems;
        for(int i = 0; i < breaker; i++) {
            cardScrollerItems.get(currentItemIndex + i).position = new Vector2(cardScrollerItems.get(currentItemIndex).position.x + (i * (maxItemSpacing + (maxItemDimensions.x * 2))), position.y);
        }
    }

    /**
     * Calculates the positions of the next cards based on the direction the scroller
     * is being moved in
     */
    public void calculateNextMultiVectors() {
        if(!multiMode || cardScrollerItems.size() <= 1) return;
        // Get starting position of next items based on the direction the scroller is going to move
        float startPosition = 0;
        startPosition = scrollDirection ? mBound.getLeft() - mBound.getWidth() : mBound.getRight();

        // Set the new item index
        calculateNextMultiIndex();

        // Set  position of first next item
        cardScrollerItems.get(nextItemIndex).position = new Vector2(startPosition + maxItemSpacing + maxItemDimensions.x, position.y);

        // Set positions of any other next items
        int breaker = nextItemIndex + maxDisplayedItems >= cardScrollerItems.size() ? cardScrollerItems.size() - nextItemIndex : maxDisplayedItems;
        for(int i = 1; i < breaker; i++) {
            cardScrollerItems.get(nextItemIndex + i).position = new Vector2(cardScrollerItems.get(nextItemIndex).position.x + (i * (maxItemSpacing + (maxItemDimensions.x * 2))), position.y);
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

        // Move current card and next card
        float moveBy = 0;
        if(!scrollDirection) moveBy = -1 * itemDistance * 0.05f;
        else moveBy = itemDistance * 0.05f;

        // Branch based on whether multi mode or single mode is enabled
        if(multiMode) {
            // Move any currently displayed items
            int breaker = currentItemIndex + maxDisplayedItems >= cardScrollerItems.size() ? cardScrollerItems.size() - currentItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                cardScrollerItems.get(i + currentItemIndex).position.add(moveBy, 0);
            }

            // Move any next items
            breaker = nextItemIndex + maxDisplayedItems >= cardScrollerItems.size() ? cardScrollerItems.size() - nextItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                cardScrollerItems.get(nextItemIndex + i).position.add(moveBy, 0);
            }
        } else {
            // Draw current item
            cardScrollerItems.get(currentItemIndex).position.add(moveBy, 0);
            if(checkDoesNextExist())
                cardScrollerItems.get(nextItemIndex).position.add(moveBy, 0);
        }
        // Carry out completing steps if the move distance has been reached
        if(addAndCheckDistanceMoved(moveBy, false)) {
            // Stop animation
            scrollAnimationTriggered = false;

            // Branch based on whether multi mode or single mode is enabled
            if(multiMode) {
                // Negate currentIndex
                if(reduceCurrentIndexAfterMoveNewCard) {
                    currentItemIndex -= maxDisplayedItems;
                    reduceCurrentIndexAfterMoveNewCard = false;
                }

                // Calculate the vectors for the new current items
                calculateCurrentMultiVectors();
            } else {
                // Set position of new current item to scroller position
                cardScrollerItems.get(currentItemIndex).position = new Vector2(position);
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

        cardScrollerItems.get(selectedItemIndex).position.add(0, moveBy);

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
     * Executes the updating of positions of items for card move animation if
     * cardMoveAnimationTriggered is true and an item is selected
     */
    private void checkAndPerformMoveCardAnimation() {
        if(!(cardMoveAnimationTriggered && itemSelected)) return;

        // Move current item and next item
        Vector2 moveVector = new Vector2((selectDestination.x - cardScrollerItems.get(selectedItemIndex).position.x) * 0.4f, (selectDestination.y - cardScrollerItems.get(selectedItemIndex).position.y) * 0.4f) ;

        cardScrollerItems.get(selectedItemIndex).position.add(moveVector);

        // Carry out completing steps if the move distance has been reached
        if(checkIfSelectedCardMovedToDest()) {
            // Stop animation
            cardMoveAnimationTriggered = false;
            // Toggle item selected and selectDirection
            itemSelected = !itemSelected;
            selectDirection = !selectDirection;

            calculateForMovingNewCard();
            newCardMoveAnimationTriggered = true;
        }
    }

    private void checkAndPerformMoveNewCardAnimation() {
        if(!(newCardMoveAnimationTriggered)) return;

        if(selectedItemIndex == -1) return;

        if(skipMoveNewCardAnimation) {
            skipMoveNewCardAnimation = false;
            newCardMoveAnimationTriggered = false;

            // Calculate new positions of displayed cards
            if(currentItemIndex == cardScrollerItems.size()){
                reduceCurrentIndexAfterMoveNewCard = true;
                pushButtonLeftPush = true;
                itemDistance = mBound.getWidth();
                autoScroll = true;
            }
            else
                calculateCurrentMultiVectors();

            newCardMoveAnimationTriggered = false;
            itemSelected = false;
            return;
        }

        // Move current item and next item
        float moveBy = -1 * newMoveDistance * 0.1f;

        cardScrollerItems.get(selectedItemIndex).position.add(moveBy, 0);

        distanceMoved += Math.abs(moveBy);

        // Carry out completing steps if the move distance has been reached
        if(distanceMoved >= newMoveDistance) {
            // Stop animation
            newCardMoveAnimationTriggered = false;

            // Set position of new card to old position
            cardScrollerItems.get(selectedItemIndex).position = movedCardOriginalPosition;

            // Remove the old now moved off scroller card
            //cardScrollerItems.remove(cardScrollerItems.size() - 1);

            // Reset distance moved
            distanceMoved = 0;

            if(cardScrollerItems.size() == 0) setMultiMode(false, 100);

            calculateCurrentMultiVectors();
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
                int breaker = currentItemIndex + maxDisplayedItems >= cardScrollerItems.size() ? cardScrollerItems.size() - currentItemIndex : maxDisplayedItems;
                for (int i = 0; i < breaker; i++) {
                    // Card touched
                    if(cardScrollerItems.get(currentItemIndex + i).getBound().contains((int) touchLocation.x, (int) touchLocation.y)) {
                            /* If a card has not been selected yet, set the selectedItemIndex to i
                               and start animation
                               else if a card has been selected, the card clicked here is the same as
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
                // Start animation if click is within the card's bounds
                if(cardScrollerItems.get(currentItemIndex).getBound().contains((int) touchLocation.x, (int) touchLocation.y)) {
                    selectedItemIndex = currentItemIndex;
                    selectAnimationTriggered = true;
                    distanceMoved = 0;
                }
            }
        } else {
            if (itemSelected) {
                if(checkIfTouchInArea(touchLocation, selectDestination, 50) && !cardMoveAnimationTriggered) {
                    cardMoveAnimationTriggered = true;
                    movedCardOriginalPosition = new Vector2(cardScrollerItems.get(selectedItemIndex).position);
                }
            }
        }
    }

    /**
     * Checks for a touch event on the scroller and carries out
     * necessary actions depending on what is touched
     */
    private void checkForTouchEvent() {
        boolean leftPushed = pushButtonLeft.isPushTriggered() || pushButtonLeftPush;
        boolean rightPushed = pushButtonRight.isPushTriggered() || pushButtonRightPush;

        // Check for input to determine if animation should be triggered to move the items
        if((leftPushed || rightPushed) && !scrollAnimationTriggered && !itemSelected && (nextItemIndex != -1)) {
            // Reset pushButton artificial booleans
            pushButtonLeftPush = false;
            pushButtonRightPush = false;

            if(multiMode && maxDisplayedItems >= cardScrollerItems.size() && !autoScroll) return;
            autoScroll = false;
            // Trigger scroll animation
            scrollAnimationTriggered = true;

            // Set direction to scroll images and vector of next card
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
     * Check if a touch is within the general area of a certain location
     * @param userTouchLocation
     * @param touchLocation
     * @param deviation
     */
    private boolean checkIfTouchInArea(Vector2 userTouchLocation, Vector2 touchLocation, float deviation) {
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
     * Checks whether the moving card has reached it's destination
     * @return
     */
    private boolean checkIfSelectedCardMovedToDest() {
        if(!(selectedItemIndex < 0 || itemSelected)) return false;

        if(Math.abs(cardScrollerItems.get(selectedItemIndex).position.x - selectDestination.x) < 15 && Math.abs(cardScrollerItems.get(selectedItemIndex).position.y - selectDestination.y) < 15) {
            cardScrollerItems.get(selectedItemIndex).position = new Vector2(selectDestination);
            return true;
        }

        return false;
    }

    /**
     * Performs necessary calculations after a card has been moved
     */
    private void calculateForMovingNewCard() {
        if(currentItemIndex == cardScrollerItems.size() - 1) {
            cardScrollerItems.remove(cardScrollerItems.size() - 1);
            skipMoveNewCardAnimation = true;
            return;
        }

        if(selectedItemIndex == cardScrollerItems.size() - 1) {
            cardScrollerItems.remove(cardScrollerItems.size() - 1);
            skipMoveNewCardAnimation = true;
            return;
        }

        cardScrollerItems.set(selectedItemIndex, new Card(position.x, position.y,maxItemDimensions.y * 2f, mGameScreen, cardScrollerItems.get(cardScrollerItems.size() - 1).getPlayerID(), 100));
        cardScrollerItems.get(selectedItemIndex).position = new Vector2(movedCardOriginalPosition);

        // Set position of new card to that of the original position
        // else if card is off screen set to position of original moved card + mBound.getWidth()
        if(cardScrollerItems.size() - 1 < currentItemIndex + maxDisplayedItems) {
            Vector2 positionOne =  cardScrollerItems.get(selectedItemIndex).position;
            Vector2 positionTwo =  cardScrollerItems.get(cardScrollerItems.size() - 1).position;
            cardScrollerItems.get(selectedItemIndex).position = new Vector2(positionTwo);

            // Use pythagorean theorem to calculate length between position of original moved card
            // and the card that is going to be moved
            double innerCalc = Math.pow((positionTwo.x - positionOne.x), 2) + Math.pow((positionTwo.y - positionOne.y), 2);
            newMoveDistance = (float) Math.sqrt(innerCalc);
        } else {
            cardScrollerItems.get(selectedItemIndex).position.add(mBound.getWidth(), 0);
            newMoveDistance = mBound.getWidth();
        }

        cardScrollerItems.remove(cardScrollerItems.size() - 1);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
        pushButtonLeft.update(elapsedTime);
        pushButtonRight.update(elapsedTime);

        if(cardScrollerItems.isEmpty()) return;

        // Checks if scroller has been touched and will carried out any
        // necessary actions depending on where is touched
        checkForTouchEvent();

        // Checks if scroll animation has been triggered and performs animation if so
        checkAndPerformScrollAnimation();

        // Checks if select animation has been triggered and performs animation if so
        checkAndPerformSelectAnimation();

        // Checks if move animation has been triggered and performs animation if so
        checkAndPerformMoveCardAnimation();

        // Checks if move new card animation has been triggered and performs animation if so
        checkAndPerformMoveNewCardAnimation();
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        super.draw(elapsedTime, graphics2D);

        if(multiMode) {
            // If a current item exists, draw any current items else return
            if(currentItemIndex == -1) return;

            // Determine how many current items to draw, then draw
            int breaker = currentItemIndex + maxDisplayedItems >= cardScrollerItems.size() ? cardScrollerItems.size() - currentItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                cardScrollerItems.get(currentItemIndex + i).draw(elapsedTime, graphics2D);
            }

            // Continue if scroll animation has been triggered else return
            if(!scrollAnimationTriggered) return;

            // Determine how many next items to draw, then draw
            breaker = nextItemIndex + maxDisplayedItems >= cardScrollerItems.size() ? cardScrollerItems.size() - nextItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                cardScrollerItems.get(nextItemIndex + i).draw(elapsedTime, graphics2D);
            }
        } else {
            // If current card exists draw else return
            if(currentItemIndex == -1) return;
            cardScrollerItems.get(currentItemIndex).draw(elapsedTime, graphics2D);

            // If a scroll animation has been triggered, draw next item
            if(!scrollAnimationTriggered) return;
            cardScrollerItems.get(nextItemIndex).draw(elapsedTime, graphics2D);
        }
    }

    /**
     * GETTERS AND SETTERS
     */

    public int getItemCount() {
        return cardScrollerItems.size();
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

    public ArrayList<Card> getCardScrollerItems() {
        return cardScrollerItems;
    }

    public void setScrollDirection(boolean scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    public void setSelectDestination(Vector2 selectDestination) {
        this.selectDestination = selectDestination;
    }
}