package uk.ac.qub.eeecs.game.screens;

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
import uk.ac.qub.eeecs.game.objects.FootballGameScreen;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.ui.InfoBar;

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
    private PushButton mMusicButton;
    private PushButton mPacksButton;

    private Music myMusic;

    private final Bitmap background;
    private final Rect backgroundRect = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    private boolean musicMuteState = false;

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

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "XP | " + String.valueOf(mGame.getXp()), "M A I N  M E N U", mGame.getMatchStats());

        // Load in the bitmaps used on the main menu screen
        AssetStore assetManager = mGame.getAssetManager();
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
        assetManager.loadAndAddBitmap("ArrowBack", "img/ArrowBack.png");
        assetManager.loadAndAddBitmap("ArrowBackPushed", "img/ArrowBackPushed.png");
        assetManager.loadAndAddBitmap("ArrowForward", "img/ArrowForward.png");
        assetManager.loadAndAddBitmap("ArrowForwardPushed", "img/ArrowForwardPushed.png");

        background = assetManager.getBitmap("MainBackground");

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
        mSquadsButton.setButtonText("PLAY", mOptionsButton.getBound().getWidth() * 0.2f, textColor);
        mMusicButton = new PushButton(
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
            mMusicButton.update(elapsedTime);
            mPacksButton.update(elapsedTime);

            if (mOptionsButton.isPushTriggered())
                changeToScreen(new OptionsScreen(mGame));
            else if (mSquadsButton.isPushTriggered())
                changeToScreen(new SquadScreen(mGame));
            else if (mHelpButton.isPushTriggered())
                changeToScreen(new HelpScreen(mGame));
            else if (mPacksButton.isPushTriggered())
                changeToScreen(new PackScreen(mGame));
            else if (mMusicButton.isPushTriggered()) {
                if(musicMuteState) {
                    mMusicButton.setmDefaultBitmap(mGame.getAssetManager().getBitmap("MusicIconMute"));
                    mMusicButton.setmPushBitmap(mGame.getAssetManager().getBitmap("MusicIconMutePushed"));
                    mGame.getBGMusic().setVolume(1.0f);
                } else {
                    mMusicButton.setmDefaultBitmap(mGame.getAssetManager().getBitmap("MusicIcon"));
                    mMusicButton.setmPushBitmap(mGame.getAssetManager().getBitmap("MusicIconMute"));
                    mGame.getBGMusic().setVolume(0);
                }
                musicMuteState = !musicMuteState;
            }
        }
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
        graphics2D.drawBitmap(background,null, backgroundRect, myPaint);
        myPaint.setTextSize(72);

        infoBar.draw(elapsedTime, graphics2D);

        mHelpButton.draw(elapsedTime, graphics2D, null, null);
        mOptionsButton.draw(elapsedTime, graphics2D, null, null);
        mSquadsButton.draw(elapsedTime, graphics2D, null, null);
        mMusicButton.draw(elapsedTime, graphics2D, null, null);
        mPacksButton.draw(elapsedTime, graphics2D, null, null);

        myPaint.reset();
        myPaint.setTextSize(100);
        graphics2D.drawText("FOOTBALL TRUMPS", mGame.getScreenWidth() * 0.5f,mGame.getScreenWidth() * 0.9f,myPaint);
    }
}