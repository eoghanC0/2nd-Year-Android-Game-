package uk.ac.qub.eeecs.game.objects;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.ui.CardScroller;

/**
 * Created by stephenmcveigh on 06/03/2018.
 */

public class Pack extends GameObject {
    ///////////////////////////////////////////////////////////////////////////
    // Constants
    ///////////////////////////////////////////////////////////////////////////
    // Animation constants
    private final int OPENING_ANIMATION_LENGTH = 60;
    private final int SCROLLER_ANIMATION_LENGTH = 8;

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    private CardScroller cardScroller;
    private int openingAnimationCounter;
    private Card bestCard;
    private int openingAnimationCardMaxHeight;
    private Bitmap openingAnimationBackground;
    private Rect openingAnimationBackgroundRect;
    private int scrollerAnimationCounter;

    ///////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////
    public Pack(GameScreen gameScreen, int numberOfCards, int numberOfRares, int minRating, int maxRating) {
        super(gameScreen.getGame().getScreenWidth()/2, gameScreen.getGame().getScreenHeight()/2, gameScreen.getGame().getScreenWidth(), gameScreen.getGame().getScreenHeight(),null, gameScreen);
        cardScroller = new CardScroller(position.x - mBound.getWidth(),mBound.getHeight()/3,mBound.getWidth(),5*mBound.getHeight()/9,gameScreen);
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

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates the given number of cards, with given constraints
     *
     * @param numberOfCards
     * @param rare
     * @param minRating
     * @param maxRating
     *
     * Authors - Stephen McVeigh and Eoghan Conlon
     */
    private void createCards(int numberOfCards, boolean rare, int minRating, int maxRating) {
        for (int i = 0; i < numberOfCards; i++) {
            cardScroller.addScrollerItem(new Card(mGameScreen, rare,null, minRating, maxRating));
        }
    }

    /**
     * Adds the cards generated to the club, while dealing with duplicates
     */
    private void addToClub() {
        FootballGame game = (FootballGame) mGameScreen.getGame();
        for (Card card : cardScroller.getScrollerItems()) {
            if (getOwnedIDs(game).contains(card.getPlayerID())) {
                //Deal with duplicates ;
                // if the card exists, just reward with XP instead
                game.addXP(card.getRating());
            } else {
                game.getClub().add(card);
            }
        }
        game.saveGame();
    }

    /**
     * Gets the playerID's of all players already owned
     *
     * @param game
     * @return
     */
    private ArrayList<String> getOwnedIDs(FootballGame game) {
        ArrayList<String> playerIDs = new ArrayList<>();
        for (Card player : game.getClub()) {
            playerIDs.add(player.getPlayerID());
        }
        for (Card player : game.getSquad()) {
            playerIDs.add(player.getPlayerID());
        }
        return playerIDs;
    }

    /**
     * Creates the pack by calling createCards() for both rares and non-rares and adding them to the club
     *
     * @param numberOfCards
     * @param numberOfRares
     * @param minRating
     * @param maxRating
     */
    private void createPack(int numberOfCards, int numberOfRares, int minRating, int maxRating) {
        createCards(numberOfCards - numberOfRares, false, minRating, maxRating);
        createCards(numberOfRares, true, minRating, maxRating);
        addToClub();
        Collections.shuffle(cardScroller.getScrollerItems()); //Shuffles the cards so that rares are mixed in with non-rares
    }

    /**
     * Returns a flag which indicates whether the animation has finished or not
     * @return
     */
    public boolean packOpened() {
        return scrollerAnimationCounter == SCROLLER_ANIMATION_LENGTH;
    }

    /**
     * Returns the card in the pack with the highest rating.
     * It is returned as a new copied object so that it can be manipulated in the opening animation
     *
     * @return
     *
     * Author - Stephen McVeigh and Eoghan Conlon
     */
    private Card getBestCard() {
        int highestRating = 0;
        Card bestCard = null;
        for (Card card : cardScroller.getScrollerItems()) {
            if (card.getRating() > highestRating) {
                highestRating = card.getRating();
                bestCard = new Card(card);
                bestCard.setPosition(position.x,position.y);
                bestCard.setHeight(0);
            }
        }
        return bestCard;
    }

    /**
     * Gets the Y coordinate associated with the given X coordinate on a quadratic with a given maximum and X-Intercept
     *
     * @param x
     * @param upperXIntercept
     * @param max
     * @returna
     */
    private int getValueFromQuadraticCurve(int x, int upperXIntercept, int max) {
        return (int) ((-4*max*x*(x-upperXIntercept))/(Math.pow(upperXIntercept,2)));
    }

    /**
     * Animates the best card during the opening animation
     */
    private void animateBestCard() {
        openingAnimationCounter++;
        bestCard.setHeight(getValueFromQuadraticCurve(openingAnimationCounter, OPENING_ANIMATION_LENGTH, openingAnimationCardMaxHeight));
    }

    /**
     * Animates the card scroller during the opening animation
     */
    private void animateCardScroller() {
        scrollerAnimationCounter++;
        cardScroller.adjustPosition(cardScroller.getBound().getWidth()/(float)SCROLLER_ANIMATION_LENGTH,0);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        if (openingAnimationCounter < OPENING_ANIMATION_LENGTH) {
            animateBestCard();
        } else if(scrollerAnimationCounter < SCROLLER_ANIMATION_LENGTH) {
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
