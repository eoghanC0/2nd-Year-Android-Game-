package uk.ac.qub.eeecs.game.performance;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.MainActivity;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

import static android.content.ContentValues.TAG;

/**
 * Created by eimhin on 02/11/2017.
 */

public class PerformanceScreen extends GameScreen {

    /**
     * Define viewports for this layer and the associated screen projection
     */
    private ScreenViewport mScreenViewport;
    private LayerViewport mLayerViewport;

    /**
     * Create ArrayList to store performance rectangles
     */
    private ArrayList<PerformanceRect> performanceRects;

    /**
     * Number of performance rects to display per update
     */
    private int batchSize;

    /**
     * PushButtons controls used to modify the batchSize
     */
    private ArrayList<PushButton> mControls;
    private PushButton increase;
    private PushButton decrease;

    /**
     * Booleans to determine if PushButton's are pushed over multiple updates
     */
    boolean increasePressed, decreasePressed;

    /**
     * Number of rects produced
     */
    private long mNumCalls;

    /**
     * Random variable
     */
    private Random mRandom;

    /**
     * Variables to store screen width and height
     */
    private final int screenWidth = mGame.getScreenWidth();
    private final int screenHeight = mGame.getScreenHeight();

    /**
     * Current Frames Per Second
     */
    private long fps;

    /**
     * Start time and current time in milliseconds
     */
    private long startTime;
    private long currentTime;

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public PerformanceScreen(Game game) {
        super("PerformanceScreen", game);

        // Instantiate variables
        mLayerViewport = new LayerViewport();
        mScreenViewport = new ScreenViewport();
        GraphicsHelper.create3To2AspectRatioScreenViewport(game, mScreenViewport);
        mNumCalls = 0;
        batchSize = 0;
        increasePressed = false;
        decreasePressed = false;
        mRandom = new Random();
        fps = 0;
        startTime = System.currentTimeMillis() - 1;
        currentTime = startTime;
        performanceRects = new ArrayList<PerformanceRect>();

        // Create increase and decrease PushButtons
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("Add", "img/Add.png");
        assetManager.loadAndAddBitmap("AddActive", "img/AddActive.png");
        assetManager.loadAndAddBitmap("Minus", "img/Minus.png");
        assetManager.loadAndAddBitmap("MinusActive", "img/MinusActive.png");
        mControls = new ArrayList<PushButton>();
        increase = new PushButton((screenWidth - 75.0f), (screenHeight - 75.0f), 100.0f, 100.0f, "Add", "AddActive", this);
        mControls.add(increase);
        decrease = new PushButton((screenWidth - 75.0f), 75.0f, 100.0f, 100.0f, "Minus", "MinusActive", this);
        mControls.add(decrease);

    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        // Update the touch buttons checking for player input
        for (PushButton control : mControls)
            control.update(elapsedTime, mLayerViewport, mScreenViewport);

        // Check if a PushButton was pressed from previous update
        // Prevents a single press from increasing batchSize by multiple amounts
        if(!increase.isPushed()) increasePressed = false;
        if(!decrease.isPushed()) decreasePressed = false;

        if(increase.isPushed() && !increasePressed) {
            batchSize+=200;
            increasePressed = true;
        }
        else if(decrease.isPushed() && batchSize > 0 && !decreasePressed) {
            batchSize-=200;
            decreasePressed = true;
        }

        updatePerformanceRects();

        //Log.d(TAG,"batchSize = " + batchSize + " increasePressed: " + increase.isPushed() + " decreasePressed: " + decrease.isPushed());

        // Set properties of each PerformanceRect
        for (int drawIdx = 0; drawIdx < batchSize; drawIdx++) {
            int rWidth = mRandom.nextInt(screenWidth - 1) + 1;
            int rHeight = mRandom.nextInt(screenHeight - 1) + 1;
            int x = mRandom.nextInt(screenWidth - rWidth);
            int y = mRandom.nextInt(screenHeight - rHeight);
            performanceRects.get(drawIdx).set(x, y, rWidth, rHeight);
            performanceRects.get(drawIdx).update(elapsedTime);
        }

        // Display a count of the number of frames that have been displayed
        mNumCalls++;

        calculateFPS();

    }

    public void updatePerformanceRects() {
        // Create the PerformanceRect ArrayList
        performanceRects.clear();
        for (int i = 0; i < batchSize; i++) {
            performanceRects.add(new PerformanceRect(0,0,1,1,this));
        }
    }

    public void calculateFPS() {
        currentTime = System.currentTimeMillis();
        float calc = ((float) mNumCalls / (currentTime - startTime)) * 1000;
        fps = (long) calc;
        //Log.d(TAG, String.format("FPS: %1$d | frames: %2$d | start time: %3$d | current time: %4$d | cu-st: %5$d | calc: %6$s", fps, mNumCalls, startTime, currentTime, (currentTime - startTime), calc));

    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        // Draw PerformanceRect's
        for (PerformanceRect performanceRect : performanceRects) {
            performanceRect.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);
        }

        // Draw button controls
        for (PushButton control : mControls)
            control.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(75.f);
        graphics2D.drawText(String.format("ST: %1$d CT: %2$d %nFrames: %3$d FPS: %4$d", startTime, 123, mNumCalls, fps), 0, 100.0f, paint);
    }

}
