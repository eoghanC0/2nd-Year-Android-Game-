package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;
import uk.ac.qub.eeecs.game.cardDemo.ui.HorizontalCardScroller;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;
import uk.ac.qub.eeecs.game.cardDemo.ui.popUpWindow;

/**
 * Created by stephenmcveigh on 07/12/2017.
 */

public class PackScreen extends FootballGameScreen {

    private final Bitmap background;
    //private final Rect backGroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    /**
     * Define InfoBar
     */
    private InfoBar infoBar;

    /**
     * Define the buttons for using the pack Screen
     */
    private PushButton m100PackButton;
    private PushButton m300PackButton;
    private PushButton m500PackButton;
    private PushButton m1000PackButton;
    private PushButton mSquadsButton;
    private PushButton mMenuButton;

    private boolean pack100Pressed;
    private boolean pack300Pressed;
    private boolean pack500Pressed;
    private boolean pack1000Pressed;
    private boolean drawPopup = true;
    private  boolean displayHorizontalScroller = false;

    // Define the spacing that will be used to position the buttons
    int spacingX = getGame().getScreenWidth() / 4;
    int spacingY = getGame().getScreenHeight() / 8;

    private Card highestRatedCard;

    private ScreenViewport screenViewport;
    private LayerViewport layerViewport;

    private Bitmap baseBitmap;
    private Bitmap backOfCard;

    private popUpWindow packPopUp;
    private Boolean drawHorizontalScroller = false;

    private HorizontalCardScroller horizontalCardScroller;

    //private final Bitmap background;
    private final Rect backGroundRectangle = new Rect(0, 0, this.getGame().getScreenWidth(), this.getGame().getScreenHeight());

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a simple menu screen
     *
     * @param game Game to which this screen belongs
     */
    public PackScreen(FootballGame game) {
        super("PackScreen", game);

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", "Test Player", "P A C K  S C R E E N", "");

        // Load in the bitmaps used on the main menu screen
        AssetStore assetManager = mGame.getAssetManager();

        assetManager.loadAndAddBitmap("Help", "img/Help.jpg");
        assetManager.loadAndAddBitmap("OptionsIcon", "img/options.png");
        assetManager.loadAndAddBitmap("musicIcon", "img/music.png");
        assetManager.loadAndAddBitmap("packsIcon", "img/ball2.jpg");
        assetManager.loadAndAddBitmap("menuButtons", "img/MenuButton.png");
        assetManager.loadAndAddBitmap("LeftArrow", "img/LeftArrow.png");
        assetManager.loadAndAddBitmap("LeftArrowActive", "img/LeftArrowActive.png");
        assetManager.loadAndAddBitmap("RightArrow", "img/RightArrow.png");
        assetManager.loadAndAddBitmap("RightArrowActive", "img/RightArrowActive.png");
        assetManager.loadAndAddBitmap("packScreenBG", "img/packScreenBG.png");
        background = assetManager.getBitmap("packScreenBG");

        // Create the trigger buttons
        mMenuButton = new PushButton(
                (spacingX / 2) * 0.8f, (spacingY / 2) * 15, spacingX / 2, spacingY, "LeftArrow", "LeftArrowActive", this);
        mSquadsButton = new PushButton(
                (spacingX / 2) * 7.2f, (spacingY / 2) * 15, spacingX / 2, spacingY, "RightArrow", "RightArrowActive", this);
        m100PackButton = new PushButton(
                spacingX / 2, (spacingY / 2) * 12, spacingX, spacingY * 2, "menuButtons", this);
        m300PackButton = new PushButton(
                (spacingX / 2) * 3, (spacingY / 2) * 12, spacingX, spacingY * 2, "menuButtons", this);
        m500PackButton = new PushButton(
                (spacingX / 2) * 5, (spacingY / 2) * 12, spacingX, spacingY * 2, "menuButtons", this);
        m1000PackButton = new PushButton(
                (spacingX / 2) * 7, (spacingY / 2) * 12, spacingX, spacingY * 2, "menuButtons", this);

        m100PackButton.setButtonText("1 Player Pack  Cost:100xp", 32, Color.WHITE);
        m300PackButton.setButtonText("3 Player Pack  Cost:300xp", 32, Color.WHITE);
        m500PackButton.setButtonText("5 Player Pack  Cost:500xp", 32, Color.WHITE);
        m1000PackButton.setButtonText("11 Player Pack  Cost:1000xp", 32, Color.WHITE);

        horizontalCardScroller = new HorizontalCardScroller(mGame.getScreenWidth() / 2, spacingY * 2.8f, mGame.getScreenWidth(), spacingY * 4, this);
        packPopUp = new popUpWindow(mGame.getScreenWidth() / 2, spacingY * 2.8f, mGame.getScreenWidth(), spacingY * 4, this, "Are you sure you want to buy this pack?", "Yes", "No");

        layerViewport = new LayerViewport();
        screenViewport = new ScreenViewport();

        assetManager.loadAndAddBitmap("BaseBitmap", "img/CardFront.png");
        baseBitmap = assetManager.getBitmap("BaseBitmap");
        if (baseBitmap == null)
            Log.d("DEBUG", "HorizontalCardScroller: NO BASE BITMAP");
        assetManager.loadAndAddBitmap("CardBack", "img/CardBack.png");
        backOfCard = assetManager.getBitmap("CardBack");
    }



