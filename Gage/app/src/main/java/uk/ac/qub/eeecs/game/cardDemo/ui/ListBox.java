package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Created by stephenmcveigh on 08/01/2018.
 */

public class ListBox extends GameObject {
    //////////////////////////////////////////////////////
    //  Properties
    //////////////////////////////////////////////////////
    /*
    The Array of strings that are displayed in the listbox
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
    True when the listbox should be displayed and enabled, false otherwise
     */
    private boolean visible = true;

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
        //The height will be rounded to the nearest 100, since each selection is 100 high
        super(x, y, width, Math.round(((int)height + 50)/100)*100, null, gameScreen);
        this.items = new ArrayList<>();
        this.selectedIndex = -1;
        this.backColor = Color.WHITE;
        this.borderColor = Color.BLACK;
        this.selectionColor = Color.BLUE;
        this.textColor = Color.BLACK;
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("boxBackground", "img/white.png");
        mBitmap = assetManager.getBitmap("boxBackground");
        assetManager.loadAndAddBitmap("downArrow", "img/DownArrow.png");
        assetManager.loadAndAddBitmap("upArrow", "img/UpArrow.png");
        assetManager.loadAndAddBitmap("downArrowActive", "img/DownArrowActive.png");
        assetManager.loadAndAddBitmap("upArrowActive", "img/UpArrowActive.png");
        btnPreviousPage = new PushButton(mBound.getRight() + 30, mBound.getBottom() + 25, 60,50, "upArrow","upArrowActive", gameScreen);
        btnNextPage = new PushButton(mBound.getRight() + 30, mBound.getTop() - 25,60, 50,"downArrow","downArrowActive",  gameScreen);
    }

    //////////////////////////////////////////////////////
    //  Getters
    //////////////////////////////////////////////////////
    public int getSelectedIndex() { return selectedIndex;}
    public boolean isVisible() {return visible;}
    public int getShowingPageNum() {return showingPageNum;}

    //////////////////////////////////////////////////////
    //  Setters
    //////////////////////////////////////////////////////
    public void setBackColor(int color) {backColor = color;}
    public void setBorderColor(int color) {borderColor = color;}
    public void setSelectionColor(int color) {selectionColor = color;}
    public void setTextColor(int color) {textColor = color;}
    public void setVisible(boolean isVisible) {visible = isVisible;}

    //////////////////////////////////////////////////////
    //  Methods
    //////////////////////////////////////////////////////
    public void update(ElapsedTime elapsedTime) {
        if (visible) {  //Only update if visible and enabled
            // Consider any touch events occurring in this update
            Input input = mGameScreen.getGame().getInput();

            // Check for a touch event on this listBox
            for (TouchEvent touchEvent : input.getTouchEvents()) {
                if (getBound().contains(touchEvent.x, touchEvent.y)) {
                    int drawnIndex = (int)((touchEvent.y - mBound.getBottom()) / 100);
                    selectedIndex = (int)(showingPageNum * mBound.getHeight()/ 100 + drawnIndex);
                    if (selectedIndex > items.size() - 1) selectedIndex = -1;
                }
            }

            //Update the two buttons
            btnPreviousPage.update(elapsedTime);
            btnNextPage.update(elapsedTime);

            //Check for interaction with these buttons to switch between page numbers and handle appropriately
            if (btnPreviousPage.isPushTriggered()) {
                if (showingPageNum != 0) {
                    showingPageNum--;
                }
            } else if (btnNextPage.isPushTriggered()) {
                if ((int)mBound.getHeight() / 100 * (showingPageNum + 1) < items.size()) {
                    showingPageNum++;
                }
            }
        }
    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        if (visible) {  //Only draw if visible
            drawScreenRect.set((int) (position.x - mBound.halfWidth),
                    (int) (position.y - mBound.halfHeight),
                    (int) (position.x + mBound.halfWidth),
                    (int) (position.y + mBound.halfHeight));

            Paint paint = mGameScreen.getGame().getPaint(); //Get the game's paint object

            //Draw the border
            paint.setColor(borderColor);
            graphics2D.drawRect(mBound.getLeft() - 10, mBound.getBottom() - 10, mBound.getRight(), mBound.getTop() + 10, paint);
            graphics2D.drawRect(mBound.getRight(), mBound.getBottom() - 10, mBound.getRight() + 60, mBound.getTop() + 10, paint);

            //Draw the listbox itself
            paint.reset();
            paint.setColorFilter(new LightingColorFilter(backColor, 0));
            graphics2D.drawBitmap(mBitmap, null, drawScreenRect, paint);

            //Draw the highlight on the selected index
            paint.reset();
            paint.setColor(selectionColor);
            if (selectedIndex / (int)(mBound.getHeight() / 100) == showingPageNum && selectedIndex > -1) {
                int rectIndex = selectedIndex % (int)(mBound.getHeight() / 100);
                graphics2D.drawRect(mBound.getLeft(),mBound.getBottom() + rectIndex * 100,  mBound.getRight(), mBound.getBottom() + (rectIndex + 1) * 100, paint);
            }

            //Draw the text
            paint.reset();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(30);
            for (int i = showingPageNum * (int)(mBound.getHeight() / 100); i < showingPageNum * (int)(mBound.getHeight() / 100) + (int)(mBound.getHeight() / 100); i++) {
                if (i == items.size()) break;
                paint.setColor(textColor);
                graphics2D.drawText(items.get(i), position.x, mBound.getBottom() + 60 + (100 * (i - showingPageNum * (int)(mBound.getHeight() / 100))), paint);
                paint.setColor(Color.GRAY);
                float lineY = mBound.getBottom() + (100 * (i + 1 - showingPageNum * (int)(mBound.getHeight() / 100)));
                if (lineY < mBound.getTop()) {
                    graphics2D.drawLine(mBound.getLeft() + 10, lineY, mBound.getRight() - 10, lineY, paint);
                }
            }

            //Finally, draw the buttons
            paint.reset();
            if ((int)mBound.getHeight() / 100 * (showingPageNum + 1) < items.size()) {
                btnNextPage.draw(elapsedTime, graphics2D);
            }
            if (showingPageNum != 0) {
                btnPreviousPage.draw(elapsedTime, graphics2D);
            }
        }
    }

    public void addItem(String item) {items.add(item);}
    public void removeItem(int index) {
        if (index == selectedIndex) {
            selectedIndex = -1;
        }
        items.remove(index);
    }
}