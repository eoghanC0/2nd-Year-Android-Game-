package uk.ac.qub.eeecs.game.cardDemo.screens;


import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.game.cardDemo.objects.FootballGameScreen;
import uk.ac.qub.eeecs.game.cardDemo.objects.FootballGame;
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

    private AssetStore assetManager = mGame.getAssetManager();


    public LoadGameScreen(FootballGame game) {
        super("LoadGameScreen", game);
        lbxGameSaves = new ListBox(mGame.getScreenWidth() * 0.5f,mGame.getScreenHeight() * 0.5f,1000,300, this);
        assetManager.loadAndAddBitmap("MainBackground", "img/MainBackground.jpg");
        background = assetManager.getBitmap("MainBackground");
        assetManager.loadAndAddBitmap("NextButton", "img/PlayIcon.png");
        assetManager.loadAndAddBitmap("NextButtonActive", "img/PlayIconPushed.png");
        assetManager.loadAndAddBitmap("DeleteIcon", "img/DeleteIcon.png");
        assetManager.loadAndAddBitmap("DeleteIconPushed", "img/DeleteIconPushed.png");
        assetManager.loadAndAddBitmap("Title", "img/Title.png");
        title = assetManager.getBitmap("Title");

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
        assetManager.deleteSave(lbxGameSaves.getSelectedIndex());
        setupSavesListBox();
    }

    //gets save name, time/date etc and adds to list box for each save.
    private void setupSavesListBox(){
        lbxGameSaves.clear();
        for(int i = 0; i < mGame.MAX_SAVE_SLOTS; i++) {
            lbxGameSaves.addItem("Save Slot " + Character.toString((char)(65 + i)) + ": New Game");
        }
        for(String saveFile : assetManager.getSaveFiles()) {
            int index = Integer.parseInt(saveFile.split("_")[1]);
            lbxGameSaves.getItems().set(index, lbxGameSaves.getItems().get(index).split(":")[0] + ": " + saveFile.split("_")[2].split("\\.")[0]);
        }
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        lbxGameSaves.update(elapsedTime);

        nextButton.update(elapsedTime);
        if (nextButton.isPushTriggered() && lbxGameSaves.getSelectedIndex() > -1) {
            if (!lbxGameSaves.getSelectedItem().contains("New Game")) {
                mGame.loadGame(lbxGameSaves.getSelectedIndex());
                changeToScreen(new MenuScreen(mGame));
            } else {
                mGame.initialiseNewGame(lbxGameSaves.getSelectedIndex());
                changeToScreen(new StarterPackScreen(mGame));
            }
        }

        deleteButton.update(elapsedTime);
        if (deleteButton.isPushTriggered() && !lbxGameSaves.getSelectedItem().contains("New Game")) {
            deleteSave();
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint myPaint = mGame.getPaint();
        graphics2D.drawBitmap(background, null, backGroundRectangle,myPaint);
        lbxGameSaves.draw(elapsedTime, graphics2D);

        if (lbxGameSaves.getSelectedIndex() > -1)
            nextButton.draw(elapsedTime, graphics2D,null,null);

        if (lbxGameSaves.getSelectedIndex() > -1 && !lbxGameSaves.getSelectedItem().contains("New Game"))
            deleteButton.draw(elapsedTime, graphics2D,null,null);

        graphics2D.drawBitmap(title, null, new Rect(5,50,mGame.getScreenWidth() - 5, (int) (mGame.getScreenHeight() * 0.2) - 5), myPaint);
    }
}
