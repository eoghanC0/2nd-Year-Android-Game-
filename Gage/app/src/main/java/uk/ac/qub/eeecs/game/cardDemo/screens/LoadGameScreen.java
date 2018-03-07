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

    private PushButton mNextButton;
    private PushButton mDeleteButton;

    private final Bitmap background;
    private final Rect backGroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    private Paint myPaint = mGame.getPaint();

    private Bitmap title;

    //TODO: Erase button

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
        createTestSaves();
        setUpSaveData();

        //Buttons
        float width = mGame.getScreenWidth();
        float height = mGame.getScreenHeight();
        mNextButton = new PushButton(
                width * 0.9f, height * 0.8f, height / 4.0f, height / 4.0f, "NextButton","NextButtonActive", this );
        mDeleteButton = new PushButton(
                width * 0.1f, height * 0.8f, height / 4.0f, height / 4.0f, "DeleteIcon","DeleteIconPushed", this );


    }

    private void changeToScreen(FootballGameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    //Sets selected save to empty values
    private void deleteSave(){
        mGame.setPlayerName("");
        mGame.setClub(new ArrayList<Card>());
        mGame.setWins(0);
        mGame.setLosses(0);
        mGame.setDraws(0);
        mGame.setXp(0);
        mGame.setDifficulty(0);
        mGame.setGameLength(0);
        mGame.setPitchBackGround("");
        mGame.saveGame(lbxGameSaves.getSelectedIndex());
        //refreshes list box
        setUpSaveData();
    }

    //TODO: Delete when no longer needed
    //Creates saves for testing
    private void createTestSaves(){
        Random rand;
        for(int i = 0; i < mGame.getSaveSlotMax(); i++) {
            rand = new Random();
            int n = rand.nextInt(50) + 1;
            mGame.setPlayerName("Test_Name" + i);
            mGame.setClub(new ArrayList<Card>());
            mGame.setWins(rand.nextInt(50) + 1);
            mGame.setLosses(rand.nextInt(50) + 1);
            mGame.setDraws(rand.nextInt(50) + 1);
            mGame.setXp(rand.nextInt(5000) + 1);
            mGame.setDifficulty(rand.nextInt(3) + 1);
            mGame.setGameLength(rand.nextInt(90) + 1);
            mGame.setPitchBackGround("BG" + i);
            mGame.saveGame(i);
        }
    }

    //gets save name, time/date etc and adds to list box for each save.
    private void setUpSaveData(){
        lbxGameSaves.clear();
        String saveTitle;
        for(int i = 0; i < mGame.getSaveSlotMax(); i++) {
            mGame.loadGame(i);
            if (mGame.getLastSaveDate() != null && !mGame.getPlayerName().equals(""))
                saveTitle = String.format("Save: %d - %s - %s",(i+1), mGame.getPlayerName(), mGame.getLastSaveDate());
            else saveTitle = String.format("Save: %d - Empty",(i+1));
            lbxGameSaves.addItem(saveTitle);
        }
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        lbxGameSaves.update(elapsedTime);

        mNextButton.update(elapsedTime);
        if (mNextButton.isPushTriggered() && lbxGameSaves.getSelectedIndex() != -1) {
            mGame.loadGame(lbxGameSaves.getSelectedIndex());
            changeToScreen(new MenuScreen(mGame));
        }
        mDeleteButton.update(elapsedTime);
        if (mDeleteButton.isPushTriggered()&& lbxGameSaves.getSelectedIndex() != -1) {
            deleteSave();
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.drawBitmap(background, null, backGroundRectangle,myPaint);
        lbxGameSaves.draw(elapsedTime, graphics2D);
        mNextButton.draw(elapsedTime, graphics2D,null,null);
        mDeleteButton.draw(elapsedTime, graphics2D,null,null);

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
