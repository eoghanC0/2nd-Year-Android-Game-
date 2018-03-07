package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
    private boolean notEnoughCoins;

    private int costOfPack = 0;
    private  int newXP = 0;

    // Define the spacing that will be used to position the buttons
    int screenWidth = getGame().getScreenWidth();
    int screenHeight = getGame().getScreenHeight();

    private Card highestRatedCard;

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

        assetManager.loadAndAddBitmap("packsIcon", "img/ball2.jpg");
        assetManager.loadAndAddBitmap("LeftArrow", "img/LeftArrow.png");
        assetManager.loadAndAddBitmap("LeftArrowActive", "img/LeftArrowActive.png");
        assetManager.loadAndAddBitmap("RightArrow", "img/RightArrow.png");
        assetManager.loadAndAddBitmap("RightArrowActive", "img/RightArrowActive.png");
        background = assetManager.getBitmap("MainBackground");

        // Create the trigger buttons
        mMenuButton = new PushButton(screenWidth * 0.075f, screenHeight * 0.9f, screenWidth * 0.1f, screenWidth * 0.1f, "ArrowBack", "ArrowBackPushed", this);
        mSquadsButton = new PushButton(screenWidth * 0.925f, screenHeight * 0.9f, screenWidth * 0.1f, screenWidth * 0.1f, "ArrowForward", "ArrowForwardPushed", this);
        m100PackButton = new PushButton(screenWidth * 0.14f, screenHeight * 0.75f, screenWidth * 0.2f, 150, "MenuButton", "MenuButtonPushed", this);
        m300PackButton = new PushButton(screenWidth * 0.38f, screenHeight * 0.75f, screenWidth * 0.2f, 150, "MenuButton", "MenuButtonPushed", this);
        m500PackButton = new PushButton(screenWidth * 0.62f, screenHeight * 0.75f, screenWidth * 0.2f, 150, "MenuButton", "MenuButtonPushed", this);
        m1000PackButton = new PushButton(screenWidth * 0.86f, screenHeight * 0.75f, screenWidth * 0.2f, 150, "MenuButton", "MenuButtonPushed", this);

        m100PackButton.setButtonText("1 Player Pack | 100xp", 64, Color.WHITE);
        m300PackButton.setButtonText("3 Player Pack  | 300xp", 64, Color.WHITE);
        m500PackButton.setButtonText("5 Player Pack  | 500xp", 64, Color.WHITE);
        m1000PackButton.setButtonText("11 Player Pack | 1000xp", 64, Color.WHITE);

        m500PackButton.setEnabled(true);
        m300PackButton.setEnabled(true);

        horizontalCardScroller = new HorizontalCardScroller(mGame.getScreenWidth() / 2,  screenHeight * 0.4f, mGame.getScreenWidth(), screenHeight * 0.6f, this);
        packPopUp = new popUpWindow(screenWidth * 0.5f, screenHeight * 0.5f, mGame.getScreenWidth(), screenHeight * 0.5f, this, "Are you sure you want to buy this pack?", "Yes", "No");
        notEnoughCoinsPopUp = new popUpWindow(screenWidth * 0.5f, screenHeight * 0.5f, mGame.getScreenWidth(), screenHeight * 0.5f, this, "You dont have enough XP to buy this", "Cancel", "Menu");
    }

    /*public PackScreen(FootballGame game, Boolean isPack100Pressed, Boolean isPack300Pressed, Boolean isPack500Pressed, Boolean isPack1000Pressed) {
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
    }*/

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
            //changeToScreen(new SplashScreen1(mGame));
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
        Paint myPaint = mGame.getPaint();
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
                if (notEnoughCoinsPopUp.getYesorNo()) {
                    drawPopup = false;
                    notEnoughCoins = false;
                    notEnoughCoinsPopUp.setYesorNo(false);
                    packPopUp.setYesorNo(false);
                    m300PackButton.setEnabled(true);
                    m500PackButton.setEnabled(true);
                } else if (notEnoughCoinsPopUp.getHasNoBeenPressed()) {
                    notEnoughCoinsPopUp.setHasNoBeenPressed(false);
                    packPopUp.setYesorNo(false);
                    changeToScreen(new MenuScreen(mGame));
                }
            } else {
                if (drawPopup) {
                    m500PackButton.setEnabled(false);
                    m300PackButton.setEnabled(false);
                    packPopUp.draw(elapsedTime, graphics2D);
                    displayHorizontalScroller = false;
                    if (packPopUp.getYesorNo()) {
                        newXP = mGame.getXp() - costOfPack;
                        mGame.setXp(newXP);
                        drawPopup = false;
                        displayHorizontalScroller = true;
                        horizontalCardScroller.draw(elapsedTime, graphics2D);
                        notEnoughCoinsPopUp.setYesorNo(false);
                        packPopUp.setYesorNo(false);
                        m300PackButton.setEnabled(true);
                        m500PackButton.setEnabled(true);
                    } else if (packPopUp.getHasNoBeenPressed()) {
                        drawPopup = false;
                        displayHorizontalScroller = false;
                        notEnoughCoinsPopUp.setHasNoBeenPressed(false);
                        packPopUp.setHasNoBeenPressed(false);
                        m300PackButton.setEnabled(true);
                        m500PackButton.setEnabled(true);
                    }
                }
                if (displayHorizontalScroller) horizontalCardScroller.draw(elapsedTime, graphics2D);
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
