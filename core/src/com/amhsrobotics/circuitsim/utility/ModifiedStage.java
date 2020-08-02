package com.amhsrobotics.circuitsim.utility;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ModifiedStage extends Stage {


    public ModifiedStage(FitViewport fitViewport, SpriteBatch batch) {
        super(fitViewport, batch);
    }

    public void addActors(Actor... actors) {
        if(actors.length > 0) {
            for(Actor actor : actors) {
                addActor(actor);
            }
        }
    }
}
