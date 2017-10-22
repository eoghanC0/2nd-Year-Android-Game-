package uk.ac.qub.eeecs.game.spaceDemo;

import uk.ac.qub.eeecs.gage.ai.SteeringBehaviours;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;

/**
 * Created by eimhin on 17/10/2017.
 */

public class Turret extends AISpaceship {

    public Turret(float startX, float startY, SpaceshipDemoScreen gameScreen) {
        super(startX, startY, gameScreen);
        maxAcceleration = 0.0f;
        maxVelocity = 0.0f;
        maxAngularVelocity = 50.0f;
        maxAngularAcceleration = 50.0f;

        //Load the assets used by Turret
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        if (assetManager.getBitmap("Turret") == null) {
            assetManager.loadAndAddBitmap("Turret", "img/Turret.png");
        }
        mBitmap = assetManager.getBitmap("Turret");
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        // Turn towards the player
        angularAcceleration =
                SteeringBehaviours.lookAt(this,
                        ((SpaceshipDemoScreen) mGameScreen).getPlayerSpaceship().position);

        super.update(elapsedTime);
    }

}
