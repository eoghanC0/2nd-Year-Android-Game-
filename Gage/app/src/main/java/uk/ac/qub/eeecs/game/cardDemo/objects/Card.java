package uk.ac.qub.eeecs.game.cardDemo.objects;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameScreen;

import uk.ac.qub.eeecs.gage.world.Sprite;

import static uk.ac.qub.eeecs.gage.engine.input.TouchEvent.TOUCH_DOWN;

/**
 * Created by Inaki on 04/11/2017.
 * Refactored by Stephen on 13/11/2017.
 */


public class Card extends Sprite {

    // ////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////
    private Bitmap frontBmp, backBmp;

    /**
     * Dimensions of the screen
     */
    private Vector2 screenDimensions = new Vector2();

    /**
     * Centre of this game object
     */
    private Vector2 cardCentre = new Vector2();

    /**
     * Variables for touch handling
     */
    private boolean touchDown;

    /**
     * Variable for the player name on the card
     */
    private String playerName;

    /**
     * Properties for the card flip animation
     */
    private float flatCardBoundHalfWidth;
    private boolean isFlipping;
    private int flipFrameCounter = 1;
    private final int animationLength = 20;

    private final int defaultPlayerNameSize = 45;
    private final int defaultAttributeSize = 35;

    /**
     * Variables used to determine whether card is clicked
     * or dragged
     */
    private boolean pushDown = false;
    private float pushTime = 0;
    private final float MAX_PUSH_TIME = 0.33f;

    // /////////////////////////////////////////////////////
    // Constructor
    // /////////////////////////////////////////////////////
    public Card(float startX, float startY, GameScreen gameScreen) {
        super(startX, startY, 300f, 422f, null, gameScreen);

        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        if (assetManager.getBitmap("CardFront") == null) {assetManager.loadAndAddBitmap("CardFront", "img/CardFront.png");}
        if (assetManager.getBitmap("CardBack") == null) {assetManager.loadAndAddBitmap("CardBack", "img/CardBack.png");}
        frontBmp = assetManager.getBitmap("CardFront");
        backBmp = assetManager.getBitmap("CardBack");

        //Show the front of the card by default
        mBitmap = frontBmp;

        //Randomly select a player name
        try {
            playerName = getRandomPlayerName();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            playerName = "Player Name";
        }

        //TODO: Remove from constructor and avoid hardcoded numbers.
        screenDimensions.x = mGameScreen.getGame().getScreenWidth();
        screenDimensions.y = mGameScreen.getGame().getScreenHeight();
        cardCentre.x = 150f;
        cardCentre.y = 211f;

        //Set the default card half width to this initial value
        flatCardBoundHalfWidth = mBound.halfWidth;
    }

