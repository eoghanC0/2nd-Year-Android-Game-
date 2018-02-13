package uk.ac.qub.eeecs.game.cardDemo.ui;

import android.graphics.Paint;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;

/**
 * Created by eimhin on 12/02/2018.
 */

public class SquadSelectionHolder extends GameObject {
    private Card card;
    private int level;
    private Paint paint = new Paint();

    public SquadSelectionHolder(GameScreen gameScreen) {
        super(gameScreen);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        level = -1;
        mBitmap = gameScreen.getGame().getAssetManager().getBitmap("Empty");
    }

    public void setCard(Card card) {
        this.card = card;
        if(card != null) {
            card.position = position;
            card.setHeight((int) mBound.getHeight());
        }
    }

    public Card getCard() {
        return card;
    }

    public void setLevel(int level) {
        if(level >= 0 && level <=3) this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setHeight(int height) {
        mBound.halfHeight = height / 2f;
        mBound.halfWidth = height * 225/355/2f;
    }

    public void setPosition(float x, float y) {
        setPositionX(x);
        setPositionY(y);
    }

    public void setPosition(Vector2 position) {
        setPositionX(position.x);
        setPositionY(position.y);
    }

    public void setPositionX(float x) {
        position.x = x;
        if(card != null) card.position.x = x;
    }

    public void setPositionY(float y) {
        position.y = y;
        if(card != null) card.position.y = y;
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        super.draw(elapsedTime, graphics2D);

        //Draw the border
        graphics2D.drawRect(mBound.getLeft(), mBound.getBottom(), mBound.getRight(), mBound.getTop(), paint);

        // Draw card if it is not null
        if(card != null)
            card.draw(elapsedTime, graphics2D);
    }

}
