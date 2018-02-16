package uk.ac.qub.eeecs.game.cardDemo.objects;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.lang.Math;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.PlayScreen;
import uk.ac.qub.eeecs.game.cardDemo.ui.CardHolder;
import uk.ac.qub.eeecs.game.cardDemo.ui.HorizontalCardScroller;

/**
 * Created by Aedan on 24/01/2018.
 */

public class MatchEvent extends GameObject{


    private Game mGame;
    private String chosenScenario, scenarioDescription;
    private CardHolder cardHolder1, cardHolder2;
    private PushButton confirmPlayer;
    private boolean playersChosen, drawCards, clearScenario;

    private int clearCounter = 0;

    //properties used for animation of objects
    private int animationCounter, size = 250;
    private float height, leftHolderPosition, rightHolderPosition;

    //Properties for using the side scroller
    private boolean scrollerEnabled = true;
    private boolean scrollerDisplayed = false;
    private boolean scrollerAnimationTriggered = false;
    private int scrollerMoveDirection = 1;
    private float distanceMoved = 0;

    private HorizontalCardScroller horizontalCardScroller;

    /**
     * Properties for drag and drop
     */
    private boolean touchDown = false;
    private int selectedItemIndex = 0;
    private Vector2 draggedCardOriginalPosition = new Vector2();

    private String winner;


    public MatchEvent(GameScreen gameScreen, Match.GameState gameState){
        super(gameScreen);
        mGame = mGameScreen.getGame();
        generateSccenario(gameState);
        animationCounter = 0;
        height = mGame.getScreenHeight() * 0.5f;
        leftHolderPosition = mGame.getScreenWidth() * 0.1f;
        rightHolderPosition = mGame.getScreenWidth() * 0.9f;

        cardHolder1 = new CardHolder(mGame.getScreenHeight()/6, leftHolderPosition , (int)(mGame.getScreenHeight() * 0.35), gameScreen);
        cardHolder2 = new CardHolder(mGame.getScreenHeight()/6, rightHolderPosition, (int)(mGame.getScreenHeight() * 0.35), gameScreen);

        horizontalCardScroller = new HorizontalCardScroller(mGame.getScreenWidth() * 0.5f, (mGame.getScreenHeight() * 0.5f) + (mGame.getScreenHeight()), mGame.getScreenWidth(), mGame.getScreenHeight() * 0.4f, mGameScreen);
        horizontalCardScroller.addTestData();
        horizontalCardScroller.setMultiMode(true, 100);
        horizontalCardScroller.setSelectMode(true);

        horizontalCardScroller.addSelectDestination(cardHolder1.getBound());

        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("ConfirmButton", "img/BUTTONCONFIRM.png");


        confirmPlayer = new PushButton(mGame.getScreenWidth() / 2.0f, mGame.getScreenHeight() / 2.3f, 200, 100, "ConfirmButton", mGameScreen  );

        winner = null;


    }

    public String getWinner(){
        return winner;
    }

    //method to generate a random scenario
    private void generateSccenario(Match.GameState gameState){

        ArrayList<String> possibleScenarios = new ArrayList<String>();

        switch (gameState){
            case MIDFIELD:
                possibleScenarios.add("PAS PAS");
                possibleScenarios.add("PAC PAC");
                possibleScenarios.add("HEA HEA");
                possibleScenarios.add("DRI DRI");
                break;

            case PLAYER_A_ATTACK:
                  possibleScenarios.add("PAC PAC");
                  possibleScenarios.add("HEA HEA");
                  possibleScenarios.add("DRI DEF");
                  possibleScenarios.add("PAS DEF");
                break;
            case PLAYER_B_ATTACK:
                possibleScenarios.add("PAC PAC");
                possibleScenarios.add("HEA HEA");
                possibleScenarios.add("DEF DRI");
                possibleScenarios.add("DEF PAS");
                break;
            case PLAYER_A_DANGEROUS_ATTACK:
                possibleScenarios.add("SHO GK");
                possibleScenarios.add("DRI DEF");
                possibleScenarios.add("HEA GK");
                possibleScenarios.add("HEA HEA");
                break;
            case PLAYER_B_DANGEROUS_ATTACK:
                possibleScenarios.add("GK SHO");
                possibleScenarios.add("DEF DRI");
                possibleScenarios.add("GK HEA");
                possibleScenarios.add("HEA HEA");
                break;
        }

        //choose the scenario
        Random rand = new Random();
        chosenScenario = possibleScenarios.get(rand.nextInt(4));
        possibleScenarios.clear();




    }

