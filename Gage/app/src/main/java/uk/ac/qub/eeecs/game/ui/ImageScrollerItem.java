package uk.ac.qub.eeecs.game.ui;

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

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Any user generated metadata possibly for assigning ID's etc
     */
    public String[] metaData;

    /**
     * Rect of the mBound
     */
    private Rect mBoundRect = new Rect();


    // /////////////////////////////////////////////////////////////////////////
    // Cnostructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Default constructor
     *
     * @param gameScreen Associated GameScreen
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
     * @param x x-position
     * @param y y-position
     * @param width Width of ImageScrollerItem
     * @param height Height of ImageScrollerItem
     * @param bitmap Bitmap of ImageScrollerItem
     * @param gameScreen Associated GameScreen
     */
    public ImageScrollerItem(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen) {
        super(x, y, width, height, bitmap, gameScreen);
        position = new Vector2(x,y);

        if(bitmap != null) mBitmap = bitmap;
        else {
            gameScreen.getGame().getAssetManager().loadAndAddBitmap("Empty", "img/empty.png");
            mBitmap = gameScreen.getGame().getAssetManager().getBitmap("Empty");
        }

        metaData = new String[]{"ImageScrollerItem"};
        calculateMBoundRect();
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Sets the width and height
     *
     * @param width New width of ImageScrollerItem
     * @param height New height of ImageScrollerItem
     */
    public void setWidthAndHeight(float width, float height) {
        mBound.halfWidth = width / 2;
        mBound.halfHeight = height / 2;
    }

    /**
     * Calculate the mBoundRect
     */
    private void calculateMBoundRect() {
        mBoundRect.set((int) (position.x - mBound.getWidth()),
                (int) (position.y - mBound.getHeight()),
                (int) (position.x + mBound.getWidth()),
                (int) (position.y + mBound.getHeight()));
    }


    // /////////////////////////////////////////////////////////////////////////
    // Update
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Setters
    // /////////////////////////////////////////////////////////////////////////

    public void setBitmap(Bitmap bitmap) {
        if(bitmap != null) mBitmap = bitmap;
    }
}
