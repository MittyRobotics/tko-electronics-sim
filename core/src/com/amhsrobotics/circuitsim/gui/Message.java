package com.amhsrobotics.circuitsim.gui;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.utility.LinkTimer;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.Rumble;
import com.amhsrobotics.circuitsim.utility.scene.ModifiedStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Message {

    private ModifiedStage stage;


    public final Label.LabelStyle LABEL = new Label.LabelStyle();
    private Label label;
    private Table table;

    private boolean visible;

    public Message(ModifiedStage stage) {
        this.stage = stage;

        LABEL.font = Constants.FONT_SMALL;
        LABEL.fontColor = new Color(247/255f, 66/255f, 18/255f, 1);

        label = new Label("", LABEL);

        table = new Table();
        table.setBackground(Constants.SKIN.getDrawable("textbox_01"));
        table.add(label).pad(5);
        table.pack();

        table.setPosition((float) Gdx.graphics.getWidth() / 2 - table.getWidth() / 2, -100);

        stage.addActor(table);
    }

    public void activateError(String text) {
        label.getStyle().fontColor = new Color(247/255f, 66/255f, 18/255f, 1);
        label.setText(text);
        table.setWidth(label.getWidth());
        table.pack();
        show();

        Rumble.rumble(3f, 0.4f);
        LinkTimer.init(3, this::hide);
    }

    public void activatePrompt(String text) {
        label.getStyle().fontColor = new Color(50/255f, 167/255f, 94/255f, 1);
        label.setText(text);
        table.setWidth(label.getWidth());
        table.pack();
        show();

        LinkTimer.init(3, this::hide);
    }

    public void activatePrompt(String text, int time) {
        label.getStyle().fontColor = new Color(50/255f, 167/255f, 94/255f, 1);
        label.setText(text);
        table.setWidth(label.getWidth());
        table.pack();
        show();

        LinkTimer.init(time, this::hide);
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
