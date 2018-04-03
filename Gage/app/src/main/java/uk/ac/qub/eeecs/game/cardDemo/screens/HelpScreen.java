package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.game.cardDemo.objects.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.objects.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.ui.ImageScroller;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;

/**
 * Created by eimhin on 27/11/2017.
 */

public class HelpScreen extends FootballGameScreen {

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
    private ImageScroller imageScroller;
    
    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public HelpScreen(FootballGame game) {
        super("HelpScreen", game);

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", "XP | " + String.valueOf(mGame.getXp()), "H E L P  S C R E E N", mGame.getMatchStats());

        AssetStore assetManager = mGame.getAssetManager();

        // Load in bitmaps
        assetManager.loadAndAddBitmap("HelpGetPlayers", "img/help-get-new-players.png");
        assetManager.loadAndAddBitmap("HelpPlayAGame", "img/help-play-a-game.png");
        assetManager.loadAndAddBitmap("HelpPlaying", "img/help-playing.png");
        assetManager.loadAndAddBitmap("HelpToggleSounds", "img/help-toggle-sounds.png");

        background = assetManager.getBitmap("MainBackground");

        menuScreenButton = new PushButton(
                mGame.getScreenWidth() * 0.075f, mGame.getScreenHeight() * 0.9f, mGame.getScreenWidth() * 0.1f, mGame.getScreenWidth() * 0.1f, "ArrowBack", "ArrowBackPushed", this);

        imageScroller = new ImageScroller(mGame.getScreenWidth() / 2, mGame.getScreenHeight() / 2, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.80f, this);
        imageScroller.addScrollerItem(assetManager.getBitmap("HelpPlayAGame"));
        imageScroller.addScrollerItem(assetManager.getBitmap("HelpPlaying"));
        imageScroller.addScrollerItem(assetManager.getBitmap("HelpGetPlayers"));
        imageScroller.addScrollerItem(assetManager.getBitmap("HelpToggleSounds"));

    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        infoBar.update(elapsedTime);
        menuScreenButton.update(elapsedTime);

        if(menuScreenButton.isPushTriggered()) changeToScreen(new MenuScreen(mGame));

        imageScroller.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(0xe6ffff);
        graphics2D.drawBitmap(background,null, backgroundRect, null);
        infoBar.draw(elapsedTime, graphics2D);
        imageScroller.draw(elapsedTime, graphics2D);
        menuScreenButton.draw(elapsedTime, graphics2D, null, null);
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


