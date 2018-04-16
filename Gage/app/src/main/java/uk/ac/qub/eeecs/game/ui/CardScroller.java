package uk.ac.qub.eeecs.game.ui;

import android.util.Log;

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
import uk.ac.qub.eeecs.game.objects.Card;

/**
 * Created by eimhin on 28/01/2018.
 */

/**
 * This class allows you to create a horizontally moving card scroller
 *
 * Swiping left-right on the scroller moves the card(s) right, displaying the previous card(s)
 * Swiping right-left on the scroller moves the card(s) left, displaying the next card(s)
 *
 * Cards are automatically scaled to fit within the scroller
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
 */
public class CardScroller extends Scroller<Card> {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /***************************
     * Interactivity Properties
     ***************************/

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
     * Use to determine where the user is allowed to click on screen to move a card
     * that has been selected
     */
    private ArrayList<BoundingBox> selectDestinations = new ArrayList<BoundingBox>();

    /**
     * Current select destination
     */
    private BoundingBox currentSelectDestination = new BoundingBox();

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
     * Used for the card drag methods
     */
    private boolean touchDown = false;

    /**
     * Stores the time of the last TOUCH_DOWN event on the selectBound
     */
    private long touchDownTime = 0;

    /**
     * Stores positino of dragged card before it was dragged
     */
    private Vector2 draggedCardOriginalPosition = new Vector2();

    /**
     * Stores whether a removed card is ready to be retrieved by an external call
     */
    private boolean removedCardReady = false;

    /**
     * Removed card
     */
    private Card removedCard;

