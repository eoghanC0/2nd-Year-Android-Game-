package uk.ac.qub.eeecs.game.cardDemo;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * Created by Inaki on 04/11/2017.
 */

public class Card extends Sprite {

    public Card(float startX, float startY, GameScreen gameScreen) {
        super(startX, startY, 300f, 422f, null, gameScreen);

        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("CardFront", "img/CardFront.png");
        assetManager.loadAndAddBitmap("CardBack", "img/CardBack.png");

        mBitmap = assetManager.getBitmap("CardFront");
    }

}
