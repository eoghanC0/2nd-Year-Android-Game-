package uk.ac.qub.eeecs.game.spaceDemo;

import java.util.Random;

import uk.ac.qub.eeecs.gage.ai.SteeringBehaviours;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * AI controlled spaceship
 *
 * @version 1.0
 */
public abstract class AISpaceship extends Sprite {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Distance at which the spaceship should avoid other game objects
     */
    private float separateThresholdShip = 125.0f;
    private float separateThresholdAsteroid = 125.0f;

    /**
     * Accumulators used to build up the net steering outcome
     */
    private Vector2 accAccumulator = new Vector2();
    private Vector2 accComponent = new Vector2();

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    private static Random random = new Random();

    /**
     * Create a AI controlled spaceship
     *
     * @param startX        x location of the AI spaceship
     * @param startY        y location of the AI spaceship
     * @param gameScreen    Gamescreen to which AI belongs
     */
    public AISpaceship(float startX, float startY,
                       SpaceshipDemoScreen gameScreen) {
        super(startX, startY, random.nextFloat()*20 + 50, random.nextFloat()*40 + 30, null, gameScreen);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the AI Spaceship
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        // Call the sprite's superclass to apply the determined accelerations
        super.update(elapsedTime);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Getters and Setters
    // /////////////////////////////////////////////////////////////////////////

    public float getSeparateThresholdShip() {
        return separateThresholdShip;
    }

    public void setSeparateThresholdShip(float separateThresholdShip) {
        this.separateThresholdShip = separateThresholdShip;
    }

    public float getSeparateThresholdAsteroid() {
        return separateThresholdAsteroid;
    }

    public void setSeparateThresholdAsteroid(float separateThresholdAsteroid) {
        this.separateThresholdAsteroid = separateThresholdAsteroid;
    }

    public Vector2 getAccAccumulator() {
        return accAccumulator;
    }

    public void setAccAccumulator(Vector2 accAccumulator) {
        this.accAccumulator = accAccumulator;
    }

    public Vector2 getAccComponent() {
        return accComponent;
    }

    public void setAccComponent(Vector2 accComponent) {
        this.accComponent = accComponent;
    }
}
