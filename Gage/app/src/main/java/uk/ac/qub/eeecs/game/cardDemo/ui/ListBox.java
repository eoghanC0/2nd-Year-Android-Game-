package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;

import static uk.ac.qub.eeecs.gage.engine.input.TouchEvent.TOUCH_DOWN;

/**
 * Created by stephenmcveigh on 08/01/2018.
 */

public class ListBox extends GameObject {
    //////////////////////////////////////////////////////
    //  Constants
    //////////////////////////////////////////////////////
    public final int ITEM_HEIGHT = 100;
    public final float SIDE_BAR_COVERAGE = 0.1f;

    //////////////////////////////////////////////////////
    //  Properties
    //////////////////////////////////////////////////////
    /*
    The Array of strings that are displayed in the list box
     */
    private ArrayList<String> items;

    /*
    The index of the currently selected item.
    If no item is selected, index = -1
     */
    private int selectedIndex;

    /*
    The background colour of the listbox
     */
    private int backColor;

    /*
    The colour of the border around the listbox
     */
    private int borderColor;

    /*
    The colour of the selected Index slot
     */
    private int selectionColor;

    /*
    The color of the text in the listbox
     */
    private int textColor;

    /*
    Keeps track of the page number that is currently displayed
     */
    private int showingPageNum = 0;

    /*
    The buttons on the listbox to move between pages
     */
    private PushButton btnNextPage, btnPreviousPage;

    //////////////////////////////////////////////////////
    //  Constructors
    //////////////////////////////////////////////////////
    public ListBox(float x, float y, float width, float height, GameScreen gameScreen) {
        //The height will be rounded to the nearest ITEM_HEIGHT -- Cannot be referenced here until super is called (100)
        super(x, y, width, Math.round(((int)height + 50)/100)*100, null, gameScreen);
        this.items = new ArrayList<>();
        this.selectedIndex = -1;
        this.backColor = Color.WHITE;
        this.borderColor = Color.BLACK;
        this.selectionColor = Color.CYAN;
        this.textColor = Color.BLACK;
        loadAssets();
        btnPreviousPage = new PushButton(position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE/2, position.y - mBound.halfHeight + ITEM_HEIGHT/2, 60,50, "ArrowUp","ArrowUpPushed", gameScreen);
        btnNextPage = new PushButton(position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE/2, position.y + mBound.halfHeight - ITEM_HEIGHT/2,60, 50,"ArrowDown","ArrowDownPushed",  gameScreen);
    }

    //////////////////////////////////////////////////////
    //  Getters
    //////////////////////////////////////////////////////
    public int getSelectedIndex() { return selectedIndex;}
    public String getSelectedItem() {
        if (selectedIndex == -1) return "";
        return items.get(selectedIndex);
    }
    public int getShowingPageNum() {return showingPageNum;}
    public ArrayList<String> getItems() {return items;}

    //////////////////////////////////////////////////////
    //  Setters
    //////////////////////////////////////////////////////
    public void setArray(ArrayList<String> array) {this.items = new ArrayList<>(array);}
    public void setBackColor(int color) {backColor = color;}
    public void setBorderColor(int color) {borderColor = color;}
    public void setSelectionColor(int color) {selectionColor = color;}
    public void setTextColor(int color) {textColor = color;}
    public void setSelectedIndex(int selectedIndex) {this.selectedIndex = selectedIndex;}

