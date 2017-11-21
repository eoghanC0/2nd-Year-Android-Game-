package uk.ac.qub.eeecs.game.performance;

import android.util.Log;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

import static android.R.attr.height;
import static android.R.attr.width;
import static android.R.attr.x;
import static android.R.attr.y;
import static android.content.ContentValues.TAG;

/**
 * Rectangular class used to draw rectangles on PerformanceScreen
 *
 * @version 1.0
 */
public class PerformanceRect extends GameObject {

    public PerformanceRect(float x, float y, float width, float height, GameScreen gameScreen) {
        super(x, y, width, height, null, gameScreen);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("PerformanceImg", "img/PerformanceRect.png");
        mBitmap = assetManager.getBitmap("PerformanceImg");
    }


    public void set(float x, float y, int width, int height) {
        position.x = x;
        position.y = y;

        mBound.x = x;
        mBound.y = y;
        mBound.halfWidth = width / 2.0f;
        mBound.halfHeight = height / 2.0f;
    }

    public String getInfo() {
        return String.format("x: %1$f | y:%2$f | width: %3$f | height: %4$f", position.x, position.y, mBound.getWidth(), mBound.getHeight());
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
    }

    public float getX() {
        return position.x;
    }

    public void setX(float x) {
        this.position.x = x;
    }

    public float getY() {
        return position.y;
    }

    public void setY(float y) {
        this.position.y = y;
    }

    public float getWidth() {
        return mBound.getWidth();
    }

    public void setWidth(float width) {
        mBound.halfWidth = width / 2.0f;
    }

    public float getHeight() {
        return mBound.getHeight();
    }

    public void setHeight(float height) {
        mBound.halfHeight = height / 2.0f;
    }

}
