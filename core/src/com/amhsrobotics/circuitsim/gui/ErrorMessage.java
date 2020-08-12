package com.amhsrobotics.circuitsim.gui;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.utility.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.Rumble;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import javax.swing.*;

public class ErrorMessage {

    private ModifiedStage stage;


    public final Label.LabelStyle LABEL_SMALL = new Label.LabelStyle();
    private Label label;
    private Table table;

    private boolean visible;

    public ErrorMessage(ModifiedStage stage) {
        this.stage = stage;

        LABEL_SMALL.font = Constants.FONT_SMALL;
        LABEL_SMALL.fontColor = new Color(234/255f, 95/255f, 58/255f, 1);

        label = new Label("", LABEL_SMALL);

        table = new Table();
        table.setBackground(Constants.SKIN.getDrawable("textbox_01"));
        table.add(label);
        table.pack();

        table.setPosition((float) Gdx.graphics.getWidth() / 2 - table.getWidth() / 2, -100);

        stage.addActor(table);
    }

    public void activate(String text) {
        label.setText(text);
        table.setWidth(label.getWidth());
        table.pack();
        show();

        Rumble.rumble(3f, 0.4f);
        Timer timer = new Timer(3000, arg0 -> {
            hide();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void show() {
        table.setPosition((float) Gdx.graphics.getWidth() / 2 - table.getWidth() / 2 - 100, 10);
        Tools.slideIn(table, "down", 0.8f, Interpolation.exp10, 200);
        visible = true;
    }

    public void hide() {
        Tools.slideOut(table, "down", 0.8f, Interpolation.exp10, 200);
        visible = false;
    }

}