    private void selectPlayers(){
        Card playerCard = cardHolder1.getCard();
        Card cpuCard = new Card(100f, 100f, 100f, mGameScreen, true, 80, 90 );

        cardHolder2.setCard(cpuCard);
        playersChosen = true;
        drawCards = false;
        getStats(playerCard, cpuCard);


    }

    private void getStats(Card player, Card cpu){
        int[] stats = new int[2];
        String[] scenario = chosenScenario.split(" ");
        Card currentCard = player;
        //use the players stamina as a percentage to modify their stats by
        float stamina;
       for (int i = 0; i < 2; i++) {
           if (i == 1){
               currentCard = cpu;
           }
           stamina = currentCard.getFitness() / 100;
           switch (scenario[i]) {
               case "DEF":
                   stats[i] = (int)stamina * currentCard.getDefending();
                   break;
               case "PAS":
                   stats[i] = (int)stamina * currentCard.getPassing();
                   break;
               case "PAC":
                   stats[i] = (int)stamina * currentCard.getPace();
                   break;
               case "DRI":
                   stats[i] = (int)stamina * currentCard.getDribbling();
                   break;
               case "SHO":
                   stats[i] = (int)stamina *  currentCard.getShooting();
                   break;
               case "HEA":
                   stats[i] = (int)stamina * currentCard.getHeading();
                   break;
               case "GK":
                   stats[i] = 0;
                   if (currentCard.getPlayerPosition().equals("GoalKeeper"))
                       stats[i] = (int)stamina * currentCard.getRating();
                   break;
             }
           if (currentCard.getPlayerPosition().equals("GoalKeeper") && !(scenario[i].equals("GK")))
               stats[i] = 0;

       }

        scenarioWinner(stats[0], stats[1]);
    }

    //method to determine which player wins the scenario
    private void scenarioWinner(int playerStat, int cpuStat){
        winner = "Player";
        //the max difference is the biggest difference between 2 stats in the game. This is used to
        //give the smaller stat a better chance of winning
        int maxDifference = 70;
        //difference between both stats
        int difference = 0;
        String smallestStat = "Player";
        if (playerStat > cpuStat)
            smallestStat = "CPU";
        difference = Math.abs(playerStat - cpuStat);
        /*determine the probability of the smaller stat winning.
        Both stats will get a base probability of 0.5. The smaller stats probability
        will be lowered depending on the difference between the two stats.
         */
        double probability = 0.5 * (1 - (difference / maxDifference));
        //generate random number. If number is less than probability then smaller stat wins
        //else larger stat wins

        if (probability < Math.random()){
            //smaller stat wins
            if (smallestStat.equals("CPU"))winner = "CPU";
        } else{
            //larger stat wins
            if (smallestStat.equals("Player"))winner = "CPU";
        }
       clearCounter = animationCounter;

    }

    private void showScroller() {
        if(!scrollerEnabled || scrollerAnimationTriggered || horizontalCardScroller.isAnimating()) return;

        if(scrollerDisplayed) scrollerMoveDirection = 1;
        else scrollerMoveDirection = -1;

        scrollerAnimationTriggered = true;
    }


