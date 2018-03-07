package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;

/**
 * Created by Eoghan on 14/02/2018.
 */

public class popUpWindow extends GameObject {
    private String message, button1Text, button2Text;
    private Boolean yesOrNo = false;
    private  Boolean hasNoBeenPressed = false;
    private PushButton Yes;
    private PushButton No;
    // Define the spacing that will be used to position the buttons
    int spacingX = mGameScreen.getGame().getScreenWidth() / 4;
    int spacingY = mGameScreen.getGame().getScreenHeight() / 8;

    public popUpWindow(float x, float y, float width, float height, GameScreen gameScreen, String message, String button1Text,String button2Text) {
        super(x, y, width > 0 ? width : -width, height > 0 ? height : -height, null, gameScreen);
        this.button1Text = button1Text;
        this.button2Text = button2Text;
        this.message = message;

        // Load in the bitmaps used on the popUp window
        AssetStore assetManager = gameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("menuButtons", "img/MenuButton.png");


        Yes = new PushButton(
                (spacingX / 2) * 5, (spacingY / 2) * 10, spacingX/2, spacingY, "menuButtons", gameScreen);
        No = new PushButton(
                (spacingX / 2) * 3, (spacingY / 2) * 10, spacingX/2, spacingY, "menuButtons", gameScreen);

        Yes.setButtonText(button1Text, 32, Color.WHITE);
        No.setButtonText(button2Text, 32, Color.WHITE);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
        //Update each button and transition if needed
        Yes.update(elapsedTime);
        No.update(elapsedTime);

        if (Yes.isPushTriggered()) {
            yesOrNo = true;
        }
            else if (No.isPushTriggered()) hasNoBeenPressed = true;
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint myPaint = new Paint();
        myPaint.setColor(Color.BLACK);

        graphics2D.drawRect((float)spacingX, (float)spacingY*2, (float)spacingX*3,(float)spacingY*6,myPaint);
        myPaint.setColor(Color.WHITE);
        myPaint.setTextSize(50);
        graphics2D.drawText(message, spacingX * 1.1f,spacingY * 3,myPaint);
        Yes.draw(elapsedTime, graphics2D, null, null);
        No.draw(elapsedTime, graphics2D);
    }

    public boolean getYesorNo() {
        return yesOrNo;
    }

    public boolean getHasNoBeenPressed() {
        return hasNoBeenPressed;
    }

    public void setYesorNo(boolean resetYesOrNo) {yesOrNo = resetYesOrNo; }

    public void setHasNoBeenPressed(boolean resethasNoBeenPressed) {hasNoBeenPressed = resethasNoBeenPressed; }

    public void disableButton1() {Yes.setEnabled(false);}

    public void disableButton2() {No.setEnabled(false);}

    public void enableButton1() {Yes.setEnabled(true);}

    public void enableButton2() {No.setEnabled(true);}

}
