package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.ui.Toggle;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;

/**
 * Created by stephenmcveigh on 08/02/2018.
 */

public class SquadSelectionPane extends GameObject {
    //////////////////////////////////////////////
    //  Constants
    //////////////////////////////////////////////
    private final float SIDE_BAR_COVERAGE = 0.1f;
    private final float TOGGLE_BITMAP_ASPECT_RATIO = 72f/168f;
    private final int LISTBOX_ANIMATION_LENGTH = 8;

    //////////////////////////////////////////////
    //  Properties
    //////////////////////////////////////////////
    /**
     * UI Uses
     */
    private ListBox formationsListBox;
    private HorizontalCardScroller cardScroller;
    private Toggle showFormationsToggle;
    private PushButton nextAreaButton;
    private PushButton previousAreaButton;

    /**
     * Asset Store
     */
    private AssetStore assetManager = mGameScreen.getGame().getAssetManager();

    /**
     * Properties Used for Animation
     */
    private boolean listBoxMoving = false;
    private int listBoxAnimationCounter = 0;
    private float openListBoxPositionY;

    /**
     * The current selection Area :
     * 0 = Forwards
     * 1 = Midfield
     * 2 = Defence
     * 3 = Goalkeeper
     */
    private int currentSelectionArea = 0;

    /**
     * The bitmap of the pitch depending on which section is currently selected
     */
    private Bitmap pitchStateBitmap;

    /**
     * The 11 card holders for the 11 players in the squad
     */
    private CardHolder[] squadSelectionHolders = new CardHolder[11];

    /**
     * The bounds of the placeholders which currently allow drag and drop
     */
    private ArrayList<BoundingBox> availableDropAreas = new ArrayList<>();

    /**
     * Properties for drag and drop
     */
    private boolean touchDown = false;
    private int selectedItemIndex = 0;
    private Vector2 draggedCardOriginalPosition = new Vector2();

    /**
     * Properties to keep track of which placeholders are currently displayed on screen
     */
    private int shownPlaceholdersStartIndex;
    private int shownPlaceholdersEndIndex;

    /**
     * Properties to hold the selected formation and the number of players in each of the 4 sections of the pitch
     */
    private String formationString = "";
    private int[] numberOfCardsOnLevel = new int[4];