    /**
     * Checks if scroller animation is triggered then performs animation
     */
    private void checkAndPerformScrollerAnimation() {
        if(!scrollerAnimationTriggered) return;

        // Move scroller
        float moveBy = scrollerMoveDirection * mGame.getScreenHeight() * 0.05f;

        // Move scroller
        // Draw current item
        horizontalCardScroller.adjustPosition(0, moveBy);



        // Add to distance moved
        distanceMoved += Math.abs(moveBy);

        // If intended distance has been moved, end animation
        if(distanceMoved >= mGame.getScreenHeight() * 0.75f) {
            scrollerAnimationTriggered = false;
            scrollerDisplayed = !scrollerDisplayed;
            distanceMoved = 0;

        }

    }

    /**
     * Check if a touch is within the general area of a certain location
     * @param userTouchLocation
     * @param touchDestination
     */
    private boolean checkIfTouchInArea(Vector2 userTouchLocation, BoundingBox touchDestination) {
        if(userTouchLocation == null || touchDestination == null) return false;

        if(touchDestination.contains(userTouchLocation.x, userTouchLocation.y)) return true;

        return false;
    }

    /**
     * Checks for touch input and performs necessary actions to move a card
     * and trigger any associated events
     */
    private void checkAndPerformDragCard() {
        Input input = mGameScreen.getGame().getInput();
        //Consider all buffered touch events
        for (TouchEvent t : input.getTouchEvents()) {
            // If there is no touch down yet, check for a touch down
            if(!touchDown) {
                // Check if touch event is a touch down
                if (t.type == TouchEvent.TOUCH_DOWN) {
                    // Check if the touch location is within the bounds of the player's card holder
                        if(checkIfTouchInArea(new Vector2(t.x, t.y), cardHolder1.getBound())) {
                            touchDown = true;
                            draggedCardOriginalPosition = new Vector2(cardHolder1.position.x, cardHolder1.position.y);
                        }

                } else // No touch down
                    continue;
            }

            // If touch event is a drag event, modify position of card
            if (t.type == TouchEvent.TOUCH_DRAGGED && touchDown) {
                if (!Float.isNaN(input.getTouchX(t.pointer)) && cardHolder1.getCard() != null) {
                    Log.d("DEBUG", "Holder: " + cardHolder1.position.x + "," + cardHolder1.position.y + " Card: " + cardHolder1.getCard().position.x + "," + cardHolder1.getCard().position.y);
                    Log.d("DEBUG", "Scroller position: "+ checkIfTouchInArea(cardHolder1.position, horizontalCardScroller.getBound()));
                    cardHolder1.setPosition(input.getTouchX(t.pointer), input.getTouchY(t.pointer));
                }
            }
            // If touch event is a touch up event, check if location is within a select destination and remove card
            // else return card to original position
            if (t.type == TouchEvent.TOUCH_UP) {
                touchDown = false;

                // For each select destination, check if touch up is within the bounds of the destination BoundingBox
                if(checkIfTouchInArea(cardHolder1.position, horizontalCardScroller.getBound())) {
                    horizontalCardScroller.addScrollerItem(cardHolder1.getCard());
                    cardHolder1.setCard(null);
                    cardHolder1.setPosition(draggedCardOriginalPosition.x, draggedCardOriginalPosition.y);
                } else
                    cardHolder1.setPosition(draggedCardOriginalPosition.x, draggedCardOriginalPosition.y);

            }
        }
    }

    /**
     * Checks if a removed card is ready from the scroller
     * If so, the position the card was dropped is cross referenced with the displayed
     * holders to determine it's position
     */

    /**
     *  Temp card used so that when the user swaps a card with a card in the card holder
     *  it gets added back to the card scroller when the scroller has finished animating
     */

    private Card tempCard;
    private boolean tempCardReady = false;
    private void checkIfRemovedCardReady() {
        if (horizontalCardScroller.isRemovedCardReady()) {
            Card removedCard = (horizontalCardScroller.getRemovedCard());
            if(removedCard.getBound().intersects(cardHolder1.getBound())) {
                if (cardHolder1.getCard() != null){
                    tempCard = cardHolder1.getCard();
                    tempCardReady = true;
                }
                cardHolder1.setCard(removedCard);
            }
        }
    }

    private void checkIfTempCardReady() {
        if(tempCardReady && !horizontalCardScroller.isAnimating()) {
            horizontalCardScroller.addScrollerItem(tempCard);
            tempCardReady = false;
        }
    }

