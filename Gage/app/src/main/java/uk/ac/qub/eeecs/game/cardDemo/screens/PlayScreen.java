package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.text.DecimalFormat;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.Button;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.objects.Match;

/**
 * Created by stephenmcveigh on 04/12/2017.
 */

public class PlayScreen extends GameScreen {
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

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////
    public PlayScreen(Game game) {
        super("PlayScreen", game);
        currentMatch = new Match(this);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("PlayScreenBackground", "img/pitch.png");
        background = assetManager.getBitmap("PlayScreenBackground");
        backgroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

        mScenarioButton = new PushButton(100f, 100f, 100, 100,
                "PlayScreenBackground", this );

        totalGameTimeLength = mGame.getIntPreference("GameLength");
        currentGameTime = 0.0;

        playerScore = currentMatch.getPlayerAScore();
        CPUScore = currentMatch.getPlayerBScore();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////
    @Override
    public void update(ElapsedTime elapsedTime) {
        mScenarioButton.update(elapsedTime);


        currentGameTime += elapsedTime.stepTime;
        if (mScenarioButton.isPushTriggered()){
            currentMatch.scenario();
        }


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
        graphics2D.drawText("Player: " + playerScore + " - " + CPUScore + " :CPU", 50, 50, paint);
        graphics2D.drawText("Timer : " + String.format("%2.2f", currentGameTime/totalGameTimeLength*90), this.getGame().getScreenWidth() - 500, 50, paint);
        graphics2D.drawText("State : " + ( currentMatch.getGameState().name() ), this.getGame().getScreenWidth() - 1500, 50, paint);

        mScenarioButton.draw(elapsedTime, graphics2D);


    }
}
