package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.game.cardDemo.objects.FootballGameScreen;
import uk.ac.qub.eeecs.game.cardDemo.objects.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Pack;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;
import uk.ac.qub.eeecs.game.cardDemo.ui.PopUpWindow;

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
    private boolean displayHorizontalScroller = false;
    private boolean notEnoughCoins;

    private Pack myPack = null;

    private int costOfPack = 0;
    private  int newXP = 0;

    // Define the spacing that will be used to position the buttons
    int screenWidth = getGame().getScreenWidth();
    int screenHeight = getGame().getScreenHeight();

    private PopUpWindow packPopUp;
    private PopUpWindow notEnoughCoinsPopUp;

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

        mGame.setXp(200000);

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", "XP | " + String.valueOf(mGame.getXp()), "P A C K  S C R E E N", mGame.getMatchStats());

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
        assetManager.loadAndAddBitmap("splashScreenBG", "img/falling-confetti-background_1048-6409.png");
        background = assetManager.getBitmap("MainBackground");

        // Create the trigger buttons
        // Create the trigger buttons
        mMenuButton = new PushButton(screenWidth * 0.075f, screenHeight * 0.9f, screenWidth * 0.09f, screenWidth * 0.09f, "ArrowBack", "ArrowBackPushed", this);
        m100PackButton = new PushButton(screenWidth * 0.14f, screenHeight * 0.74f, screenWidth * 0.2f, 150, "MenuButton", "MenuButtonPushed", this);
        m300PackButton = new PushButton(screenWidth * 0.38f, screenHeight * 0.74f, screenWidth * 0.2f, 150, "MenuButton", "MenuButtonPushed", this);
        m500PackButton = new PushButton(screenWidth * 0.62f, screenHeight * 0.74f, screenWidth * 0.2f, 150, "MenuButton", "MenuButtonPushed", this);
        m1000PackButton = new PushButton(screenWidth * 0.86f, screenHeight * 0.74f, screenWidth * 0.2f, 150, "MenuButton", "MenuButtonPushed", this);

        m100PackButton.setButtonText("1 Player Pack | 100xp", 64, Color.WHITE);
        m300PackButton.setButtonText("3 Player Pack  | 300xp", 64, Color.WHITE);
        m500PackButton.setButtonText("5 Player Pack  | 500xp", 64, Color.WHITE);
        m1000PackButton.setButtonText("11 Player Pack | 1000xp", 64, Color.WHITE);

        m500PackButton.setEnabled(true);
        m300PackButton.setEnabled(true);

        packPopUp = new PopUpWindow(screenWidth * 0.5f, screenHeight * 0.5f, mGame.getScreenWidth(), screenHeight * 0.5f, this, "Are you sure you want to buy this pack?", "Yes", "No");
        notEnoughCoinsPopUp = new PopUpWindow(screenWidth * 0.5f, screenHeight * 0.5f, mGame.getScreenWidth(), screenHeight * 0.5f, this, "You dont have enough XP to buy this", "Cancel", "Menu");
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
        packPopUp.update(elapsedTime);
        notEnoughCoinsPopUp.update(elapsedTime);
        if (myPack != null )myPack.update(elapsedTime);

        // Update each button and transition if needed
        mMenuButton.update(elapsedTime);
        m100PackButton.update(elapsedTime);
        m300PackButton.update(elapsedTime);
        m500PackButton.update(elapsedTime);
        m1000PackButton.update(elapsedTime);

        infoBar.setAreaOneText("XP | " + String.valueOf(mGame.getXp()));

        if (mMenuButton.isPushTriggered())
            changeToScreen(new MenuScreen(mGame));
        else if (m100PackButton.isPushTriggered()) {
            pack100Pressed = true;
            pack300Pressed = false;
            pack500Pressed = false;
            pack1000Pressed = false;
            if (mGame.getXp() >= 100) {
                drawPopup = true;
                selectPackPlayers(1);
                notEnoughCoins = false;
                costOfPack = 100;
            }
            else {
                notEnoughCoins = true;
            }
        } else if (m300PackButton.isPushTriggered()) {

            pack100Pressed = false;
            pack300Pressed = true;
            pack500Pressed = false;
            pack1000Pressed = false;

            if (mGame.getXp() >= 300) {
                drawPopup = true;
                selectPackPlayers(3);
                notEnoughCoins = false;
                costOfPack = 300;
            }
            else {
                notEnoughCoins = true;
            }

        } else if (m500PackButton.isPushTriggered()) {
            pack100Pressed = false;
            pack300Pressed = false;
            pack500Pressed = true;
            pack1000Pressed = false;

            if (mGame.getXp() >= 500) {
                drawPopup = true;
                selectPackPlayers(5);
                notEnoughCoins = false;
                costOfPack = 500;
            }
            else {
                notEnoughCoins = true;
            }
        } else if (m1000PackButton.isPushTriggered()) {

            pack100Pressed = false;
            pack300Pressed = false;
            pack500Pressed = false;
            pack1000Pressed = true;

            if (mGame.getXp() >= 1000) {
                drawPopup = true;
                selectPackPlayers(11);
                notEnoughCoins = false;
                costOfPack = 1000;
            }
            else {
                notEnoughCoins = true;
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

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        // Clear the screen and draw the buttons
        Paint myPaint = mGame.getPaint();
        myPaint.setTextSize(72);

        graphics2D.drawBitmap(background, null, backGroundRectangle, myPaint);
        infoBar.draw(elapsedTime, graphics2D);

        mMenuButton.draw(elapsedTime, graphics2D, null, null);
        m100PackButton.draw(elapsedTime, graphics2D, null, null);
        m300PackButton.draw(elapsedTime, graphics2D, null, null);
        m500PackButton.draw(elapsedTime, graphics2D, null, null);
        m1000PackButton.draw(elapsedTime, graphics2D, null, null);

        if (pack1000Pressed || pack500Pressed || pack300Pressed || pack100Pressed) {
            if (notEnoughCoins) {
                notEnoughCoinsPopUp.draw(elapsedTime, graphics2D);
                m500PackButton.setEnabled(false);
                m300PackButton.setEnabled(false);
                notEnoughCoinsPopUp.enableButton1();
                notEnoughCoinsPopUp.enableButton2();
                if (notEnoughCoinsPopUp.getYesorNo()) {
                    drawPopup = false;
                    notEnoughCoins = false;
                    notEnoughCoinsPopUp.setYesorNo(false);
                    packPopUp.setYesorNo(false);
                    m300PackButton.setEnabled(true);
                    m500PackButton.setEnabled(true);
                    notEnoughCoinsPopUp.disableButton1();
                    notEnoughCoinsPopUp.disableButton2();
                    packPopUp.disableButton2();
                    packPopUp.disableButton1();
                } else if (notEnoughCoinsPopUp.getHasNoBeenPressed()) {
                    notEnoughCoinsPopUp.setHasNoBeenPressed(false);
                    packPopUp.setYesorNo(false);
                    changeToScreen(new MenuScreen(mGame));
                    notEnoughCoinsPopUp.disableButton1();
                    notEnoughCoinsPopUp.disableButton2();
                    packPopUp.disableButton2();
                    packPopUp.disableButton1();
                }
            } else {
                if (drawPopup) {
                    m500PackButton.setEnabled(false);
                    m300PackButton.setEnabled(false);
                    packPopUp.draw(elapsedTime, graphics2D);
                    displayHorizontalScroller = false;
                    packPopUp.enableButton1();
                    packPopUp.enableButton2();
                    if (packPopUp.getYesorNo()) {
                        newXP = mGame.getXp() - costOfPack;
                        mGame.setXp(newXP);
                        drawPopup = false;
                        displayHorizontalScroller = true;
                        notEnoughCoinsPopUp.setYesorNo(false);
                        packPopUp.setYesorNo(false);
                        m300PackButton.setEnabled(true);
                        m500PackButton.setEnabled(true);
                        packPopUp.disableButton2();
                        packPopUp.disableButton1();
                        notEnoughCoinsPopUp.disableButton1();
                        notEnoughCoinsPopUp.disableButton2();
                    } else if (packPopUp.getHasNoBeenPressed()) {
                        drawPopup = false;
                        displayHorizontalScroller = false;
                        notEnoughCoinsPopUp.setHasNoBeenPressed(false);
                        packPopUp.setHasNoBeenPressed(false);
                        m300PackButton.setEnabled(true);
                        m500PackButton.setEnabled(true);
                        packPopUp.disableButton2();
                        packPopUp.disableButton1();
                        notEnoughCoinsPopUp.disableButton1();
                        notEnoughCoinsPopUp.disableButton2();
                    }
                }
                    if (displayHorizontalScroller) {
                        if (myPack != null) myPack.draw(elapsedTime, graphics2D);
                        }
                    }
                }
            }


    public void selectPackPlayers(int packSizes) {
        int noOfRares, minRating, maxRating;

        switch (packSizes) {
            case 1: noOfRares = 0;
                    minRating = 0;
                    maxRating = 99;
            break;
            case 3: noOfRares = 1;
                    minRating = 0;
                    maxRating = 99;
            break;
            case 5: noOfRares = 2;
                    minRating = 0;
                    maxRating = 99;
            break;
            case 11: noOfRares = 5;
                    minRating = 0;
                    maxRating = 99;
            break;
            default: noOfRares = 0;
                    minRating = 0;
                    maxRating = 0;
            break;
        }
        myPack = new Pack(this, packSizes, noOfRares,minRating, maxRating);
    }

}
