package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.gage.util.Vector2;

/**
 * Created by eimhin on 30/11/2017.
 */

public class InfoBar extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Player data
     */
    private Bitmap playerBitmap;
    private String playerName;
    private String winLossDraw;
    private int experience;
    private Rect playerIconRect;
    private Paint textPaint;

    /**
     * Area text content
     */
    private String areaOneText;
    private String areaTwoText;
    private String areaThreeText;

    /**
     * Area text coordinates
     */
    private Vector2 areaOneVector;
    private Vector2 areaTwoVector;
    private Vector2 areaThreeVector;

    /**
     * Internal matrix use to support draw requests
     */
    protected Matrix drawMatrix = new Matrix();

    /**
     * Stores queued notifications
     */
    ArrayList<iNotification> notificationArrayList = new ArrayList<iNotification>();

    /**
     * Current notification
     */
    iNotification currentNotification;

    /**
     * Notification class
     */
    // TODO: Continue work on iNotification
    private class iNotification {
        private String text;
        private int type;
        private float displayTime;

        public iNotification(String text, int type, float displayTime) {
            this.text = text;
            this.type = type;
            this.displayTime = displayTime;
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    // TODO: Clean up constructors

    /**
     * Main constructor
     * @param x
     * @param y
     * @param width
     * @param height
     * @param gameScreen
     */
    public InfoBar(float x, float y, float width, float height, GameScreen gameScreen) {
        super(x, y, width, height, null, gameScreen);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("InfoBar", "img/InfoBar.png");
        assetManager.loadAndAddBitmap("PlayerIcon", "img/Ball.png");
        mBitmap = assetManager.getBitmap("InfoBar");
        playerBitmap = assetManager.getBitmap("InfoBar");
        playerIconRect = new Rect(200,200,200,200);
        this.playerName = "Player Name";
        this.winLossDraw = "0-0-0";
        this.experience = 0;
        areaOneText = "Area One Text";
        areaTwoText = "Area Two Text";
        areaThreeText = "Area Three Text";

        setDefaultProperties();
    }

    /**
     * Overloaded constructor allowing user to pass in player data
     * @param x
     * @param y
     * @param width
     * @param height
     * @param gameScreen
     * @param playerIconPath
     * @param playerName
     * @param winLossDraw
     * @param experience
     */
    public InfoBar(float x, float y, float width, float height, GameScreen gameScreen, String playerIconPath, String playerName, String winLossDraw, int experience) {
        super(x, y, width, height, null, gameScreen);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("InfoBar", "img/InfoBar.png");
        assetManager.loadAndAddBitmap("PlayerIcon", "img/Ball.png");
        mBitmap = assetManager.getBitmap("InfoBar");
        playerBitmap = assetManager.getBitmap("PlayerIcon");
        playerIconRect = new Rect(200,200,200,200);
        this.playerName = playerName;
        areaOneText = playerName;
        this.winLossDraw = winLossDraw;
        areaTwoText = winLossDraw;
        this.experience = experience;
        areaThreeText = String.valueOf(experience);

        setDefaultProperties();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Called inside the constructors, this sets default properties of InfoBar
     */
    private void setDefaultProperties() {
        textPaint = new Paint();
        textPaint.setTextSize(getBound().getHeight() * 0.40f);
        textPaint.setColor(Color.WHITE);

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));
    }

    // TODO: Add notification queue system
    public void addNotification(String notification) {

    }

    // TODO: Check notification queue and update current notification
    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        if (GraphicsHelper.getSourceAndScreenRect(this, layerViewport,
                screenViewport, drawSourceRect, drawScreenRect)) {

            float scaleX =
                    (float) drawScreenRect.width()
                            / (float) drawSourceRect.width();
            float scaleY =
                    (float) drawScreenRect.height()
                            / (float) drawSourceRect.height();

            // Build an appropriate transformation matrix
            drawMatrix.reset();
            drawMatrix.postScale(scaleX, scaleY);
            drawMatrix.postRotate(0, scaleX * mBitmap.getWidth()
                    / 2.0f, scaleY * mBitmap.getHeight() / 2.0f);
            drawMatrix.postTranslate(drawScreenRect.left, drawScreenRect.top);

            // Draw the image
            graphics2D.drawBitmap(mBitmap, drawMatrix, null);
        }

        Vector2 areaOneVector = getAreaTextVector(textPaint, areaOneText, getBound().getWidth(), getBound().getHeight(), 0.06f, 0.313f, 0);
        Vector2 areaTwoVector = getAreaTextVector(textPaint, areaTwoText, getBound().getWidth(), getBound().getHeight(), 0.373f, 0.313f, 1);
        Vector2 areaThreeVector = getAreaTextVector(textPaint, areaThreeText, getBound().getWidth(), getBound().getHeight(), 0.686f, 0.313f, 2);

        graphics2D.drawText(areaOneText, areaOneVector.x, areaOneVector.y, textPaint);
        graphics2D.drawText(areaTwoText, areaTwoVector.x, areaTwoVector.y, textPaint);
        graphics2D.drawText(areaThreeText, areaThreeVector.x, areaThreeVector.y, textPaint);
    }

    /**
     * Method to calculate the X position of text elements inserted into InfoBar
     * @param paint
     * @param text
     * @param totalAreaWidth
     * @param offsetPercent
     * @param areaWidthPercentage
     * @param alignment 0 = left | 1 = center | 2 = right
     * @return Vector2
     */
    private Vector2 getAreaTextVector(Paint paint, String text, float totalAreaWidth, float totalAreaHeight, float offsetPercent, float areaWidthPercentage, int alignment) {
        Rect textBounds = getTextBounds(paint, text);
        float yVal = (totalAreaHeight * 0.76f) - (textBounds.height());

        switch(alignment) {
            case 0:
                return new Vector2((getBound().getWidth() * offsetPercent) + (totalAreaWidth * 0.01f), yVal);
            case 1:
                return new Vector2((getBound().getWidth() * offsetPercent) + (((totalAreaWidth * areaWidthPercentage) - getTextBounds(paint, text).width()) / 2), yVal);
            case 2:
                return new Vector2((getBound().getWidth() * offsetPercent) + ((totalAreaWidth * areaWidthPercentage) - getTextBounds(paint, text).width()) - (totalAreaWidth * 0.01f), yVal);
            default:
                return new Vector2(0,0);
        }
    }

    private Rect getTextBounds(Paint paint, String text) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds;
    }
}
