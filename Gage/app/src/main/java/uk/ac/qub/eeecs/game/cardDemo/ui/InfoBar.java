package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.util.Vector2;

/**
 * Created by eimhin on 30/11/2017.
 */

public class InfoBar extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

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
    private Queue<Notification> notificationQueue = new LinkedList();

    /**
     * Current notification
     */
    private Notification currentNotification;

    /*********************
     * Notification class
     *********************/

    public class Notification {

        // /////////////////////////////////////////////////////////////////////////
        // Properties
        // /////////////////////////////////////////////////////////////////////////

        private String text;
        private int type;
        private float displayTime;

        // /////////////////////////////////////////////////////////////////////////
        // Constructor
        // /////////////////////////////////////////////////////////////////////////

        /**
         * Notification constructor
         *
         * @param text Notification message
         * @param type 0 = Default | 1 = Red | 2 = Green
         * @param displayTime Seconds to display notification
         */
        public Notification(String text, int type, float displayTime) {
            this.text = text;

            if(!(type >= 0 && type <= 2)) this.type = 0;
            else this.type = type;

            if(!(displayTime > 0)) displayTime = 1f;
            this.displayTime = displayTime;
        }

        // /////////////////////////////////////////////////////////////////////////
        // Method
        // /////////////////////////////////////////////////////////////////////////

        @Override
        public String toString() {
            return String.format("Notification: '%1$s' | Type: %2$d | Display Time: %3$.2f", text, type, displayTime);
        }

        // /////////////////////////////////////////////////////////////////////////
        // Getters
        // /////////////////////////////////////////////////////////////////////////

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

    /**
     * Main constructor
     *
     * @param x x-position of InfoBar
     * @param y y-position of InfoBar
     * @param width Width of InfoBar
     * @param height Height of InfoBar
     * @param gameScreen Associated GameScreen
     */
    public InfoBar(float x, float y, float width, float height, GameScreen gameScreen) {
        super(x, y, width == 0 ? 1 : width, height == 0 ? 1 : height, null, gameScreen);

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
     *
     * @param x x-position
     * @param y y-position
     * @param width Width of InfoBar
     * @param height Height of InfoBar
     * @param gameScreen Associated GameScreen
     * @param areaOneText String of text for area one
     * @param areaTwoText String of text for area two
     * @param areaThreeText String of text for area three
     */
    public InfoBar(float x, float y, float width, float height, GameScreen gameScreen, String areaOneText, String areaTwoText, String areaThreeText) {
        super(x, y, width > 0 ? width : -width, height > 0 ? height : -height, null, gameScreen);

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

    /**
     * Sets the bitmap of the InfoBar
     *
     * @param bg String of AssetStore name of InfoBar bitmap to use
     */
    private void setBitmap(String bg) {
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        if(bg == "InfoBar" || bg == "InfoBarRed" || bg == "InfoBarGreen") mBitmap = assetManager.getBitmap(bg);
    }

    /**
     * Method to calculate the X position of text elements inserted into InfoBar
     *
     * @param text String of text
     * @param totalAreaWidth Width of the total area to draw to
     * @param offsetPercent Percentage text should be offset by
     * @param areaWidthPercentage Percentage text should occupy
     * @param alignment 0 = left | 1 = center | 2 = right
     * @return Vector2 containing position of text
     */
    private Vector2 getAreaTextVector(String text, float totalAreaWidth, float totalAreaHeight, float offsetPercent, float areaWidthPercentage, int alignment) {
        float yVal = totalAreaHeight / 2.0f;

        switch(alignment) {
            case 0:
                return new Vector2((getBound().getWidth() * offsetPercent) + (totalAreaWidth * 0.01f), yVal);
            case 1:
                return new Vector2((getBound().getWidth() * offsetPercent) + (((totalAreaWidth * areaWidthPercentage) - getTextBounds(text).width()) / 2), yVal);
            case 2:
                return new Vector2((getBound().getWidth() * offsetPercent) + ((totalAreaWidth * areaWidthPercentage) - getTextBounds(text).width()) - (totalAreaWidth * 0.01f), yVal);
            default:
                return new Vector2(0,0);
        }
    }

    /**
     * Gets area occupied by block of text (uses the current paint object)
     *
     * @param text String of text
     * @return Rect of the area occupied by the text
     */
    private Rect getTextBounds(String text) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Notification Management Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Creates and adds notification to notification queue
     *
     * @param notification String of text for the notification
     * @param type 0 = Default | 1 = Red | 2 = Green
     * @param duration in seconds
     */
    public void addNotification(String notification, int type, float duration) {
        if(notification != null && type >= 0 && type <= 2 && duration >= 0) {
            notificationQueue.add(new Notification(notification, type, duration));
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
     *
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
     *
     * @return boolean as true if there is a notification still displayed, else false
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
        for (Notification i : notificationQueue) {
            notificationQueue.remove(i);
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Update and Draw
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
        checkNotifications();

    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        // Draw the image
        drawScreenRect.set(0,0,mGameScreen.getGame().getScreenWidth(),(int) (mGameScreen.getGame().getScreenHeight() * 0.1));
        graphics2D.drawBitmap(mBitmap, null, drawScreenRect, null);

        // Get positions of area texts
        areaOneVector = getAreaTextVector(areaOneText, getBound().getWidth(), getBound().getHeight(), 0.01f, 0.313f, 0);
        areaTwoVector = getAreaTextVector(areaTwoText, getBound().getWidth(), getBound().getHeight(), 0.368f, 0.313f, 1);
        areaThreeVector = getAreaTextVector(areaThreeText, getBound().getWidth(), getBound().getHeight(), 0.681f, 0.313f, 2);

        // Draw area texts
        graphics2D.drawText(areaOneText, areaOneVector.x, areaOneVector.y, textPaint);
        graphics2D.drawText(areaTwoText, areaTwoVector.x, areaTwoVector.y, textPaint);
        graphics2D.drawText(areaThreeText, areaThreeVector.x, areaThreeVector.y, textPaint);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Getters and Setters
    // /////////////////////////////////////////////////////////////////////////

    public void setCurrentNotificationDisplayTime(long currentNotificationDisplayTime) { this.currentNotificationDisplayTime = currentNotificationDisplayTime;}

    public Notification getCurrentNotification() {
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