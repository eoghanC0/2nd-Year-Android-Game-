package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
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

    public Match currentMatch;

    private InfoBar infoBar;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////
    public PlayScreen(FootballGame game) {
        super("PlayScreen", game);
        currentMatch = new Match(this);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("PlayScreenBackground", "img/pitch.png");
        assetManager.loadAndAddBitmap("HIS-Background", "img/his-background.png");
        background = assetManager.getBitmap("PlayScreenBackground");
        backgroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

        mScenarioButton = new PushButton(100f, 100f, 100, 100,
                "PlayScreenBackground", this );


        totalGameTimeLength = mGame.getIntPreference("GameLength");
        currentGameTime = 0.0;

        playerScore = currentMatch.getPlayerAScore();
        CPUScore = currentMatch.getPlayerBScore();

        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", "Test Player", "M A I N  M E N U", "0 | 0 | 0");

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




    @Override
    public void update(ElapsedTime elapsedTime) {
        mScenarioButton.update(elapsedTime);

        currentGameTime += elapsedTime.stepTime;
        if (mScenarioButton.isPushTriggered()){
            currentMatch.makeScenario();
        }


        updateInfoBar();

        currentMatch.update(elapsedTime);
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

        infoBar.draw(elapsedTime, graphics2D);

       currentMatch.draw(elapsedTime, graphics2D);

    }
}
