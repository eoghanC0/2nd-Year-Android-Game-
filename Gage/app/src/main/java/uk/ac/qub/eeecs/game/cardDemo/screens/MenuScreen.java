package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.audio.Music;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
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

    private boolean musicButtonState = false;

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

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", mGame.getPlayerName(), "M A I N  M E N U", mGame.getMatchStats());

        // Load in the bitmaps used on the main menu screen
        AssetStore assetManager = mGame.getAssetManager();

        assetManager.loadAndAddBitmap("menuScreenBackground", "img/help-background.jpg");
        background = assetManager.getBitmap("menuScreenBackground");
        assetManager.loadAndAddBitmap("HelpIcon", "img/HelpIcon.png");
        assetManager.loadAndAddBitmap("HelpIconPushed", "img/HelpIconPushed.png");
        assetManager.loadAndAddBitmap("OptionsIcon", "img/Button.png");
        assetManager.loadAndAddBitmap("MusicIcon", "img/MusicIcon.png");
        assetManager.loadAndAddBitmap("MusicIconPushed", "img/MusicIconPushed.png");
        assetManager.loadAndAddBitmap("MusicIconMute", "img/MusicIconMute.png");
        assetManager.loadAndAddBitmap("MusicIconMutePushed", "img/MusicIconMutePushed.png");
        assetManager.loadAndAddBitmap("packsIcon", "img/ball2.jpg");
        assetManager.loadAndAddBitmap("MenuButton", "img/MenuButton.png");
        assetManager.loadAndAddBitmap("MenuButtonPushed", "img/MenuButtonPushed.png");

        // Define the spacing that will be used to position the buttons
        int screenWidth = game.getScreenWidth();
        int screenHeight = game.getScreenHeight();

        int textColor = Color.rgb(242, 242, 242);
        // Create the trigger buttons
        mHelpButton = new PushButton(
                screenWidth * 0.925f, screenHeight * 0.9f, screenWidth * 0.1f, screenWidth * 0.1f, "HelpIcon", "HelpIconPushed", this);
        mOptionsButton = new PushButton(
                screenWidth * 0.725f, screenHeight * 0.375f, screenWidth * 0.2f, screenHeight * 0.2f, "MenuButton", "MenuButtonPushed", this);
        mOptionsButton.setButtonText("OPTIONS", mOptionsButton.getBound().getWidth() * 0.2f, textColor);
        mSquadsButton = new PushButton(
                screenWidth * 0.375f, screenHeight * 0.5f, screenWidth * 0.4f, screenHeight* 0.45f, "MenuButton", "MenuButtonPushed", this);
        mSquadsButton.setButtonText("SQUADS", mOptionsButton.getBound().getWidth() * 0.2f, textColor);
        musicButton = new PushButton(
                screenWidth * 0.075f, screenHeight * 0.9f, screenWidth * 0.1f, screenWidth * 0.1f, "MusicIcon", "MusicIconPushed", this);
        mPacksButton = new PushButton(
                screenWidth * 0.725f, screenHeight * 0.625f, screenWidth * 0.2f, screenHeight * 0.2f, "MenuButton", "MenuButtonPushed", this);
        mPacksButton.setButtonText("PACKS", mOptionsButton.getBound().getWidth() * 0.2f, textColor);
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
            else if (musicButton.isPushTriggered()) {
                if(musicButtonState) {
                    musicButton.setmDefaultBitmap(mGame.getAssetManager().getBitmap("MusicIconMute"));
                    musicButton.setmPushBitmap(mGame.getAssetManager().getBitmap("MusicIconMutePushed"));
                    musicButtonState = !musicButtonState;
                } else {
                    musicButton.setmDefaultBitmap(mGame.getAssetManager().getBitmap("MusicIcon"));
                    musicButton.setmPushBitmap(mGame.getAssetManager().getBitmap("MusicIconMute"));
                    musicButtonState = !musicButtonState;
                }

            }
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
        myPaint.setTextSize(36);
        graphics2D.drawBitmap(background,null, backGroundRectangle, myPaint);
        myPaint.setTextSize(72);

        infoBar.draw(elapsedTime, graphics2D);

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