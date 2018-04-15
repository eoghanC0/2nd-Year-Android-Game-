package uk.ac.qub.eeecs.game.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.objects.FootballGameScreen;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.objects.Card;
import uk.ac.qub.eeecs.game.objects.Match;

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
    private int endGameCounter;
    private boolean startCount;

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
        Log.d("DEBUG", "Players in Squad: " + playerTeam.size() + " " + mGame.getSquad().size());
        currentMatch = new Match(this, difficulty, gameLength, playerTeam);
        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("PlayScreenBackground", "img/pitch.png");
        assetManager.loadAndAddBitmap("HIS-Background", "img/his-background.png");
        background = assetManager.getBitmap("PlayScreenBackground");
        backgroundRectangle = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());
        this.endGameCounter = 0;
        this.startCount = false;

    }

    private void checkForWinner(){
        if (currentMatch.getGameResult() != null)
            startCount = true;
    }


    private void endGame(){
        String result = currentMatch.getGameResult();
            switch (result){
                case "Player A":
                    mGame.setWins(mGame.getWins() + 1);
                    mGame.setXp(mGame.getXp() + (200 * (difficulty + 1)));
                    break;
                case "Player B":
                    mGame.setLosses(mGame.getLosses() + 1);
                    mGame.setXp(mGame.getXp() + (50 * (difficulty + 1)));
                    break;
                case "Draw":
                    mGame.setDraws(mGame.getDraws() + 1);
                    mGame.setXp(mGame.getXp() + (100 * (difficulty + 1)));
                    break;

            }
            mGame.saveGame();
            changeToScreen(new MenuScreen(mGame));

    }
    @Override
    public void update(ElapsedTime elapsedTime) {
        currentMatch.update(elapsedTime);
        checkForWinner();
        if (startCount)
            endGameCounter++;
        if (endGameCounter > 50)
            endGame();

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