    /**
     * Bound of removed card
     */
    private BoundingBox removedCardBound = null;

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
    public CardScroller(float x, float y, float width, float height, GameScreen gameScreen) {
        super(x, y, width > 0 ? width : -width, height > 0 ? height : -height, gameScreen);

        // Set BoundingBox of card selection area
        selectBound = new BoundingBox();
        selectBound.x = position.x;
        selectBound.y = position.y;
        selectBound.halfWidth = mBound.getWidth() * 0.4f;
        selectBound.halfHeight = mBound.getHeight();

        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("BaseBitmap", "img/CardFront.png");
        baseBitmap = assetManager.getBitmap("BaseBitmap");
        if(baseBitmap == null) {
            Log.d("ERROR", "CardScroller: BASE BITMAP NOT FOUND. THIS IS A PROBLEM.");
            baseBitmap = assetManager.getBitmap("Empty");
        }

        scrollerItems = new ArrayList<>();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void addScrollerItem(GameObject gameObject) {
        if(gameObject instanceof Card) {
            addScrollerItem((Card) gameObject);
        }
    }

    /**
     * Adds item to scroller
     *
     * @param card Card to add to scroller items
     */
    public void addScrollerItem(Card card) {
        if(card != null && scrollerItems.size() <= maxScrollerItems && !isAnimating()) {
            if(scrollerItems.size() == 0) currentItemIndex = 0;
            else if(scrollerItems.size() == 1) nextItemIndex = 1;
            if(multiMode) {
                card.setHeight((int) maxItemDimensions.y * 2);
            } else {
                Vector2 dimensions = getNewBitmapDimensions(baseBitmap, (int) mBound.getHeight(), true);
                card.setHeight((int) dimensions.y * 2);
            }

            scrollerItems.add(card);

            // Check if card should be displayed immediately
            int relativePosition = currentItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - currentItemIndex - 1: -1;
            if(multiMode && relativePosition != -1) {
                calculateCurrentMultiVectors();
                scrollerItems.get(scrollerItems.size() - 1).position = new Vector2(scrollerItems.get(currentItemIndex).position.x + (relativePosition * (maxItemSpacing + (maxItemDimensions.x * 2))), position.y);
            } else
                scrollerItems.get(scrollerItems.size() - 1).position = new Vector2(position);

            // Trigger flag to check page icons
            checkPageChange = true;

            Log.d("DEBUG", "addScrollerItem: Added card " + card.toString());
        }
    }

    /**
     * Sets the value of selectMode
     *
     * @param selectMode boolean for toggling select mode on or off
     */
    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Multi Mode Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Executes the updating of positions of items for card move animation if
     * cardMoveAnimationTriggered is true and an item is selected
     */
    private void checkAndPerformMoveCardAnimation() {
        if(!(cardMoveAnimationTriggered && itemSelected)) return;

        // Move current item and next item
        Vector2 moveVector = new Vector2((currentSelectDestination.x - scrollerItems.get(selectedItemIndex).position.x) * 0.4f, (currentSelectDestination.y - scrollerItems.get(selectedItemIndex).position.y) * 0.4f);

        scrollerItems.get(selectedItemIndex).position.add(moveVector);

        // Carry out completing steps if the move distance has been reached
        if(checkIfSelectedCardMovedToDest()) {
            // Stop animation
            cardMoveAnimationTriggered = false;
            // Toggle item selected
            itemSelected = !itemSelected;

            calculateForMovingNewCard();
            newCardMoveAnimationTriggered = true;
        }
    }

    /**
     * Executes the updating of a new card after a card has been moved from the scroller
     * if newCardMoveAnimationTriggered is true and an item is selected
     */
    private void checkAndPerformMoveNewCardAnimation() {
        if(!(newCardMoveAnimationTriggered)) return;

        if(selectedItemIndex == -1) return;

        if(skipMoveNewCardAnimation) {
            skipMoveNewCardAnimation = false;
            newCardMoveAnimationTriggered = false;

            // Calculate new positions of displayed cards
            if(currentItemIndex == scrollerItems.size() && scrollerItems.size() != 0){
                scrollAnimationTriggered = true;
                scrollDirection = true;
                itemDistance = mBound.getWidth();
                nextItemIndex = scrollerItems.size() - maxDisplayedItems;
            }
            else
                calculateCurrentMultiVectors();

            newCardMoveAnimationTriggered = false;
            itemSelected = false;
            return;
        }

        // Move current item and next item
        float moveBy = -1 * newMoveDistance * 0.1f;

        if(useSimulatedTouchEvents) moveBy = -1 * newMoveDistance;

        scrollerItems.get(selectedItemIndex).position.add(moveBy, 0);

        distanceMoved += Math.abs(moveBy);

        // Carry out completing steps if the move distance has been reached
        if(distanceMoved >= newMoveDistance) {
            // Stop animation
            newCardMoveAnimationTriggered = false;

            // Set position of new card to old position
            scrollerItems.get(selectedItemIndex).position = movedCardOriginalPosition;

            // Reset distance moved
            distanceMoved = 0;

            if(scrollerItems.size() == 0) setMultiMode(false, 100);
        }
    }

    /**
     * Checks whether the moving card has reached it's destination
     *
     * @return boolean as true if card at scrollerItems[selectedItemIndex] has position within
     *         15 pixels of currentSelectDestination
     */
    private boolean checkIfSelectedCardMovedToDest() {
        if(!(selectedItemIndex < 0 || itemSelected)) return false;

        if(Math.abs(scrollerItems.get(selectedItemIndex).position.x - currentSelectDestination.x) < 15 && Math.abs(scrollerItems.get(selectedItemIndex).position.y - currentSelectDestination.y) < 15) {
            scrollerItems.get(selectedItemIndex).position = new Vector2(currentSelectDestination.x, currentSelectDestination.y);
            return true;
        }

        return false;
    }

    /**
     * Performs necessary calculations after a card has been moved
     */
    private void calculateForMovingNewCard() {
        if(currentItemIndex == scrollerItems.size() - 1) {
            scrollerItems.remove(scrollerItems.size() - 1);
            skipMoveNewCardAnimation = true;
            return;
        }

        if(selectedItemIndex == scrollerItems.size() - 1) {
            scrollerItems.remove(scrollerItems.size() - 1);
            skipMoveNewCardAnimation = true;
            return;
        }

        Card temp = new Card(mGameScreen, scrollerItems.get(scrollerItems.size() - 1).getPlayerID(), 100);
        temp.setHeight((int) maxItemDimensions.y * 2);
        scrollerItems.set(selectedItemIndex, temp);
        scrollerItems.get(selectedItemIndex).position = new Vector2(movedCardOriginalPosition);

        // Set position of new card to that of the original position
        // else if card is off screen set to position of original moved card + mBound.getWidth()
        if(scrollerItems.size() - 1 < currentItemIndex + maxDisplayedItems) {
            Vector2 positionOne =  scrollerItems.get(selectedItemIndex).position;
            Vector2 positionTwo =  scrollerItems.get(scrollerItems.size() - 1).position;
            scrollerItems.get(selectedItemIndex).position = new Vector2(positionTwo);

            // Use pythagorean theorem to calculate length between position of original moved card
            // and the card that is going to be moved
            double innerCalc = Math.pow((positionTwo.x - positionOne.x), 2) + Math.pow((positionTwo.y - positionOne.y), 2);
            newMoveDistance = (float) Math.sqrt(innerCalc);
        } else {
            scrollerItems.get(selectedItemIndex).position.add(mBound.getWidth(), 0);
            newMoveDistance = mBound.getWidth();
        }

        scrollerItems.remove(scrollerItems.size() - 1);
    }

    /**
     * Adds a destination which the user can select to move an item
     *
     * @param destination BoundingBox used for dragging items into for selection
     */
    public void addSelectDestination(BoundingBox destination) {
        if(destination != null) selectDestinations.add(destination);
    }

    /**
     * Removes a select destination from selectDestinations ArrayList
     *
     * @param index Index of the select destination to remove from selectDestinations
     */
    public void removeSelectDestination(int index) {
        if(isAnimating() || index < 0 || index >= selectDestinations.size()) return;
        selectDestinations.remove(index);
    }

    /**
     * Returns whether an animation is occuring
     *
     * @return boolean as true if any animation has been triggered, else false
     */
    @Override
    public boolean isAnimating() {
        return scrollAnimationTriggered || cardMoveAnimationTriggered || newCardMoveAnimationTriggered;
    }

    /**
     * Checks for a touch event on a card and moves card
     * Card is removed if a TOUCH_UP is detected in a select destination
     */
    private void checkAndPerformDragCard() {
        if(!selectMode || scrollerItems.isEmpty() || isAnimating()) { return; }

        List<TouchEvent> touchEvents;
        if(!useSimulatedTouchEvents) touchEvents = mGameScreen.getGame().getInput().getTouchEvents();
        else touchEvents = simulatedTouchEvents;

        //Consider all buffered touch events
        for (TouchEvent t : touchEvents) {
            // If there is no touch down yet, check for a touch down
            if(!touchDown) {
                // Check if touch event is a touch down
                if (t.type == TouchEvent.TOUCH_DOWN) {
                    // Set touchDownTime
                    touchDownTime = System.nanoTime();
                    // Breaker to determine the amount of cards to iterate through while checking check location
                    int breaker = currentItemIndex + 1 >= scrollerItems.size() ? 0 : currentItemIndex + 1;
                    if(multiMode)
                        breaker = currentItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - currentItemIndex : maxDisplayedItems;

                    // Check if the touch location is within the bounds of any displayed cards
                    for (int i = 0; i < breaker; i++) {
                        if(checkIfTouchInArea(new Vector2(t.x, t.y), scrollerItems.get(currentItemIndex + i).getBound())) {
                            selectedItemIndex = currentItemIndex + i;
                            touchDown = true;
                            draggedCardOriginalPosition = new Vector2(scrollerItems.get(selectedItemIndex).position.x, scrollerItems.get(selectedItemIndex).position.y);
                            itemSelected = true;
                        }
                    }
                } else // No touch down
                    continue;
            }

            // If touch event is a drag event, modify position of card
            if (t.type == TouchEvent.TOUCH_DRAGGED && touchDown) {
                if(System.nanoTime() - touchDownTime > 250000000) {
                    scrollEnabled = false;
                    if (!Float.isNaN(t.x)) {
                        scrollerItems.get(selectedItemIndex).position.x = t.x;
                        scrollerItems.get(selectedItemIndex).position.y = t.y;
                    }
                }
            }

            // If touch event is a touch up event, check if location is within a select destination and remove card
            // else return card to original position
            if (t.type == TouchEvent.TOUCH_UP) {
                touchDown = false;

                // For each select destination, check if touch up is within the bounds of the destination BoundingBox
                boolean inDestination = false;
                for (BoundingBox selectDestination : selectDestinations) {
                    if(checkIfTouchInArea(scrollerItems.get(selectedItemIndex).position, selectDestination) && !cardMoveAnimationTriggered) {
                        // Trigger flag to check page icons
                        checkPageChange = true;

                        currentSelectDestination = selectDestination;
                        cardMoveAnimationTriggered = true;
                        movedCardOriginalPosition = draggedCardOriginalPosition;
                        scrollerItems.get(selectedItemIndex).position = new Vector2(selectDestination.x, selectDestination.y);
                        removedCard = scrollerItems.get(selectedItemIndex);
                        removedCardBound = selectDestination;
                        removedCardReady = true;
                        inDestination = true;
                        break;
                    }
                }
                if(!inDestination)
                    scrollerItems.get(selectedItemIndex).position = new Vector2(draggedCardOriginalPosition);

                scrollEnabled = true;
            }
        }
    }

    /**
     * Returns whether there is a removed card ready to be retrieved
     *
     * @return boolean containing value of removedCardReady
     */
    public boolean isRemovedCardReady() {
        return removedCardReady;
    }

    /**
     * Returns the removed card
     *
     * @return Card that has been removed from scroller if removedCardReady flag is true
     */
    public Card getRemovedCard() {
        if(removedCardReady) {
            removedCardReady = false;
            return removedCard;
        }
        else return null;
    }

    /*
     * Returns BoundingBox containing bound of removed card
     *
     * @return BoundingBox of removedCard if removedCardReady is true
     */
    public BoundingBox getRemovedCardBound() {
        if (removedCardReady) return removedCardBound;
        return null;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Update and Draw
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);

        // Checks if a card has been dragged/released and will carry out any
        // necessary actions
        checkAndPerformDragCard();

        // Checks if move animation has been triggered and performs animation if so
        checkAndPerformMoveCardAnimation();

        // Checks if move new card animation has been triggered and performs animation if so
        checkAndPerformMoveNewCardAnimation();
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        super.draw(elapsedTime, graphics2D);

        if(multiMode) {
            // If a current item exists, draw any current items else return
            if(currentItemIndex == -1) return;

            drawPageIcons(graphics2D);

            // Determine how many current items to draw, then draw
            int breaker = currentItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - currentItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                if(touchDown && currentItemIndex + i == selectedItemIndex) continue;
                scrollerItems.get(currentItemIndex + i).draw(elapsedTime, graphics2D);
            }

            // Draw selectedItem so that it renders above all other cards
            if(touchDown && selectedItemIndex >= 0)
                scrollerItems.get(selectedItemIndex).draw(elapsedTime, graphics2D);

            // Continue if scroll animation has been triggered else return
            if(!scrollAnimationTriggered) return;

            // Determine how many next items to draw, then draw
            breaker = nextItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - nextItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                scrollerItems.get(nextItemIndex + i).draw(elapsedTime, graphics2D);
            }
        } else {
            // If current card exists draw else return
            if(currentItemIndex == -1) return;
            scrollerItems.get(currentItemIndex).draw(elapsedTime, graphics2D);

            // If a scroll animation has been triggered, draw next item
            if(!scrollAnimationTriggered) return;
            scrollerItems.get(nextItemIndex).draw(elapsedTime, graphics2D);
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Testing
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void setUseSimulatedTouchEvents(boolean useSimulatedTouchEvents) {
        super.setUseSimulatedTouchEvents(useSimulatedTouchEvents);

        for (Card card : scrollerItems) {
            card.setUseSimulatedTouchEvents(useSimulatedTouchEvents);
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

    // /////////////////////////////////////////////////////////////////////////
    // Setters
    // /////////////////////////////////////////////////////////////////////////

    public void setScrollDirection(boolean scrollDirection) { this.scrollDirection = scrollDirection; }

    public int getMaxScrollerItems() { return maxScrollerItems; }

    public void setMaxScrollerItems(int maxScrollerItems) { this.maxScrollerItems = maxScrollerItems > 0 ? maxScrollerItems : 25; }

    public ArrayList<BoundingBox> getSelectDestinations() {
        return selectDestinations;
    }

    public void setSelectDestinations(ArrayList<BoundingBox> selectDestinations) { this.selectDestinations = selectDestinations; }

    public void setTouchDownTime(long touchDownTime) {
        this.touchDownTime = touchDownTime;
    }
}