package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;


/**
 * Created by Iñaki McKearney on 06/12/2017.
 */

public class SquadScreen extends FootballGameScreen {

    /**
     * Define variables for background
     */
    private final Bitmap background;
    private final Rect backGroundRectangle = new Rect(0,0, mGame.getScreenWidth(),mGame.getScreenHeight());

    /**
     * Define InfoBar
     */
    private InfoBar infoBar;

    /**
     * Define buttons /
     */
    private PushButton mBackButton;
    private PushButton mPlayButton;

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public SquadScreen(FootballGame game) {
        super("SquadScreen", game);
        FootballGame fGame = new FootballGame();

        //Load assets
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("SquadScreenBackground", "img/squadScreenBG.jpg");
        background = assetManager.getBitmap("SquadScreenBackground");
        assetManager.loadAndAddBitmap("BackButton", "img/LeftArrow.png");
        assetManager.loadAndAddBitmap("BackButtonActive", "img/LeftArrowActive.png");
        assetManager.loadAndAddBitmap("PlayButton", "img/RightArrow.png");
        assetManager.loadAndAddBitmap("PlayButtonActive", "img/RightArrowActive.png");

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", mGame.getPlayerName(), "S Q U A D S", mGame.getMatchStats());

        //Buttons
        float width = mGame.getScreenWidth();
        float height = mGame.getScreenHeight();
        mBackButton = new PushButton(width/10.0f, height /8.0f*7.0f, width / 10.0f, height / 6.0f,
                "BackButton","BackButtonActive", this );
        mPlayButton = new PushButton(width/10.0f*9.0f, height / 8.0f*7.0f, width / 10.0f, height / 6.0f,
                "PlayButton","PlayButtonActive", this );
    }

    private void changeToScreen(FootballGameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        infoBar.update(elapsedTime);

        mBackButton.update(elapsedTime);
        mPlayButton.update(elapsedTime);

        if (mBackButton.isPushTriggered())
            changeToScreen(new MenuScreen(mGame));
        if (mPlayButton.isPushTriggered()) {
            changeToScreen(new PlayScreen(mGame));
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.drawBitmap(background,null, backGroundRectangle, null);
        mBackButton.draw(elapsedTime, graphics2D, null, null);
        mPlayButton.draw(elapsedTime, graphics2D, null, null);
        infoBar.draw(elapsedTime, graphics2D);

    }
}