    //////////////////////////////////////////////
    //  Constructors
    //////////////////////////////////////////////
    public SquadSelectionPane(GameScreen gameScreen) {
        super(gameScreen.getGame().getScreenWidth()/2, gameScreen.getGame().getScreenHeight()/2, gameScreen.getGame().getScreenWidth(), gameScreen.getGame().getScreenHeight(), null, gameScreen);
        loadAssets();
        pitchStateBitmap = assetManager.getBitmap("Pitch_Top");

        //Set up the UI Elements of the SelectionPane
        cardScroller = new HorizontalCardScroller(gameScreen.getGame().getScreenWidth()/2, gameScreen.getGame().getScreenHeight() * 0.25f, gameScreen.getGame().getScreenWidth(), gameScreen.getGame().getScreenHeight()/2, gameScreen);
        cardScroller.addTestData();
        cardScroller.setMultiMode(true, 80);
        cardScroller.setSelectMode(true);
        showFormationsToggle = new Toggle( mBound.getRight() - mBound.getWidth() * (SIDE_BAR_COVERAGE/2),mBound.getBottom() + mBound.getHeight()*7/12,
                mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f) * (TOGGLE_BITMAP_ASPECT_RATIO),
                "ToggleButton_Off", "ToggleButton_On", gameScreen);
        previousAreaButton = new PushButton(mBound.getRight() - mBound.getWidth() * (SIDE_BAR_COVERAGE/2), mBound.getBottom() + mBound.getHeight()*9/12,
                mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), "UpArrow", "UpArrow_Pushed", gameScreen);
        nextAreaButton = new PushButton(mBound.getRight() - mBound.getWidth() * (SIDE_BAR_COVERAGE/2), mBound.getBottom() + mBound.getHeight()*11/12,
                mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), "DownArrow", "DownArrow_Pushed", gameScreen);
        openListBoxPositionY = position.y + mBound.getHeight()*3/12;
        formationsListBox = new ListBox(position.x, openListBoxPositionY, mBound.getWidth()/1.5f, mBound.getHeight()/2, gameScreen);
        setUpFormationsListBox();
        initializeCardHolders();
    }

    //////////////////////////////////////////////
    //  Methods
    //////////////////////////////////////////////
    /**
     * Creates new CardHolders
     */
    private void initializeCardHolders() {
        for (int i = 0; i < squadSelectionHolders.length; i++) {
            squadSelectionHolders[i] = new CardHolder(mGameScreen);
            squadSelectionHolders[i].setHeight((int) (mBound.getHeight()*1/3));
        }
    }

    /**
     * Sets up the formations list box with the required formations
     * and also sets the position of the list box off-screen.
     */
    private void setUpFormationsListBox() {
        formationsListBox.setPosition(formationsListBox.position.x, formationsListBox.position.y + formationsListBox.getBound().getHeight());
        formationsListBox.addItem("3-4-3");
        formationsListBox.addItem("3-5-2");
        formationsListBox.addItem("4-3-3");
        formationsListBox.addItem("4-4-2");
        formationsListBox.addItem("4-5-1");
        formationsListBox.addItem("5-3-2");
    }

    /**
     * Loads in all of the assets that the SelectionPane uses.
     */
    private void loadAssets() {
        assetManager.loadAndAddBitmap("Pitch_Top", "img/pitch_top.png");
        assetManager.loadAndAddBitmap("Pitch_Centre", "img/pitch_centre.png");
        assetManager.loadAndAddBitmap("Pitch_Bottom", "img/pitch_bottom.png");
        assetManager.loadAndAddBitmap("Pitch_Goal", "img/pitch_goal.png");
        assetManager.loadAndAddBitmap("ToggleButton_Off", "img/Toggles_Off.png");
        assetManager.loadAndAddBitmap("ToggleButton_On", "img/Toggles_On.png");
        assetManager.loadAndAddBitmap("UpArrow", "img/UpArrow.png");
        assetManager.loadAndAddBitmap("UpArrow_Pushed", "img/UpArrowActive.png");
        assetManager.loadAndAddBitmap("DownArrow", "img/DownArrow.png");
        assetManager.loadAndAddBitmap("DownArrow_Pushed", "img/DownArrowActive.png");
    }

    /**
     * Sets the pitch bitmap depending on the area that is currently selected
     */
    private void setCurrentBackground() {
        switch (currentSelectionArea) {
            case 0 :
                pitchStateBitmap = assetManager.getBitmap("Pitch_Top");
                break;
            case 1 :
                pitchStateBitmap = assetManager.getBitmap("Pitch_Centre");
                break;
            case 2 :
                pitchStateBitmap = assetManager.getBitmap("Pitch_Bottom");
                break;
            case 3 :
                pitchStateBitmap = assetManager.getBitmap("Pitch_Goal");
                break;
        }
    }

    /**
     * Moves the list box to the correct position depending if the list box is toggled On/Off
     */
    private void animateListBox() {
        if (showFormationsToggle.isToggledOn()) {
            formationsListBox.position.add(0, formationsListBox.getBound().getHeight() / LISTBOX_ANIMATION_LENGTH * -1);
        } else {
            formationsListBox.position.add(0, formationsListBox.getBound().getHeight() / LISTBOX_ANIMATION_LENGTH);
        }
        formationsListBox.setButtonPositions();
        listBoxAnimationCounter++;
        if (listBoxAnimationCounter == LISTBOX_ANIMATION_LENGTH) listBoxMoving = false;
    }

    /**
     * Check if a touch is within the general area of a certain location
     * @param userTouchLocation
     * @param touchDestination
     */
    private boolean checkIfTouchInArea(Vector2 userTouchLocation, BoundingBox touchDestination) {
        if(userTouchLocation == null || touchDestination == null) return false;

        if(touchDestination.contains(userTouchLocation.x, userTouchLocation.y)) return true;

        return false;
    }

    /**
     * Checks for touch input and performs necessary actions to move a card
     * and trigger any associated events
     */
    private void checkAndPerformDragCard() {
        Input input = mGameScreen.getGame().getInput();
        //Consider all buffered touch events
        for (TouchEvent t : input.getTouchEvents()) {
            // If there is no touch down yet, check for a touch down
            if(!touchDown) {
                // Check if touch event is a touch down
                if (t.type == TouchEvent.TOUCH_DOWN) {
                    // Check if the touch location is within the bounds of any displayed cards
                    for (int i = shownPlaceholdersStartIndex; i < shownPlaceholdersEndIndex; i++) {

                        if(checkIfTouchInArea(new Vector2(t.x, t.y), squadSelectionHolders[i].getBound())) {
                            selectedItemIndex = i;
                            touchDown = true;
                            draggedCardOriginalPosition = new Vector2(squadSelectionHolders[selectedItemIndex].position.x, squadSelectionHolders[selectedItemIndex].position.y);
                        }
                    }
                } else // No touch down
                    continue;
            }

            // If touch event is a drag event, modify position of card
            if (t.type == TouchEvent.TOUCH_DRAGGED && touchDown) {
                if (!Float.isNaN(input.getTouchX(t.pointer)) && squadSelectionHolders[selectedItemIndex].getCard() != null) {
                    squadSelectionHolders[selectedItemIndex].setPosition(input.getTouchX(t.pointer), input.getTouchY(t.pointer));
                }
            }
            // If touch event is a touch up event, check if location is within a select destination and remove card
            // else return card to original position
            if (t.type == TouchEvent.TOUCH_UP) {
                touchDown = false;
                if(selectedItemIndex < -1) return;

                // For each select destination, check if touch up is within the bounds of the destination BoundingBox
                if(checkIfTouchInArea(squadSelectionHolders[selectedItemIndex].position, cardScroller.getBound())) {
                    cardScroller.addScrollerItem(squadSelectionHolders[selectedItemIndex].getCard());
                    squadSelectionHolders[selectedItemIndex].setCard(null);
                    squadSelectionHolders[selectedItemIndex].setPosition(draggedCardOriginalPosition.x, draggedCardOriginalPosition.y);
                } else {
                    boolean foundPlaceholder = false;
                    for (BoundingBox placeholder : availableDropAreas) {
                        if (checkIfTouchInArea(squadSelectionHolders[selectedItemIndex].position, placeholder) && squadSelectionHolders[selectedItemIndex].getCard() != null) {
                            int indexOfDroppedCardHolder = (int) ((2*numberOfCardsOnLevel[currentSelectionArea]*placeholder.x-2*numberOfCardsOnLevel[currentSelectionArea]*position.x+numberOfCardsOnLevel[currentSelectionArea]*mBound.getWidth()-mBound.getWidth())/(2*mBound.getWidth())) + shownPlaceholdersStartIndex;
                            squadSelectionHolders[indexOfDroppedCardHolder].setCard(squadSelectionHolders[selectedItemIndex].getCard());
                            squadSelectionHolders[selectedItemIndex].setPosition(draggedCardOriginalPosition.x, draggedCardOriginalPosition.y);
                            squadSelectionHolders[selectedItemIndex].setCard(null);
                            foundPlaceholder = true;
                            break;
                        }
                    }
                    if (!foundPlaceholder)
                        squadSelectionHolders[selectedItemIndex].setPosition(draggedCardOriginalPosition.x, draggedCardOriginalPosition.y);
                }

                selectedItemIndex = -1;
            }
        }
    }

    /**
     * Checks if the selected item of the list box has changed
     * If so, the item is retrieved and the calculations to figure out the positions of
     * new formations is performed
     */
    private void checkListBoxChanged() {
        // Check if formation is different to stored formation
        if(formationString.equals(formationsListBox.getSelectedItem())) return;

        formationString = formationsListBox.getSelectedItem();

        // Split formation
        String temp[] = formationString.split("-");

        // Assign values of formation to array
        numberOfCardsOnLevel[0] = Integer.valueOf(temp[2]);
        numberOfCardsOnLevel[1] = Integer.valueOf(temp[1]);
        numberOfCardsOnLevel[2] = Integer.valueOf(temp[0]);
        numberOfCardsOnLevel[3] = 1;

        // Calculate positions of holders
        calculateHolderPositions();

        // Calculate available positions
        calculateAvailableHolders();

        //Calculate the indices of the placeholders that are drawn
        calculateShownPlaceholderIndicies();
    }

    /**
     * returns whether all of the placeholders contain a card or not
     * @return
     */
    public boolean isSquadFull() {
        for (int i = 0; i < squadSelectionHolders.length; i++) {
            if (squadSelectionHolders[i].getCard() == null) return false;
        }
        return true;
    }

    /**
     * Calculates where to place card holders based on the formation
     */
    private void calculateHolderPositions() {
        int numberOfHoldersDealtWith = 0;
        for (int i = 0; i < numberOfCardsOnLevel.length; i++) {
            for (int j = 0; j < numberOfCardsOnLevel[i]; j++) {
                squadSelectionHolders[j + numberOfHoldersDealtWith].setPosition(position.x - mBound.halfWidth + mBound.getWidth()*((2*j)+1)
                        /(numberOfCardsOnLevel[i]*2), position.y + mBound.getHeight() * 0.25f);
            }
            numberOfHoldersDealtWith += numberOfCardsOnLevel[i];
        }
    }

    private void calculateShownPlaceholderIndicies() {
        shownPlaceholdersStartIndex = 0;
        shownPlaceholdersEndIndex = 0;
        for (int i = 0; i < currentSelectionArea; i++) {
            shownPlaceholdersStartIndex += numberOfCardsOnLevel[i];
        }
        shownPlaceholdersEndIndex = shownPlaceholdersStartIndex + numberOfCardsOnLevel[currentSelectionArea];
    }

    /**
     * Calculates which holders are available to be used by the scroller to drop cards into
     */
    private void calculateAvailableHolders() {
        // Calculate available positions
        availableDropAreas.clear();
        for (int i = shownPlaceholdersStartIndex; i < shownPlaceholdersEndIndex; i++) {
            if (squadSelectionHolders[i].getCard() == null)
                availableDropAreas.add(squadSelectionHolders[i].getBound());
        }
        cardScroller.setSelectDestinations(availableDropAreas);
    }

    /**
     * Checks if a removed card is ready from the scroller
     * If so, the position the card was dropped is cross referenced with the displayed
     * holders to determine it's position
     */
    private void checkIfRemovedCardReady() {
        if (cardScroller.isRemovedCardReady()) {
            Card removedCard = new Card(cardScroller.getRemovedCard());
                for (int i = shownPlaceholdersStartIndex; i < shownPlaceholdersEndIndex; i++) {
                    if(removedCard.getBound().intersects(squadSelectionHolders[i].getBound())) {
                        squadSelectionHolders[i].setCard(removedCard);
                        break;
                    }
                }

            cardScroller.removeSelectDestination(cardScroller.getSelectDestinations().indexOf(cardScroller.getRemovedCardBound()));
        }
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        if (listBoxMoving) {
            animateListBox();
        } else {
            //update UI
            nextAreaButton.update(elapsedTime);
            previousAreaButton.update(elapsedTime);
            formationsListBox.update(elapsedTime);
            showFormationsToggle.update(elapsedTime);
            cardScroller.update(elapsedTime);
            for (CardHolder squadSelectionHolder : squadSelectionHolders) {
                squadSelectionHolder.update(elapsedTime);
            }

            //Change the background and drawn placeholders when the selectionArea is changed
            if (nextAreaButton.isPushTriggered() && currentSelectionArea < 3) {
                currentSelectionArea++;
                setCurrentBackground();
                calculateShownPlaceholderIndicies();
            }
            if (previousAreaButton.isPushTriggered() && currentSelectionArea > 0) {
                currentSelectionArea--;
                setCurrentBackground();
                calculateShownPlaceholderIndicies();
            }

            //Show/Hide the list box when the toggle is changed
            if ((showFormationsToggle.isToggledOn() && formationsListBox.position.y > openListBoxPositionY) ||
                    (!showFormationsToggle.isToggledOn() && formationsListBox.position.y == openListBoxPositionY)) {
                listBoxAnimationCounter = 0;
                listBoxMoving = true;
            }

            calculateAvailableHolders();
            checkListBoxChanged();
            checkAndPerformDragCard();
            checkIfRemovedCardReady();
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint paint = mGameScreen.getGame().getPaint(); //Get the game's paint object

        //Draw the pitch which takes up half of the object
        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));
        paint.setAlpha(200);
        graphics2D.drawBitmap(pitchStateBitmap, null, drawScreenRect, paint);

        //Draw a separator between the scroller and the pitch
        paint.reset();
        paint.setStrokeWidth(10);
        graphics2D.drawLine(position.x - mBound.halfWidth, position.y,position.x + mBound.halfWidth, position.y, paint);

        // Draw displayed squadSelectionHolders
        for (int i = shownPlaceholdersStartIndex; i < shownPlaceholdersEndIndex; i++)
            squadSelectionHolders[i].draw(elapsedTime, graphics2D);

        //Draw the side bar with buttons and toggle
        paint.reset();
        paint.setColor(Color.GRAY);
        paint.setAlpha(175);
        graphics2D.drawRect(position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE, position.y, position.x + mBound.halfWidth, position.y + mBound.halfHeight, paint);
        if (currentSelectionArea < 3) nextAreaButton.draw(elapsedTime, graphics2D);
        if (currentSelectionArea > 0) previousAreaButton.draw(elapsedTime, graphics2D);
        paint.reset();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20);
        graphics2D.drawText("Formation", position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE/2, mBound.getBottom() + mBound.getHeight()*7/12 - mBound.getHeight()/24, paint);
        showFormationsToggle.draw(elapsedTime, graphics2D);

        cardScroller.draw(elapsedTime, graphics2D);

        formationsListBox.draw(elapsedTime, graphics2D);

        //Draw the border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        graphics2D.drawRect(mBound.getLeft(), mBound.getBottom(), mBound.getRight(), mBound.getTop(), paint);
    }
}
