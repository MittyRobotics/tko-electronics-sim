package com.amhsrobotics.circuitsim.gui;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.screens.MenuScreen;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import me.rohanbansal.ricochet.camera.CameraAction;
import me.rohanbansal.ricochet.camera.CameraController;
import me.rohanbansal.ricochet.tools.Actions;

import java.util.ArrayList;

public class CircuitGUIManager {

    private Stage stage;

    private TextButton back;

    public CircuitGUIManager(Stage stage, final CameraController camera, final Game game) {
        this.stage = stage;

        TextButton.TextButtonStyle tStyle = new TextButton.TextButtonStyle();
        tStyle.font = Constants.FONT_SMALL;
        tStyle.up = Constants.SKIN.getDrawable("button_03");
        tStyle.down = Constants.SKIN.getDrawable("button_02");

        //Add back button

        back = new TextButton(" Back ", tStyle);
        back.setPosition(20, Gdx.graphics.getHeight() - 70);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                camera.attachCameraSequence(new ArrayList<CameraAction>() {{
                    add(Actions.zoomCameraTo(1f, 1f, Interpolation.exp10));
                }});
                Tools.slideOut(back, "left", 0.5f, Interpolation.exp10, 100, new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MenuScreen(game));
                    }
                });
            }
        });

        Tools.slideIn(back, "left", 0.5f, Interpolation.exp10, 100);

        stage.addActor(back);
    }

    public void update(float delta) {
        stage.act(delta);
        stage.draw();
    }
}
