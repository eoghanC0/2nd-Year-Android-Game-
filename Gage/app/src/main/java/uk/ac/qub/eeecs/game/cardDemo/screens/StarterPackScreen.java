package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Pack;

/**
 * Created by stephenmcveigh on 07/03/2018.
 */

public class StarterPackScreen extends FootballGameScreen {
    private final int SCROLLER_ANIMATION_LENGTH = 7;
    private int scrollerAnimationCounter = 0;
    private Pack starterPack;
    private final Bitmap background;
    private final Rect backgroundRect = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());
    private AssetStore assetManager = mGame.getAssetManager();

    public StarterPackScreen(FootballGame game) {
        super("StarterPackScreen", game);
        starterPack = new Pack( this, 11, 5, 0,100);
        background = assetManager.getBitmap("MainBackground");
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        if (starterPack.packOpened()) {
            if (scrollerAnimationCounter < SCROLLER_ANIMATION_LENGTH) {
                scrollerAnimationCounter++;
            }
        }
        starterPack.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.drawBitmap(background, null, backgroundRect, null);
        starterPack.draw(elapsedTime, graphics2D);
    }
}
