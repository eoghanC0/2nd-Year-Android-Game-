package uk.ac.qub.eeecs.game.spaceDemo;

import uk.ac.qub.eeecs.gage.ai.SteeringBehaviours;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;

/**
 * Created by eimhin on 17/10/2017.
 */

public class Seeker extends AISpaceship {
    public Seeker(float startX, float startY, SpaceshipDemoScreen gameScreen) {
        super(startX, startY, gameScreen);
        maxAcceleration = 30.0f;
        maxVelocity = 50.0f;
        maxAngularVelocity = 400.0f;
        maxAngularAcceleration = 300.0f;

        //Load the assets used by seekers
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        if (assetManager.getBitmap("Spaceship2") == null) {
            assetManager.loadAndAddBitmap("Spaceship2", "img/Spaceship2.png");
        }
        mBitmap = assetManager.getBitmap("Spaceship2");
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        // Seek towards the player
        SteeringBehaviours.seek(this,
                ((SpaceshipDemoScreen) mGameScreen).getPlayerSpaceship().position,
                acceleration);

        // Try to avoid a collision with the player ship
        SteeringBehaviours.separate(this,
                ((SpaceshipDemoScreen) mGameScreen).getPlayerSpaceship(),
                getSeparateThresholdShip(), 100.0f, getAccComponent());
        getAccAccumulator().set(getAccComponent());

        // Try to avoid a collision with the other spaceships
        SteeringBehaviours.separate(this,
                ((SpaceshipDemoScreen) mGameScreen).getAISpaceships(),
                getSeparateThresholdShip(), 50.0f, getAccComponent());
        getAccAccumulator().add(getAccComponent());

        // Try to avoid a collision with the asteroids
        SteeringBehaviours.separate(this,
                ((SpaceshipDemoScreen) mGameScreen).getAsteroids(),
                getSeparateThresholdAsteroid(), 50.0f, getAccComponent());
        getAccAccumulator().add(getAccComponent());

        // If we are trying to avoid a collision then combine
        // it with the seek behaviour, placing more emphasis on
        // avoiding a collision.
        if (!getAccAccumulator().isZero()) {
            acceleration.x = 0.1f * acceleration.x + 0.9f * getAccAccumulator().x;
            acceleration.y = 0.1f * acceleration.y + 0.9f * getAccAccumulator().y;
        }

        // Make sure we point in the direction of travel.
        angularAcceleration = SteeringBehaviours.alignWithMovement(this);

        super.update(elapsedTime);
    }
}
