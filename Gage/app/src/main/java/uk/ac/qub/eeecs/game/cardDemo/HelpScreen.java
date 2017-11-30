package uk.ac.qub.eeecs.game.cardDemo;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Created by eimhin on 27/11/2017.
 */

public class HelpScreen extends GameScreen {

    /**
     * Define viewports for this layer and the associated screen projection
     */
    private ScreenViewport mScreenViewport;
    private LayerViewport mLayerViewport;

    /**
     * Define InfoBar
     */
    private InfoBar infoBar;

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public HelpScreen(Game game) {
        super("HelpScreen", game);

        // Instantiate variables
        mLayerViewport = new LayerViewport();
        mScreenViewport = new ScreenViewport();
        GraphicsHelper.create3To2AspectRatioScreenViewport(game, mScreenViewport);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        infoBar.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        infoBar.draw(elapsedTime, graphics2D, mLayerViewport, mScreenViewport);
    }
}


