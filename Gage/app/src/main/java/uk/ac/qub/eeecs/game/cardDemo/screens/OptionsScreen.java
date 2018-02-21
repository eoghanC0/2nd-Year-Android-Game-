package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.ui.InfoBar;


/**
 * Created by aedan on 02/11/2017.
 */

public class OptionsScreen extends FootballGameScreen {

    /**
     * Define InfoBar
     */
    private InfoBar infoBar;

    /**
     * Define viewports for this layer and the associated screen projection
     */
    private ScreenViewport mScreenViewport;
    private LayerViewport mLayerViewport;


    //define the buttons
    private PushButton mMenuButton;
    private PushButton mDifficultyUp;
    private PushButton mDifficultyDown;
    private PushButton mTimeUp;
    private PushButton mTimeDown;
    private PushButton mScreenUp;
    private PushButton mScreenDown;

    private Bitmap background;
    private final Rect backgroundRect = new Rect(0,0, this.getGame().getScreenWidth(),this.getGame().getScreenHeight());

    private Paint mPaint;

    private int spacingX = getGame().getScreenWidth() / 5;
    private int spacingY = getGame().getScreenHeight() / 3;

    private Vector2[] textPositions = new Vector2[6];
    private Vector2 centerPosition = new Vector2();

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public OptionsScreen(FootballGame game) {

        super("OptionsScreen", game);

        // Instantiate InfoBar
        infoBar = new InfoBar(mGame.getScreenWidth() / 2, 270, mGame.getScreenWidth(), mGame.getScreenHeight() * 0.1f, this, "", mGame.getPlayerName(), "O P T I O N S", mGame.getMatchStats());

        // Create the screen viewport
        mScreenViewport = new ScreenViewport(0, 0, game.getScreenWidth(),
                game.getScreenHeight());

        // Create the layer viewport, taking into account the orientation
        // and aspect ratio of the screen.
        if (mScreenViewport.width > mScreenViewport.height)
            mLayerViewport = new LayerViewport(240.0f, 240.0f
                    * mScreenViewport.height / mScreenViewport.width, 240,
                    240.0f * mScreenViewport.height / mScreenViewport.width);
        else
            mLayerViewport = new LayerViewport(240.0f * mScreenViewport.height
                    / mScreenViewport.width, 240.0f, 240.0f
                    * mScreenViewport.height / mScreenViewport.width, 240);

        AssetStore assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("MenuButton", "img/MenuButton.png");
        assetManager.loadAndAddBitmap("ArrowLeft", "img/ArrowLeft.png");
        assetManager.loadAndAddBitmap("ArrowLeftPushed", "img/ArrowLeftPushed.png");
        assetManager.loadAndAddBitmap("ArrowRight", "img/ArrowRight.png");
        assetManager.loadAndAddBitmap("ArrowRightPushed", "img/ArrowRightPushed.png");
        assetManager.loadAndAddBitmap("OptionsBackground", "img/MainBackground.jpg");

        background = assetManager.getBitmap("OptionsBackground");

        mPaint = mGame.getPaint();

        Rect temp = new Rect();

        textPositions[0] = new Vector2(mGame.getScreenWidth() * 0.05f, mGame.getScreenHeight() * 0.3f);
        textPositions[1] = new Vector2(mGame.getScreenWidth() * 0.05f, mGame.getScreenHeight() * 0.5f);
        textPositions[2] = new Vector2(mGame.getScreenWidth() * 0.05f, mGame.getScreenHeight() * 0.7f);
        textPositions[3] = new Vector2(mGame.getScreenWidth() * 0.7f, mGame.getScreenHeight() * 0.3f);
        textPositions[4] = new Vector2(mGame.getScreenWidth() * 0.7f, mGame.getScreenHeight() * 0.5f);
        textPositions[5] = new Vector2(mGame.getScreenWidth() * 0.7f, mGame.getScreenHeight() * 0.7f);

        // Instantiate buttons
        mPaint.setTextSize(85);
        float leftButtonX = mGame.getScreenWidth() * 0.7f - mGame.getScreenWidth() * 0.05f;
        float rightButtonX = mGame.getScreenWidth() * 0.7f + getTextBounds(mPaint, "Beginner").width() + mGame.getScreenWidth() * 0.05f;
        float yAdjustment = getTextBounds(mPaint, "Abc").height() / 2f;
        mMenuButton = new PushButton(mGame.getScreenHeight() * 0.1f, mGame.getScreenHeight() - (mGame.getScreenHeight() * 0.1f), mGame.getScreenHeight() * 0.15f, mGame.getScreenHeight() * 0.15f,
                "ArrowBack", "ArrowBackPushed",this );
        mDifficultyUp = new PushButton(rightButtonX, textPositions[3].y - yAdjustment, mGame.getScreenHeight() * 0.1f, mGame.getScreenHeight() * 0.1f,
                "ArrowRight", "ArrowRightPushed", this );
        mDifficultyDown = new PushButton(leftButtonX, textPositions[3].y - yAdjustment, mGame.getScreenHeight() * 0.1f, mGame.getScreenHeight() * 0.1f,
                "ArrowLeft", "ArrowLeftPushed", this );
        mTimeUp = new PushButton(rightButtonX, textPositions[4].y - yAdjustment, mGame.getScreenHeight() * 0.1f, mGame.getScreenHeight() * 0.1f,
                "ArrowRight", "ArrowRightPushed", this );
        mTimeDown = new PushButton(leftButtonX, textPositions[4].y - yAdjustment, mGame.getScreenHeight() * 0.1f, mGame.getScreenHeight() * 0.1f,
                "ArrowLeft", "ArrowLeftPushed", this );
        mScreenUp = new PushButton(rightButtonX, textPositions[5].y - yAdjustment, mGame.getScreenHeight() * 0.1f, mGame.getScreenHeight() * 0.1f,
                "ArrowRight", "ArrowRightPushed", this );
        mScreenDown = new PushButton(leftButtonX, textPositions[5].y - yAdjustment, mGame.getScreenHeight() * 0.1f, mGame.getScreenHeight() * 0.1f,
                "ArrowLeft", "ArrowLeftPushed", this );

        textPositions[3] = calculateNewTextPosition(mDifficultyDown, mDifficultyUp, textPositions[3], mGame.getStringPreference("Difficulty"));
        textPositions[4] = calculateNewTextPosition(mDifficultyDown, mDifficultyUp, textPositions[4],String.valueOf((mGame.getIntPreference("GameLength") / 60)) + " mins");
        textPositions[5] = calculateNewTextPosition(mDifficultyDown, mDifficultyUp, textPositions[5], "Screen " + mGame.getIntPreference("ScreenType"));
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        // Process any touch events occurring since the last update
        Input input = mGame.getInput();

        List<TouchEvent> touchEvents = input.getTouchEvents();
        if (touchEvents.size() > 0) {

            // Just check the first touch event that occurred in the frame.
            // It means pressing the screen with several fingers may not
            // trigger a 'button', but, hey, it's an exceedingly basic menu.
            TouchEvent touchEvent = touchEvents.get(0);

            // Update each button and transition if needed
            mMenuButton.update(elapsedTime);
            mDifficultyDown.update(elapsedTime);
            mDifficultyUp.update(elapsedTime);
            mTimeDown.update(elapsedTime);
            mTimeUp.update(elapsedTime);
            mScreenDown.update(elapsedTime);
            mScreenUp.update(elapsedTime);

            if (mMenuButton.isPushTriggered())
                changeToScreen(new MenuScreen(mGame));

            if (mDifficultyDown.isPushTriggered()) {
                if(mGame.getStringPreference("Difficulty").equals("Amateur")){
                    mGame.setPreference("Difficulty", "Beginner" );
                }else if (mGame.getStringPreference("Difficulty").equals("Difficult")){
                    mGame.setPreference("Difficulty", "Amateur" );
                }

                textPositions[3] = calculateNewTextPosition(mDifficultyDown, mDifficultyUp, textPositions[3], mGame.getStringPreference("Difficulty"));
            }

            if (mDifficultyUp.isPushTriggered()){
                if(mGame.getStringPreference("Difficulty").equals("Amateur")){
                    mGame.setPreference("Difficulty", "Difficult" );
                }else if (mGame.getStringPreference("Difficulty").equals("Beginner")){
                    mGame.setPreference("Difficulty", "Amateur" );
                }

                textPositions[3] = calculateNewTextPosition(mDifficultyDown, mDifficultyUp, textPositions[3], mGame.getStringPreference("Difficulty"));
            }

            int length = (mGame.getIntPreference("GameLength"));
            if (mTimeUp.isPushTriggered()){
                if (length < 360){
                    length += 60;
                    mGame.setPreference("GameLength", length);
                }
                textPositions[4] = calculateNewTextPosition(mTimeDown, mTimeUp, textPositions[4], String.valueOf((mGame.getIntPreference("GameLength") / 60)) + " mins");
            }
            if (mTimeDown.isPushTriggered()){
                if (length > 240){
                    length -= 60;
                    mGame.setPreference("GameLength", length);
                }

                textPositions[4] = calculateNewTextPosition(mTimeDown, mTimeUp, textPositions[4], String.valueOf((mGame.getIntPreference("GameLength") / 60)) + " mins");
            }

            int screenType = mGame.getIntPreference("ScreenType");
            if (mScreenDown.isPushTriggered()){
                if (screenType > 1){
                    screenType--;
                    mGame.setPreference("ScreenType", screenType);
                }

                textPositions[5] = calculateNewTextPosition(mDifficultyDown, mDifficultyUp, textPositions[5],"Screen " + mGame.getIntPreference("ScreenType"));
            }
            if (mScreenUp.isPushTriggered()){
                if (screenType < 3){
                    screenType++;
                    mGame.setPreference("ScreenType", screenType);
                }

                textPositions[5] = calculateNewTextPosition(mDifficultyDown, mDifficultyUp, textPositions[5], "Screen " + mGame.getIntPreference("ScreenType"));
            }
        }
    }