    private void checkForDrawAnimations(){
        if (animationCounter >= 50){
            if ((animationCounter % 1) == 0) {
                if (height > mGame.getScreenHeight() * 0.25)
                    height -= 10;
                if (size > 75)
                    size -= 5;
            }

            if ((height <= mGame.getScreenHeight() * 0.25) && size <= 75 && !playersChosen)
                drawCards = true;

            if (drawCards){
                if (animationCounter % 2 == 0) {
                    if (leftHolderPosition < mGame.getScreenWidth() * 0.25) {
                        leftHolderPosition += 10;
                        cardHolder1.setPosition(leftHolderPosition, (int) (mGame.getScreenHeight() * 0.35));
                    }
                }
                if (!scrollerDisplayed) showScroller();
            }
            if (playersChosen){
                if(animationCounter % 2 == 0){
                    if (rightHolderPosition > mGame.getScreenWidth() * 0.8  ) {
                        rightHolderPosition -= 10;
                        cardHolder2.setPosition(rightHolderPosition, (int) (mGame.getScreenHeight() * 0.35));
                    }
                }
                if (scrollerDisplayed)showScroller();
            }
            if (clearCounter != 0 && animationCounter - clearCounter > 100){
                clearScenario = true;
            }
        }
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        animationCounter++;
        horizontalCardScroller.update(elapsedTime);
        cardHolder1.update(elapsedTime);
        confirmPlayer.update(elapsedTime);

        checkForDrawAnimations();

        checkAndPerformScrollerAnimation();

        checkAndPerformDragCard();
        checkIfRemovedCardReady();
        checkIfTempCardReady();

        if (confirmPlayer.isPushTriggered()) selectPlayers();

    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        Paint paint = mGame.getPaint();

        if (!clearScenario){
            paint.reset();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(20);
            graphics2D.drawRect((int)(mGame.getScreenWidth() * 0.1f), (int)(mGame.getScreenHeight() * 0.15f),(int)(mGame.getScreenWidth() * 0.9f), (int)(mGame.getScreenHeight() * 0.9f), paint);

            paint.reset();
            paint.setColor(Color.GRAY);
            graphics2D.drawRect((int)(mGame.getScreenWidth() * 0.1f), (int)(mGame.getScreenHeight() * 0.15f),(int)(mGame.getScreenWidth() * 0.9f), (int)(mGame.getScreenHeight() * 0.9f), paint);

            paint.reset();
            paint.setColor(Color.BLACK);
            paint.setTextSize(size);

            graphics2D.drawText(chosenScenario, (int)(mGame.getScreenWidth()/ 2.25), height, paint);

            if(drawCards) {
                graphics2D.drawText("Please choose your card", (int) (mGame.getScreenWidth() / 3), (int) (height * 1.5), paint);

                paint.reset();
                paint.setTextSize(50);
                paint.setColor(Color.BLACK);
                graphics2D.drawText("Your Card", leftHolderPosition - 100, (int) (mGame.getScreenHeight() * 0.5), paint);


                horizontalCardScroller.draw(elapsedTime, graphics2D);

                cardHolder1.draw(elapsedTime, graphics2D);
                if (cardHolder1.getCard() != null) confirmPlayer.draw(elapsedTime, graphics2D);

            }

            if (playersChosen){

                cardHolder2.draw(elapsedTime, graphics2D);
                cardHolder1.draw(elapsedTime, graphics2D);

            }

            paint.reset();
            paint.setColor(Color.BLACK);
            paint.setTextSize(150);
            if (winner != null) {
                if (winner.equals("Player")) {
                    graphics2D.drawText("You won this scenario!", mGame.getScreenWidth() / 5, mGame.getScreenHeight() * 0.6f, paint);
                } else{
                    graphics2D.drawText("You lost this scenario!", mGame.getScreenWidth() / 5, mGame.getScreenHeight() * 0.6f, paint);

                }
            }
        }
















    }
}
