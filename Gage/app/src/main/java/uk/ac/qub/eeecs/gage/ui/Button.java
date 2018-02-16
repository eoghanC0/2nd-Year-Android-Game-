package uk.ac.qub.eeecs.gage.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.engine.input.TouchHandler;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.util.InputHelper;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

import static android.content.ContentValues.TAG;

/**
 * Button base class. Provides touch detection for both screen space and layer
 * space buttons.
 *
 * @version 1.0
 */
public abstract class Button extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Specify if the button will be processed in layer space or screen space
     */
    private boolean mProcessInLayerSpace = false;

    /**
     * Track if the button is currently pushed
     */
    private boolean mIsPushed;

    /**
     * Private variable to track the touch position
     */
    private Vector2 touchLocation = new Vector2();

    /**
     * Protected class to manage text property of a Button
     */
    // TODO: ButtonText is WIP
    protected class ButtonText {
        private Button button;
        private String text = "";
        private float textSize = 1;
        private Vector2 textLocation;
        private int colour = Color.BLACK;
        private Paint paint;

        public ButtonText(Button button) {
            this.button = button;
            paint = new Paint();
            paint.setTextSize(textSize);
            paint.setColor(colour);
            textLocation = new Vector2(position.x, position.y);
        }

        public void setButtonText(String text) {
            setButtonText(text, paint.getTextSize(), paint.getColor());
        }

        public void setButtonText(String text, float textSize, int colour) {
            this.text = text;
            this.colour = colour;
            paint = new Paint();
            paint.setTextSize(textSize); paint.setColor(colour);

            Rect textBounds = getTextBounds();

            float textWidth = textBounds.width();
            float textHeight = textBounds.height();

            while(textWidth > button.getBound().getWidth() * 0.9f) {
                paint.setTextSize(paint.getTextSize() - (paint.getTextSize() * 0.05f));
                textBounds = getTextBounds();
                textWidth = textBounds.width();
                textHeight = textBounds.height();
            }

            while(textHeight > button.getBound().getHeight()) {
                paint.setTextSize(paint.getTextSize() - (paint.getTextSize() * 0.05f));
                textBounds = getTextBounds();
                textWidth = textBounds.width();
                textHeight = textBounds.height();
            }

            textLocation = new Vector2();

            textLocation.x = position.x - (textWidth / 2);

            textLocation.y = position.y + (textHeight / 2);
        }

        /**
         * Gets area occupied by block of text
         * @return dimensions of text in the form of a Rect
         */
        private Rect getTextBounds() {
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            return bounds;
        }
    }

    protected ButtonText buttonText;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Setup base Button properties
     *
     * @param x                   Centre y position of the button
     * @param y                   Centre x position of the button
     * @param width               Width of the button
     * @param height              Height of the button
     * @param baseButtonImage     Base bitmap used to represent this button
     * @param processInLayerSpace Specify if the button is to be processed in
     *                            layer space (screen by default)
     * @param gameScreen          Gamescreen to which this control belongs
     */
    public Button(float x, float y, float width, float height,
                  String baseButtonImage,
                  boolean processInLayerSpace,
                  GameScreen gameScreen) {
        super(x, y, width, height,
                gameScreen.getGame().getAssetManager().getBitmap(baseButtonImage),
                gameScreen);
        this.mProcessInLayerSpace = processInLayerSpace;
        buttonText = new ButtonText(this);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Determine if the button is processed in layer space or in screen space.
     * <p>
     * If processed in layer space a valid layer and screen viewport needs to be
     * passed to the update and draw methods. If in screen space the update and
     * draw methods that do not require a viewport parameter can be used.
     *
     * @param processInLayerSpace True if in layer space, false if in screen space.
     */
    public void processInLayerSpace(boolean processInLayerSpace) {
        mProcessInLayerSpace = processInLayerSpace;
    }

    /**
     * Update the button.
     *
     * @param elapsedTime Elapsed time information
     */
    public void update(ElapsedTime elapsedTime) {
        this.update(elapsedTime, null, null);
    }

    /**
     * Update the button.
     *
     * @param elapsedTime    Elapsed time information
     * @param layerViewport  Layer viewport
     * @param screenViewport Screen viewport
     */
    public void update(ElapsedTime elapsedTime,
                       LayerViewport layerViewport, ScreenViewport screenViewport) {
        // Consider any touch events occurring in this update

        Input input = mGameScreen.getGame().getInput();

        List<TouchEvent> touchEvents = useSimulatedTouchEvents ? simulatedTouchEvents : mGameScreen.getGame().getInput().getTouchEvents();

        BoundingBox bound = getBound();

        // Check for a trigger event on this button
        for (TouchEvent touchEvent : touchEvents) {
            getTouchLocation(touchLocation, touchEvent.x, touchEvent.y,
                    layerViewport, screenViewport);
            if (bound.contains(touchLocation.x, touchLocation.y)) {
                // Check if a trigger has occurred and invoke specific behaviour
                updateTriggerActions(touchEvent, touchLocation);
            }
        }

        if(!useSimulatedTouchEvents) {
            // Check for any touch events on this button
            for (int idx = 0; idx < TouchHandler.MAX_TOUCHPOINTS; idx++) {
                if (input.existsTouch(idx)) {
                    getTouchLocation(touchLocation,
                            input.getTouchX(idx), input.getTouchY(idx),
                            layerViewport, screenViewport);
                    if (bound.contains(touchLocation.x, touchLocation.y)) {
                        if (!mIsPushed) {
                            // Record the button has been touched and take button specific behaviour
                            mIsPushed = true;
                            updateTouchActions(touchLocation);
                        }
                        return;
                    }
                }
            }
        }

        // If we have not returned by this point, then there is no touch event on the button
        if (mIsPushed) {
            // Take any default button specific behaviour
            updateDefaultActions();
            mIsPushed = false;
        }
        setButtonText(buttonText.text);
    }

    /**
     * Get the button touch position, converting from screen space to layer
     * space if needed. The touch position is stored within the specified
     * touchLocation vector.
     *
     * @param touchLocation  Touch position instance to be updated by this method.
     * @param x              Touch x screen position
     * @param y              Touch y screen position
     * @param layerViewport  Layer viewport
     * @param screenViewport Screen viewport
     */
    private void getTouchLocation(Vector2 touchLocation, float x, float y,
                                  LayerViewport layerViewport,
                                  ScreenViewport screenViewport) {
        if (!mProcessInLayerSpace) {
            // If in screen space just store the touch position
            touchLocation.x = x;
            touchLocation.y = y;
        } else {
            // If in layer screen convert and store the touch position
            InputHelper.convertScreenPosIntoLayer(screenViewport,
                    x, y, layerViewport, touchLocation);
        }
    }

    /**
     * Check for and undertake trigger actions for the button.
     * <p>
     * A trigger check is undertaken for each touch event that occurs
     * on this button. If detected, then the method will also take
     * appropriate trigger actions.
     *
     * @param touchEvent    Touch event that gave rise to the trigger
     * @param touchLocation Touch position at which the trigger occurred
     */
    protected abstract void updateTriggerActions(
            TouchEvent touchEvent, Vector2 touchLocation);

    /**
     * Undertake touch actions for the button.
     * <p>
     * These actions will be triggered each frame there is at least one
     * touch point on the button.
     *
     * @param touchLocation Touch position at which the trigger occurred
     */
    protected abstract void updateTouchActions(Vector2 touchLocation);

    /**
     * Undertake default actions for the untouched button.
     * <p>
     * The default action is triggered if there is no touch event on
     * the button. It is used to undertake non-touch behaviours, e.g.
     * setting the displayed image to the appropriate non-touch bitmap.
     */
    protected abstract void updateDefaultActions();

    /*
     * (non-Javadoc)
     *
     * @see
     * uk.ac.qub.eeecs.gage.world.GameObject#draw(uk.ac.qub.eeecs.gage.engine
     * .ElapsedTime, uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D,
     * uk.ac.qub.eeecs.gage.world.LayerViewport,
     * uk.ac.qub.eeecs.gage.world.ScreenViewport)
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {

        if (mProcessInLayerSpace) {
            // If in layer space, then determine an appropriate screen space bound
            if (GraphicsHelper.getClippedSourceAndScreenRect(this, layerViewport,
                    screenViewport, drawSourceRect, drawScreenRect)) {
                graphics2D.drawBitmap(mBitmap, drawSourceRect, drawScreenRect, null);

            }
        } else {
            // If in screen space just draw the whole thing
            draw(elapsedTime, graphics2D);
        }

        drawButtonText(elapsedTime, graphics2D);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        super.draw(elapsedTime, graphics2D);

        drawButtonText(elapsedTime, graphics2D);
    }

    /**
     * Called in the draw methods to draw the ButtonText
     * @param elapsedTime
     * @param graphics2D
     */
    public void drawButtonText(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        if(buttonText.text != null) {
            graphics2D.drawText(buttonText.text, buttonText.textLocation.x, buttonText.textLocation.y, buttonText.paint);
        }
    }

    /**
     * Sets the text property of the ButtonText
     * @param text
     */
    public void setButtonText(String text) {
        buttonText.setButtonText(text);
    }

    /**
     * Sets the text, text size and colour of the ButtonText
     * @param text
     * @param textSize
     * @param colour
     */
    public void setButtonText(String text, float textSize, int colour) {
        buttonText.setButtonText(text, textSize, colour);
    }

    /**
     * Sets font size of the ButtonText to the max possible within the bounds of the Button
     */
    public void setButtonTextSizeMax() {
        buttonText.setButtonText(buttonText.text, 1000, buttonText.colour);
    }

    /**
     * Changes position of both the Button and the ButtonText
     * Should be used for animations as opposed to directly modifying the position variable
     * @param x
     * @param y
     */
    public void adjustPosition(float x, float y) {
        position.add(x, y);
        buttonText.textLocation.add(x, y);
    }

    private boolean useSimulatedTouchEvents = false;
    private List<TouchEvent> simulatedTouchEvents = new ArrayList<TouchEvent>();

    public void setSimulatedTouchEvents(List<TouchEvent> simulatedTouchEvents) {
        this.simulatedTouchEvents = simulatedTouchEvents;
    }

    public void setUseSimulatedTouchEvents(boolean useSimulatedTouchEvents) {
        this.useSimulatedTouchEvents = useSimulatedTouchEvents;
    }

}
