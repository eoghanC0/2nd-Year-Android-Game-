package uk.ac.qub.eeecs.game.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

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
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.objects.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.game.objects.Card;

/**
 * Created by stephenmcveigh on 08/02/2018.
 */

public class SquadSelectionPane extends GameObject {
    //////////////////////////////////////////////
    //  Constants
    //////////////////////////////////////////////
    private final float SIDE_BAR_COVERAGE = 0.1f;  // The percentage cover of the side bar from the right hand side of the element
    private final float TOGGLE_BITMAP_ASPECT_RATIO = 72f/168f;  // The aspect ratio of the toggle bitmap
    private final int LISTBOX_ANIMATION_LENGTH = 8;  // The number of frames the listbox animation is carried out over ~ higher = slower

    //////////////////////////////////////////////
    //  Properties
    //////////////////////////////////////////////
    /**
     * UI Uses
     */
    private ListBox formationsListBox;
    private CardScroller cardScroller;
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
    private int selectedItemIndex = -1;
    private Vector2 draggedCardOriginalPosition = new Vector2();

    /**
     * Properties to keep track of which placeholders are currently displayed on screen
     */
    private int shownPlaceholdersStartIndex;
    private int shownPlaceholdersEndIndex;

    /**
     * Properties to hold the selected formation and the number of players in each of the 4 sections of the pitch
     */
    private String formationString = "0-0-0-0";
    private int[] numberOfCardsOnLevel = {0,0,0,0};

    //////////////////////////////////////////////
    //  Constructors
    //////////////////////////////////////////////

