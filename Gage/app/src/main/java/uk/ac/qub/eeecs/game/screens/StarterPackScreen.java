package uk.ac.qub.eeecs.game.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.game.objects.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.objects.Pack;

/**
 * Created by stephenmcveigh on 07/03/2018.
 */

public class StarterPackScreen extends FootballGameScreen {
    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////
    private Pack starterPack;
    private PushButton menuScreenButton;
    private Bitmap background;
    private final Rect backgroundRect = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());
    private Paint paint;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////
    public StarterPackScreen(FootballGame game) {
        super("StarterPackScreen", game);
        starterPack = new Pack(this, 11, 5, 0, 80);
        mGame.getAssetManager().loadAndAddBitmap("MenuButton", "img/MenuButton.png");
        mGame.getAssetManager().loadAndAddBitmap("MenuButtonPushed", "img/MenuButtonPushed.png");
        menuScreenButton = new PushButton(mGame.getScreenWidth() * 0.5f, mGame.getScreenHeight() * 0.82f, mGame.getScreenWidth() * 0.4f, mGame.getScreenWidth() * 0.15f, "MenuButton", "MenuButtonPushed", this);
        menuScreenButton.setButtonText("S T A R T  G A M E", 200, Color.rgb(250, 250, 250));
        background = mGame.getAssetManager().getBitmap("MainBackground");
        paint = game.getPaint();
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        starterPack.update(elapsedTime);
        menuScreenButton.update(elapsedTime);

        if(menuScreenButton.isPushTriggered()) {
            changeToScreen(new MenuScreen(getGame()));
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.drawBitmap(background,null, backgroundRect, paint);

        starterPack.draw(elapsedTime, graphics2D);

        if(starterPack.packOpened()) {
            menuScreenButton.draw(elapsedTime, graphics2D);
        }

    }
}
