package uk.ac.qub.eeecs.game;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.*;
import uk.ac.qub.eeecs.game.cardDemo.screens.PackScreen;

/**
 * Created by Eoghan on 17/11/2017.
 */

public class SplashScreen1 extends FootballGameScreen {
    private Bitmap splashBmp;

    /*
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public SplashScreen1(FootballGame game, String message) {
        super("SplashScreen1", game);

        // Load in the bitmaps used on the Splash Screen
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("AndroidLogo", "img/Android-logo.png");
        splashBmp = assetManager.getBitmap("AndroidLogo");
    }

    @Override
    public void update(ElapsedTime elapsedTime) {

        // Process any touch events occurring since the last update
        Input input = mGame.getInput();

        List<TouchEvent> touchEvents = input.getTouchEvents();

        if (elapsedTime.totalTime >= 5.00) {
            //transition to main screen
            changeToScreen(new PackScreen(mGame));
        } else if (touchEvents.size() > 0) {
            //transition to main screen
            changeToScreen(new PackScreen(mGame));
        }
    }

    private void changeToScreen(FootballGameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        int screenWidth = mGame.getScreenWidth();
        int screenHeight = mGame.getScreenHeight();
        Matrix myMatrix = new Matrix();
        Paint myPaint = new Paint();
        graphics2D.clear(Color.BLACK);
        myMatrix.reset();
        myMatrix.setRotate((float) elapsedTime.totalTime * 122, (screenWidth / 2) - 100, (screenHeight / 2) - 100);
        //graphics2D.drawBitmap(splashBmp,myMatrix,myPaint);
    }
}
