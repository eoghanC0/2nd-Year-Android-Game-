package uk.ac.qub.eeecs.game.cardDemo.screens;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Pack;

/**
 * Created by stephenmcveigh on 07/03/2018.
 */

public class StarterPackScreen extends FootballGameScreen {
    private Pack starterPack;

    public StarterPackScreen(FootballGame game) {
        super("StarterPackScreen", game);
        starterPack = new Pack(this, 11, 5, 0,80);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        starterPack.update(elapsedTime);
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        starterPack.draw(elapsedTime, graphics2D);
    }
}
