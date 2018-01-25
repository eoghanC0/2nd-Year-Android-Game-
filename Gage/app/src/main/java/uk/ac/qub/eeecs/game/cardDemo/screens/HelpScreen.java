package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.cardDemo.ui.HorizontalImageScroller;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;
import uk.ac.qub.eeecs.game.cardDemo.ui.iHorizontalImageScroller;

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
     * Background image
     */
    private Bitmap background;

    /**
     * Background image draw rect
     */
    private Rect backgroundRect = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    /**
     * Define InfoBar
     */
    private InfoBar infoBar;

    /**
     * Button to return to MenuScreen
     */
    private PushButton menuScreenButton;

    /**
     * Allows user to scroll between help images
     */
    //private HorizontalImageScroller horizontalImageScroller;
    private HorizontalImageScroller horizontalImageScroller;

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
        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", "Test Player", "H E L P  S C R E E N", "");

        AssetStore assetManager = mGame.getAssetManager();

        // Load in bitmaps
        assetManager.loadAndAddBitmap("HelpBackground", "img/help-background.jpg");
        assetManager.loadAndAddBitmap("LeftArrow", "img/LeftArrow.png");
        assetManager.loadAndAddBitmap("LeftArrowActive", "img/LeftArrowActive.png");

        background = assetManager.getBitmap("HelpBackground");

        menuScreenButton = new PushButton(mGame.getScreenHeight() * 0.06f,mGame.getScreenHeight() * 0.94f, mGame.getScreenHeight() * 0.1f,mGame.getScreenHeight() * 0.1f, "LeftArrow", "LeftArrowActive", this);

        horizontalImageScroller = new HorizontalImageScroller(mGame.getScreenWidth() / 2, mGame.getScreenHeight() / 2, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.80f, this);
        horizontalImageScroller.setDisplayMaxBitmaps(true, 60);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        infoBar.update(elapsedTime);
        menuScreenButton.update(elapsedTime);

        if(menuScreenButton.isPushTriggered()) changeToScreen(new MenuScreen(mGame));

        horizontalImageScroller.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(0xe6ffff);
        graphics2D.drawBitmap(background,null, backgroundRect, null);
        infoBar.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);
        menuScreenButton.draw(elapsedTime, graphics2D, null, null);
        horizontalImageScroller.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);
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