    public PackScreen(FootballGame game, Boolean isPack100Pressed, Boolean isPack300Pressed, Boolean isPack500Pressed, Boolean isPack1000Pressed) {
        super("PackScreen", game);
        this.pack100Pressed = isPack100Pressed;
        this.pack300Pressed = isPack300Pressed;
        this.pack500Pressed = isPack500Pressed;
        this.pack1000Pressed = isPack1000Pressed;

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", "Test Player", "P A C K  S C R E E N", "");

        // Load in the bitmaps used on the main menu screen
        AssetStore assetManager = mGame.getAssetManager();

        assetManager.loadAndAddBitmap("menuButtons", "img/MenuButton.png");
        assetManager.loadAndAddBitmap("LeftArrow", "img/LeftArrow.png");
        assetManager.loadAndAddBitmap("LeftArrowActive", "img/LeftArrowActive.png");
        assetManager.loadAndAddBitmap("RightArrow", "img/RightArrow.png");
        assetManager.loadAndAddBitmap("RightArrowActive", "img/RightArrowActive.png");
        assetManager.loadAndAddBitmap("packScreenBG", "img/packScreenBG.png");
        background = assetManager.getBitmap("packScreenBG");

        // Create the trigger buttons
        mMenuButton = new PushButton(
                (spacingX / 2) * 0.8f, (spacingY / 2) * 15, spacingX / 2, spacingY, "LeftArrow", "LeftArrowActive", this);
        mSquadsButton = new PushButton(
                (spacingX / 2) * 7.2f, (spacingY / 2) * 15, spacingX / 2, spacingY, "RightArrow", "RightArrowActive", this);
        m100PackButton = new PushButton(
                spacingX / 2, (spacingY / 2) * 12, spacingX, spacingY * 2, "menuButtons", this);
        m300PackButton = new PushButton(
                (spacingX / 2) * 3, (spacingY / 2) * 12, spacingX, spacingY * 2, "menuButtons", this);
        m500PackButton = new PushButton(
                (spacingX / 2) * 5, (spacingY / 2) * 12, spacingX, spacingY * 2, "menuButtons", this);
        m1000PackButton = new PushButton(
                (spacingX / 2) * 7, (spacingY / 2) * 12, spacingX, spacingY * 2, "menuButtons", this);

        m100PackButton.setButtonText("1 Player Pack  Cost:100xp", 32, Color.WHITE);
        m300PackButton.setButtonText("3 Player Pack  Cost:300xp", 32, Color.WHITE);
        m500PackButton.setButtonText("5 Player Pack  Cost:500xp", 32, Color.WHITE);
        m1000PackButton.setButtonText("11 Player Pack  Cost:1000xp", 32, Color.WHITE);

        horizontalCardScroller = new HorizontalCardScroller(mGame.getScreenWidth() / 2, spacingY * 2.8f, mGame.getScreenWidth(), spacingY * 4, this);

        assetManager.loadAndAddBitmap("BaseBitmap", "img/CardFront.png");
        baseBitmap = assetManager.getBitmap("BaseBitmap");
        if (baseBitmap == null)
            Log.d("DEBUG", "HorizontalCardScroller: NO BASE BITMAP");
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the pack screen
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        infoBar.update(elapsedTime);
        horizontalCardScroller.update(elapsedTime);
        packPopUp.update(elapsedTime);

        // Update each button and transition if needed
        mSquadsButton.update(elapsedTime);
        mMenuButton.update(elapsedTime);
        m100PackButton.update(elapsedTime);
        m300PackButton.update(elapsedTime);
        m500PackButton.update(elapsedTime);
        m1000PackButton.update(elapsedTime);

        if (mSquadsButton.isPushTriggered())
            changeToScreen(new SquadScreen(mGame));
        else if (mMenuButton.isPushTriggered())
            changeToScreen(new MenuScreen(mGame));
        else if (m100PackButton.isPushTriggered()) {
            pack100Pressed = true;
        pack300Pressed = false;
        pack500Pressed = false;
        pack1000Pressed = false;
        drawPopup = true;
        selectPackPlayers(1);
        //changeToScreen(new packScreenSplashScreen(mGame,highestRatedCard, pack100Pressed,pack300Pressed,pack500Pressed,pack1000Pressed));
        } else if (m300PackButton.isPushTriggered()) {
            //changeToScreen(new SplashScreen1(mGame));
            pack100Pressed = false;
            pack300Pressed = true;
            pack500Pressed = false;
            pack1000Pressed = false;
            drawPopup = true;
            selectPackPlayers(3);
        } else if (m500PackButton.isPushTriggered()) {
            //changeToScreen(new SplashScreen1(mGame));
            pack100Pressed = false;
            pack300Pressed = false;
            pack500Pressed = true;
            pack1000Pressed = false;
            drawPopup = true;
            selectPackPlayers(5);
            horizontalCardScroller.setMultiMode(true, 80);
        } else if (m1000PackButton.isPushTriggered()) {
            //changeToScreen(new SplashScreen1(mGame));
// check if player has enough xp!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            pack100Pressed = false;
            pack300Pressed = false;
            pack500Pressed = false;
            pack1000Pressed = true;
            drawPopup = true;
            selectPackPlayers(11);
            horizontalCardScroller.setMultiMode(true, 80);
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

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        // Clear the screen and draw the buttons
        graphics2D.clear(Color.WHITE);
        Paint myPaint = mGame.getPaint();
        myPaint.setAlpha(100);
        myPaint.setTextSize(72);

        graphics2D.drawBitmap(background, null, backGroundRectangle, myPaint);
        infoBar.draw(elapsedTime, graphics2D);

        mSquadsButton.draw(elapsedTime, graphics2D, null, null);
        mMenuButton.draw(elapsedTime, graphics2D, null, null);
        m100PackButton.draw(elapsedTime, graphics2D, null, null);
        m300PackButton.draw(elapsedTime, graphics2D, null, null);
        m500PackButton.draw(elapsedTime, graphics2D, null, null);
        m1000PackButton.draw(elapsedTime, graphics2D, null, null);

        if (pack1000Pressed || pack500Pressed || pack300Pressed || pack100Pressed) {
            if (drawPopup) {
                packPopUp.draw(elapsedTime, graphics2D);
                displayHorizontalScroller = false;
                if (packPopUp.getYesorNo()) {
                    drawPopup = false;
                    displayHorizontalScroller = true;
                    horizontalCardScroller.draw(elapsedTime, graphics2D);
                    packPopUp.setYesorNo(false);
                } else if (packPopUp.getHasNoBeenPressed()) {
                    drawPopup = false;
                    displayHorizontalScroller = false;
                    packPopUp.setHasNoBeenPressed(false);
                }
            }
            if (displayHorizontalScroller) horizontalCardScroller.draw(elapsedTime,graphics2D);
            }
        }


    public void selectPackPlayers(int packSizes) {
        int noOfRares = 0;
        Card packPlayers[] = new Card[packSizes];
        Random rnd = new Random();
        //Clear Scroller
        horizontalCardScroller.clearScroller();
        int highestRating = 0;
        for (int i = 0; i < packSizes; i++) {
            int rndPlayerID = rnd.nextInt(629);
            packPlayers[i] = new Card(960, 378, 540, this, String.valueOf(rndPlayerID), 100);
            if (packPlayers[i].isRare()) noOfRares = noOfRares + 1;
            if (noOfRares > 3 && packPlayers[i].isRare()) {
                while (packPlayers[i].isRare()) {
                    rndPlayerID = rnd.nextInt(629);
                    packPlayers[i] = new Card(960, 378, 540, this, String.valueOf(rndPlayerID), 100);
                }
            }
            /*if (i > 0 && (packPlayers[i].getRating() > packPlayers[i-1].getRating())) {
                Log.d("Debug", String.valueOf(packPlayers[i].getRating()));
                Log.d("Debug1", String.valueOf(packPlayers[i-1].getRating()));
                Card temp = packPlayers[i];
                packPlayers[i] = packPlayers[i-1];
                packPlayers[i-1] = temp;
                Log.d("Debug2", String.valueOf(packPlayers[i].getRating()));
                Log.d("Debug3", String.valueOf(packPlayers[i-1].getRating()));
            }*/
            if (packPlayers[i].getRating() > highestRating) {
                highestRating = packPlayers[i].getRating();
                highestRatedCard = packPlayers[i];
            }
            horizontalCardScroller.addScrollerItem(packPlayers[i]);
            mGame.getClub().add(packPlayers[i]);
        }

        horizontalCardScroller.setMultiMode(true, 80);
    }
}
