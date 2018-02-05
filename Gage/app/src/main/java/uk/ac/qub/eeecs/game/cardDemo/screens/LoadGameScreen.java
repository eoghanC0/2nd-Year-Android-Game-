package uk.ac.qub.eeecs.game.cardDemo.screens;


import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.ui.ListBox;

/**
 * Created by stephenmcveigh on 21/01/2018.
 */

public class LoadGameScreen extends FootballGameScreen {
    private ListBox lbxGameSaves;

    private PushButton mNextButton;

    private final Bitmap background;
    private final Rect backGroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());


    //TODO: Erase button

    public LoadGameScreen(FootballGame game) {
        super("LoadGameScreen", game);
        lbxGameSaves = new ListBox(600,500,400,300, this);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("menuScreenBackground", "img/help-background.jpg");
        background = assetManager.getBitmap("menuScreenBackground");
        assetManager.loadAndAddBitmap("NextButton", "img/RightArrow.png");
        assetManager.loadAndAddBitmap("NextButtonActive", "img/RightArrowActive.png");
        //TODO: Replace with player names/difficulty/date and only add if they exist.
        lbxGameSaves.addItem("Save 1");
        lbxGameSaves.addItem("Save 2");
        lbxGameSaves.addItem("Save 3");

        //Buttons
        float width = mGame.getScreenWidth();
        float height = mGame.getScreenHeight();
        mNextButton = new PushButton(
                width * 0.925f, height * 0.9f, width / 10.0f, height / 6.0f, "NextButton","NextButtonActive", this );
        }

    private void changeToScreen(FootballGameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        lbxGameSaves.update(elapsedTime);

        mNextButton.update(elapsedTime);
        if (mNextButton.isPushTriggered() && lbxGameSaves.getSelectedIndex() != -1) {
            mGame.loadGame(lbxGameSaves.getSelectedIndex());
            changeToScreen(new MenuScreen(mGame));
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint myPaint = mGame.getPaint();
        graphics2D.drawBitmap(background, null, backGroundRectangle,myPaint);
        lbxGameSaves.draw(elapsedTime, graphics2D);
        mNextButton.draw(elapsedTime, graphics2D,null,null);
    }
}
