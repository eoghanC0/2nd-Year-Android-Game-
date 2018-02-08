package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

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
     * Icon bitmap
     */
    private Bitmap iconBitmap;

    /**
     * Paint object for text
     */
    private Paint textPaint;

    /**
     * Area text content container
     */
    private String areaOneText;
    private String areaTwoText;
    private String areaThreeText;

    /**
     * Area text content data
     */
    private String[] areaTextData = new String[3];

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
     * Current background bitmap
     */
    private String currentBitmap;

    /**
     * Stores whether a notification is currently displayed
     */
    private boolean notificationDisplayed = false;

    /**
     * Stores time that last notification was displayed
     */
    private long currentNotificationDisplayTime;

    /**
     * Stores queued notifications
     */
    private Queue<iNotification> notificationQueue = new LinkedList();

    /**
     * Current notification
     */
    private iNotification currentNotification;

    /**
     * Notification class
     */
    // TODO: Continue work on iNotification
    public class iNotification {
        /**
         * Data
         */
        private String text;
        private int type;
        private float displayTime;

        /**
         * iNotification constructor
         * @param text Notification message
         * @param type 0 = Default | 1 = Red | 2 = Green
         * @param displayTime Seconds to display notification
         */
        public iNotification(String text, int type, float displayTime) {
            this.text = text;

            if(!(type >= 0 && type <= 2)) this.type = 0;
            else this.type = type;

            if(!(displayTime > 0)) displayTime = 1f;
            this.displayTime = displayTime;
        }

        @Override
        public String toString() {
            return String.format("Notification: '%1$s' | Type: %2$d | Display Time: %3$.2f", text, type, displayTime);
        }

        public String getText() {
            return text;
        }
        public int getType() {
            return type;
        }
        public float getDisplayTime() {
            return displayTime;
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
        super(x, y, width > 0 ? width : -width, height > 0 ? height : -height, null, gameScreen);
        if(width < 0) width = 100;
        if(height < 0) height = 100;
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("IconBitmap", "img/empty.png");
        iconBitmap = assetManager.getBitmap("IconBitmap");

        areaTextData[0] = "";
        areaTextData[1] = "";
        areaTextData[2] = "";

        areaOneText = areaTextData[0];
        areaTwoText = areaTextData[1];
        areaThreeText = areaTextData[2];

        setDefaultProperties();
    }

    /**
     * Overloaded constructor allowing user to pass in text data
     * @param x
     * @param y
     * @param width
     * @param height
     * @param gameScreen
     * @param iconPath
     * @param areaOneText
     * @param areaTwoText
     * @param areaThreeText
     */
    public InfoBar(float x, float y, float width, float height, GameScreen gameScreen, String iconPath, String areaOneText, String areaTwoText, String areaThreeText) {
        super(x, y, width > 0 ? width : -width, height > 0 ? height : -height, null, gameScreen);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("IconBitmap", iconPath);
        iconBitmap = assetManager.getBitmap("IconBitmap");

        if(iconBitmap == null) {
            assetManager.loadAndAddBitmap("IconBitmap", "img/empty.png");
            iconBitmap = assetManager.getBitmap("IconBitmap");
        }

        areaTextData[0] = areaOneText;
        areaTextData[1] = areaTwoText;
        areaTextData[2] = areaThreeText;

        this.areaOneText = areaOneText;
        this.areaTwoText = areaTwoText;
        this.areaThreeText = areaThreeText;

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

        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("InfoBar", "img/InfoBar.png");
        assetManager.loadAndAddBitmap("InfoBarRed", "img/InfoBarRed.png");
        assetManager.loadAndAddBitmap("InfoBarGreen", "img/InfoBarGreen.png");

        mBitmap = assetManager.getBitmap("InfoBar");
    }

    /*  = = = = = = = = = = = = = = = =
        Notification Management Methods
        = = = = = = = = = = = = = = = = */

    /**
     * Adds notification to notification queue
     * @param notification
     * @param type
     * @param duration
     */
    public void addNotification(String notification, int type, float duration) {
        if(notification != null && type >= 0 && type <= 2 && duration > 0) {
            notificationQueue.add(new iNotification(notification, type, duration));
            Log.d("DEBUG", "Successfully added notification. Current queue: " + notificationQueue.toString());
        }
    }

    /**
     * Checks whether any notifications are ready in the queue and displays
     * the next notification
     */
    private void checkNotifications() {
       boolean notificationAvailable = false;
       // Notification present in queue
       if(notificationQueue.size() > 0) notificationAvailable = true;

       // Is a notification currently displayed?
       if(notificationDisplayed) {
           // States whether the current notification should be removed or not
           // true = remove false = remain
           boolean currentNotificationStatus = checkCurrentNotification();

           // If notification should be removed then display next notification if a notification is
           // present in the queue else return to default state
           if(!currentNotificationStatus && notificationAvailable) displayNextNotification();
           else if(!currentNotificationStatus && !notificationAvailable) setDefaultState();

           return;
       } else if(!notificationDisplayed && notificationAvailable){
           displayNextNotification();
       }
    }

    /**
     * Sets InfoBar back to default non-notification state
     */
    private void setDefaultState() {
        Log.d("DEBUG", "Returning to default..." + areaTextData[0]);
        setBitmap("InfoBar");
        areaOneText = areaTextData[0];
        areaTwoText = areaTextData[1];
        areaThreeText = areaTextData[2];
        notificationDisplayed = false;
    }

    /**
     * Sets InfoBar to notification state based on the type passed in
     * @param type 0 = Default | 1 = Red | 2 = Green
     */
    private void setNotificationState(int type) {
        switch(type) {
            case 0:
                setBitmap("InfoBar");
                areaOneText = "";
                areaTwoText = currentNotification.text;
                areaThreeText = "";
                notificationDisplayed = true;
                break;
            case 1:
                setBitmap("InfoBarRed");
                areaOneText = "";
                areaTwoText = currentNotification.text;
                areaThreeText = "";
                notificationDisplayed = true;
                break;
            case 2:
                setBitmap("InfoBarGreen");
                areaOneText = "";
                areaTwoText = currentNotification.text;
                areaThreeText = "";
                notificationDisplayed = true;
                break;
        }

    }

    /**
     * Checks whether the current notification displayed has exceeded its display time or not
     * @return
     */
    private boolean checkCurrentNotification() {
        if(System.nanoTime() - currentNotificationDisplayTime > (currentNotification.displayTime * 1e+9)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Assigns the next notification in the queue as the current notification
     */
    private void displayNextNotification() {
        currentNotification = notificationQueue.element();
        notificationQueue.remove();
        currentNotificationDisplayTime = System.nanoTime();
        setNotificationState(currentNotification.type);
    }

    public void clearNotifications() {
        if(!(notificationQueue.size() > 0)) return;
        for (iNotification i : notificationQueue) {
            notificationQueue.remove(i);
        }
    }

    /*  = = = = = = = = = = = = = = = = = = = =
        End Of Notification Management Methods
        = = = = = = = = = = = = = = = = = = = =*/

    /**
     * Sets the bitmap of the InfoBar
     * @param bg
     */
    private void setBitmap(String bg) {
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        if(bg == "InfoBar" || bg == "InfoBarRed" || bg == "InfoBarGreen") mBitmap = assetManager.getBitmap(bg);
    }

    /**
     * Sets data of an element within areaTextData array
     * @param data
     * @param index
     */
    private void setAreaTextDataByIndex(String data, int index) {
        if(index >= 0 && index <= 3)
            areaTextData[index] = data;
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
        checkNotifications();

    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        float scaleX = (float) drawScreenRect.width() / (float) drawSourceRect.width();
        float scaleY = (float) drawScreenRect.height() / (float) drawSourceRect.height();

        // TODO: Profile icon needs more work. Probably doesn't work on different resolutions.
        // Build an appropriate transformation matrix
        drawMatrix.reset();
        drawMatrix.postScale(scaleX, scaleY);
        drawMatrix.postRotate(0, scaleX * iconBitmap.getWidth()
                / 2.0f, scaleY * iconBitmap.getHeight() / 2.0f);
        drawMatrix.postTranslate(drawScreenRect.left, drawScreenRect.top);
        drawMatrix.setScale(0.35f, 0.35f);

        // Draw the image
        graphics2D.drawBitmap(iconBitmap, null, drawScreenRect,null);

        // Draw the image
        drawScreenRect.set(0,0,mGameScreen.getGame().getScreenWidth(),(int) (mGameScreen.getGame().getScreenHeight() * 0.1));
        graphics2D.drawBitmap(mBitmap, null, drawScreenRect, null);

        areaOneVector = getAreaTextVector(textPaint, areaOneText, getBound().getWidth(), getBound().getHeight(), 0.06f, 0.313f, 0);
        areaTwoVector = getAreaTextVector(textPaint, areaTwoText, getBound().getWidth(), getBound().getHeight(), 0.373f, 0.313f, 1);
        areaThreeVector = getAreaTextVector(textPaint, areaThreeText, getBound().getWidth(), getBound().getHeight(), 0.686f, 0.313f, 2);

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

    /**
     * Gets area occupied by block of text
     * @param paint
     * @param text
     * @return area occupied
     */
    private Rect getTextBounds(Paint paint, String text) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds;
    }

    /*  = = = = = = = = = = = = = = = =
      GETTERS AND SETTERS FOR TESTING
    = = = = = = = = = = = = = = = = */

    public void setCurrentNotificationDisplayTime(long time) {
        currentNotificationDisplayTime = time;
    }

    public iNotification getCurrentNotification() {
        return currentNotification;
    }

    public boolean getNotificationDisplayed() {
        return notificationDisplayed;
    }

    public void setAreaOneText(String areaOneText) {
        this.areaOneText = areaOneText;
    }

    public void setAreaTwoText(String areaTwoText) {
        this.areaTwoText = areaTwoText;
    }

    public void setAreaThreeText(String areaThreeText) {
        this.areaThreeText = areaThreeText;
    }

    public String getAreaOneText() {
        return areaOneText;
    }

    public String getAreaTwoText() {
        return areaTwoText;
    }

    public String getAreaThreeText() {
        return areaThreeText;
    }



}