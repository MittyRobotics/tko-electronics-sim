package com.amhsrobotics.circuitsim.utility.scene;

import com.badlogic.gdx.Gdx;
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.unfocusAll();
        Gdx.input.setOnscreenKeyboardVisible(false);
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
