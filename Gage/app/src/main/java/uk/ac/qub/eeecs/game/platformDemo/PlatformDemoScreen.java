package uk.ac.qub.eeecs.game.platformDemo;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.R;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.CollisionDetector;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.gage.engine.audio.Music;

import static android.R.attr.level;
import static android.content.ContentValues.TAG;

/**
 * A simple platform-style demo that generates a number of platforms and
 * provides a player controlled entity that can move about the images.
 * <p>
 * Illustrates both button based user input and collision handling.
 *
 * @version 1.0
 */
public class PlatformDemoScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the width and height of the game world
     */
    private final float LEVEL_WIDTH = 2000.0f;
    private final float LEVEL_HEIGHT = 320.0f;

    /**
     * Define the width and height of the game world grid's individual boxes
     * Used to calculate the appropriate spawn positions of platforms
     */
    private final float LEVEL_GRID_WIDTH = 35.0f;
    private final float LEVEL_GRID_HEIGHT = 35.0f;

    /**
     * Define the layer and screen view ports
     */
    private ScreenViewport mScreenViewport;
    private LayerViewport mLayerViewport;

    /**
     * Create three simple touch controls for player input
     */
    private PushButton moveLeft, moveRight, jumpUp;
    private List<PushButton> mControls;

    /**
     * Define an array of sprites to populate the game world
     */
    private ArrayList<Platform> mPlatforms;

    /**
     * Define the player
     */
    private PlayerSphere mPlayer;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a simple platform game level
     *
     * @param game Game to which this screen belongs
     */
    public PlatformDemoScreen(Game game) {
        super("PlatformDemoScreen", game);

        // Create the view ports
        mLayerViewport = new LayerViewport(240, 160, 240, 160);
        mScreenViewport = new ScreenViewport();
        GraphicsHelper.create3To2AspectRatioScreenViewport(game, mScreenViewport);

        // Load in the assets used by this layer
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("RightArrow", "img/RightArrow.png");
        assetManager.loadAndAddBitmap("LeftArrow", "img/LeftArrow.png");
        assetManager.loadAndAddBitmap("UpArrow", "img/UpArrow.png");
        assetManager.loadAndAddBitmap("RightArrowActive", "img/RightArrowActive.png");
        assetManager.loadAndAddBitmap("LeftArrowActive", "img/LeftArrowActive.png");
        assetManager.loadAndAddBitmap("UpArrowActive", "img/UpArrowActive.png");
        assetManager.loadAndAddSound("JumpSound", "sounds/Jump_Sound.mp3");

        // Determine the screen size to correctly position the touch buttons
        int screenWidth = game.getScreenWidth();
        int screenHeight = game.getScreenHeight();

        // Create and position the touch buttons
        mControls = new ArrayList<PushButton>();
        moveLeft = new PushButton(100.0f, (screenHeight - 100.0f),
                100.0f, 100.0f, "LeftArrow", "LeftArrowActive", this);
        mControls.add(moveLeft);
        moveRight = new PushButton(225.0f, (screenHeight - 100.0f),
                100.0f, 100.0f, "RightArrow", "RightArrowActive", this);
        mControls.add(moveRight);
        jumpUp = new PushButton((screenWidth - 125.0f),
                (screenHeight - 100.0f), 100.0f, 100.0f, "UpArrow", "UpArrowActive", "JumpSound", false, this);
        mControls.add(jumpUp);

        // Create the player
        mPlayer = new PlayerSphere(100.0f, 100.0f, this);

        // Create the platforms
        mPlatforms = new ArrayList<Platform>();

        // Add a wide platform for the ground tile
        int groundTileWidth = 64, groundTileHeight = 35, groundTiles = 50;
        mPlatforms.add(
                new Platform(groundTileWidth * groundTiles / 2, groundTileHeight / 2,
                        groundTileWidth * groundTiles, groundTileHeight,
                        "Ground", groundTiles, 1, this));

        // Add a number of randomly positioned platforms. They are not added in
        // the first 300 units of the level to avoid overlap with the player.
        Random random = new Random();
        int platformWidth = 70, platformHeight = 70, nNumRandomPlatforms = 30;
        int platformX, platformY, randNum;
        Platform tempPlatform = new Platform(0f,0f,0f,0f,"Platform",this);
        String platformName = "Platform";
        
        for (int idx = 0; idx < nNumRandomPlatforms; idx++) {
            boolean addPlatform = false;
            while (!addPlatform) {
                randNum = random.nextInt(3);
                switch (randNum){
                    case 1:
                        platformName = "Platform";
                        platformHeight = (int) LEVEL_GRID_HEIGHT * 2;
                        platformWidth = (int) LEVEL_GRID_WIDTH * 2;
                        break;
                    case 2:
                        platformName = "Platform2";
                        platformHeight = (int) LEVEL_GRID_HEIGHT * 2;
                        platformWidth = (int) LEVEL_GRID_HEIGHT * 4;
                        break;
                    default:
                        platformName = "Platform3";
                        platformHeight = (int) LEVEL_GRID_HEIGHT;
                        platformWidth = (int) LEVEL_GRID_HEIGHT * 2;

                }
                platformX = (int) (random.nextFloat() * LEVEL_WIDTH);
                platformX -= (platformX % (LEVEL_GRID_WIDTH * 2));
                if((platformWidth / LEVEL_GRID_WIDTH) % 4 == 0) platformX += LEVEL_GRID_WIDTH;

                platformY = (int) (random.nextFloat() * (LEVEL_HEIGHT));
                platformY -= (platformY % platformHeight);
                if (platformY < (LEVEL_GRID_HEIGHT * 2)) platformY += LEVEL_GRID_HEIGHT * 2;

                tempPlatform = new Platform(300.f + platformX, platformY, platformWidth, platformHeight, platformName, this);

                if(mPlatforms.size() == 0) {
                    mPlatforms.add(tempPlatform);
                    break;
                }

                boolean collisionDetected = false;
                for (int i = 0; i < mPlatforms.size(); i++) {
                    if (CollisionDetector.determineCollisionType(tempPlatform.getBound(), mPlatforms.get(i).getBound()) != CollisionDetector.CollisionType.None) {
                        Log.d(TAG, String.format("PlatformDemoScreen: Platform x Platform collision detected --> temp: x=%1$f y=%2$f w=%3$f h=%4$f | mPlatforms[%5$s]: x=%6$f y=%7$f w=%8$f h=%9$f", tempPlatform.position.x, tempPlatform.position.y, tempPlatform.getBound().getWidth(), tempPlatform.getBound().getHeight(), i, mPlatforms.get(i).position.x, mPlatforms.get(i).position.y, mPlatforms.get(i).getBound().getWidth(), mPlatforms.get(i).getBound().getHeight()));
                        collisionDetected = true;
                    }
                }
                if(!collisionDetected) break;
            }
            mPlatforms.add(tempPlatform);
        }

        for (int i = 0; i < mPlatforms.size(); i++) {
            Log.d(TAG, String.format("Platform[%1$s]: x=%2$f y=%2$f w=%3$f h=%4$f", i, mPlatforms.get(i).getBound().getLeft(), mPlatforms.get(i).getBound().getTop(), mPlatforms.get(i).getBound().getWidth(), mPlatforms.get(i).getBound().getHeight()));
        }

        // (UNIT TEST) Check if any platforms are colliding
        // Ignores first platform of mPlatforms as this is the ground platform
        /*boolean platformCollision = false;
        int platformCollisions = 0;
        for (int i = 1; i < mPlatforms.size() - 1; i++) {
            for (int j = i + 1; j < mPlatforms.size(); j++) {
                if (CollisionDetector.determineCollisionType(mPlatforms.get(i).getBound(), mPlatforms.get(j).getBound()) != CollisionDetector.CollisionType.None) {
                    Log.d(TAG, String.format("PlatformDemoScreen: Platform x Platform collision detected --> mPlatforms[%1$s]: x=%2$f y=%3$f w=%4$f h=%5$f | mPlatforms[%6$s]: x=%7$f y=%8$f w=%9$f h=%10$f", i, mPlatforms.get(i).position.x, mPlatforms.get(i).position.y, mPlatforms.get(i).getBound().getWidth(), mPlatforms.get(i).getBound().getHeight(), j, mPlatforms.get(j).position.x, mPlatforms.get(j).position.y, mPlatforms.get(j).getBound().getWidth(), mPlatforms.get(j).getBound().getHeight()));
                    platformCollision = true; platformCollisions++;
                }
            }
        }
        if(!platformCollision) Log.d(TAG, "PlatformDemoScreen: No platform collisions detected");
        else Log.d(TAG, String.format("PlatformDemoScreen: %1$d platform collisions detected", platformCollisions));*/

        //Get the music file from the resources.
        AssetFileDescriptor afd = game.getResources().openRawResourceFd(R.raw.platform_bgmusic);
        //Plays the background song
        new Music(afd).play();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Update and Draw
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the platform demo screen
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Update the touch buttons checking for player input
        for (PushButton control : mControls)
            control.update(elapsedTime, mLayerViewport, mScreenViewport);

        // Update the player
        mPlayer.update(elapsedTime, moveLeft.isPushed(),
                moveRight.isPushed(), jumpUp.isPushed(), mPlatforms);

        // Check that our new position has not collided with any of
        // the defined platforms. If so, then remove any overlap and
        // ensure a valid velocity.
        mPlayer.checkForAndResolveCollisions(mPlatforms);

        // Ensure the player cannot leave the confines of the world
        BoundingBox playerBound = mPlayer.getBound();
        if (playerBound.getLeft() < 0)
            mPlayer.position.x -= playerBound.getLeft();
        else if (playerBound.getRight() > LEVEL_WIDTH)
            mPlayer.position.x -= (playerBound.getRight() - LEVEL_WIDTH);

        if (playerBound.getBottom() < 0)
            mPlayer.position.y -= playerBound.getBottom();
        else if (playerBound.getTop() > LEVEL_HEIGHT)
            mPlayer.position.y -= (playerBound.getTop() - LEVEL_HEIGHT);

        //Change the viewport size depending on the speed of the playerSphere
        //Multiplying by 1.5 to preserve resolution
        mLayerViewport.set(240 + (Math.abs(mPlayer.velocity.x) * 0.2f * 1.5f), 160 + Math.abs(mPlayer.velocity.x) * 0.2f, 240 + (Math.abs(mPlayer.velocity.x) * 0.2f * 1.5f), 160 + Math.abs(mPlayer.velocity.x) * 0.2f);


        // Focus the layer viewport on the player's x location
        mLayerViewport.x = mPlayer.position.x;

        // Ensure the viewport cannot leave the confines of the world
        if (mLayerViewport.getLeft() < 0)
            mLayerViewport.x -= mLayerViewport.getLeft();
        else if (mLayerViewport.getRight() > LEVEL_WIDTH)
            mLayerViewport.x -= (mLayerViewport.getRight() - LEVEL_WIDTH);

        if (mLayerViewport.getBottom() < 0)
            mLayerViewport.y -= mLayerViewport.getBottom();
        else if (mLayerViewport.getTop() > LEVEL_HEIGHT)
            mLayerViewport.y -= (mLayerViewport.getTop() - LEVEL_HEIGHT);
    }

    /**
     * Draw the platform demo screen
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        graphics2D.clear(Color.WHITE);

        // Draw the player
        mPlayer.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);

        // Draw each of the platforms
        for (Platform platform : mPlatforms)
            platform.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport, mPlayer);

        // Draw the controls last of all
        for (PushButton control : mControls)
            control.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);
    }
}
