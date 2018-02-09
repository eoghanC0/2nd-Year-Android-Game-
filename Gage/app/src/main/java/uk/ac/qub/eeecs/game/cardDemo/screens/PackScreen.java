package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

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
import uk.ac.qub.eeecs.game.SplashScreen1;
import uk.ac.qub.eeecs.game.cardDemo.ui.HorizontalImageScroller;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;

/**
 * Created by stephenmcveigh on 07/12/2017.
 */

public class PackScreen extends GameScreen {

    private final Bitmap background;
    //private final Rect backGroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    /**
     * Define InfoBar
     */
    private InfoBar infoBar;

    /**
     * Define the buttons for playing the 'games'
     */
    private PushButton m100PackButton;
    private PushButton m300PackButton;
    private PushButton m500PackButton;
    private PushButton m1000PackButton;
    private PushButton mSquadsButton;
    private PushButton mMenuButton;

    private Music myMusic;

    private HorizontalImageScroller horizontalImageScroller;

    //private final Bitmap background;
    private final Rect backGroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a simple menu screen
     *
     * @param game Game to which this screen belongs
     */
    public PackScreen(Game game) {
        super("PackScreen", game);

        Log.d("DEBUG", "SCREEN WIDTH: " + mGame.getScreenWidth() + " SCREEN HEIGHT: " + mGame.getScreenHeight());
        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", mGame.getPlayerName(), "P A C K  S C R E E N", mGame.getMatchStats());

        // Load in the bitmaps used on the main menu screen
        AssetStore assetManager = mGame.getAssetManager();

        assetManager.loadAndAddBitmap("Help", "img/Help.jpg");
        assetManager.loadAndAddBitmap("OptionsIcon", "img/options.png");
        assetManager.loadAndAddBitmap("musicIcon", "img/music.png");
        assetManager.loadAndAddBitmap("packsIcon", "img/ball2.jpg");
        assetManager.loadAndAddBitmap("menuButtons", "img/Button1.png");
        assetManager.loadAndAddBitmap("LeftArrow", "img/LeftArrow.png");
        assetManager.loadAndAddBitmap("LeftArrowActive", "img/LeftArrowActive.png");
        assetManager.loadAndAddBitmap("RightArrow", "img/RightArrow.png");
        assetManager.loadAndAddBitmap("RightArrowActive", "img/RightArrowActive.png");
        assetManager.loadAndAddBitmap("packScreenBG", "img/packScreenBG.png");
        background = assetManager.getBitmap("packScreenBG");

        // Define the spacing that will be used to position the buttons
        int spacingX = game.getScreenWidth() / 4;
        int spacingY = game.getScreenHeight() / 8;

        // Create the trigger buttons
        mMenuButton = new PushButton(
                (spacingX/2)*0.8f, (spacingY/2) * 15, spacingX/2, spacingY, "LeftArrow","LeftArrowActive", this);
        mSquadsButton = new PushButton(
                (spacingX/2) * 7.2f, (spacingY/2) * 15, spacingX/2, spacingY, "RightArrow","RightArrowActive", this);
        m100PackButton = new PushButton(
                spacingX/2, (spacingY/2) * 12, spacingX, spacingY * 2, "menuButtons", this);
        m300PackButton = new PushButton(
                (spacingX/2) * 3, (spacingY/2) * 12, spacingX, spacingY * 2, "menuButtons", this);
        m500PackButton = new PushButton(
                (spacingX/2) * 5, (spacingY/2)* 12, spacingX, spacingY* 2, "menuButtons", this);
        m1000PackButton = new PushButton(
                (spacingX/2) * 7, (spacingY/2)* 12, spacingX, spacingY * 2, "menuButtons", this);

        horizontalImageScroller = new HorizontalImageScroller(mGame.getScreenWidth() / 2, spacingY * 2.8f, mGame.getScreenWidth(), spacingY * 4, this);

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
        horizontalImageScroller.update(elapsedTime);

        List<TouchEvent> touchEvents = input.getTouchEvents();
        if (touchEvents.size() > 0) {

            // Just check the first touch event that occurred in the frame.
            // It means pressing the screen with several fingers may not
            // trigger a 'button'.
            TouchEvent touchEvent = touchEvents.get(0);

            // Update each button and transition if needed
            //mHelpButton.update(elapsedTime);
            mSquadsButton.update(elapsedTime);
            //musicButton.update(elapsedTime);
            mMenuButton.update(elapsedTime);
            m100PackButton.update(elapsedTime);
            m300PackButton.update(elapsedTime);
            m500PackButton.update(elapsedTime);
            m1000PackButton.update(elapsedTime);

            if (mSquadsButton.isPushTriggered())
                changeToScreen(new SquadScreen(mGame));
            else if (mMenuButton.isPushTriggered())
                changeToScreen(new MenuScreen(mGame));
            else if (m100PackButton.isPushTriggered())
                changeToScreen(new SplashScreen1(mGame, "m100PackButton"));
            else if (m300PackButton.isPushTriggered())
                changeToScreen(new SplashScreen1(mGame, "m300PackButton"));
            else if (m500PackButton.isPushTriggered())
                changeToScreen(new SplashScreen1(mGame, "m500PackButton"));
            else if (m1000PackButton.isPushTriggered())
                changeToScreen(new SplashScreen1(mGame, "m1000PackButton"));
        }
    }

    /**
     * Remove the current game screen and then change to the specified screen
     *
     * @param screen game screen to become active
     */
    public void changeToScreen(GameScreen screen) {
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
        myPaint.setTextSize(72);

        graphics2D.drawBitmap(background,null, backGroundRectangle, myPaint);
        infoBar.draw(elapsedTime, graphics2D);
        horizontalImageScroller.draw(elapsedTime, graphics2D);

        mSquadsButton.draw(elapsedTime, graphics2D, null, null);
        mMenuButton.draw(elapsedTime, graphics2D, null, null);
        m100PackButton.draw(elapsedTime, graphics2D, null, null);
        m300PackButton.draw(elapsedTime, graphics2D, null, null);
        m500PackButton.draw(elapsedTime, graphics2D, null, null);
        m1000PackButton.draw(elapsedTime, graphics2D, null, null);

        m100PackButton.setButtonText("1 Player Pack  Cost:100xp", 32,Color.BLACK);
        m300PackButton.setButtonText("3 Player Pack  Cost:300xp", 32,Color.BLACK);
        m500PackButton.setButtonText("5 Player Pack  Cost:500xp", 32,Color.BLACK);
        m1000PackButton.setButtonText("11 Player Pack  Cost:1000xp", 32,Color.BLACK);

    }
}
