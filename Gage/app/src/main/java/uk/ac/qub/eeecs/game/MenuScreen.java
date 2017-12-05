package uk.ac.qub.eeecs.game;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.R;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.CardDemoScreen;
import uk.ac.qub.eeecs.game.cardDemo.PlayScreen;
import uk.ac.qub.eeecs.game.cardDemo.HelpScreen;
import uk.ac.qub.eeecs.gage.engine.audio.Music;


import uk.ac.qub.eeecs.game.performance.PerformanceScreen;
import uk.ac.qub.eeecs.game.platformDemo.PlatformDemoScreen;
import uk.ac.qub.eeecs.game.spaceDemo.SpaceshipDemoScreen;

/**
 * An exceedingly basic menu screen with a couple of touch buttons
 *
 * @version 1.0
 */
public class MenuScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the buttons for playing the 'games'
     */
    private PushButton mSpaceshipDemoButton;
    private PushButton mPlatformDemoButton;
    private PushButton mHelpButton;
    private PushButton mPerformanceScreenButton;
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
    public MenuScreen(Game game) {
        super("MenuScreen", game);

        // Load in the bitmaps used on the main menu screen
        AssetStore assetManager = mGame.getAssetManager();

        assetManager.loadAndAddBitmap("menuScreenBackground", "img/CampNou.png");
        background = assetManager.getBitmap("menuScreenBackground");
        assetManager.loadAndAddBitmap("Help", "img/Help.jpg");
        assetManager.loadAndAddBitmap("OptionsIcon", "img/options.png");
        assetManager.loadAndAddBitmap("musicIcon", "img/music.png");
        assetManager.loadAndAddBitmap("packsIcon", "img/packs.png");
        assetManager.loadAndAddBitmap("menuButtons", "img/Button.png");

        // Define the spacing that will be used to position the buttons
        int spacingX = game.getScreenWidth() / 5;
        int spacingY = game.getScreenHeight() / 3;

        // Create the trigger buttons

        mHelpButton = new PushButton(
                spacingX * 4.6f, spacingY * 2.7f, spacingX/2, spacingY/2, "Help", this);
        mOptionsButton = new PushButton(
                spacingX * 3.6f, spacingY * 2.0f, spacingX, spacingY, "menuButtons", this);
        mSquadsButton = new PushButton(
                spacingX * 1.9f, spacingY * 1.5f, spacingX*2, spacingY*2, "menuButtons", this);
        musicButton = new PushButton(
                spacingX/2, spacingY * 2.7f, spacingX/2, spacingY/2, "musicIcon", this);
        mPacksButton = new PushButton(
                spacingX * 3.6f, spacingY * 1.0f, spacingX, spacingY, "menuButtons", this);

        //Get the music file from the resources.
        AssetFileDescriptor afd = game.getResources().openRawResourceFd(R.raw.platform_bgmusic);
        //Plays the background song
        myMusic = new Music(afd);
        myMusic.play();
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

        // Process any touch events occurring since the update
        Input input = mGame.getInput();

        List<TouchEvent> touchEvents = input.getTouchEvents();
        if (touchEvents.size() > 0) {

            // Just check the first touch event that occurred in the frame.
            // It means pressing the screen with several fingers may not
            // trigger a 'button', but, hey, it's an exceedingly basic menu.
            TouchEvent touchEvent = touchEvents.get(0);

            // Update each button and transition if needed

            //  mSpaceshipDemoButton.update(elapsedTime);
            // mCardDemoButton.update(elapsedTime);
            mHelpButton.update(elapsedTime);
            //mPerformanceScreenButton.update(elapsedTime);
            mOptionsButton.update(elapsedTime);
            mSquadsButton.update(elapsedTime);
            musicButton.update(elapsedTime);
            mPacksButton.update(elapsedTime);

            if (mOptionsButton.isPushTriggered())
                changeToScreen(new OptionsScreen(mGame));
            else if (mSquadsButton.isPushTriggered())
                changeToScreen(new PlayScreen(mGame));
                //else if (mHelpButton.isPushTriggered())
                //changeToScreen(new HelpScreen(mGame));
            else if (musicButton.isPushTriggered())
            if (myMusic.isPlaying())
                myMusic.pause();
            else
                myMusic.play();
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
        Paint myPaint = new Paint();
        Paint mPaint = new Paint();
        myPaint.setTextSize(72);

        //mSpaceshipDemoButton.draw(elapsedTime, graphics2D, null, null);
        //mCardDemoButton.draw(elapsedTime, graphics2D, null, null);
        mHelpButton.draw(elapsedTime, graphics2D, null, null);
        //mPerformanceScreenButton.draw(elapsedTime, graphics2D, null, null);
        mOptionsButton.draw(elapsedTime, graphics2D, null, null);
        mSquadsButton.draw(elapsedTime, graphics2D, null, null);
        musicButton.draw(elapsedTime, graphics2D, null, null);
        mPacksButton.draw(elapsedTime, graphics2D, null, null);

        graphics2D.drawText("FOOTBALL TRUMPS", 600,1000,myPaint);
        graphics2D.drawText("SQUADS", 570,550,myPaint);
        graphics2D.drawText("PACKS", 1270,380,myPaint);
        graphics2D.drawText("OPTIONS", 1225,750,myPaint);

        myPaint.reset();
        myPaint.setAlpha(100);
        myPaint.setTextSize(36);
        graphics2D.drawBitmap(background,null, backGroundRectangle, myPaint);

        //graphics2D.drawText("Record: ", 100,100,myPaint);
        //graphics2D.drawText("Points: ", 1500,100,myPaint);
    }
}
