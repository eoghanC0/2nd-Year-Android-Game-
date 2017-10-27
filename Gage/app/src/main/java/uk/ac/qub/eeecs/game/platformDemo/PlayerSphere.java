package uk.ac.qub.eeecs.game.platformDemo;

import android.util.Log;

import java.util.List;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.audio.Sound;
import uk.ac.qub.eeecs.gage.util.CollisionDetector;
import uk.ac.qub.eeecs.gage.util.CollisionDetector.CollisionType;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.Sprite;

import static android.content.ContentValues.TAG;

/**
 * Simple controllable player sprite.
 *
 * @version 1.0
 */
public class PlayerSphere extends Sprite {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Strength of gravity to apply along the y-axis
     */
    private float GRAVITY = -800.0f;

    /**
     * Acceleration with which the player can move along the x-axis
     */
    private float RUN_ACCELERATION;

    /**
     * Maximum velocity of the player along the x-axis
     */
    private float MAX_X_VELOCITY;

    /**
     * Scale factor that is applied to the x-velocity when the player is not
     * moving left or right
     */
    private float RUN_DECAY;

    /**
     * Instantaneous velocity with which the player jumps up
     */
    private float JUMP_VELOCITY;

    /**
     * Trigger downwards velocity under which a jump will be permitted.
     * Used to reflect the fact that gravity provides a small download
     * acceleration each frame.
     */
    private float JUMP_VELOCITY_THRESHOLD;

    /**
     * Scale factor that is used to turn the x-velocity into an angular velocity
     * to give the visual appearance that the sphere is rotating as the player
     * moves.
     */
    private float ANGULAR_VELOCITY_SCALE = 1.5f;

    /**
     * Sound effect when the ball hits a platform
     */
    private Sound collisionSound;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the player's sphere
     *
     * @param startX     x location of the sphere
     * @param startY     y location of the sphere
     * @param gameScreen Gamescreen to which sphere belongs
     * @param BallType which ball will be used
     */
    public PlayerSphere(float startX, float startY, String BallType, GameScreen gameScreen) {
        super(startX, startY, 50.0f, 50.0f, null, gameScreen);

        //Load assets the sphere will use
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("Ball2", "img/ball2.jpg");
        assetManager.loadAndAddBitmap("Ball", "img/Ball.png");
        assetManager.loadAndAddSound("BallBounce", "sounds/Ball_Bounce.mp3");
        collisionSound = assetManager.getSound("BallBounce");
        if (BallType.equals("ballOne")){
            mBitmap = assetManager.getBitmap("Ball");
            RUN_ACCELERATION = 150.0f;
            MAX_X_VELOCITY = 200.0f;
            JUMP_VELOCITY = 450.0f;
            JUMP_VELOCITY_THRESHOLD = 25.0f;
            RUN_DECAY = 0.95f;
        } else {
            mBitmap = assetManager.getBitmap("Ball2");
            RUN_ACCELERATION = 350.0f;
            MAX_X_VELOCITY = 400.0f;
            JUMP_VELOCITY = 600.0f;
            JUMP_VELOCITY_THRESHOLD = 40.0f;
            RUN_DECAY = 0.95f;
        }

    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the player's sphere
     *
     * @param elapsedTime Elapsed time information
     * @param moveLeft    True if the move left control is active
     * @param moveRight   True if the move right control is active
     * @param jumpUp      True if the jump up control is active
     * @param platforms   Array of platforms in the world
     */
    public void update(ElapsedTime elapsedTime, boolean moveLeft,
                       boolean moveRight, boolean jumpUp, List<Platform> platforms) {

        // Apply gravity to the y-axis acceleration
        acceleration.y = GRAVITY;

        // Depending upon the left and right movement touch controls
        // set an appropriate x-acceleration. If the user does not
        // want to move left or right, then the x-acceleration is zero
        // and the velocity decays towards zero.
        if (moveLeft && !moveRight) {
            acceleration.x = -RUN_ACCELERATION;
        } else if (moveRight && !moveLeft) {
            acceleration.x = RUN_ACCELERATION;
        } else {
            acceleration.x = 0.0f;
            velocity.x *= RUN_DECAY;
        }

        // If the user wants to jump up then provide an immediate
        // boost to the y velocity.
        if (jumpUp && (velocity.y > -JUMP_VELOCITY_THRESHOLD
                && velocity.y < JUMP_VELOCITY_THRESHOLD)) {
            velocity.y = JUMP_VELOCITY;
        }

        // We want the player's sphere to rotate to give the appearance
        // that the sphere is rolling as the player moves. The faster
        // the player is moving the faster the angular velocity.
        angularVelocity = ANGULAR_VELOCITY_SCALE * velocity.x;

        // Call the sprite's update method to apply the defined
        // accelerations and velocities to provide a new position
        // and orientation.
        super.update(elapsedTime);

        // The player's sphere is constrained by a maximum x-velocity,
        // but not a y-velocity. Make sure we have not exceeded this.
        if (Math.abs(velocity.x) > MAX_X_VELOCITY)
            velocity.x = Math.signum(velocity.x) * MAX_X_VELOCITY;
    }

    /**
     * Check for and then resolve any collision between the sphere and the
     * platforms.
     *
     * @param platforms Array of platforms to test for collision against
     */
    public boolean checkForAndResolveCollisions(List<Platform> platforms) {

        CollisionType collisionType;
        boolean collisionOccurred = false;

        // Consider each platform for collision
        for (Platform platform : platforms) {
            collisionType =
                    CollisionDetector.determineAndResolveCollision(this, platform);

            switch (collisionType) {
                case Top:
                    velocity.y = -0.5f * velocity.y;
                    break;
                case Bottom:
                    velocity.y = -0.5f * velocity.y;
                    break;
                case Left:
                    velocity.x = -0.5f * velocity.x;
                    break;
                case Right:
                    velocity.x = -0.5f * velocity.x;
                    break;
                case None:
                    break;
            }

            if (collisionType != collisionType.None) {
                //Sound will only play if the ball is going a certain speed
                if (Math.sqrt((velocity.x * velocity.x) + (velocity.y * velocity.y)) >= (Math.abs(MAX_X_VELOCITY * 0.75))) {
                    if (velocity.y > 30) collisionSound.play();
                }
                collisionOccurred = true;
            }
        }
        return collisionOccurred;
    }
}
