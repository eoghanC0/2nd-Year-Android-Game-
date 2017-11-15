package uk.ac.qub.eeecs.game;

import android.graphics.Color;
import android.graphics.Paint;
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


    //define the background
    private GameObject mOptionsBackground;

    //define the buttons
    private PushButton mMenuButton;
    private PushButton mColourButton;

    private float screen_height = 320.0f;
    private float screen_width = 480.0f;



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

        mMenuButton = new PushButton(100.0f, 50.0f, screen_width / 5, screen_height / 3,
                "MenuButton", this );


        mOptionsBackground = new GameObject(screen_width / 2,
                screen_height / 2, screen_width, screen_height,
                getGame().getAssetManager().getBitmap("OptionsBackground"), this);



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

            if (mMenuButton.isPushTriggered())
                changeToScreen(new MenuScreen(mGame));


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
        graphics2D.clear(Color.WHITE);
        mMenuButton.draw(elapsedTime, graphics2D, null, null);

    }

}
