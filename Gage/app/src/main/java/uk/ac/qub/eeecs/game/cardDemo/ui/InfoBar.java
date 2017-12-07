package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.gage.util.Vector2;

/**
 * Created by eimhin on 30/11/2017.
 */

public class InfoBar extends GameObject {

    private Bitmap playerBitmap;
    private String playerName;
    private String winLossDraw;
    private int experience;
    private Rect playerIconRect;
    private Paint textPaint;

    private String areaOneText;
    private String areaTwoText;
    private String areaThreeText;

    private Vector2 areaOneVector;
    private Vector2 areaTwoVector;
    private Vector2 areaThreeVector;

    /**
     * Internal matrix use to support draw requests
     */
    protected Matrix drawMatrix = new Matrix();

    public InfoBar(float x, float y, float width, float height, GameScreen gameScreen) {
        super(x, y, width, height, null, gameScreen);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("InfoBar", "img/InfoBar.png");
        assetManager.loadAndAddBitmap("PlayerIcon", "img/Ball.png");
        mBitmap = assetManager.getBitmap("InfoBar");
        playerBitmap = assetManager.getBitmap("InfoBar");
        playerIconRect = new Rect(200,200,200,200);
        this.playerName = "Player Name";
        this.winLossDraw = "0-0-0";
        this.experience = 0;
        areaOneText = "Area One Text";
        areaTwoText = "Area Two Text";
        areaThreeText = "Area Three Text";
        textPaint = new Paint();
        textPaint.setTextSize(height * 0.45f);
        textPaint.setColor(Color.BLACK);

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));
    }

    public InfoBar(float x, float y, float width, float height, GameScreen gameScreen, String playerIconPath, String playerName, String winLossDraw, int experience) {
        super(x, y, width, height, null, gameScreen);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("InfoBar", "img/InfoBar.png");
        assetManager.loadAndAddBitmap("PlayerIcon", "img/Ball.png");
        mBitmap = assetManager.getBitmap("InfoBar");
        playerBitmap = assetManager.getBitmap("PlayerIcon");
        playerIconRect = new Rect(200,200,200,200);
        this.playerName = playerName;
        areaOneText = playerName;
        this.winLossDraw = winLossDraw;
        areaTwoText = winLossDraw;
        this.experience = experience;
        areaThreeText = String.valueOf(experience);
        textPaint = new Paint();
        textPaint.setTextSize(height * 0.45f);
        textPaint.setColor(Color.BLACK);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        //super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
        Log.d("DEBUG", drawScreenRect.toString() + " this.getbound.getbottom = " + this.getBound().getBottom()  + " this.getbound.gettop = " + this.getBound().getTop() + "position x:y" + position.x + " " +position.y);
        graphics2D.drawText(areaOneText, this.getBound().getLeft() + 280, -58, textPaint);
        graphics2D.drawText(areaTwoText, this.getBound().getLeft() + 900, position.y - mBound.halfHeight, textPaint);
        graphics2D.drawText(areaThreeText, this.getBound().getLeft() + 1600, this.getBound().getBottom(), textPaint);

        //graphics2D.drawBitmap(playerBitmap, null, playerIconRect, new Paint());

        if (GraphicsHelper.getSourceAndScreenRect(this, layerViewport,
                screenViewport, drawSourceRect, drawScreenRect)) {

            float scaleX =
                    (float) drawScreenRect.width()
                            / (float) drawSourceRect.width();
            float scaleY =
                    (float) drawScreenRect.height()
                            / (float) drawSourceRect.height();

            // Build an appropriate transformation matrix
            drawMatrix.reset();
            drawMatrix.postScale(scaleX, scaleY);
            drawMatrix.postRotate(0, scaleX * mBitmap.getWidth()
                    / 2.0f, scaleY * mBitmap.getHeight() / 2.0f);
            drawMatrix.postTranslate(drawScreenRect.left, drawScreenRect.top);

            // Draw the image
            graphics2D.drawBitmap(mBitmap, drawMatrix, null);

            graphics2D.drawText(areaOneText, scaleX * 100, scaleY * mBound.getTop(), textPaint);
        }
    }
}
