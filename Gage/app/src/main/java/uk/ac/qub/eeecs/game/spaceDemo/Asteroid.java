package uk.ac.qub.eeecs.game.spaceDemo;

import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * Simple asteroid
 *
 * @version 1.0
 */
public class Asteroid extends Sprite {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Private Random instance used to create the asteroids
     */
    private static Random random = new Random();

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create an asteroid
     *
     * @param startX     x location of the asteroid
     * @param startY     y location of the asteroid
     * @param gameScreen Gamescreen to which asteroid belongs
     */
    public Asteroid(float startX, float startY, GameScreen gameScreen) {
        super(startX, startY, 200.0f, 200.0f, null, gameScreen);

        //Load the assets used by Asteroid
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        if (assetManager.getBitmap("Asteroid1") == null) {
            assetManager.loadAndAddBitmap("Asteroid1", "img/Asteroid1.png");
        }
        if (assetManager.getBitmap("Asteroid2") == null) {
            assetManager.loadAndAddBitmap("Asteroid2", "img/Asteroid2.png");
        }

        mBitmap = assetManager.getBitmap(random.nextBoolean() ? "Asteroid1" : "Asteroid2");

        //Inaki - random float between 10 and 30 generated for size
        float halfLength = (random.nextFloat()*20 + 10);

        mBound.halfWidth = halfLength;
        mBound.halfHeight = halfLength;

        angularVelocity = random.nextFloat() * 240.0f - 20.0f;
    }
}
