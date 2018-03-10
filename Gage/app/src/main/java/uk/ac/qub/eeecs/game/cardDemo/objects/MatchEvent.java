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
    private Vector2 draggedCardOriginalPosition = new Vector2();

    private String winner;
    private ArrayList<Card> AITeam;


    public MatchEvent(GameScreen gameScreen, Match.GameState gameState, ArrayList<Card> AISquad){
        super(gameScreen);
        mGame = mGameScreen.getGame();
        this.AITeam = AISquad;
        generateSccenario(gameState);
        animationCounter = 0;
        height = mGame.getScreenHeight() * 0.5f;
        leftHolderPosition = mGame.getScreenWidth() * 0.1f;
        rightHolderPosition = mGame.getScreenWidth() * 0.9f;

        cardHolder1 = new CardHolder(mGame.getScreenHeight()/6, leftHolderPosition , (int)(mGame.getScreenHeight() * 0.35), gameScreen);
        cardHolder2 = new CardHolder(mGame.getScreenHeight()/6, rightHolderPosition, (int)(mGame.getScreenHeight() * 0.35), gameScreen);

        horizontalCardScroller = new HorizontalCardScroller(mGame.getScreenWidth() * 0.5f, (mGame.getScreenHeight() * 0.5f) + (mGame.getScreenHeight()), mGame.getScreenWidth(), mGame.getScreenHeight() * 0.4f, mGameScreen);

        for (int i = 0; i < AITeam.size(); i++) {
            horizontalCardScroller.addScrollerItem(AITeam.get(i));
        }
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
        Card cpuCard = null;
        String[] scenario = chosenScenario.split(" ");
        int playerStats = getStats(playerCard, scenario[0]);
        boolean found = false;
        int counter = 0;
        if (scenario[1].equals("GK")){
            while (!found){
                if (AITeam.get(counter).getPlayerPosition().equals("GoalKeeper")) {
                    cpuCard = AITeam.get(counter);
                    found = true;
                }
            }
        }else{
            bubbleSort(scenario[1]);

            Random rnd = new Random();
            int randomNumber = rnd.nextInt(100);
            if (randomNumber < 40)
                cpuCard = AITeam.get(10);
            else if (randomNumber >= 40 && randomNumber < 55)
                cpuCard = AITeam.get(9);
            else if (randomNumber >= 55 && randomNumber < 65)
                cpuCard = AITeam.get(8);
            else if (randomNumber >= 65 && randomNumber < 72)
                cpuCard = AITeam.get(7);
            else if (randomNumber >= 72 && randomNumber < 79)
                cpuCard = AITeam.get(6);
            else if (randomNumber >= 79 && randomNumber < 85)
                cpuCard = AITeam.get(5);
            else if (randomNumber >= 85 && randomNumber < 90)
                cpuCard = AITeam.get(4);
            else if (randomNumber >= 90 && randomNumber < 93)
                cpuCard = AITeam.get(3);
            else if (randomNumber >= 93 && randomNumber < 95)
                cpuCard = AITeam.get(2);
            else if (randomNumber >= 95 && randomNumber < 97)
                cpuCard = AITeam.get(1);
            else if (randomNumber >= 97)
                cpuCard = AITeam.get(0);
        }

        int cpuStats = getStats(cpuCard, scenario[1]);
        scenarioWinner(playerStats, cpuStats);
        cardHolder2.setCard(cpuCard);
        playersChosen = true;
        drawCards = false;
    }

    private int getStats(Card player, String scenario){
        Card currentCard = player;
        //use the players stamina as a percentage to modify their stats by
        float stamina;
        int stat = 0;
        stamina = currentCard.getFitness() / 100;
        switch (scenario) {
                    case "DEF":
                        stat = (int) stamina * currentCard.getDefending();
                        break;
                    case "PAS":
                        stat = (int) stamina * currentCard.getPassing();
                        break;
                    case "PAC":
                        stat = (int) stamina * currentCard.getPace();
                        break;
                    case "DRI":
                        stat= (int) stamina * currentCard.getDribbling();
                        break;
                    case "SHO":
                        stat = (int) stamina * currentCard.getShooting();
                        break;
                    case "HEA":
                        stat = (int) stamina * currentCard.getHeading();
                        break;
                    case "GK":
                        if (currentCard.getPlayerPosition().equals("GoalKeeper"))
                            stat = (int) stamina * currentCard.getRating();
                        else
                            stat = (int)(stamina * currentCard.getRating())/2;
                        break;
                }

                currentCard.setFitness(currentCard.getFitness() - 10);
        return stat;
    }

    private void swap(int x) {//go through the array and sort from smallest to highest
        Card temp = AITeam.get(x - 1);
        AITeam.set(x - 1, AITeam.get(x));
        AITeam.set(x, temp);
    }

    private  void bubbleSort(String scenario) {

        int n = AITeam.size();
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {

                switch (scenario) {
                    case "DEF":
                        if (AITeam.get(j - 1).getDefending() > AITeam.get(j).getDefending())
                            swap(j);
                        break;
                    case "PAS":
                        if (AITeam.get(j - 1).getPassing() > AITeam.get(j).getPassing())
                            swap(j);
                        break;
                    case "PAC":
                        if (AITeam.get(j - 1).getPace() > AITeam.get(j).getPace())
                            swap(j);
                        break;
                    case "DRI":
                        if (AITeam.get(j - 1).getDribbling() > AITeam.get(j).getDribbling())
                            swap(j);
                        break;
                    case "SHO":
                        if (AITeam.get(j - 1).getShooting() > AITeam.get(j).getShooting())
                            swap(j);
                        break;
                    case "HEA":
                        if (AITeam.get(j - 1).getHeading() > AITeam.get(j).getHeading())
                            swap(j);
                        break;
                }
            }
        }
    }


    //method to determine which player wins the scenario
    private void scenarioWinner(int playerStat, int cpuStat){
        winner = "Player";
        //the max difference is the biggest difference between 2 stats in the game. This is used to
        //give the smaller stat a better chance of winning
        int maxDifference = 70;
        //difference between both stats
        String smallestStat = "Player";
        if (playerStat > cpuStat)
            smallestStat = "CPU";
        int difference = Math.abs(playerStat - cpuStat);

        /*determine the probability of the smaller stat winning.
        Both stats will get a base probability of 0.5. The smaller stats probability
        will be lowered depending on the difference between the two stats.
         */
        double probability;
        if (difference >=  maxDifference)
            probability = 0;
        else
            probability = 0.5 * (1 - (difference / maxDifference));
        //generate random number. If number is less than probability then smaller stat wins
        //else larger stat wins
        double randomNumber = Math.random();
        if (randomNumber < probability){
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