    //////////////////////////////////////////////////////
    //  Methods
    //////////////////////////////////////////////////////
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x,y);
        setButtonPositions();
    }

    /**
     * Set the position of the next and previous page buttons on this listbox
     */
    public void setButtonPositions() {
        btnPreviousPage.setPosition(position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE/2, position.y - mBound.halfHeight + ITEM_HEIGHT/2);
        btnNextPage.setPosition(position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE/2, position.y + mBound.halfHeight - ITEM_HEIGHT/2);
    }

    /**
     * Check if the passed in coordinates are inside the side bar of the list box
     *
     * @param x
     * @param y
     * @return
     */
    private boolean touchOccurredInSideBar(float x, float y) {
        if (x > position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE && x < position.x + mBound.halfWidth
                && y > position.y - mBound.halfHeight && y < position.y + mBound.halfHeight) return true;
        return false;
    }

    /**
     * Set the selected index of the list box depending on the coordinates of a touch event
     *
     * @param touchX
     * @param touchY
     */
    private void handleTouchEvents(float touchX, float touchY) {
        if (touchOccurredInSideBar(touchX, touchY)) return; //Exit if the touch event occurs in the side bar
        int drawnIndex = (int)((touchY - (position.y - mBound.halfHeight)) / ITEM_HEIGHT);
        selectedIndex = (int)(showingPageNum * mBound.getHeight()/ ITEM_HEIGHT + drawnIndex);
        if (selectedIndex > items.size() - 1) selectedIndex = -1;
    }

    private void handlePrevButtonTrigger() {
        if (showingPageNum > 0) {
            showingPageNum--;
        }
    }

    private int getNumberOfItemsPerPage() {
        return (int)(mBound.getHeight() / ITEM_HEIGHT);
    }

    private boolean isNextButtonEnabled() {
        if (getNumberOfItemsPerPage() * (showingPageNum + 1) < items.size())
            return true;
        return false;
    }

    private void handleNextButtonTrigger() {
        if (isNextButtonEnabled()) {
            showingPageNum++;
        }
    }

    private void loadAssets() {
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        if (assetManager.getBitmap("boxBackground") == null)
            assetManager.loadAndAddBitmap("boxBackground", "img/white.png");
        if (assetManager.getBitmap("ArrowDown") == null)
            assetManager.loadAndAddBitmap("ArrowDown", "img/ArrowDown.png");
        if (assetManager.getBitmap("ArrowUp") == null)
            assetManager.loadAndAddBitmap("ArrowUp", "img/ArrowUp.png");
        if (assetManager.getBitmap("ArrowDownPushed") == null)
            assetManager.loadAndAddBitmap("ArrowDownPushed", "img/ArrowDownPushed.png");
        if (assetManager.getBitmap("ArrowUpPushed") == null)
            assetManager.loadAndAddBitmap("ArrowUpPushed", "img/ArrowUpPushed.png");
        mBitmap = assetManager.getBitmap("boxBackground");
    }

    public void update(ElapsedTime elapsedTime) {
        // Consider any touch events occurring in this update
        Input input = mGameScreen.getGame().getInput();

        // Check for a touch event on this listBox
        for (TouchEvent touchEvent : input.getTouchEvents()) {
            if (getBound().contains(touchEvent.x, touchEvent.y) && touchEvent.type == TOUCH_DOWN) {
                handleTouchEvents(touchEvent.x, touchEvent.y);
            }
        }

        //Update the two buttons
        btnPreviousPage.update(elapsedTime);
        btnNextPage.update(elapsedTime);

        //Check for interaction with these buttons to switch between page numbers and handle appropriately
        if (btnPreviousPage.isPushTriggered()) {
            handlePrevButtonTrigger();
        } else if (btnNextPage.isPushTriggered()) {
            handleNextButtonTrigger();
        }
    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));

        Paint paint = mGameScreen.getGame().getPaint(); //Get the game's paint object

        //Draw the listbox itself
        paint.setColorFilter(new LightingColorFilter(backColor, 0));
        graphics2D.drawBitmap(mBitmap, null, drawScreenRect, paint);

        //Draw the highlight on the selected index
        paint.reset();
        paint.setColor(selectionColor);
        if (selectedIndex / getNumberOfItemsPerPage() == showingPageNum && selectedIndex > -1) {
            int rectIndex = selectedIndex % getNumberOfItemsPerPage();
            graphics2D.drawRect(position.x - mBound.halfWidth,position.y - mBound.halfHeight + rectIndex * 100,  position.x + mBound.halfWidth, position.y - mBound.halfHeight + (rectIndex + 1) * 100, paint);
        }

        //Draw the text
        paint.reset();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(30);
        for (int i = showingPageNum * getNumberOfItemsPerPage(); i < (showingPageNum + 1) * getNumberOfItemsPerPage(); i++) {
            if (i == items.size()) break;
            paint.setColor(textColor);
            graphics2D.drawText(items.get(i), position.x, position.y - mBound.halfHeight + 60 + (ITEM_HEIGHT * (i - showingPageNum * getNumberOfItemsPerPage())), paint);
            paint.setColor(Color.GRAY);
            float lineY = position.y - mBound.halfHeight + (ITEM_HEIGHT * (i + 1 - showingPageNum * getNumberOfItemsPerPage()));
            if (lineY < mBound.getTop()) {
                graphics2D.drawLine(position.x - mBound.halfWidth + 10, lineY, position.x + mBound.halfWidth - 10, lineY, paint);
            }
        }

        //draw the buttons
        paint.reset();
        if (isNextButtonEnabled() || showingPageNum > 0) {
            paint.setColor(Color.argb(175, 0,176,186));
            graphics2D.drawRect(position.x + mBound.halfWidth - mBound.getWidth() * SIDE_BAR_COVERAGE, position.y - mBound.halfHeight, position.x + mBound.halfWidth, position.y + mBound.halfHeight, paint);
            if (isNextButtonEnabled()) {
                btnNextPage.draw(elapsedTime, graphics2D);
            }
            if (showingPageNum > 0) {
                btnPreviousPage.draw(elapsedTime, graphics2D);
            }
        }

        //Draw the border
        paint.reset();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        graphics2D.drawRect(position.x - mBound.halfWidth, position.y - mBound.halfHeight, position.x + mBound.halfWidth, position.y + mBound.halfHeight, paint);
    }

    public void addItem(String item) {items.add(item);}

    public void clear() {items.clear();}

    public void removeItem(int index) {
        if (index == selectedIndex) {
            selectedIndex = -1;
        }
        items.remove(index);
    }
}