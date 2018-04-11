package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Created by eimhin on 11/01/2018.
 */

/**
 * This class allows you to create a horizontally moving image scroller
 *
 * Clicking the left side of the scroller moves the image(s) right, displaying the previous image
 * Clicking the right side of the scroller moves the image(s) left, displaying the next image
 *
 * Images are automatically scaled to fit within the scroller
 *
 * User can toggle between single bitmap and multi-bitmap mode
 *
 * - Single bitmap
 *      Bitmaps displayed one at a time
 *      Bitmaps are scaled to fit the total height of the scroller
 *      Scroller cycles in a loop
 * - Multi-bitmap
 *      Displays multiple bitmaps at a time by using the full width of the scroller
 *      User chooses the maximum bitmap height, allowing for more/less bitmaps to be displayed
 *      Scroller cycles in a loop
 *
 * Default Settings:
 * - multiMode = false
 *
 */
public class ImageScroller extends Scroller<ImageScrollerItem> {

    // /////////////////////////////////////////////////////////////////////////
    // Constructor
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Main constructor
     * @param x
     * @param y
     * @param width
     * @param height
     * @param gameScreen
     */
    public ImageScroller(float x, float y, float width, float height, GameScreen gameScreen) {
        super(x, y, width > 0 ? width : -width, height > 0 ? height : -height, gameScreen);
        if(width < 0) width = 100;
        if(height < 0) height = 100;
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("Empty", "img/empty.png");
        assetManager.loadAndAddBitmap("Test","img/help-image-test.png");
        mBitmap = assetManager.getBitmap("Empty");

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));

        currentItemPosition = new Vector2(position.x,position.y);
        calculateNextSingleVector();

        itemDistance = mBound.getWidth();

        scrollerItems = new ArrayList<ImageScrollerItem>();
    }

    @Override
    public void addScrollerItem(GameObject imageScrollerItem) {
        if(imageScrollerItem != null || scrollerItems.size() <= maxScrollerItems) {
            if(scrollerItems.size() == 0) {
                currentItemIndex = 0;
                baseBitmap = imageScrollerItem.getBitmap();
            }
            else if(scrollerItems.size() == 1) nextItemIndex = 1;

            Vector2 dimensions = getNewBitmapDimensions(imageScrollerItem.getBitmap(), (int) mBound.getHeight(), true);
            scrollerItems.add(new ImageScrollerItem(position.x, position.y, dimensions.x * 2, dimensions.y * 2, imageScrollerItem.getBitmap(), mGameScreen));
        }
    }

    /**
     * Adds item to scroller
     * @param bitmap
     */
    public void addScrollerItem(Bitmap bitmap) {
        if(bitmap != null || scrollerItems.size() <= maxScrollerItems) {
            if(scrollerItems.size() == 0) currentItemIndex = 0;
            else if(scrollerItems.size() == 1) nextItemIndex = 1;

            Vector2 dimensions = getNewBitmapDimensions(bitmap, (int) mBound.getHeight(), true);
            scrollerItems.add(new ImageScrollerItem(position.x, position.y, dimensions.x * 2, dimensions.y * 2, bitmap, mGameScreen));
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Update and Draw
    // /////////////////////////////////////////////////////////////////////////


    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        super.draw(elapsedTime, graphics2D);

        if(multiMode) {
            // If a current item exists, draw any current items else return
            if(currentItemIndex == -1) return;

            // Determine how many current items to draw, then draw
            int breaker = currentItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - currentItemIndex : maxDisplayedItems;
            for (int i = 0; i < breaker; i++) {
                scrollerItems.get(currentItemIndex + i).draw(elapsedTime, graphics2D);
            }

            // Continue if scroll animation has been triggered else return
            if(scrollAnimationTriggered) {
                // Determine how many next items to draw, then draw
                breaker = nextItemIndex + maxDisplayedItems >= scrollerItems.size() ? scrollerItems.size() - nextItemIndex : maxDisplayedItems;
                for (int i = 0; i < breaker; i++) {
                    scrollerItems.get(nextItemIndex + i).draw(elapsedTime, graphics2D);
                }
            }
        } else {
            // If current item exists draw else return
            if(currentItemIndex == -1) return;
            scrollerItems.get(currentItemIndex).draw(elapsedTime, graphics2D);

            // Draw page icons
            drawPageIcons(graphics2D);

            if(nextItemIndex == -1) return;

            // If a scroll animation has been triggered, draw next item
            if(scrollAnimationTriggered)
                scrollerItems.get(nextItemIndex).draw(elapsedTime, graphics2D);
        }

        // Draw page icons
        drawPageIcons(graphics2D);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Testing
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void addTestData() {
        mGameScreen.getGame().getAssetManager().loadAndAddBitmap("TestBitmap", "img/card-1.png");
        Bitmap testBitmap =  mGameScreen.getGame().getAssetManager().getBitmap("TestBitmap");
        ImageScrollerItem imageScrollerItem = new ImageScrollerItem(0,0,100,200, testBitmap, mGameScreen);
        for (int i = 0; i < 8; i++) {
            addScrollerItem(imageScrollerItem);
        }
    }
}