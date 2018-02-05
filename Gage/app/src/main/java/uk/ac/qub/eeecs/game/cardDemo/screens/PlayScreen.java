package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Match;
import uk.ac.qub.eeecs.game.cardDemo.ui.HorizontalCardScroller;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;

/**
 * Created by stephenmcveigh on 04/12/2017.
 */

public class PlayScreen extends FootballGameScreen {
    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////
    public final Bitmap background;
    private final Rect backgroundRectangle;
    private final int totalGameTimeLength;
    private double currentGameTime;
    private int playerScore, CPUScore;
    private PushButton mScenarioButton;
    private PushButton scrollerButton;
    public Match currentMatch;

    private LayerViewport mLayerViewport;
    private ScreenViewport mScreenViewport;
    private InfoBar infoBar;

    private boolean scrollerEnabled = true;
    private boolean scrollerDisplayed = false;
    private boolean scrollerAnimationTriggered = false;
    private int scrollerMoveDirection = 1;
    private float distanceMoved = 0;

    private HorizontalCardScroller horizontalCardScroller;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////
    public PlayScreen(FootballGame game) {
        super("PlayScreen", game);
        mLayerViewport = new LayerViewport();
        mScreenViewport = new ScreenViewport();
        currentMatch = new Match(this);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("PlayScreenBackground", "img/pitch.png");
        assetManager.loadAndAddBitmap("HIS-Background", "img/his-background.png");
        background = assetManager.getBitmap("PlayScreenBackground");
        backgroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

        mScenarioButton = new PushButton(100f, 100f, 100, 100,
                "PlayScreenBackground", this );
        scrollerButton = new PushButton(mGame.getScreenWidth() * 0.5f, mGame.getScreenHeight() * 0.9f, mGame.getScreenWidth() * 0.2f, mGame.getScreenHeight() * 0.1f, "MenuButton", "MenuButtonPushed", this);
        scrollerButton.setButtonText("Show Scroller", 20, Color.rgb(242, 242, 242));
        scrollerButton.setButtonTextSizeMax();

        totalGameTimeLength = mGame.getIntPreference("GameLength");
        currentGameTime = 0.0;

        playerScore = currentMatch.getPlayerAScore();
        CPUScore = currentMatch.getPlayerBScore();

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", "Test Player", "M A I N  M E N U", "0 | 0 | 0");
        horizontalCardScroller = new HorizontalCardScroller(mGame.getScreenWidth() * 0.5f, (mGame.getScreenHeight() * 0.5f) + (mGame.getScreenHeight()), mGame.getScreenWidth(), mGame.getScreenHeight() * 0.4f, this);
        horizontalCardScroller.addTestData();
        horizontalCardScroller.setMultiMode(true, 100);
        horizontalCardScroller.setSelectMode(true);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Updates properties of the InfoBar
     */
    private void updateInfoBar() {
        infoBar.setAreaOneText(String.format("Player %1$d | %2$d CPU", playerScore, CPUScore));
        infoBar.setAreaTwoText(currentMatch.getGameState().name().replace("_", " ").replace("PLAYER A", "PLAYER").replace("PLAYER B", "CPU"));
        infoBar.setAreaThreeText(String.format("%2.2f", currentGameTime / totalGameTimeLength * 90));
    }

    /**
     * Called when the scroller button is clicked to trigger the scroller
     * move animation
     */
    private void scrollerButtonClicked() {
        if(!scrollerEnabled || scrollerAnimationTriggered) return;

        if(scrollerDisplayed) scrollerMoveDirection = 1;
        else scrollerMoveDirection = -1;

        scrollerAnimationTriggered = true;
    }

    /**
     * Checks if scroller animation is triggered then performs animation
     */
    private void checkAndPerformScrollerAnimation() {
        if(!scrollerAnimationTriggered) return;

        // Move scroller
        float moveBy = scrollerMoveDirection * mGame.getScreenHeight() * 0.05f;

        // Move scroller
        // Draw current item
        horizontalCardScroller.adjustPosition(0, moveBy);
        scrollerButton.adjustPosition(0f, moveBy * 0.6f);

        // Add to distance moved
        distanceMoved += Math.abs(moveBy);

        // If intended distance has been moved, end animation
        if(distanceMoved >= mGame.getScreenHeight() * 0.75f) {
            scrollerAnimationTriggered = false;
            scrollerDisplayed = !scrollerDisplayed;
            distanceMoved = 0;
            if(scrollerDisplayed) scrollerButton.setButtonText("Hide Players");
            else scrollerButton.setButtonText("Show Players");
        }

    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        mScenarioButton.update(elapsedTime);
        scrollerButton.update(elapsedTime);
        horizontalCardScroller.update(elapsedTime);

        currentGameTime += elapsedTime.stepTime;
        if (mScenarioButton.isPushTriggered()){
            currentMatch.scenario();
        }

        if (scrollerButton.isPushTriggered()) scrollerButtonClicked();

        updateInfoBar();
        checkAndPerformScrollerAnimation();
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        playerScore = currentMatch.getPlayerAScore();
        CPUScore = currentMatch.getPlayerBScore();
        Paint paint = mGame.getPaint();
        paint.setAlpha(100);
        graphics2D.drawBitmap(background,null, backgroundRectangle, paint);
        paint.reset();
        paint.setTextSize(45f);
        paint.setColor(Color.BLUE);

        mScenarioButton.draw(elapsedTime, graphics2D);
        scrollerButton.draw(elapsedTime, graphics2D);

        infoBar.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);

        horizontalCardScroller.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);
    }
}
