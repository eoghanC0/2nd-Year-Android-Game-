package uk.ac.qub.eeecs.game.cardDemo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.text.DecimalFormat;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Created by stephenmcveigh on 04/12/2017.
 */

public class PlayScreen extends GameScreen {
    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////
    private final Bitmap background;
    private final Rect backGroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());
    private final Paint paint = new Paint();
    private final int totalGameTimeLength = 5400;
    private double currentGameTime;
    private int playerScore, CPUScore;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////
    public PlayScreen(Game game) {
        super("PlayScreen", game);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("PlayScreenBackground", "img/pitch.png");
        background = assetManager.getBitmap("PlayScreenBackground");
        currentGameTime = 0.0;
        playerScore = 0;
        CPUScore = 0;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////
    public void update(ElapsedTime elapsedTime) {
        currentGameTime += elapsedTime.stepTime;
    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        paint.reset();
        paint.setAlpha(100);
        graphics2D.drawBitmap(background,null, backGroundRectangle, paint);
        paint.setTextSize(45f);
        paint.setColor(Color.BLUE);
        graphics2D.drawText("Player: " + playerScore + " - " + CPUScore + " :CPU", 50, 50, paint);
        graphics2D.drawText("Timer : " + Double.toString(currentGameTime/totalGameTimeLength*90), this.getGame().getScreenWidth() - 500, 50, paint);
    }
}
