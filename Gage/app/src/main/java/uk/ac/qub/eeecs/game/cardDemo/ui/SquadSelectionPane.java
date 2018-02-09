package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;

/**
 * Created by stephenmcveigh on 08/02/2018.
 */

public class SquadSelectionPane extends GameObject {

    private enum PitchArea {
        FORWARD, MIDFIELD, DEFENCE, GOALKEEPER
    }

    private Bitmap background;
    private Card[] cards = new Card[11];
    private ArrayList<BoundingBox> placeholders = new ArrayList<>();
    private PitchArea currentSelectionArea;
    private PushButton showFormationsButton;
    private PushButton nextAreaButton;
    private PushButton previousAreaButton;
    private ListBox formationsListBox;
    private String selectedFormation;

    public SquadSelectionPane(FootballGameScreen gameScreen) {
        super(gameScreen.getGame().getScreenWidth()/2, gameScreen.getGame().getScreenHeight() * 0.75f, gameScreen.getGame().getScreenWidth(), gameScreen.getGame().getScreenHeight()/2, null, gameScreen);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("Pitch_Top", "img/pitch_top.png");
        assetManager.loadAndAddBitmap("Pitch_Centre", "img/pitch_centre.png");
        assetManager.loadAndAddBitmap("Pitch_Bottom", "img/pitch_bottom.png");
        background = assetManager.getBitmap("Pitch_Top");
        selectedFormation = mGameScreen.getGame().getSelectedFormation();

    }

    @Override
    public void update(ElapsedTime elapsedTime) {
//        if (formationsListBox.getSelectedIndex() == -1 && )

    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint paint = mGameScreen.getGame().getPaint(); //Get the game's paint object

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        //Draw the border
        graphics2D.drawRect(mBound.getLeft(), mBound.getBottom(), mBound.getRight(), mBound.getTop(), paint);

        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));
        paint.setAlpha(200);
        graphics2D.drawBitmap(background, null, drawScreenRect, paint);
    }
}
