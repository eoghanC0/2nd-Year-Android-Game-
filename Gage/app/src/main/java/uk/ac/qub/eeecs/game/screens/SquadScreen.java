package uk.ac.qub.eeecs.game.screens;

import android.graphics.Bitmap;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.game.objects.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.ui.CardHolder;
import uk.ac.qub.eeecs.game.ui.SquadSelectionPane;

public class SquadScreen extends FootballGameScreen {
    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    private SquadSelectionPane selectionPane;
    private Bitmap background;
    private Rect backgroundRect = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());
    private PushButton startButton;
    private boolean startButtonVisible = false;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////
    public SquadScreen(FootballGame game) {
        super("SquadScreen", game);

        background = game.getAssetManager().getBitmap("MainBackground");
        selectionPane = new SquadSelectionPane(this);
        startButton = new PushButton(mGame.getScreenWidth() * 0.9f, mGame.getScreenHeight() * 0.85f, mGame.getScreenHeight() * 0.25f, mGame.getScreenHeight() * 0.25f, "NextButton","NextButtonActive", this );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////
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
            // Save squad from SquadSelectionPane to game
            CardHolder[] squadCards = selectionPane.getSquadSelectionHolders();
            mGame.clearSquad();
            for (int i = 0; i < 11; i++) {
                mGame.addToSquad(squadCards[i].getCard());
            }

            mGame.setFormation(selectionPane.getFormationString());

            // Save game
            mGame.saveGame();

            // Transition to PlayScreen
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
