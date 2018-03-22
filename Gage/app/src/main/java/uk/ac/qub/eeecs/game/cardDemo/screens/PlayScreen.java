package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.game.cardDemo.objects.FootballGameScreen;
import uk.ac.qub.eeecs.game.cardDemo.objects.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;
import uk.ac.qub.eeecs.game.cardDemo.objects.Match;

/**
 * Created by stephenmcveigh on 04/12/2017.
 */

public class PlayScreen extends FootballGameScreen {
    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////
    private final Bitmap background;
    private final Rect backgroundRectangle;
    private int difficulty, gameLength;

    private Match currentMatch;


    private ArrayList<Card> playerTeam;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////
    public PlayScreen(FootballGame game) {
        super("PlayScreen", game);
        this.difficulty = mGame.getDifficulty();
        this.gameLength = mGame.getGameLength();
        this.playerTeam = mGame.getSquad();
        currentMatch = new Match(this, difficulty, gameLength, playerTeam);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("PlayScreenBackground", "img/pitch.png");
        assetManager.loadAndAddBitmap("HIS-Background", "img/his-background.png");
        background = assetManager.getBitmap("PlayScreenBackground");
        backgroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    }


    @Override
    public void update(ElapsedTime elapsedTime) {
        currentMatch.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint paint = mGame.getPaint();
        paint.setAlpha(100);
        graphics2D.drawBitmap(background,null, backgroundRectangle, paint);
        paint.reset();
        paint.setTextSize(45f);
        paint.setColor(Color.BLUE);

        currentMatch.draw(elapsedTime, graphics2D);

    }

}
