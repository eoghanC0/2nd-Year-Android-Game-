package uk.ac.qub.eeecs.game.cardDemo.objects;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.Collections;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
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
        cardScroller = new HorizontalCardScroller(position.x - mBound.getWidth(),mBound.getHeight()/3,mBound.getWidth(),5*mBound.getHeight()/9,gameScreen);
        createPack(numberOfCards, numberOfRares, minRating, maxRating);
        cardScroller.setMultiMode(true, 80);
        openingAnimationCounter = 0;
        scrollerAnimationCounter = 0;
        bestCard = getBestCard();
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("packOpeningBackground", "img/Wembley.jpg");
        openingAnimationBackground = assetManager.getBitmap("packOpeningBackground");
        openingAnimationBackgroundRect = new Rect((int) mBound.getLeft(), (int) mBound.getBottom(), (int) mBound.getRight(), (int) mBound.getTop());
        openingAnimationCardMaxHeight = (int) (mBound.getHeight()/1.5f);
    }

    private void createCards(int numberOfCards, boolean rare, int minRating, int maxRating) {
        for (int i = 0; i < numberOfCards; i++) {
            cardScroller.addScrollerItem(new Card(mGameScreen, rare,null, minRating, maxRating));
        }
    }

    private void createPack(int numberOfCards, int numberOfRares, int minRating, int maxRating) {
        createCards(numberOfCards - numberOfRares, false, minRating, maxRating);
        createCards(numberOfRares, true, minRating, maxRating);
        Collections.shuffle(cardScroller.getCardScrollerItems());
    }

    public boolean packOpened() {
        return scrollerAnimationCounter == SCROLLER_ANIMATION_LENGTH;
    }

    private Card getBestCard() {
        int highestRating = 0;
        Card bestCard = null;
        for (Card card : cardScroller.getCardScrollerItems()) {
            if (card.getRating() > highestRating) {
                highestRating = card.getRating();
                bestCard = new Card(card);
                bestCard.setPosition(position.x,position.y);
                bestCard.setHeight(0);
            }
        }
        return bestCard;
    }

    private int getValueFromQuadraticCurve(int x, int upperXIntercept, int max) {
        return (int) ((-4*max*x*(x-upperXIntercept))/(Math.pow(upperXIntercept,2)));
    }

    private void animateBestCard() {
        openingAnimationCounter++;
        bestCard.setHeight(getValueFromQuadraticCurve(openingAnimationCounter, OPENING_ANIMATION_LENGTH, openingAnimationCardMaxHeight));
    }

    private void animateCardScroller() {
        scrollerAnimationCounter++;
        cardScroller.adjustPosition(cardScroller.getBound().getWidth()/(float)SCROLLER_ANIMATION_LENGTH,0);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        if (openingAnimationCounter < OPENING_ANIMATION_LENGTH) {
            animateBestCard();
        }else if(scrollerAnimationCounter < SCROLLER_ANIMATION_LENGTH) {
            animateCardScroller();
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
            cardScroller.draw(elapsedTime, graphics2D);
        }
    }
}
