package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.util.Log;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;

/**
 * Created by eimhin on 27/11/2017.
 */

public class HelpScreen extends GameScreen {

    /**
     * Define viewports for this layer and the associated screen projection
     */
    private ScreenViewport mScreenViewport;
    private LayerViewport mLayerViewport;

    /**
     * Define InfoBar
     */
    private InfoBar infoBar;

    /**
     *
     */
    private PushButton menuScreenButton;

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public HelpScreen(Game game) {
        super("HelpScreen", game);

        // Instantiate variables
        mLayerViewport = new LayerViewport();
        mScreenViewport = new ScreenViewport();
        //GraphicsHelper.create3To2AspectRatioScreenViewport(game, mScreenViewport);
        Log.d("DEBUG", "SCREEN WIDTH: " + mGame.getScreenWidth() + " SCREEN HEIGHT: " + mGame.getScreenHeight());
        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this);

        AssetStore assetManager = mGame.getAssetManager();

        // Load in bitmaps
        assetManager.loadAndAddBitmap("LeftArrow", "img/LeftArrow.png");
        assetManager.loadAndAddBitmap("LeftArrowActive", "img/LeftArrowActive.png");

        menuScreenButton = new PushButton(mGame.getScreenHeight() * 0.06f,mGame.getScreenHeight() * 0.94f, mGame.getScreenHeight() * 0.1f,mGame.getScreenHeight() * 0.1f, "LeftArrow", "LeftArrowActive", this);

        infoBar.addNotification("Test notification 1", 1, 5);
        infoBar.addNotification("Test notification 2", 2, 2);
        infoBar.addNotification("Test notification 3", -1, 5);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        infoBar.update(elapsedTime);
        menuScreenButton.update(elapsedTime);

        if(menuScreenButton.isPushTriggered()) changeToScreen(new MenuScreen(mGame));
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        infoBar.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);
        menuScreenButton.draw(elapsedTime, graphics2D, null, null);

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
}


