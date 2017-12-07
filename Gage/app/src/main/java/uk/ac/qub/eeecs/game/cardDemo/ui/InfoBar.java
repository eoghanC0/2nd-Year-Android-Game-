package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Created by eimhin on 30/11/2017.
 */

public class InfoBar extends GameObject {

    private Bitmap playerBitmap;
    private String playerName;
    private String winLossDraw;
    private int experience;
    private Rect playerIconRect;
    private Paint textPaint;

    public InfoBar(float x, float y, float width, float height, GameScreen gameScreen, String playerIconPath, String playerName, String winLossDraw, int experience) {
        super(x, y, width, height, null, gameScreen);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("InfoBar", "img/InfoBar.png");
        assetManager.loadAndAddBitmap("PlayerIcon", "img/Ball.png");
        mBitmap = assetManager.getBitmap("InfoBar");
        playerBitmap = assetManager.getBitmap("InfoBar");
        playerIconRect = new Rect(200,200,200,200);
        this.playerName = playerName;
        this.winLossDraw = winLossDraw;
        this.experience = experience;
        textPaint = mGameScreen.getGame().getPaint();
        textPaint.setTextSize(45f);
        textPaint.setColor(Color.WHITE);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {

    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);

        graphics2D.drawText(playerName, this.getBound().getLeft() + 280, this.getBound().getBottom() - 235, textPaint);
        graphics2D.drawText(String.valueOf(experience), this.getBound().getLeft() + 900, this.getBound().getBottom() - 235, textPaint);
        graphics2D.drawText(winLossDraw, this.getBound().getLeft() + 1600, this.getBound().getBottom() - 235, textPaint);

        graphics2D.drawBitmap(playerBitmap, null, playerIconRect,null);
    }
}
