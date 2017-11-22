package uk.ac.qub.eeecs.game;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;


/**
 * Created by aedan on 02/11/2017.
 */

public class OptionsScreen extends GameScreen {

    /**
     * Define viewports for this layer and the associated screen projection
     */
    private ScreenViewport mScreenViewport;
    private LayerViewport mLayerViewport;


    // Define the variables to change the colour of the background
    private int backgroundColour = 255;
    private boolean decreaseColour = true;
    private boolean changeColour = false;

    //define the buttons
    private PushButton mMenuButton;
    private PushButton mColourButton;
    private PushButton mChangeFlagButton;

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public OptionsScreen(Game game) {

        super("OptionsScreen", game);

        // Create the screen viewport
        mScreenViewport = new ScreenViewport(0, 0, game.getScreenWidth(),
                game.getScreenHeight());

        // Create the layer viewport, taking into account the orientation
        // and aspect ratio of the screen.
        if (mScreenViewport.width > mScreenViewport.height)
            mLayerViewport = new LayerViewport(240.0f, 240.0f
                    * mScreenViewport.height / mScreenViewport.width, 240,
                    240.0f * mScreenViewport.height / mScreenViewport.width);
        else
            mLayerViewport = new LayerViewport(240.0f * mScreenViewport.height
                    / mScreenViewport.width, 240.0f, 240.0f
                    * mScreenViewport.height / mScreenViewport.width, 240);

        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("OptionsBackground", "img/optionBG.jpg");
        assetManager.loadAndAddBitmap("MenuButton", "img/menu button.png");
        assetManager.loadAndAddBitmap("FlagButton", "img/round_arrow.png");
        assetManager.loadAndAddBitmap("ColourShift", "img/colourButton.png");



        int spacingX = game.getScreenWidth() / 5;
        int spacingY = game.getScreenHeight() / 3;

        //create the buttons for the screen
        mMenuButton = new PushButton(spacingX * 1.0f, spacingY * 1.5f, spacingX / 2, spacingY / 2,
                "MenuButton", this );
        mColourButton = new PushButton(spacingX * 2.5f, spacingY * 1.5f, spacingX / 2, spacingY / 2,
                "ColourShift", this );
        mChangeFlagButton = new PushButton(spacingX * 4.0f, spacingY * 1.5f, spacingX / 2, spacingY / 2,
                "FlagButton", this );


    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        // Process any touch events occurring since the last update
        Input input = mGame.getInput();

        List<TouchEvent> touchEvents = input.getTouchEvents();
        if (touchEvents.size() > 0) {

            // Just check the first touch event that occurred in the frame.
            // It means pressing the screen with several fingers may not
            // trigger a 'button', but, hey, it's an exceedingly basic menu.
            TouchEvent touchEvent = touchEvents.get(0);

            // Update each button and transition if needed
            mMenuButton.update(elapsedTime);
            mColourButton.update(elapsedTime);
            mChangeFlagButton.update(elapsedTime);

            if (mMenuButton.isPushTriggered())
                changeToScreen(new MenuScreen(mGame));

            if (mChangeFlagButton.isPushTriggered()) {
                mGame.setPreference("Flag", !(mGame.getPreference("Flag")));
            }

            //button to start or stop colour switching
            if (mColourButton.isPushed()){
                if (!changeColour){
                    changeColour = true;
                } else {
                    changeColour = false;
                }
            }
        }
    }

    /**
     * Remove the current game screen and then change to the specified screen
     *
     * @param screen game screen to become active
     */
    private void changeToScreen(GameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Rect rectangle = new Rect(0, 0, this.getGame().getScreenWidth(),
                this.getGame().getScreenHeight());
        graphics2D.clear(Color.WHITE);
        Paint mPaint = new Paint();
        mPaint.setColorFilter(new LightingColorFilter(Color.rgb(backgroundColour, backgroundColour, backgroundColour), 0));
        //check if the button has been pressed and if so start shifting the colours

        if (changeColour){
            if(decreaseColour){
                backgroundColour -=5;
                //when background colour reaches 0 start to increase it
                if (backgroundColour == 0){
                    decreaseColour = false;
                }
            }else {
                backgroundColour +=5;
                //when background colour reaches 255 begin decreasing it
                if (backgroundColour == 255) {
                    decreaseColour = true;
                }
            }
        }

        //draw the background first and then draw the buttons
        graphics2D.drawBitmap(getGame().getAssetManager().getBitmap("OptionsBackground"), rectangle, rectangle, mPaint);
        mMenuButton.draw(elapsedTime, graphics2D, null, null);
        mColourButton.draw(elapsedTime, graphics2D, null, null);
        mChangeFlagButton.draw(elapsedTime, graphics2D);
        //Paint the boolean flag value on screen
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(75);
        graphics2D.drawText(String.valueOf(mGame.getPreference("flag")), mMenuButton.getBound().getLeft(), mMenuButton.getBound().getBottom() + mMenuButton.getBound().getHeight() + 100, textPaint);


    }


}
