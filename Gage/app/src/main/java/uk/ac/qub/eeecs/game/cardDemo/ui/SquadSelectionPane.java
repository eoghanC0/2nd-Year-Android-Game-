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
    private final float SIDE_BAR_COVERAGE = 0.1f;
    private final float TOGGLE_BITMAP_ASPECT_RATIO = 72f/168f;

    private ListBox formationsListBox;
    private HorizontalCardScroller cardScroller;
    private Toggle showFormationsToggle;
    private PushButton nextAreaButton;
    private PushButton previousAreaButton;
    private Bitmap background;
    private ArrayList<BoundingBox> availablePlaceholders;
    private int currentSelectionArea = 3;
    private boolean listBoxMoving = false;
    private int listBoxAnimationCounter = 0;
    private int listBoxAnimationLength = 8;
    private float openListBoxPositionY;
    private AssetStore assetManager = mGameScreen.getGame().getAssetManager();

    private SquadSelectionHolder[] squadSelectionHolders = new SquadSelectionHolder[11];
    private boolean touchDown = false;
    private int selectedItemIndex = 0;
    private Vector2 draggedCardOriginalPosition = new Vector2();
    private int placeHolderHalfHeight = (int) (mBound.getHeight()*1/6);
    private int placeHolderHalfWidth = placeHolderHalfHeight * 225/355;
    private String formationString = "";
    private int[] formation = new int[4];
    private int currentLevel = 0;

    public SquadSelectionPane(GameScreen gameScreen) {
        super(gameScreen.getGame().getScreenWidth()/2, gameScreen.getGame().getScreenHeight()/2, gameScreen.getGame().getScreenWidth(), gameScreen.getGame().getScreenHeight(), null, gameScreen);
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
        background = assetManager.getBitmap("Pitch_Top");
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
        formationsListBox.setPosition(formationsListBox.position.x, formationsListBox.position.y + formationsListBox.getBound().getHeight());
        formationsListBox.addItem("3-4-3");
        formationsListBox.addItem("3-5-2");
        formationsListBox.addItem("4-3-3");
        formationsListBox.addItem("4-4-2");
        formationsListBox.addItem("4-5-1");
        formationsListBox.addItem("5-3-2");
        availablePlaceholders = new ArrayList<>();

        for (int i = 0; i < squadSelectionHolders.length; i++) {
            squadSelectionHolders[i] = new SquadSelectionHolder(gameScreen);
            squadSelectionHolders[i].setHeight(placeHolderHalfHeight * 2);
        }
    }

    public void setCurrentBackground() {
        switch (currentSelectionArea) {
            case 0 :
                background = assetManager.getBitmap("Pitch_Top");
                break;
            case 1 :
                background = assetManager.getBitmap("Pitch_Centre");
                break;
            case 2 :
                background = assetManager.getBitmap("Pitch_Bottom");
                break;
            case 3 :
                background = assetManager.getBitmap("Pitch_Goal");
                break;
        }
    }

    private void animateListBox() {
        if (showFormationsToggle.isToggledOn()) {
            formationsListBox.position.add(0, formationsListBox.getBound().getHeight() / listBoxAnimationLength * -1);
        } else {
            formationsListBox.position.add(0, formationsListBox.getBound().getHeight() / listBoxAnimationLength);
        }
        formationsListBox.setButtonPositions();
        listBoxAnimationCounter++;
        if (listBoxAnimationCounter == listBoxAnimationLength) listBoxMoving = false;
    }

    /**
     * Check if a touch is within the general area of a certain location
     * @param userTouchLocation
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
                    for (int i = 0; i < squadSelectionHolders.length; i++) {
                        if(squadSelectionHolders[i].getLevel() != currentLevel || squadSelectionHolders[i].getCard() == null) continue;

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
                if (!Float.isNaN(input.getTouchX(t.pointer))) {
                    squadSelectionHolders[selectedItemIndex].setPositionX(input.getTouchX(t.pointer));
                    squadSelectionHolders[selectedItemIndex].setPositionY(input.getTouchY(t.pointer));
                }
            }
            // If touch event is a touch up event, check if location is within a select destination and remove card
            // else return card to original position
            if (t.type == TouchEvent.TOUCH_UP) {
                touchDown = false;
                if(selectedItemIndex < -1) return;

                // For each select destination, check if touch up is within the bounds of the destination BoundingBox
                if(checkIfTouchInArea(squadSelectionHolders[selectedItemIndex].position, cardScroller.getBound())) {
                    cardScroller.addScrollerItem(new Card(squadSelectionHolders[selectedItemIndex].getCard()));
                    squadSelectionHolders[selectedItemIndex].setCard(null);
                    squadSelectionHolders[selectedItemIndex].setPosition(draggedCardOriginalPosition);
                } else
                    squadSelectionHolders[selectedItemIndex].setPosition(draggedCardOriginalPosition);

                selectedItemIndex = -1;
            }
        }
    }

    /**
     * Checks if the selected item of the listbox has changed
     * If so, the item is retrieved and the calculations to figure out the positions of
     * new formations is performed
     */
    private void checkListboxChanged() {
        // Check if formation is different to stored formation
        if(formationString == formationsListBox.getSelectedItem()) return;

        formationString = formationsListBox.getSelectedItem();
        // Split formation
        String temp[] = formationString.split("-");

        // Assign values of formation to array
        formation[0] = 1;
        formation[1] = Integer.valueOf(temp[0]);
        formation[2] = Integer.valueOf(temp[1]);
        formation[3] = Integer.valueOf(temp[2]);

        // Calculate positions of holders
        calculateHolderPositions();

        // Calculate available positions
        calculateAvailableHolders();
    }

    /**
     * Calculates where to place card holders based on the formation
     */
    private void calculateHolderPositions() {
        int levelCurrentItem = 0;
        int levelLastItem = 1;
        float yPos = position.y + mBound.getHeight() * 0.25f;

        // Calculate level 0 (goalkeeper)
        squadSelectionHolders[levelCurrentItem].setLevel(0);
        squadSelectionHolders[levelCurrentItem].setPosition(position.x, yPos);

        // Calculate level 1
        levelCurrentItem += formation[0];
        float spacing = (mBound.getWidth() - (placeHolderHalfWidth * 2f) * formation[1]) / (formation[1] + 1);
        squadSelectionHolders[levelCurrentItem].setLevel(1);
        squadSelectionHolders[levelCurrentItem].setPosition(mBound.getLeft() + spacing + placeHolderHalfWidth, yPos);
        levelLastItem += formation[1];

        for (int i = levelCurrentItem + 1; i < levelLastItem; i++) {
            squadSelectionHolders[i].setLevel(1);
            squadSelectionHolders[i].setPosition(squadSelectionHolders[i - 1].position.x + spacing + (placeHolderHalfWidth * 2f), yPos);
        }

        // Calculate level 2
        levelCurrentItem += formation[1];
        spacing = (mBound.getWidth() - (placeHolderHalfWidth * 2f) * formation[2]) / (formation[2] + 1);
        squadSelectionHolders[levelCurrentItem].setLevel(2);
        squadSelectionHolders[levelCurrentItem].setPosition(mBound.getLeft() + spacing + placeHolderHalfWidth, yPos);
        levelLastItem += formation[2];

        for (int i = levelCurrentItem + 1; i < levelLastItem; i++) {
            squadSelectionHolders[i].setLevel(2);
            squadSelectionHolders[i].setPosition(squadSelectionHolders[i - 1].position.x + spacing + (placeHolderHalfWidth * 2f), yPos);
        }

        // Calculate level 3
        levelCurrentItem += formation[2];
        spacing = (mBound.getWidth() - (placeHolderHalfWidth * 2f) * formation[3]) / (formation[3] + 1);
        squadSelectionHolders[levelCurrentItem].setLevel(3);
        squadSelectionHolders[levelCurrentItem].setPosition(mBound.getLeft() + spacing + placeHolderHalfWidth, yPos);
        levelLastItem += formation[3];

        for (int i = levelCurrentItem + 1; i < levelLastItem; i++) {
            squadSelectionHolders[i].setLevel(3);
            squadSelectionHolders[i].setPosition(squadSelectionHolders[i - 1].position.x + spacing + (placeHolderHalfWidth * 2f), yPos);
        }
    }

    /**
     * Calculates which holders are available to be used by the scroller to drop cards
     * into
     */
    private void calculateAvailableHolders() {
        // Calculate available positions
        availablePlaceholders.clear();
        for (int i = 0; i < squadSelectionHolders.length; i++) {
            if(squadSelectionHolders[i].getLevel() != currentLevel  || squadSelectionHolders[i].getCard() != null) continue;
            availablePlaceholders.add(squadSelectionHolders[i].getBound());
        }

        cardScroller.setSelectDestinations(availablePlaceholders);
    }

    /**
     * Checks if a removed card is ready from the scroller
     * If so, the position the card was dropped is cross referenced with the displayed
     * holders to determine it's position
     */
    private void checkIfRemovedCardReady() {
        if (cardScroller.isRemovedCardReady()) {
            Card removedCard = new Card(cardScroller.getRemovedCard());
                for (int i = 0; i < squadSelectionHolders.length; i++) {
                    if(squadSelectionHolders[i].getLevel() != currentLevel) continue;

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
            nextAreaButton.update(elapsedTime);
            previousAreaButton.update(elapsedTime);
            formationsListBox.update(elapsedTime);
            showFormationsToggle.update(elapsedTime);
            cardScroller.update(elapsedTime);

            for (SquadSelectionHolder squadSelectionHolder : squadSelectionHolders) {
                squadSelectionHolder.update(elapsedTime);
            }

            if (nextAreaButton.isPushTriggered() && currentSelectionArea < 3) {
                currentLevel--;
                currentSelectionArea++;
                setCurrentBackground();
                calculateHolderPositions();
            }
            if (previousAreaButton.isPushTriggered() && currentSelectionArea > 0) {
                currentLevel++;
                currentSelectionArea--;
                setCurrentBackground();
                calculateHolderPositions();
            }
            if ((showFormationsToggle.isToggledOn() && formationsListBox.position.y > openListBoxPositionY) ||
                    (!showFormationsToggle.isToggledOn() && formationsListBox.position.y == openListBoxPositionY)) {
                listBoxAnimationCounter = 0;
                listBoxMoving = true;
            }

            calculateAvailableHolders();

            checkListboxChanged();

            checkAndPerformDragCard();

            checkIfRemovedCardReady();
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint paint = mGameScreen.getGame().getPaint(); //Get the game's paint object

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));
        paint.setAlpha(200);
        graphics2D.drawBitmap(background, null, drawScreenRect, paint);

        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);

        cardScroller.draw(elapsedTime, graphics2D);

        paint.reset();
        paint.setColor(Color.GRAY);
        paint.setAlpha(200);
        graphics2D.drawRect(position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE, position.y, position.x + mBound.halfWidth, position.y + mBound.halfHeight, paint);
        if (currentSelectionArea < 3) nextAreaButton.draw(elapsedTime, graphics2D);
        if (currentSelectionArea > 0) previousAreaButton.draw(elapsedTime, graphics2D);
        showFormationsToggle.draw(elapsedTime, graphics2D);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        //Draw the border
        graphics2D.drawRect(mBound.getLeft(), mBound.getBottom(), mBound.getRight(), mBound.getTop(), paint);

        // Draw displayed squadSelectionHolders
        for (int i = 0; i < squadSelectionHolders.length; i++) {
            if(squadSelectionHolders[i].getLevel() != currentLevel) continue;

            squadSelectionHolders[i].draw(elapsedTime, graphics2D);
        }

        formationsListBox.draw(elapsedTime, graphics2D);
    }
}
