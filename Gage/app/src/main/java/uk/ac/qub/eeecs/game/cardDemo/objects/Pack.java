package uk.ac.qub.eeecs.game.cardDemo.objects;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.HorizontalCardScroller;

/**
 * Created by stephenmcveigh on 06/03/2018.
 */

public class Pack extends GameObject {
    private final int ANIMATION_LENGTH = 70;
    private ArrayList<Card> cards = new ArrayList<>();
    private HorizontalCardScroller cardScroller;
    private int openingAnimationCounter;
    private Card bestCard;
    private int animationCardMaxHeight;
    private Bitmap openingAnimationBackground;
    private Rect openingAnimationBackgroundRect;

    public Pack(float x, float y, float width, float height, GameScreen gameScreen, int numberOfCards) {
        super(x, y, width, height,null, gameScreen);
        cardScroller = new HorizontalCardScroller(x,y,width,height,gameScreen);
        for (int i = 0; i < numberOfCards; i++) {
            cards.add(new Card(gameScreen, false, 0, 100));
            cardScroller.addScrollerItem(cards.get(i));
        }
        cardScroller.setMultiMode(true, 80);
        openingAnimationCounter = 0;
        bestCard = getBestCard();
        bestCard.setPosition(x,y);
        bestCard.setHeight(0);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("packOpeningBackground", "img/Wembley.jpg");
        openingAnimationBackground = assetManager.getBitmap("packOpeningBackground");
        openingAnimationBackgroundRect = new Rect((int) mBound.getLeft(), (int) mBound.getBottom(), (int) mBound.getRight(), (int) mBound.getTop());
        animationCardMaxHeight = (int) (height/1.5f);
    }

    private Card getBestCard() {
        int highestRating = 0;
        Card bestCard = null;
        for (Card card : cards) {
            if (card.getRating() > highestRating);
                bestCard = new Card(card);
        }
        return bestCard;
    }

    private int getCardHeightThisFrame(int frameNumber, int animationLength, int maxHeight) {
        return (int) ((-4*maxHeight*frameNumber*(frameNumber-animationLength))/(Math.pow(animationLength,2)));
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        if (openingAnimationCounter < ANIMATION_LENGTH) {
            openingAnimationCounter++;
            bestCard.setHeight(getCardHeightThisFrame(openingAnimationCounter, ANIMATION_LENGTH, (int) (mBound.getHeight()/1.5f)));
        } else {
            cardScroller.update(elapsedTime);
        }
    }


    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint paint = mGameScreen.getGame().getPaint();
        paint.setAlpha(150);
        if (openingAnimationCounter < ANIMATION_LENGTH) {
            if (openingAnimationCounter >= ANIMATION_LENGTH/2)
                paint.setAlpha(getCardHeightThisFrame(openingAnimationCounter, ANIMATION_LENGTH, 150));
            graphics2D.drawBitmap(openingAnimationBackground, null, openingAnimationBackgroundRect, paint);
            bestCard.draw(elapsedTime,graphics2D);
        } else {
            cardScroller.draw(elapsedTime, graphics2D);
        }
    }
}
