package com.amhsrobotics.circuitsim.gui;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.scene.ModifiedStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class PropertiesBox {

    private ModifiedStage stage;
    public Table container;
    private Table table;
    public final TextButton.TextButtonStyle TBUTTON = new TextButton.TextButtonStyle();;
    public final TextButton.TextButtonStyle TBUTTON_ALT = new TextButton.TextButtonStyle();
    public final Label.LabelStyle LABEL = new Label.LabelStyle();
    public final Label.LabelStyle LABEL_SMALL = new Label.LabelStyle();
    public final TextTooltip.TextTooltipStyle TOOLTIP = new TextTooltip.TextTooltipStyle();
    private ScrollPane scroll;

    private boolean visible;

    public boolean hovering;

    public PropertiesBox(ModifiedStage stage) {
        this.stage = stage;

        TBUTTON.font = Constants.FONT_SMALL;
        TBUTTON.up = Constants.SKIN.getDrawable("button_03");
        TBUTTON.down = Constants.SKIN.getDrawable("button_02");

        TBUTTON_ALT.font = Constants.FONT_SMALL;
        TBUTTON_ALT.up = Constants.SKIN_ALTERNATE.getDrawable("button_03");
        TBUTTON_ALT.down = Constants.SKIN_ALTERNATE.getDrawable("button_02");

        LABEL.font = Constants.FONT_MEDIUM;
        LABEL.fontColor = Color.BLACK;

        LABEL_SMALL.font = Constants.FONT_SMALL;
        LABEL_SMALL.fontColor = Color.BLACK;

        TOOLTIP.background = Constants.SKIN.getDrawable("button_01");
        TOOLTIP.wrapWidth = 150;
        TOOLTIP.label = LABEL_SMALL;

        ScrollPane.ScrollPaneStyle sStyle = new ScrollPane.ScrollPaneStyle();
        sStyle.vScrollKnob = Constants.SKIN.getDrawable("scroll_back_ver");

        container = new Table();
        container.setBackground(Constants.SKIN.getDrawable("textbox_01"));
        container.setWidth(200);
        container.setHeight(200);
        container.setPosition(Gdx.graphics.getWidth() - 420, Gdx.graphics.getHeight());
        stage.addActor(container);

        table = new Table();
        scroll = new ScrollPane(table, sStyle);
        scroll.setScrollingDisabled(true,false);
        scroll.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setScrollFocus(scroll);
                hovering = true;
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                stage.setScrollFocus(null);
                hovering = false;
            }
        });
        table.pad(10).defaults().expandX().space(6);
        container.add(scroll).expand().fill();

    }


    public void addElement(Widget widget, boolean newRow, int colspan) {
        if(widget instanceof Label) {
            ((Label) widget).setAlignment(Align.center);
        }
        if(newRow) table.row();
        table.add(widget).width(70).colspan(colspan);
    }

    public void addElement(WidgetGroup widget, boolean newRow, int colspan) {
        if(newRow) table.row();
        table.add(widget).width(70).colspan(colspan);
    }

    public void clearTable() {
        table.clearChildren();
        table.pack();
    }

    public void show() {
        if(!CircuitGUIManager.isPanelShown()) {
            shift(1);
        } else {
            shift(2);
        }
        Tools.slideIn(container, "top", 0.8f, Interpolation.exp10, 200);
        visible = true;
    }

    public void select(int location) {
        scroll.scrollTo(0, 30 + location * 10, scroll.getWidth(), 40 + location * 10);
    }

    public void shift(int direction) {
        if(direction == 1) {
            container.setPosition(Gdx.graphics.getWidth() - 210, Gdx.graphics.getHeight() - 210);
        } else if(direction == 2) {
            container.setPosition(Gdx.graphics.getWidth() - 420, Gdx.graphics.getHeight() - 210);
        }
    }

    public void hide() {
        Tools.slideOut(container, "top", 0.8f, Interpolation.exp10, 200);
        visible = false;
    }

    public void hideAndClear() {
        Tools.slideOut(container, "top", 0.8f, Interpolation.exp10, 200, new Runnable() {
            @Override
            public void run() {
                visible = false;
                table.clearChildren();
                table.pack();
            }
        });
    }

    public boolean isVisible() {
        return visible;
    }
}
