package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.audio.Music;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;

/**
 * An exceedingly basic menu screen with a couple of touch buttons
 *
 * @version 1.0
 */
public class MenuScreen extends FootballGameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    private ScreenViewport mScreenViewport;
    private LayerViewport mLayerViewport;

    /**
     * Define InfoBar
     */
    private InfoBar infoBar;

    /**
     * Define the buttons for playing the 'games'
     */
    private PushButton mHelpButton;
    private PushButton mOptionsButton;
    private PushButton mSquadsButton;
    private PushButton musicButton;
    private PushButton mPacksButton;

    private Music myMusic;

    private final Bitmap background;
    private final Rect backGroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a simple menu screen
     *
     * @param game Game to which this screen belongs
     */
    public MenuScreen(FootballGame game) {
        super("MenuScreen", game);

        // Instantiate variables
        mLayerViewport = new LayerViewport();
        mScreenViewport = new ScreenViewport();

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", "Test Player", "M A I N  M E N U", mGame.getMatchStats());

        // Load in the bitmaps used on the main menu screen
        AssetStore assetManager = mGame.getAssetManager();

        assetManager.loadAndAddBitmap("menuScreenBackground", "img/CampNou.png");
        background = assetManager.getBitmap("menuScreenBackground");
        assetManager.loadAndAddBitmap("Help", "img/Help.jpg");
        assetManager.loadAndAddBitmap("OptionsIcon", "img/Button.png");
        assetManager.loadAndAddBitmap("musicIcon", "img/music.png");
        assetManager.loadAndAddBitmap("packsIcon", "img/ball2.jpg");
        assetManager.loadAndAddBitmap("menuButtons", "img/Button.png");

        // Define the spacing that will be used to position the buttons
        int spacingX = game.getScreenWidth() / 5;
        int spacingY = game.getScreenHeight() / 3;

        // Create the trigger buttons
        mHelpButton = new PushButton(
                spacingX * 4.6f, spacingY * 2.7f, spacingX/2, spacingY/2, "Help", this);
        mOptionsButton = new PushButton(
                spacingX * 3.6f, spacingY * 2.0f, spacingX, spacingY, "menuButtons", this);
        mOptionsButton.setButtonText("OPTIONS", 100, 0);
        mSquadsButton = new PushButton(
                spacingX * 1.9f, spacingY * 1.5f, spacingX*2, spacingY*2, "menuButtons", this);
        mSquadsButton.setButtonText("SQUADS", 100, 0);
        musicButton = new PushButton(
                spacingX/2, spacingY * 2.7f, spacingX/2, spacingY/2, "musicIcon", this);
        mPacksButton = new PushButton(
                spacingX * 3.6f, spacingY * 1.0f, spacingX, spacingY, "menuButtons", this);
        mPacksButton.setButtonText("PACKS", 800, 0);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the menu screen
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        infoBar.update(elapsedTime);
        // Process any touch events occurring since the update
        Input input = mGame.getInput();

        List<TouchEvent> touchEvents = input.getTouchEvents();
        if (touchEvents.size() > 0) {

            // Just check the first touch event that occurred in the frame.
            // It means pressing the screen with several fingers may not
            // trigger a 'button', but, hey, it's an exceedingly basic menu.
            TouchEvent touchEvent = touchEvents.get(0);

            // Update each button and transition if needed
            mHelpButton.update(elapsedTime);
            mOptionsButton.update(elapsedTime);
            mSquadsButton.update(elapsedTime);
            musicButton.update(elapsedTime);
            mPacksButton.update(elapsedTime);

            if (mOptionsButton.isPushTriggered())
                changeToScreen(new OptionsScreen(mGame));
            else if (mSquadsButton.isPushTriggered())
                changeToScreen(new SquadScreen(mGame));
            else if (mHelpButton.isPushTriggered())
                changeToScreen(new HelpScreen(mGame));
            else if (mPacksButton.isPushTriggered())
                changeToScreen(new PackScreen(mGame));
            else if (musicButton.isPushTriggered());
            //{if (myMusic.isPlaying())
                    //    myMusic.pause();
                    //else
                      //  myMusic.play();}
        }
    }

    /**
     * Remove the current game screen and then change to the specified screen
     *
     * @param screen game screen to become active
     */
    public void changeToScreen(FootballGameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    /**
     * Draw the menu screen
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        // Clear the screen and draw the buttons
        graphics2D.clear(Color.WHITE);
        Paint myPaint = mGame.getPaint();
        myPaint.setAlpha(100);
        myPaint.setTextSize(36);
        graphics2D.drawBitmap(background,null, backGroundRectangle, myPaint);
        myPaint.setTextSize(72);

        infoBar.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);

        mHelpButton.draw(elapsedTime, graphics2D, null, null);
        mOptionsButton.draw(elapsedTime, graphics2D, null, null);
        mSquadsButton.draw(elapsedTime, graphics2D, null, null);
        musicButton.draw(elapsedTime, graphics2D, null, null);
        mPacksButton.draw(elapsedTime, graphics2D, null, null);

        myPaint.reset();
        myPaint.setTextSize(100);
        graphics2D.drawText("FOOTBALL TRUMPS", mGame.getScreenWidth() * 0.5f,mGame.getScreenWidth() * 0.9f,myPaint);
    }
}