package uk.ac.qub.eeecs.game.cardDemo.screens;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;


/**
 * Created by aedan on 02/11/2017.
 */

public class OptionsScreen extends GameScreen {

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

    private int spacingX = getGame().getScreenWidth() / 5;
    private int spacingY = getGame().getScreenHeight() / 3;

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public OptionsScreen(Game game) {

        super("OptionsScreen", game);

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
        assetManager.loadAndAddBitmap("Increase", "img/difficultyUp.png");
        assetManager.loadAndAddBitmap("MenuButton", "img/menu button.png");
        assetManager.loadAndAddBitmap("Decrease", "img/difficultyDown.png");
        assetManager.loadAndAddBitmap("Background", "img/bernabeu.jpg");


        //create the buttons for the screen
        mMenuButton = new PushButton(spacingX * 1.0f, spacingY * 2.5f, spacingX / 2, spacingY / 2,
                "MenuButton", this );
        mDifficultyUp = new PushButton(spacingX * 2.85f, spacingY * 1f, spacingX / 5, spacingY / 5,
                "Increase", this );
        mDifficultyDown = new PushButton(spacingX * 1.85f, spacingY * 1f, spacingX / 5, spacingY / 5,
                "Decrease", this );
        mTimeUp = new PushButton(spacingX * 2.85f, spacingY * 1.5f, spacingX / 5, spacingY / 5,
                "Increase", this );
        mTimeDown = new PushButton(spacingX * 1.85f, spacingY * 1.5f, spacingX / 5, spacingY / 5,
                "Decrease", this );
        mScreenUp = new PushButton(spacingX * 2.85f, spacingY * 2f, spacingX / 5, spacingY / 5,
                "Increase", this );
        mScreenDown = new PushButton(spacingX * 1.85f, spacingY * 2f, spacingX / 5, spacingY / 5,
                "Decrease", this );

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
            }

            if (mDifficultyUp.isPushTriggered()){
                if(mGame.getStringPreference("Difficulty").equals("Amateur")){
                    mGame.setPreference("Difficulty", "Difficult" );
                }else if (mGame.getStringPreference("Difficulty").equals("Beginner")){
                    mGame.setPreference("Difficulty", "Amateur" );
                }
            }

            int length = (mGame.getIntPreference("GameLength"));
            if (mTimeUp.isPushTriggered()){
               if (length < 360){
                   length += 60;
                   mGame.setPreference("GameLength", length);
               }
            }
            if (mTimeDown.isPushTriggered()){
                if (length > 240){
                    length -= 60;
                    mGame.setPreference("GameLength", length);
                }
            }

            int screenType = mGame.getIntPreference("ScreenType");
            if (mScreenDown.isPushTriggered()){
                if (screenType > 1){
                    screenType--;
                    mGame.setPreference("ScreenType", screenType);
                }
            }
            if (mScreenUp.isPushTriggered()){
                if (screenType < 3){
                    screenType++;
                    mGame.setPreference("ScreenType", screenType);
                }
            }







        }
    }

    /**
     * Remove the current game screen and then change to the specified screen
     *
     * @param screen game screen to become active
     */
    private void changeToScreen(GameScreen screen) {
        mGame.getScreenManager().removeScreen(this.getName());
        mGame.getScreenManager().addScreen(screen);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        Rect rectangle = new Rect(0, 0, this.getGame().getScreenWidth(),
                this.getGame().getScreenHeight());

        graphics2D.clear(Color.WHITE);
        Paint mPaint = mGame.getPaint();
        mPaint.setAlpha(75);
        graphics2D.drawBitmap(mGame.getAssetManager().getBitmap("Background"), null, rectangle, mPaint);
        mPaint.reset();
        mDifficultyUp.draw(elapsedTime, graphics2D);
        mDifficultyDown.draw(elapsedTime, graphics2D);
        mTimeUp.draw(elapsedTime, graphics2D);
        mTimeDown.draw(elapsedTime, graphics2D);
        mScreenUp.draw(elapsedTime, graphics2D);
        mScreenDown.draw(elapsedTime, graphics2D);
        mMenuButton.draw(elapsedTime, graphics2D);


        mPaint.reset();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(75);
        graphics2D.drawText("Difficulty: ",spacingX * 0.5f, spacingY * 1.05f, mPaint );
        graphics2D.drawText("Game Length: ",spacingX * 0.5f, spacingY * 1.55f, mPaint );
        graphics2D.drawText("Game Screen: ",spacingX * 0.5f, spacingY * 2.05f, mPaint );
        graphics2D.drawText(String.valueOf(mGame.getStringPreference("Difficulty")),spacingX * 1.95f, spacingY * 1.05f, mPaint);
        graphics2D.drawText(String.valueOf((mGame.getIntPreference("GameLength") / 60)) + " mins",spacingX * 2.05f, spacingY * 1.55f, mPaint);
        graphics2D.drawText(String.valueOf("Screen " + mGame.getIntPreference("ScreenType")),spacingX * 1.95f, spacingY * 2.05f, mPaint);


        mPaint.setTextSize(150);
        graphics2D.drawText("Options", spacingX * 2f, spacingY * 0.5f, mPaint);

    }


}
