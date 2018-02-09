package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.HorizontalCardScroller;
import uk.ac.qub.eeecs.game.cardDemo.ui.SquadSelectionPane;


/**
 * Created by Iñaki McKearney on 06/12/2017.
 */

public class SquadScreen extends GameScreen {

    private SquadSelectionPane selectionPane;
    private HorizontalCardScroller cardScroller;
    private Bitmap background;
    private Rect backgroundRect = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public SquadScreen(Game game) {
        super("SquadScreen", game);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("SquadBackground", "img/help-background.jpg");
        background = assetManager.getBitmap("SquadBackground");
        selectionPane = new SquadSelectionPane(this);
        cardScroller = new HorizontalCardScroller(mGame.getScreenWidth()/2, mGame.getScreenHeight() * 0.25f, mGame.getScreenWidth(), mGame.getScreenHeight()/2, this);
        cardScroller.addTestData();
        cardScroller.addTestData();
        cardScroller.setMultiMode(true, 80);
        cardScroller.setSelectMode(true);
    }

    private void changeToScreen(GameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        cardScroller.update(elapsedTime);
        selectionPane.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.drawBitmap(background, null, backgroundRect, null);
        selectionPane.draw(elapsedTime, graphics2D);
        cardScroller.draw(elapsedTime, graphics2D);
    }
}
