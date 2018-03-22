package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.ui.SquadSelectionPane;


/**
 * Created by Iñaki McKearney on 06/12/2017.
 */

public class SquadScreen extends FootballGameScreen {

    private SquadSelectionPane selectionPane;
    private Bitmap background;
    private Rect backgroundRect = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());
    private PushButton startButton;
    private boolean startButtonVisible = false;

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public SquadScreen(FootballGame game) {
        super("SquadScreen", game);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("SquadBackground", "img/MainBackground.jpg");
        background = assetManager.getBitmap("SquadBackground");
        selectionPane = new SquadSelectionPane(this);
        startButton = new PushButton(mGame.getScreenWidth() * 0.9f, mGame.getScreenHeight() * 0.85f, mGame.getScreenHeight() * 0.25f, mGame.getScreenHeight() * 0.25f, "NextButton","NextButtonActive", this );
    }

    private void changeToScreen(GameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        selectionPane.update(elapsedTime);

        // Check if all CardHolders are occupied
        if(selectionPane.isSquadSelected()) {
            startButtonVisible = true;
            startButton.setEnabled(true);
        }
        else {
            startButtonVisible = false;
            startButton.setEnabled(false);
        }

        startButton.update(elapsedTime);
        if(startButton.isPushTriggered()) {
            changeToScreen(new PlayScreen(mGame));
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.drawBitmap(background, null, backgroundRect, null);
        selectionPane.draw(elapsedTime, graphics2D);

        if(startButtonVisible)
            startButton.draw(elapsedTime, graphics2D);
    }
}
