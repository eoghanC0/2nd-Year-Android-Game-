package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.ToggleButton;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.ui.Toggle;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;

/**
 * Created by stephenmcveigh on 08/02/2018.
 */

public class SquadSelectionPane extends GameObject {

    /**
     * The coverage of the side bar
     */
    private final float SIDE_BAR_COVERAGE = 0.1f;
    private final float TOGGLE_BITMAP_ASPECT_RATIO = 72f/168f;
    private final int ANIMATION_SPEED = 60;

    private Bitmap background;
    private ArrayList<Card> cards;
    private ArrayList<BoundingBox> placeholders;
    private int currentSelectionArea = 0;
    private String selectedFormation;
    private Toggle showFormationsToggle;
    private PushButton nextAreaButton;
    private PushButton previousAreaButton;
    private ListBox formationsListBox;
    private boolean listBoxMoving = false;
    private int listBoxAnimationCounter = 0;
    private int listBoxAnimationLength = 10;
    private float distanceMoved = 0;
    private float openListBoxPositionY;
    private AssetStore assetManager = mGameScreen.getGame().getAssetManager();


    public SquadSelectionPane(GameScreen gameScreen) {
        super(gameScreen.getGame().getScreenWidth()/2, gameScreen.getGame().getScreenHeight() * 0.75f, gameScreen.getGame().getScreenWidth(), gameScreen.getGame().getScreenHeight()/2, null, gameScreen);
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
        //cards = mGameScreen.getGame().getCurrentSquad();
        placeholders = new ArrayList<>();
        setCurrentBackground();
        //selectedFormation = mGameScreen.getGame().getSelectedFormation();
        showFormationsToggle = new Toggle( mBound.getRight() - mBound.getWidth() * (SIDE_BAR_COVERAGE/2),mBound.getBottom() + mBound.getHeight()/6,
                mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f) * (TOGGLE_BITMAP_ASPECT_RATIO),
                "ToggleButton_Off", "ToggleButton_On", gameScreen);
        previousAreaButton = new PushButton(mBound.getRight() - mBound.getWidth() * (SIDE_BAR_COVERAGE/2), mBound.getBottom() + mBound.getHeight()/2,
                mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), "UpArrow", "UpArrow_Pushed", gameScreen);
        nextAreaButton = new PushButton(mBound.getRight() - mBound.getWidth() * (SIDE_BAR_COVERAGE/2), mBound.getBottom() + mBound.getHeight()/6*5,
                mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), mBound.getWidth() * (SIDE_BAR_COVERAGE - 0.04f), "DownArrow", "DownArrow_Pushed", gameScreen);
        formationsListBox = new ListBox(position.x, position.y + mBound.getHeight()/6, mBound.getWidth()/1.5f, mBound.getHeight() * 2/3, gameScreen);
        openListBoxPositionY = formationsListBox.position.y + (mBound.getHeight() * 2/3 - formationsListBox.getBound().getHeight())/2;
        formationsListBox.setPosition(position.x, openListBoxPositionY + formationsListBox.getBound().getHeight());
        formationsListBox.addItem("3-4-3");
        formationsListBox.addItem("3-5-2");
        formationsListBox.addItem("4-3-3");
        formationsListBox.addItem("4-4-2");
        formationsListBox.addItem("4-5-1");
        formationsListBox.addItem("5-3-2");
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

    private void moveListbox() {
        if (showFormationsToggle.isToggledOn()) {
            formationsListBox.position.add(0, formationsListBox.getBound().getHeight()/listBoxAnimationLength * -1);
        } else {
            formationsListBox.position.add(0, formationsListBox.getBound().getHeight()/listBoxAnimationLength);
        }
        formationsListBox.setButtonPositions();
        listBoxAnimationCounter++;
        if (listBoxAnimationCounter == listBoxAnimationLength) listBoxMoving = false;
    }

    public ArrayList<BoundingBox> getPlaceholders() {return placeholders;}

    @Override
    public void update(ElapsedTime elapsedTime) {
        if (listBoxMoving) {
            moveListbox();
        } else {
            nextAreaButton.update(elapsedTime);
            previousAreaButton.update(elapsedTime);
            formationsListBox.update(elapsedTime);
            showFormationsToggle.update(elapsedTime);
            if (nextAreaButton.isPushTriggered() && currentSelectionArea < 3) {
                currentSelectionArea++;
                setCurrentBackground();
            }
            if (previousAreaButton.isPushTriggered() && currentSelectionArea > 0) {
                currentSelectionArea--;
                setCurrentBackground();
            }
            if ((showFormationsToggle.isToggledOn() && formationsListBox.position.y > openListBoxPositionY) ||
                    (!showFormationsToggle.isToggledOn() && formationsListBox.position.y == openListBoxPositionY)) {
                listBoxAnimationCounter = 0;
                distanceMoved = 0;
                listBoxMoving = true;
            }
            placeholders.clear();
            selectedFormation = "1-" + formationsListBox.getSelectedItem();
            int placeHolderHalfHeight = 40;
            int placeHolderHalfWidth = 40;
            int numberOfCardsOnThisLevel;
            if (formationsListBox.getSelectedIndex() == -1) {
                numberOfCardsOnThisLevel = 0;
            } else {
                numberOfCardsOnThisLevel = (int) selectedFormation.charAt(selectedFormation.length() - 1 - currentSelectionArea * 2) - 48;
            }
            for (int i = 0; i < numberOfCardsOnThisLevel; i++) {
                placeholders.add(new BoundingBox(position.x - mBound.halfWidth + mBound.getWidth()*((2*i)+1)/(numberOfCardsOnThisLevel*2), position.y, placeHolderHalfWidth, placeHolderHalfHeight));
            }
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint paint = mGameScreen.getGame().getPaint(); //Get the game's paint object

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));
        paint.setAlpha(200);
        graphics2D.drawBitmap(background, null, drawScreenRect, paint);

        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        for (BoundingBox placeholder : placeholders) {
            graphics2D.drawRect(placeholder.getLeft(), placeholder.getBottom(), placeholder.getRight(), placeholder.getTop(), paint);
        }

        paint.reset();
        paint.setColor(Color.GRAY);
        paint.setAlpha(200);
        graphics2D.drawRect(position.x + mBound.halfWidth - mBound.getWidth() * 0.1f, position.y - mBound.halfHeight, position.x + mBound.halfWidth, position.y + mBound.halfHeight, paint);
        if (currentSelectionArea < 3) nextAreaButton.draw(elapsedTime, graphics2D);
        if (currentSelectionArea > 0) previousAreaButton.draw(elapsedTime, graphics2D);
        showFormationsToggle.draw(elapsedTime, graphics2D);
        formationsListBox.draw(elapsedTime, graphics2D);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        //Draw the border
        graphics2D.drawRect(mBound.getLeft(), mBound.getBottom(), mBound.getRight(), mBound.getTop(), paint);
    }
}
