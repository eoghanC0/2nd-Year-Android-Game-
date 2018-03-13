package uk.ac.qub.eeecs.game.cardDemo.objects;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
    private final int OPENING_ANIMATION_LENGTH = 60;
    private final int SCROLLER_ANIMATION_LENGTH = 8;
    private HorizontalCardScroller cardScroller;

    private int openingAnimationCounter;
    private Card bestCard;
    private int openingAnimationCardMaxHeight;
    private Bitmap openingAnimationBackground;
    private Rect openingAnimationBackgroundRect;

    private int scrollerAnimationCounter;

    public Pack(GameScreen gameScreen, int numberOfCards, int numberOfRares, int minRating, int maxRating) {
        super(gameScreen.getGame().getScreenWidth()/2, gameScreen.getGame().getScreenHeight()/2, gameScreen.getGame().getScreenWidth(), gameScreen.getGame().getScreenHeight(),null, gameScreen);
        cardScroller = new HorizontalCardScroller(position.x - mBound.getWidth(),mBound.getHeight()/3,mBound.getWidth(),2*mBound.getHeight()/3,gameScreen);
        for (int i = 0; i < numberOfCards - numberOfRares; i++) {
            cardScroller.addScrollerItem(new Card(gameScreen, false,null, minRating, maxRating));
        }
        for (int i = 0; i < numberOfRares; i++) {
            cardScroller.addScrollerItem(new Card(gameScreen, true,null, minRating, maxRating));
        }
        Collections.shuffle(cardScroller.getCardScrollerItems());
        cardScroller.setMultiMode(true, 80);
        openingAnimationCounter = 0;
        scrollerAnimationCounter = 0;
        bestCard = getBestCard();
        bestCard.setPosition(position.x,position.y);
        bestCard.setHeight(0);
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("packOpeningBackground", "img/Wembley.jpg");
        openingAnimationBackground = assetManager.getBitmap("packOpeningBackground");
        openingAnimationBackgroundRect = new Rect((int) mBound.getLeft(), (int) mBound.getBottom(), (int) mBound.getRight(), (int) mBound.getTop());
        openingAnimationCardMaxHeight = (int) (mBound.getHeight()/1.5f);
    }

    public boolean packOpened() {
        if (scrollerAnimationCounter == SCROLLER_ANIMATION_LENGTH)
            return true;
        return false;
    }

    private Card getBestCard() {
        int highestRating = 0;
        Card bestCard = null;
        for (Card card : cardScroller.getCardScrollerItems()) {
            if (card.getRating() > highestRating) {
                highestRating = card.getRating();
                bestCard = new Card(card);
            }
        }
        return bestCard;
    }

    private int getValueFromQuadraticCurve(int x, int upperXIntercept, int max) {
        return (int) ((-4*max*x*(x-upperXIntercept))/(Math.pow(upperXIntercept,2)));
    }

    public ArrayList<Card> getPlayers() {
        return cardScroller.getCardScrollerItems();
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        if (openingAnimationCounter < OPENING_ANIMATION_LENGTH) {
            openingAnimationCounter++;
            bestCard.setHeight(getValueFromQuadraticCurve(openingAnimationCounter, OPENING_ANIMATION_LENGTH, openingAnimationCardMaxHeight));
        } else if(scrollerAnimationCounter < SCROLLER_ANIMATION_LENGTH) {
            scrollerAnimationCounter++;
            cardScroller.adjustPosition(cardScroller.getBound().getWidth()/(float)SCROLLER_ANIMATION_LENGTH,0);
        }else {
            cardScroller.update(elapsedTime);
        }
    }


    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Paint paint = mGameScreen.getGame().getPaint();
        if (openingAnimationCounter < OPENING_ANIMATION_LENGTH) {
            paint.setAlpha(150);
            if (openingAnimationCounter >= OPENING_ANIMATION_LENGTH/2)
                paint.setAlpha(getValueFromQuadraticCurve(openingAnimationCounter, OPENING_ANIMATION_LENGTH, paint.getAlpha()));
            graphics2D.drawBitmap(openingAnimationBackground, null, openingAnimationBackgroundRect, paint);
            bestCard.draw(elapsedTime,graphics2D);
        } else {

        }
        cardScroller.draw(elapsedTime, graphics2D);
    }
}
