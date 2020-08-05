package com.amhsrobotics.circuitsim.gui;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.ObjectType;
import com.amhsrobotics.circuitsim.screens.MenuScreen;
import com.amhsrobotics.circuitsim.utility.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import me.rohanbansal.ricochet.camera.CameraAction;
import me.rohanbansal.ricochet.camera.CameraController;
import me.rohanbansal.ricochet.tools.Actions;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;
import java.util.HashMap;

public class CircuitGUIManager {

    private ModifiedStage stage;

    private TextButton back;
    private Table container;

    private HashMap<TextButton, Boolean> filtersMap = new HashMap<>();
    public static PropertiesBox propertiesBox;

    public CircuitGUIManager(ModifiedStage stage, final CameraController camera, final Game game) {
        this.stage = stage;

        propertiesBox = new PropertiesBox(stage);

        final TextButton.TextButtonStyle tStyle = new TextButton.TextButtonStyle();
        tStyle.font = Constants.FONT_SMALL;
        tStyle.up = Constants.SKIN.getDrawable("button_03");
        tStyle.down = Constants.SKIN.getDrawable("button_02");

        final TextButton.TextButtonStyle t2Style = new TextButton.TextButtonStyle();
        t2Style.font = Constants.FONT_SMALL;
        t2Style.up = Constants.SKIN_ALTERNATE.getDrawable("button_03");
        t2Style.down = Constants.SKIN_ALTERNATE.getDrawable("button_02");

        Label.LabelStyle lStyle = new Label.LabelStyle();
        lStyle.font = Constants.FONT_SMALL;
        lStyle.fontColor = Color.SALMON;

        Label.LabelStyle l2Style = new Label.LabelStyle();
        l2Style.font = Constants.FONT_SMALL;
        l2Style.fontColor = Color.BLACK;

        final TextTooltip.TextTooltipStyle ttStyle = new TextTooltip.TextTooltipStyle();
        ttStyle.background = Constants.SKIN.getDrawable("button_01");
        ttStyle.wrapWidth = 150;
        ttStyle.label = lStyle;

        ScrollPane.ScrollPaneStyle sStyle = new ScrollPane.ScrollPaneStyle();
        sStyle.vScrollKnob = Constants.SKIN.getDrawable("scroll_back_ver");

        container = new Table();
        container.setBackground(Constants.SKIN.getDrawable("textbox_01"));
        container.setWidth(200);
        container.setPosition(Gdx.graphics.getWidth() - 200, 0);
        container.setHeight(Gdx.graphics.getHeight() - 150);
        stage.addActor(container);

        Table table = new Table();
        ScrollPane scroll = new ScrollPane(table, sStyle);
        scroll.setScrollingDisabled(true,false);
        table.pad(10).defaults().expandX().space(4);
//        for (int i = 0; i < 100; i++) {
//            table.row();
//
//            Label label = new Label("Device " + (i + 1) + " Image", lStyle);
//            label.setAlignment(Align.center);
//            label.setWrap(true);
//            table.add(label).width(Gdx.graphics.getWidth());
//        }
        table.row();
        TextButton reg_cable = new TextButton("Regular Cable", tStyle);
        reg_cable.addListener(new TextTooltip("A regular hardware to hardware wire", ttStyle));
        table.add(reg_cable).width(120);
        table.row();
        TextButton sandcrab = new TextButton("2-Way Wago", tStyle);
        sandcrab.addListener(new TextTooltip("A connector that connects two wires together", ttStyle));
        table.add(sandcrab).width(120);
        table.row();
        TextButton sandcrab3 = new TextButton("3-Way Wago", tStyle);
        sandcrab3.addListener(new TextTooltip("A connector with 1 input and 2 outputs", ttStyle));
        table.add(sandcrab3).width(120);

        container.add(scroll).expand().fill();

        reg_cable.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = ObjectType.WIRE;
            }
        });
        sandcrab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = ObjectType.WAGO2;
            }
        });
        sandcrab3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = ObjectType.WAGO3;
            }
        });

        final Table filters = new Table();
        filters.setBackground(Constants.SKIN.getDrawable("textbox_01"));
        filters.setWidth(180);
        filters.setHeight(130);
        filters.setPosition(Gdx.graphics.getWidth() - 190, Gdx.graphics.getHeight() - 140);
        stage.addActor(filters);


        Table table2 = new Table();
        ScrollPane scrollFilters = new ScrollPane(table2, sStyle);
        scrollFilters.setScrollingDisabled(true,false);
        table2.pad(5).defaults().expandX().space(6);
        final TextButton fil1 = new TextButton("Elec.", tStyle);
        fil1.addListener(new TextTooltip("Electronics", ttStyle));
        table2.add(fil1).width(70);
        filtersMap.put(fil1, false);
        final TextButton fil2 = new TextButton("Pneum.", tStyle);
        fil2.addListener(new TextTooltip("Pneumatics", ttStyle));
        table2.add(fil2).width(70);
        filtersMap.put(fil2, false);
        table2.row();
        final TextButton fil3 = new TextButton("Wires", tStyle);
        table2.add(fil3).width(70);
        filtersMap.put(fil3, false);
        final TextButton fil4 = new TextButton("Other", tStyle);
        table2.add(fil4).width(70);
        filtersMap.put(fil4, false);
        filters.add(scrollFilters).expand().fill();

        fil1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(filtersMap.get(fil1)) {
                    filtersMap.put(fil1, false);
                    fil1.setStyle(t2Style);
                } else {
                    filtersMap.put(fil1, true);
                    fil1.setStyle(tStyle);
                }
            }
        });
        fil2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(filtersMap.get(fil2)) {
                    filtersMap.put(fil2, false);
                    fil2.setStyle(t2Style);
                } else {
                    filtersMap.put(fil2, true);
                    fil2.setStyle(tStyle);
                }
            }
        });
        fil3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(filtersMap.get(fil3)) {
                    filtersMap.put(fil3, false);
                    fil3.setStyle(t2Style);
                } else {
                    filtersMap.put(fil3, true);
                    fil3.setStyle(tStyle);
                }
            }
        });
        fil4.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(filtersMap.get(fil4)) {
                    filtersMap.put(fil4, false);
                    fil4.setStyle(t2Style);
                } else {
                    filtersMap.put(fil4, true);
                    fil4.setStyle(tStyle);
                }
            }
        });


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
                Tools.sequenceSlideOut("right", 0.5f, Interpolation.exp10, 100, 0.2f, filters, container);
            }
        });

        Tools.slideIn(back, "left", 0.5f, Interpolation.exp10, 100);
        Tools.sequenceSlideIn("right", 1f, Interpolation.exp10, 100, 0.3f, filters, container);

        stage.addActor(back);
    }

    public void update(float delta, ModifiedShapeRenderer renderer) {

//        renderer.begin(ShapeRenderer.ShapeType.Filled);
//        renderer.setColor(Color.DARK_GRAY);
//        renderer.rect(Gdx.graphics.getWidth() - 200, 0, 200, Gdx.graphics.getHeight());
//        renderer.end();

        stage.act(delta);
        stage.draw();
    }
}