    /**
     * Remove the current game screen and then change to the specified screen
     *
     * @param screen game screen to become active
     */
    private void changeToScreen(FootballGameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.drawBitmap(background, null, backgroundRect, mPaint);
        mPaint.reset();
        mDifficultyUp.draw(elapsedTime, graphics2D);
        mDifficultyDown.draw(elapsedTime, graphics2D);
        mTimeUp.draw(elapsedTime, graphics2D);
        mTimeDown.draw(elapsedTime, graphics2D);
        mScreenUp.draw(elapsedTime, graphics2D);
        mScreenDown.draw(elapsedTime, graphics2D);
        mMenuButton.draw(elapsedTime, graphics2D);

        mPaint.reset();
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(85);
        mPaint.setFakeBoldText(true);

        drawText(graphics2D, "D I F F I C U L T Y", textPositions[0].x, textPositions[0].y);
        drawText(graphics2D, "G A M E  L E N G T H", textPositions[1].x, textPositions[1].y);
        drawText(graphics2D, "P I T C H", textPositions[2].x, textPositions[2].y);

        for (int i = 0; i < 3; i++)
            drawText(graphics2D, "|", mGame.getScreenWidth() * 0.5f, textPositions[i].y);


        drawText(graphics2D, String.valueOf(mGame.getStringPreference("Difficulty")),textPositions[3].x, textPositions[3].y);
        drawText(graphics2D, String.valueOf((mGame.getIntPreference("GameLength") / 60)) + " mins",textPositions[4].x, textPositions[4].y);
        drawText(graphics2D, String.valueOf("Screen " + mGame.getIntPreference("ScreenType")),textPositions[5].x, textPositions[5].y);

        infoBar.draw(elapsedTime, graphics2D);
    }

    private void drawText(IGraphics2D graphics2D, String text, float x, float y) {
        mPaint.setColor(Color.rgb(47, 120, 175));
        graphics2D.drawText(text, x + 5, y + 5, mPaint);
        mPaint.setColor(Color.WHITE);
        graphics2D.drawText(text, x, y, mPaint);
    }

    /**
     * Gets area occupied by block of text
     * @param paint
     * @param text
     * @return area occupied
     */
    private Rect getTextBounds(Paint paint, String text) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds;
    }

    private Vector2 calculateNewTextPosition(PushButton buttonOne, PushButton buttonTwo, Vector2 textPosition, String text) {
        if(buttonOne == null || buttonTwo == null || textPosition == null) return null;

        float availableWidth = Math.abs(buttonOne.position.x - buttonTwo.position.x) - buttonOne.getBound().halfWidth - buttonTwo.getBound().halfWidth;

        float textWidth = getTextBounds(mPaint, text).width();

        float textX = buttonOne.position.x + buttonOne.getBound().halfWidth + ((availableWidth - textWidth) / 2f);

        return new Vector2(textX, textPosition.y);
    }
}
