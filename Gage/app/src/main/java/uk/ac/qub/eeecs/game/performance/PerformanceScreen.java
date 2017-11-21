package uk.ac.qub.eeecs.game.performance;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
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
    private float fps;

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
        batchSize = 0;
        increasePressed = false;
        decreasePressed = false;
        mRandom = new Random();
        fps = 0;
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

        Log.d(TAG, String.format("SCREEN WIDTH: %1$d SCREEN HEIGHT: %2$d", screenWidth, screenHeight));

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
            batchSize+=50;
            increasePressed = true;
        }
        else if(decrease.isPushed() && batchSize > 0 && !decreasePressed) {
            batchSize-=50;
            decreasePressed = true;
        }

        updatePerformanceRects();

        // Set properties of each PerformanceRect
        for (int drawIdx = 0; drawIdx < batchSize; drawIdx++) {
            int rWidth = mRandom.nextInt(screenWidth / 2) + 1;
            int rHeight = mRandom.nextInt(screenHeight / 2 ) + 1;
            int x = mRandom.nextInt(screenWidth - (rWidth / 2)) + 1;
            int y = mRandom.nextInt(screenHeight - (rHeight / 2)) + 1;
            performanceRects.get(drawIdx).set(x, y, rWidth, rHeight);
            performanceRects.get(drawIdx).update(elapsedTime);
            Log.d(TAG, performanceRects.get(drawIdx).getInfo());
        }

        // Retrieve FPS
        retrieveAverageFPS();

    }

    public void updatePerformanceRects() {
        // Create the PerformanceRect ArrayList
        performanceRects.clear();
        for (int i = 0; i < batchSize; i++) {
            performanceRects.add(new PerformanceRect(0,0,1,1,this));
        }
    }

    public void retrieveAverageFPS() {
        fps = mGame.getAverageFramesPerSecond();
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

        // Draw FPS counter
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(75.f);
        graphics2D.drawText(String.format("FPS:%1$.2f", fps), 14, 104, paint);
        paint.setColor(Color.RED);
        graphics2D.drawText(String.format("FPS:%1$.2f", fps), 10, 100, paint);
    }

}
