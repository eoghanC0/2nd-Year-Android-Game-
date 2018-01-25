package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Created by eimhin on 24/01/2018.
 */

public class ImageScrollerItem extends GameObject {
    /**
     * Any user generated metadata possibly for assigning ID's etc
     */
    public String[] metaData;

    /**
     * Used by the ImageScroller for management purposes
     */
    public int index;

    /**
     * Rect of the mBound
     */
    private Rect mBoundRect = new Rect();

    /**
     * Scaled dimensions
     */
    private Vector2 scaledDimensions = new Vector2();

    /**
     * Default constructor
     * @param gameScreen
     */
    public ImageScrollerItem(GameScreen gameScreen) {
        super(gameScreen);
        position = new Vector2(0,0);
        AssetStore assetStore = gameScreen.getGame().getAssetManager();
        assetStore.loadAndAddBitmap("Empty", "img/empty.png");
        mBitmap = assetStore.getBitmap("Empty");
        metaData = new String[]{"ImageScrollerItem"};
    }

    /**
     * Parameterised constructor
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bitmap
     * @param gameScreen
     */
    public ImageScrollerItem(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen) {
        super(x, y, width, height, bitmap, gameScreen);
        position = new Vector2(x,y);

        if(bitmap != null) mBitmap = bitmap;
        else {
            AssetStore assetStore = gameScreen.getGame().getAssetManager();
            assetStore.loadAndAddBitmap("Empty", "img/empty.png");
        }

        metaData = new String[]{"ImageScrollerItem"};
        calculateMBoundRect();
    }

    public void setBitmap(Bitmap bitmap) {
        if(bitmap != null) mBitmap = bitmap;
    }

    public void setWidthAndHeight(float width, float height) {
        mBound.halfWidth = width / 2;
        mBound.halfHeight = height / 2;
    }

    public void setAssetStoreBitmap(String bitmapName, GameScreen gameScreen) {
        // Retrieve bitmap from gameScreen's AssetStore
        AssetStore assetStore = gameScreen.getGame().getAssetManager();
        mBitmap = assetStore.getBitmap(bitmapName);
        if(mBitmap == null) mBitmap = assetStore.getBitmap("Empty");
    }

    private void calculateMBoundRect() {
        mBoundRect.set((int) (position.x - mBound.getWidth()),
                (int) (position.y - mBound.getHeight()),
                (int) (position.x + mBound.getWidth()),
                (int) (position.y + mBound.getHeight()));
    }

    public Rect getMBoundRect() {
        return mBoundRect;
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
        calculateMBoundRect();
    }
}