    /**
     * @param gameScreen The screen the pane is displayed on
     */
    public SquadSelectionPane(FootballGameScreen gameScreen) {
        super(gameScreen.getGame().getScreenWidth()/2, gameScreen.getGame().getScreenHeight()/2, gameScreen.getGame().getScreenWidth(), gameScreen.getGame().getScreenHeight(), null, gameScreen);
        loadAssets();
        pitchStateBitmap = assetManager.getBitmap("Pitch_Top");

        //Set up the UI Elements of the SelectionPane
        showFormationsToggle = new Toggle( mBound.getLeft() + mBound.getWidth() * (SIDE_BAR_COVERAGE/2),mBound.getBottom() + mBound.getHeight()*7/12,
                mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f) * (TOGGLE_BITMAP_ASPECT_RATIO),
                "ToggleButton_Off", "ToggleButton_On", gameScreen);
        previousAreaButton = new PushButton(mBound.getLeft() + mBound.getWidth() * (SIDE_BAR_COVERAGE/2), mBound.getBottom() + mBound.getHeight()*9/12,
                mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), "ArrowUp", "ArrowUpPushed", gameScreen);
        nextAreaButton = new PushButton(mBound.getLeft() + mBound.getWidth() * (SIDE_BAR_COVERAGE/2), mBound.getBottom() + mBound.getHeight()*11/12,
                mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), "ArrowDown", "ArrowDownPushed", gameScreen);
        openListBoxPositionY = position.y + mBound.getHeight()*3/12;
        formationsListBox = new ListBox(position.x, openListBoxPositionY, mBound.getWidth()/1.5f, mBound.getHeight()/2, gameScreen);
        setUpFormationsListBox();
        initializeCardHolders();
        cardScroller = new CardScroller(gameScreen.getGame().getScreenWidth()/2, gameScreen.getGame().getScreenHeight() * 0.25f, gameScreen.getGame().getScreenWidth(), gameScreen.getGame().getScreenHeight()/2, gameScreen);

        // If a squad exists, automatically add it to the selection pane
        if(gameScreen.getGame().getSquad().size() == 11) {
            // Determine formation to use
            formationsListBox.setSelectedIndex(0);
            for (int i = 0; i < formationsListBox.getItems().size(); i++) {
                if(formationsListBox.getItems().get(i) == gameScreen.getGame().getFormation()) {
                    formationsListBox.setSelectedIndex(i);
                    break;
                }
            }

            // Populate squad card holders
            for (int i = 0; i < gameScreen.getGame().getSquad().size(); i++) {
                squadSelectionHolders[i].setCard(gameScreen.getGame().getSquad().get(i));
            }
        }

        // Add club to card scroller
        for (Card card : gameScreen.getGame().getClub()) {
            cardScroller.addScrollerItem(card);
        }

        cardScroller.setMultiMode(true, 80);
        cardScroller.setSelectMode(true);
    }

    //////////////////////////////////////////////
    //  Methods
    //////////////////////////////////////////////

    public ArrayList<Card> getScrollerItems() {
        return cardScroller.getScrollerItems();
    }

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
        assetManager.loadAndAddBitmap("ArrowUp", "img/ArrowUp.png");
        assetManager.loadAndAddBitmap("ArrowUpPushed", "img/ArrowUpPushed.png");
        assetManager.loadAndAddBitmap("ArrowDown", "img/ArrowDown.png");
        assetManager.loadAndAddBitmap("ArrowDownPushed", "img/ArrowDownPushed.png");
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
            default :
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
    }

    /**
     * Check if a touch is within the general area of a certain location
     * @param destination The bounding box where coords are to be checked
     * @return CoordsInArea
     */
    private boolean checkIfCoordsInArea(float x, float y, BoundingBox destination) {
        return destination.contains(x, y);
    }

    /**
     * Checks if the touch event is contained within a card placeholder.
     * If so, trigger the drag
     * @param t The touch event to be considered
     */
    private boolean checkTouchInCardAndTriggerDrag(TouchEvent t, int holderIndex) {
        // Check if the touch location is within the bounds of the displayed card
        if(checkIfCoordsInArea(t.x, t.y, squadSelectionHolders[holderIndex].getBound())) {
            selectedItemIndex = holderIndex;
            draggedCardOriginalPosition.set(squadSelectionHolders[selectedItemIndex].position.x, squadSelectionHolders[selectedItemIndex].position.y);
            return true;
        }
        return false;
    }

    /**
     * Sets the position of the card placeholder to the current position of the touch event
     * @param touchEvent The touch event to be considered
     */
    private boolean checkSelectedItemAndPerformDrag(TouchEvent touchEvent) {
        if (selectedItemIndex > -1) {
            if (squadSelectionHolders[selectedItemIndex].getCard() != null) {
                //A holder is selected which contains a card
                squadSelectionHolders[selectedItemIndex].setPosition(touchEvent.x, touchEvent.y);
                return true;
            }
        }
        return false;
    }

    /**
     * Drops the dragged card into the card scroller if they are overlapping.
     * Returns true if overlapping and returns false if not.
     * @return success
     */
    private boolean dropIntoScroller() {
        if(checkIfCoordsInArea(squadSelectionHolders[selectedItemIndex].position.x, squadSelectionHolders[selectedItemIndex].position.y, cardScroller.getBound())) {
            cardScroller.addScrollerItem(squadSelectionHolders[selectedItemIndex].getCard());
            squadSelectionHolders[selectedItemIndex].setCard(null);
            return true; //success
        }
        return false; //failure
    }

    /**
     * Drops the dragged card into another card holder if they are overlapping
     */
    private boolean dropIntoHolder(BoundingBox placeholder) {
        if (checkIfCoordsInArea(squadSelectionHolders[selectedItemIndex].position.x, squadSelectionHolders[selectedItemIndex].position.y, placeholder)) {
            int indexOfDroppedCardHolder = (int) ((2*numberOfCardsOnLevel[currentSelectionArea]*placeholder.x-2*numberOfCardsOnLevel[currentSelectionArea]*position.x+numberOfCardsOnLevel[currentSelectionArea]*mBound.getWidth()-mBound.getWidth())/(2*mBound.getWidth())) + shownPlaceholdersStartIndex;
            squadSelectionHolders[indexOfDroppedCardHolder].setCard(squadSelectionHolders[selectedItemIndex].getCard());
            squadSelectionHolders[selectedItemIndex].setCard(null);
            return true; //success
        }
        return false; //failure
    }

    /**
     * Performs the card drop in the required place.
     * Sets the position of the card holder back to the original position
     */
    private void performDrop() {
        if (selectedItemIndex > -1) {
            if(!dropIntoScroller()) {
                for (BoundingBox placeholder : availableDropAreas) {
                    if (dropIntoHolder(placeholder)) break;
                }
            }
            squadSelectionHolders[selectedItemIndex].setPosition(draggedCardOriginalPosition.x, draggedCardOriginalPosition.y);
            selectedItemIndex = -1;
        }
    }

    /**
     * Gets user input and carries out the required drag and drop methods depending on the input
     */
    private void performDragAndDropFromHolders() {
        Input input = mGameScreen.getGame().getInput();

        for (TouchEvent t : input.getTouchEvents()) {
            switch (t.type) {
                case TouchEvent.TOUCH_DOWN:
                    //Loop through the displayed indices
                    for (int i = shownPlaceholdersStartIndex; i < shownPlaceholdersEndIndex; i++) {
                        if (checkTouchInCardAndTriggerDrag(t, i)) break;
                    }
                    break;
                case TouchEvent.TOUCH_DRAGGED:
                    checkSelectedItemAndPerformDrag(t);
                    break;
                case TouchEvent.TOUCH_UP:
                    performDrop();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Reads the current formation and assigns the correct number of cards to each level
     */
    private void assignCardsToLevels() {
        // Split formation
        String temp[] = formationString.split("-");

        // Assign values of formation to array
        numberOfCardsOnLevel[0] = Integer.valueOf(temp[2]);
        numberOfCardsOnLevel[1] = Integer.valueOf(temp[1]);
        numberOfCardsOnLevel[2] = Integer.valueOf(temp[0]);
        numberOfCardsOnLevel[3] = (formationsListBox.getSelectedIndex() > -1) ? 1 : 0;

        // Calculate positions of holders
        calculateHolderPositions();

        // Calculate available positions
        calculateAvailableHolders();

        // Calculate the indices of the placeholders that are drawn
        calculateShownPlaceholderIndices();
    }

    /**
     * Checks if the selected item of the list box has changed
     * If so, the item is retrieved and the calculations to figure out the positions of
     * new formations is performed
     */
    private void setFormationFromListBox() {
        if (!formationString.equals(formationsListBox.getSelectedItem())) {
            formationString = formationsListBox.getSelectedItem();
            if (formationString.equals("")) {
                formationString = "0-0-0";
            }
            assignCardsToLevels();
        }
    }

    /**
     * returns whether all of the placeholders contain a card or not
     * @return squadIsFull
     */
    public boolean squadIsFull() {
        for (CardHolder holder : squadSelectionHolders) {
            if (holder.getCard() == null) return false;
        }
        return true;
    }

    /**
     * Calculates where to place card holders based on the formation
     */
    private void calculateHolderPositions() {
        int numberOfHoldersDealtWith = 0;
        for (int numOfCards : numberOfCardsOnLevel) {
            for (int j = 0; j < numOfCards; j++) {
                squadSelectionHolders[j + numberOfHoldersDealtWith].setPosition(position.x - mBound.halfWidth + mBound.getWidth()*((2*j)+1)
                        /(numOfCards*2), position.y + mBound.getHeight() * 0.25f);
            }
            numberOfHoldersDealtWith += numOfCards;
        }
    }

    /**
     * Calculates the indices of the displayed card placeholders within the squadSelectionHolders array
     */
    private void calculateShownPlaceholderIndices() {
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
    private void getCardFromScroller() {
        if (cardScroller.isRemovedCardReady()) {
            Card removedCard = cardScroller.getRemovedCard();
                for (int i = shownPlaceholdersStartIndex; i < shownPlaceholdersEndIndex; i++) {
                    if(removedCard.getBound().intersects(squadSelectionHolders[i].getBound())) {
                        squadSelectionHolders[i].setCard(removedCard);
                        break;
                    }
                }
            cardScroller.removeSelectDestination(cardScroller.getSelectDestinations().indexOf(removedCard.getBound()));
        }
    }

    /**
     * Check trigger and increment the selection area, reset the background and reposition the holders
     */
    private void handleNextAreaButtonTrigger() {
        if (currentSelectionArea < 3) {
            currentSelectionArea++;
            setCurrentBackground();
            calculateShownPlaceholderIndices();
        }
    }

    /**
     * Check trigger and decrement the selection area, reset the background and reposition the holders
     */
    private void handlePreviousAreaButtonTrigger() {
        if (currentSelectionArea > 0) {
            currentSelectionArea--;
            setCurrentBackground();
            calculateShownPlaceholderIndices();
        }
    }

    /**
     * Check the listbox toggle/position and trigger an animation to open/close the listbox if needed
     */
    private void checkToggleAndTriggerListBoxAnimation() {
        //Show/Hide the list box when the toggle is changed
        if ((showFormationsToggle.isToggledOn() && formationsListBox.position.y > openListBoxPositionY) ||
                (!showFormationsToggle.isToggledOn() && formationsListBox.position.y == openListBoxPositionY)) {
            listBoxAnimationCounter = 0;
        }
    }

    /**
     * Checks if all CardHolders are occupied
     * @return boolean
     */
    public boolean isSquadSelected() {
        int sum = 0;
        for (CardHolder cardHolder: squadSelectionHolders) {
            if(cardHolder.getCard() != null) sum++;
        }
        if(sum == squadSelectionHolders.length) return true;
        else return false;
    }

    public CardHolder[] getSquadSelectionHolders() {
        return squadSelectionHolders;
    }

    public String getFormationString() {
        return formationString;
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        if (listBoxAnimationCounter < LISTBOX_ANIMATION_LENGTH) {
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

            if (nextAreaButton.isPushTriggered())
                handleNextAreaButtonTrigger();
            if (previousAreaButton.isPushTriggered())
                handlePreviousAreaButtonTrigger();
            checkToggleAndTriggerListBoxAnimation();
            calculateAvailableHolders();
            setFormationFromListBox();
            performDragAndDropFromHolders();
            getCardFromScroller();
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
        paint.setColor(Color.argb(125, 0,242,255));
        //graphics2D.drawRect(position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE, position.y, position.x + mBound.halfWidth, position.y + mBound.halfHeight, paint);
        if (currentSelectionArea < 3) nextAreaButton.draw(elapsedTime, graphics2D);
        if (currentSelectionArea > 0) previousAreaButton.draw(elapsedTime, graphics2D);
        paint.reset();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20);
        graphics2D.drawText("Formation", position.x - mBound.halfWidth + mBound.getWidth() * SIDE_BAR_COVERAGE/2, mBound.getBottom() + mBound.getHeight()*7/12 - mBound.getHeight()/24, paint);
        showFormationsToggle.draw(elapsedTime, graphics2D);

        cardScroller.draw(elapsedTime, graphics2D);


        formationsListBox.draw(elapsedTime, graphics2D);

        //Draw the border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        graphics2D.drawRect(mBound.getLeft(), mBound.getBottom(), mBound.getRight(), mBound.getTop(), paint);
    }
}