    // ///////////////////////////////////////////////////////////
    // Methods
    // ///////////////////////////////////////////////////////////

    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);

        // Ensure the card cannot leave the confines of the screen
        BoundingBox cardBound = getBound();
        if (cardBound.getLeft() < 0)
            this.position.x -= cardBound.getLeft();
        else if (cardBound.getRight() >  mGameScreen.getGame().getScreenWidth())
            this.position.x -= (cardBound.getRight() -   mGameScreen.getGame().getScreenWidth());

        if (cardBound.getBottom() < 0)
            this.position.y -= cardBound.getBottom();
        else if (cardBound.getTop() >   mGameScreen.getGame().getScreenHeight())
            this.position.y -= (cardBound.getTop() -   mGameScreen.getGame().getScreenHeight());

        //Get all inputs on the screen since the last update
        Input input = mGameScreen.getGame().getInput();

        boolean touchOnCard = false;

        if(pushDown) {
            pushTime += elapsedTime.stepTime;
        }

        //Consider all buffered touch events
        for (TouchEvent t : input.getTouchEvents()) {

            //Consider Touch events within the area of the card
            if ((input.getTouchX(t.pointer) > position.x - cardCentre.x)
                    && (input.getTouchX(t.pointer) < position.x + cardCentre.x)
                    && (input.getTouchY(t.pointer) > position.y - cardCentre.y)
                    && (input.getTouchY(t.pointer) < position.y + cardCentre.y))
                touchOnCard = true;

            //Consider TOUCH_DOWN events
            if (t.type == TOUCH_DOWN && touchOnCard) {
                pushTime = 0;
                pushDown = true;
                touchDown = true;
                Log.d("Card", "Down detected");

                //Card should flip on touching
                isFlipping = true;
            }

            //Consider TOUCH_DRAGGED events after TOUCH_DOWN event
            if (t.type == TouchEvent.TOUCH_DRAGGED && touchDown) {
                if (!Float.isNaN(input.getTouchX(t.pointer))) {
                    position.x = input.getTouchX(t.pointer);
                    position.y = input.getTouchY(t.pointer);
                    Log.d("Card", "Drag detected");
                }
            }

            //touch ends then change touchdown,activecard,doneMovement else check is dragged
            if (t.type == TouchEvent.TOUCH_UP) {
                pushDown = false;
                touchDown = false;
                touchOnCard = false;
                Log.d("Card", "Up detected");
            }
        }

        //Show an animation if the card is currently being flipped
        if (isFlipping && !pushDown && pushTime <= MAX_PUSH_TIME) {
            pushTime = 0;
            if (flipFrameCounter <= animationLength / 2) {
                mBound.halfWidth -= (mBound.halfWidth / (animationLength / 2));
            } else {
                mBound.halfWidth += (mBound.halfWidth / (animationLength / 2));
            }

            if (flipFrameCounter == animationLength / 2) {
                if (mBitmap == frontBmp) {
                    mBitmap = backBmp;
                } else {
                    mBitmap = frontBmp;
                }
            }

            if (flipFrameCounter == animationLength) {
                isFlipping = false;
                flipFrameCounter = 1;
            }

            flipFrameCounter++;
        } else {
            //Make sure the card is back to its usual size
            mBound.halfWidth = flatCardBoundHalfWidth;
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime,IGraphics2D graphics2D) {
        super.draw(elapsedTime, graphics2D);

        if (mBitmap == frontBmp) {
            //Draw on the card
            Paint paint = mGameScreen.getGame().getPaint();
            paint.setColor(Color.BLACK);

            //sets text size based on player name
            setPlayerNameTextSize(paint, playerName);

            //int currentPlayerNameSize = defaultPlayerNameSize * ((int) mBound.halfWidth / (int) flatCardBoundHalfWidth);
            //paint.setTextSize(currentPlayerNameSize);

            paint.setTextAlign(Paint.Align.CENTER);
            graphics2D.drawText(playerName, position.x, position.y - 80, paint);

            int currentAttributeSize = defaultAttributeSize * ((int) mBound.halfWidth / (int) flatCardBoundHalfWidth);
            paint.setTextSize(currentAttributeSize);

            graphics2D.drawText("PAC", position.x - 50, position.y + 80, paint);
            graphics2D.drawText("SHO", position.x - 50, position.y + 130, paint);
            graphics2D.drawText("PAS", position.x - 50, position.y + 180, paint);

            graphics2D.drawText("DRI", position.x + 95, position.y + 80, paint);
            graphics2D.drawText("DEF", position.x + 95, position.y + 130, paint);
            graphics2D.drawText("HEA", position.x + 95, position.y + 180, paint);
        }
    }

    //Retruns a random player name from a csv file
    private String getRandomPlayerName()throws FileNotFoundException{
        //arraylist to store csv content by line
            ArrayList<String> playerNames = new ArrayList<>();
        try{
        DataInputStream textFileStream = new DataInputStream(mGameScreen.getGame().getActivity().getApplicationContext().getAssets()
                .open(String.format("footballers_real.csv")));
        Scanner scan = new Scanner(textFileStream);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                playerNames.add(line);
            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "Player_Name";
        }
        //Selects from all values in the arraylist except the first which is the csv header
        return playerNames.get(new Random().nextInt(playerNames.size()-2)+1);
    }

    //So that player name is never larger than the card
    private void setPlayerNameTextSize(Paint paint, String text) {
        //Larger float => greater accuracy
        final float testTextSize = 50f;

        // Get the bounds of the text using testTestSize
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = (testTextSize * getBound().getWidth() / bounds.width())-.2f;

        //Ensures short named cards like "Pepe" don't take up the whole card
        if (desiredTextSize > defaultPlayerNameSize)
            desiredTextSize = defaultPlayerNameSize;

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }
}
