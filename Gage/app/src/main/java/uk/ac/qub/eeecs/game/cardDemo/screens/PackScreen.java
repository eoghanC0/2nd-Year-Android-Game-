package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.Menu;

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
import uk.ac.qub.eeecs.game.packScreenSplashScreen;

/**
 * Created by stephenmcveigh on 07/12/2017.
 */

public class PackScreen extends FootballGameScreen {

    private final Bitmap background;
    private final Bitmap splashScreenBackground;
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
    private boolean packBoughtDisplaySplashScreen;
    private boolean splashScreenDislpayed = false;
    private boolean trackTime;

    private int costOfPack = 0;
    private  int newXP = 0;
    private float highestRatedCardX;
    private float highestRatedCardY;


    // Define the spacing that will be used to position the buttons
    int spacingX = getGame().getScreenWidth() / 4;
    int spacingY = getGame().getScreenHeight() / 8;

    private Card highestRatedCard;

    private Bitmap baseBitmap;
    private Bitmap backOfCard;

    private popUpWindow packPopUp;
    private popUpWindow notEnoughCoinsPopUp;

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

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", "Test Player", "P A C K  S C R E E N", "XP: " + mGame.getXp());

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
        assetManager.loadAndAddBitmap("splashScreenBG", "img/falling-confetti-background_1048-6409.png");
        background = assetManager.getBitmap("packScreenBG");
        splashScreenBackground = assetManager.getBitmap("splashScreenBG");

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

        m500PackButton.setEnabled(true);
        m300PackButton.setEnabled(true);

        horizontalCardScroller = new HorizontalCardScroller(mGame.getScreenWidth() / 2, spacingY * 2.8f, mGame.getScreenWidth(), spacingY * 4, this);
        packPopUp = new popUpWindow(mGame.getScreenWidth() / 2, spacingY * 2.8f, mGame.getScreenWidth(), spacingY * 4, this, "Are you sure you want to buy this pack?", "Yes", "No");
        notEnoughCoinsPopUp = new popUpWindow(mGame.getScreenWidth() / 2, spacingY * 2.8f, mGame.getScreenWidth(), spacingY * 4, this, "You dont have enough XP to buy this", "Cancel", "Menu");

        assetManager.loadAndAddBitmap("BaseBitmap", "img/CardFront.png");
        baseBitmap = assetManager.getBitmap("BaseBitmap");
        if (baseBitmap == null)
            Log.d("DEBUG", "HorizontalCardScroller: NO BASE BITMAP");
        assetManager.loadAndAddBitmap("CardBack", "img/CardBack.png");
        backOfCard = assetManager.getBitmap("CardBack");
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
        notEnoughCoinsPopUp.update(elapsedTime);

        // Update each button and transition if needed
        mSquadsButton.update(elapsedTime);
        mMenuButton.update(elapsedTime);
        m100PackButton.update(elapsedTime);
        m300PackButton.update(elapsedTime);
        m500PackButton.update(elapsedTime);
        m1000PackButton.update(elapsedTime);

        infoBar.setAreaThreeText("XP : " + String.valueOf(mGame.getXp()));

        if (mSquadsButton.isPushTriggered())
            changeToScreen(new SquadScreen(mGame));
        else if (mMenuButton.isPushTriggered())
            changeToScreen(new MenuScreen(mGame));
        else if (m100PackButton.isPushTriggered()) {
            pack100Pressed = true;
            pack300Pressed = false;
            pack500Pressed = false;
            pack1000Pressed = false;
            splashScreenDislpayed = false;
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
            splashScreenDislpayed = false;

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
            splashScreenDislpayed = false;

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
            splashScreenDislpayed = false;

            if (mGame.getXp() >= 1000) {
                drawPopup = true;
                selectPackPlayers(11);
                notEnoughCoins = false;
                costOfPack = 1000;
            }
            else {
                notEnoughCoins = true;
            }
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
Log.d("Debug", "Draw");
        if (elapsedTime.totalTime % 5 > 0 && elapsedTime.totalTime % 5 < 0.1) packBoughtDisplaySplashScreen = false;

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
            if (notEnoughCoins) {
                notEnoughCoinsPopUp.draw(elapsedTime, graphics2D);
                m500PackButton.setEnabled(false);
                m300PackButton.setEnabled(false);
                notEnoughCoinsPopUp.enableButton1();
                notEnoughCoinsPopUp.enableButton2();
                packBoughtDisplaySplashScreen = false;
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
                        packBoughtDisplaySplashScreen = true;
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
                        packBoughtDisplaySplashScreen = false;
                    }
                }

                if (packBoughtDisplaySplashScreen) {
                    splashScreen(elapsedTime,graphics2D);
                    splashScreenDislpayed = true;
                }

                if (splashScreenDislpayed && packBoughtDisplaySplashScreen == false) {
                    highestRatedCard.setPosition(highestRatedCardX,highestRatedCardY);
                    if (displayHorizontalScroller) horizontalCardScroller.draw(elapsedTime, graphics2D);

                    m100PackButton.setEnabled(true);
                    m300PackButton.setEnabled(true);
                    m500PackButton.setEnabled(true);
                    m1000PackButton.setEnabled(true);
                    mSquadsButton.setEnabled(true);
                    mMenuButton.setEnabled(true);
                }
            }
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

            if (packPlayers[i].getRating() > highestRating) {
                highestRating = packPlayers[i].getRating();
                highestRatedCard = packPlayers[i];
            }
            horizontalCardScroller.addScrollerItem(packPlayers[i]);
            mGame.getClub().add(packPlayers[i]);
        }
        highestRatedCardX = highestRatedCard.getBound().x;
        highestRatedCardY = highestRatedCard.getBound().y;
        Log.d("debug", String.valueOf(highestRatedCardX));
        Log.d("debug", String.valueOf(highestRatedCardY));

        horizontalCardScroller.setMultiMode(true, 80);
    }

    public void splashScreen(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        Paint myPaint = mGame.getPaint();
        myPaint.reset();
        m100PackButton.setEnabled(false);
        m300PackButton.setEnabled(false);
        m500PackButton.setEnabled(false);
        m1000PackButton.setEnabled(false);
        mSquadsButton.setEnabled(false);
        mMenuButton.setEnabled(false);
        graphics2D.drawBitmap(splashScreenBackground, null, backGroundRectangle, myPaint);

        highestRatedCard.setPosition(spacingX * 2, spacingY*4);
        highestRatedCard.draw(elapsedTime, graphics2D);
    }
}
