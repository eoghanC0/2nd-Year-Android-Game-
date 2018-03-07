package uk.ac.qub.eeecs.game.cardDemo.screens;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;
import uk.ac.qub.eeecs.game.cardDemo.ui.ListBox;

/**
 * Created by stephenmcveigh on 21/01/2018.
 */

public class LoadGameScreen extends FootballGameScreen {
    private ListBox lbxGameSaves;

    private PushButton nextButton;
    private PushButton deleteButton;

    private final Bitmap background;
    private final Rect backGroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());
    private Bitmap title;


    public LoadGameScreen(FootballGame game) {
        super("LoadGameScreen", game);
        lbxGameSaves = new ListBox(mGame.getScreenWidth() * 0.5f,mGame.getScreenHeight() * 0.5f,1000,300, this);
        lbxGameSaves.setSelectionColor(Color.CYAN);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("MainBackground", "img/MainBackground.jpg");
        background = assetManager.getBitmap("MainBackground");
        assetManager.loadAndAddBitmap("NextButton", "img/PlayIcon.png");
        assetManager.loadAndAddBitmap("NextButtonActive", "img/PlayIconPushed.png");
        assetManager.loadAndAddBitmap("DeleteIcon", "img/DeleteIcon.png");
        assetManager.loadAndAddBitmap("DeleteIconPushed", "img/DeleteIconPushed.png");
        assetManager.loadAndAddBitmap("Title", "img/Title.png");
        title = assetManager.getBitmap("Title");

        //TODO: Remove below method
        setupSavesListBox();

        //Buttons
        float width = mGame.getScreenWidth();
        float height = mGame.getScreenHeight();
        nextButton = new PushButton(
                width * 0.9f, height * 0.8f, height / 4.0f, height / 4.0f, "NextButton","NextButtonActive", this );
        deleteButton = new PushButton(
                width * 0.1f, height * 0.8f, height / 4.0f, height / 4.0f, "DeleteIcon","DeleteIconPushed", this );


    }

    private void changeToScreen(FootballGameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    //Sets selected save to empty values
    private void deleteSave(){

    }

    //gets save name, time/date etc and adds to list box for each save.
    private void setupSavesListBox(){
        lbxGameSaves.clear();
        mGame.setXp(20000);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        lbxGameSaves.update(elapsedTime);

        nextButton.update(elapsedTime);
        if (nextButton.isPushTriggered()) {
            mGame.loadGame(lbxGameSaves.getSelectedIndex());
            changeToScreen(new MenuScreen(mGame));
        }

        deleteButton.update(elapsedTime);
        if (deleteButton.isPushTriggered()&& lbxGameSaves.getSelectedIndex() != -1) {
            deleteSave();
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint myPaint = mGame.getPaint();
        graphics2D.drawBitmap(background, null, backGroundRectangle,myPaint);
        lbxGameSaves.draw(elapsedTime, graphics2D);
        nextButton.draw(elapsedTime, graphics2D,null,null);
        deleteButton.draw(elapsedTime, graphics2D,null,null);

        graphics2D.drawBitmap(title, null, new Rect(5,50,mGame.getScreenWidth() - 5, (int) (mGame.getScreenHeight() * 0.2) - 5), myPaint);
    }

    /**
     * Gets area occupied by block of text
     * @param paint
     * @param text
     * @return area occupied
     */
    private Rect getTextBounds(Paint paint, String text) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds;
    }
}
