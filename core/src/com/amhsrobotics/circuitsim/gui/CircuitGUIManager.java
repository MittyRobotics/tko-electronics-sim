package com.amhsrobotics.circuitsim.gui;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.screens.MenuScreen;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.input.DigitFilter;
import com.amhsrobotics.circuitsim.utility.scene.ModifiedStage;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import me.rohanbansal.ricochet.camera.CameraAction;
import me.rohanbansal.ricochet.camera.CameraController;
import me.rohanbansal.ricochet.tools.Actions;

import java.util.ArrayList;
import java.util.HashMap;

public class CircuitGUIManager {

    private final ModifiedStage stage;

    private final TextButton back, help, options, hidePanel;
    public static Table container, table, table2, filters;
    private Window helpMenu, optionsMenu;
    private final TextButton.TextButtonStyle tStyle, t2Style;
    private final TextButton fil1, fil2, fil3, fil4;
    private final TextButton reg_cable, sandcrab, sandcrab3, pdp, vrm, rbr, tln, pcm, spk, neo, brk, m775, fcn, battery, rad;

    private final HashMap<TextButton, Boolean> filtersMap = new HashMap<>();
    public static PropertiesBox propertiesBox;
    public static ErrorMessage error;
    private TextField gridSizingX, gridSizingY, gridSpacing;

    public boolean helpMenuShown, optionsMenuShown;
    public static boolean panelShown = true;

    private boolean filterChanged = false;
    private boolean addAll = true;

    public CircuitGUIManager(ModifiedStage stage, final CameraController camera, final Game game) {
        this.stage = stage;

        propertiesBox = new PropertiesBox(stage);
        error = new ErrorMessage(stage);

        tStyle = new TextButton.TextButtonStyle();
        tStyle.font = Constants.FONT_SMALL;
        tStyle.up = Constants.SKIN.getDrawable("button_03");
        tStyle.down = Constants.SKIN.getDrawable("button_02");

        t2Style = new TextButton.TextButtonStyle();
        t2Style.font = Constants.FONT_SMALL;
        t2Style.up = Constants.SKIN_ALTERNATE.getDrawable("button_03");
        t2Style.down = Constants.SKIN_ALTERNATE.getDrawable("button_02");

        Label.LabelStyle lStyle = new Label.LabelStyle();
        lStyle.font = Constants.FONT_SMALL;
        lStyle.fontColor = Color.SALMON;

        Label.LabelStyle l2Style = new Label.LabelStyle();
        l2Style.font = Constants.FONT_SMALL;
        l2Style.fontColor = Color.BLACK;

        TextTooltip.TextTooltipStyle ttStyle = new TextTooltip.TextTooltipStyle();
        ttStyle.background = Constants.SKIN.getDrawable("button_01");
        ttStyle.wrapWidth = 150;
        ttStyle.label = lStyle;

        final Window.WindowStyle wStyle = new Window.WindowStyle();
        wStyle.background = Constants.SKIN.getDrawable("window_02");
        wStyle.titleFont = Constants.FONT_MEDIUM;
        wStyle.titleFontColor = Color.WHITE;

        final TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = Constants.SKIN.getDrawable("textbox_02");
        textFieldStyle.cursor = Constants.SKIN.getDrawable("textbox_cursor_02");
        textFieldStyle.font = Constants.FONT_SMALL;
        textFieldStyle.fontColor = Color.BLACK;

        ScrollPane.ScrollPaneStyle sStyle = new ScrollPane.ScrollPaneStyle();
        sStyle.vScrollKnob = Constants.SKIN.getDrawable("scroll_back_ver");

        container = new Table();
        container.setBackground(Constants.SKIN.getDrawable("textbox_01"));
        container.setWidth(200);
        container.setPosition(Gdx.graphics.getWidth() - 200, 0);
        container.setHeight(Gdx.graphics.getHeight() - 150);
        stage.addActor(container);

        table = new Table();
        ScrollPane scroll = new ScrollPane(table, sStyle);
        scroll.setScrollingDisabled(true,false);
        scroll.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setScrollFocus(scroll);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                stage.setScrollFocus(null);
            }
        });
        table.pad(10).defaults().expandX().space(4);
//        for (int i = 0; i < 100; i++) {
//            table.row();
//
//            Label label = new Label("Device " + (i + 1) + " Image", lStyle);
//            label.setAlignment(Align.center);
//            label.setWrap(true);
//            table.add(label).width(Gdx.graphics.getWidth());
//        }
        reg_cable = new TextButton("Cable", tStyle);
        reg_cable.addListener(new TextTooltip("An adjustable hardware to hardware wire", ttStyle));
        //table.add(reg_cable).width(120);
        sandcrab = new TextButton("2-Way Wago", tStyle);
        sandcrab.addListener(new TextTooltip("A connector that connects two wires together", ttStyle));
        //table.add(sandcrab).width(120);
        sandcrab3 = new TextButton("3-Way Wago", tStyle);
        sandcrab3.addListener(new TextTooltip("A connector with 1 input and 2 outputs", ttStyle));
        battery = new TextButton("Battery", tStyle);
        battery.addListener(new TextTooltip("A 12 Volt battery to power the circuit", ttStyle));
        //table.add(sandcrab3).width(120);
        pdp = new TextButton("PDP", tStyle);
        pdp.addListener(new TextTooltip("Power Distribution Panel", ttStyle));
        //table.add(pdp).width(120);
        vrm = new TextButton("VRM", tStyle);
        vrm.addListener(new TextTooltip("Voltage Regulation Module", ttStyle));
        //table.add(vrm).width(120);
        rbr = new TextButton("roboRIO", tStyle);
        rbr.addListener(new TextTooltip("roboRIO Advanced Robotics Controller", ttStyle));
        //table.add(rbr).width(120);
        tln = new TextButton("Talon", tStyle);
        tln.addListener(new TextTooltip("Talon SRX Smart Motor Controller", ttStyle));
        //table.add(tln).width(120);
        pcm = new TextButton("PCM", tStyle);
        pcm.addListener(new TextTooltip("Pneumatics Control Module", ttStyle));
        //table.add(pcm).width(120);
        spk = new TextButton("SPARK MAX", tStyle);
        spk.addListener(new TextTooltip("SPARK MAX Motor Controller", ttStyle));
        //table.add(spk).width(120);
        neo = new TextButton("NEO", tStyle);
        neo.addListener(new TextTooltip("NEO Brushless Motor", ttStyle));
        //table.add(neo).width(120);
        brk = new TextButton("Breaker", tStyle);
        brk.addListener(new TextTooltip("Main Circuit Breaker", ttStyle));
        //table.add(brk).width(120);
        m775 = new TextButton("775", tStyle);
        m775.addListener(new TextTooltip("775 RedLine Motor", ttStyle));
        //table.add(m775).width(120);
        fcn = new TextButton("Falcon", tStyle);
        fcn.addListener(new TextTooltip("Falcon 500 Brushless Motor", ttStyle));
        //table.add(fcn).width(120);
        rad = new TextButton("Radio", tStyle);
        rad.addListener(new TextTooltip("Radio", ttStyle));

        container.add(scroll).expand().fill();

        reg_cable.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.WIRE;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        sandcrab.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.DOUBLESANDCRAB;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        sandcrab3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.TRIPLESANDCRAB;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        pdp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.PDP;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        vrm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.VRM;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        rbr.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.ROBORIO;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        tln.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.TALON;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        pcm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.PCM;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        spk.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.SPARK;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        neo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.NEO;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
            }
        });
        m775.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.MOTOR775;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        fcn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.FALCON;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        brk.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.BREAKER;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        battery.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.BATTERY;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });
        rad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Constants.placing_object = HardwareType.RADIO;
                CableManager.currentCable = null;
                HardwareManager.currentHardware = null;
                propertiesBox.hide();
            }
        });

        filters = new Table();
        filters.setBackground(Constants.SKIN.getDrawable("textbox_01"));
        filters.setWidth(180);
        filters.setHeight(130);
        filters.setPosition(Gdx.graphics.getWidth() - 190, Gdx.graphics.getHeight() - 140);
        stage.addActor(filters);


        table2 = new Table();
        ScrollPane scrollFilters = new ScrollPane(table2, sStyle);
        scrollFilters.setScrollingDisabled(true,false);
        table2.pad(5).defaults().expandX().space(6);
        fil1 = new TextButton("Wires", tStyle);
        table2.add(fil1).width(70);
        filtersMap.put(fil1, false);
        fil2 = new TextButton("Control", tStyle);
        table2.add(fil2).width(70);
        filtersMap.put(fil2, false);
        table2.row();
        fil3 = new TextButton("Motors", tStyle);
        table2.add(fil3).width(70);
        filtersMap.put(fil3, false);
        fil4 = new TextButton("Pneum.", tStyle);
        fil4.addListener(new TextTooltip("Pneumatics", ttStyle));
        table2.add(fil4).width(70);
        filtersMap.put(fil4, false);
        filters.add(scrollFilters).expand().fill();

        fil1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(filtersMap.get(fil1)) {
                    filtersMap.put(fil1, false);
                    fil1.setStyle(tStyle);
                } else {
                    filtersMap.put(fil1, true);
                    fil1.setStyle(t2Style);
                }
                filterChanged = true;
            }
        });
        fil2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(filtersMap.get(fil2)) {
                    filtersMap.put(fil2, false);
                    fil2.setStyle(tStyle);
                } else {
                    filtersMap.put(fil2, true);
                    fil2.setStyle(t2Style);
                }
                filterChanged = true;
            }
        });
        fil3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(filtersMap.get(fil3)) {
                    filtersMap.put(fil3, false);
                    fil3.setStyle(tStyle);
                } else {
                    filtersMap.put(fil3, true);
                    fil3.setStyle(t2Style);
                }
                filterChanged = true;
            }
        });
        fil4.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(filtersMap.get(fil4)) {
                    filtersMap.put(fil4, false);
                    fil4.setStyle(tStyle);
                } else {
                    filtersMap.put(fil4, true);
                    fil4.setStyle(t2Style);
                }
                filterChanged = true;
            }
        });


        back = new TextButton("Quit", tStyle);
        back.setPosition(20, Gdx.graphics.getHeight() - 70);
        hidePanel = new TextButton("Toggle Panel", tStyle);
        hidePanel.setPosition(260, Gdx.graphics.getHeight() - 70);
        help = new TextButton("Help", tStyle);
        help.setPosition(100, Gdx.graphics.getHeight() - 70);
        options = new TextButton("Options", tStyle);
        options.setPosition(180, Gdx.graphics.getHeight() - 70);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                camera.getCamera().translate(Constants.WORLD_DIM.x / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE-3, Constants.WORLD_DIM.y / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE-2);
                camera.attachCameraSequence(new ArrayList<CameraAction>() {{
                    add(Actions.zoomCameraTo(1f, 1f, Interpolation.exp10));
                }});
                Tools.sequenceSlideOut("top", 0.5f, Interpolation.exp10, 100, 0.15f, hidePanel, options, help);
                Tools.slideOut(back, "left", 0.5f, Interpolation.exp10, 100, new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MenuScreen(game));
                    }
                });
                Tools.sequenceSlideOut("right", 0.5f, Interpolation.exp10, 100, 0.2f, filters, container);
            }
        });
        help.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(helpMenuShown) {
                    hideHelpMenu();
                } else {
                    showHelpMenu();
                }
            }
        });
        options.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(optionsMenuShown) {
                    hideOptionsMenu();
                } else {
                    showOptionsMenu();
                }
            }
        });
        hidePanel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(panelShown) {
                    hidePanel();
                } else {
                    showPanel();
                }
            }
        });

        buildHelpMenu(wStyle, lStyle, l2Style);
        buildOptionsMenu(wStyle, lStyle, l2Style, textFieldStyle);

        Tools.slideIn(back, "left", 0.5f, Interpolation.exp10, 100);
        Tools.sequenceSlideIn("right", 1f, Interpolation.exp10, 100, 0.3f, filters, container);
        Tools.sequenceSlideIn("top", 1f, Interpolation.exp10, 100, 0.3f, help, options, hidePanel);

        stage.addActors(back, help, helpMenu, optionsMenu, options, hidePanel);
    }

    private void buildHelpMenu(Window.WindowStyle wStyle, Label.LabelStyle lStyle, Label.LabelStyle l2Style) {
        helpMenu = new Window("Help", wStyle);
        helpMenu.setWidth(500);
        helpMenu.setHeight(600);
        helpMenu.setKeepWithinStage(false);
        helpMenu.setMovable(false);
        helpMenu.setPosition(-700, -700);

        Table helpTable = new Table();
        helpMenu.add(helpTable).expand().fill();
        helpTable.row();
        helpTable.add(new Label("Keybinds", l2Style)).width(80).colspan(2).align(Align.center);
        helpTable.row();
        helpTable.add(new Label("DELETE", l2Style)).width(80).align(Align.center);
        helpTable.add(new Label("Remove a node or device", lStyle)).width(180).align(Align.center);
        helpTable.row();
        helpTable.add(new Label("LSHIFT", l2Style)).width(80).align(Align.center);
        helpTable.add(new Label("Snap node or device to grid", lStyle)).width(180).align(Align.center);
        helpTable.row();
        helpTable.add(new Label("SCROLL", l2Style)).width(80).align(Align.center);
        helpTable.add(new Label("Zoom in/out grid", lStyle)).width(180).align(Align.center);
        helpTable.row();
        helpTable.add(new Label("SCROLL + LSHIFT", l2Style)).width(155).align(Align.center);
        helpTable.add(new Label("Vertical scroll grid", lStyle)).width(180).align(Align.center);
        helpTable.row();
        helpTable.add(new Label("SCROLL + LALT", l2Style)).width(155).align(Align.center);
        helpTable.add(new Label("Horizontal scroll grid", lStyle)).width(180).align(Align.center);
        helpTable.row();
        helpTable.add(new Label("ESCAPE", l2Style)).width(80).align(Align.center);
        helpTable.add(new Label("Remove focus from device", lStyle)).width(180).align(Align.center);
        helpTable.row();
        helpTable.add(new Label("CTRL + LSHIFT + /", l2Style)).width(155).align(Align.center);
        helpTable.add(new Label("Help Menu", lStyle)).width(180).align(Align.center);
        helpTable.row();
        helpTable.add(new Label("CTRL + LSHIFT + .", l2Style)).width(155).align(Align.center);
        helpTable.add(new Label("Options Menu", lStyle)).width(180).align(Align.center);

        helpMenu.row();
        helpMenu.add(new Label("'Escape' to close window", l2Style)).align(Align.bottom);
    }

    private void buildOptionsMenu(Window.WindowStyle wStyle, Label.LabelStyle lStyle, Label.LabelStyle l2Style, TextField.TextFieldStyle textFieldStyle) {
        optionsMenu = new Window("Options", wStyle);
        optionsMenu.setWidth(500);
        optionsMenu.setHeight(600);
        optionsMenu.setKeepWithinStage(false);
        optionsMenu.setMovable(false);
        optionsMenu.setPosition(-700, -700);

        Table optionsTable = new Table();
        optionsMenu.add(optionsTable).expand().fill();

        optionsTable.row();
        Label spacing = new Label("Grid Spacing", l2Style);
        spacing.setAlignment(Align.center);
        optionsTable.add(spacing).width(180);

        gridSpacing = new TextField(Constants.GRID_SIZE + "", textFieldStyle);
        gridSpacing.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        optionsTable.add(gridSpacing).width(180);

        optionsTable.row();
        Label size = new Label("Grid Size X", l2Style);
        size.setAlignment(Align.center);
        optionsTable.add(size).width(180);

        gridSizingX = new TextField(Constants.WORLD_DIM.x + "", textFieldStyle);
        gridSizingX.setTextFieldFilter(new DigitFilter());
        optionsTable.add(gridSizingX).width(180);

        optionsTable.row();
        Label sizey = new Label("Grid Size Y", l2Style);
        sizey.setAlignment(Align.center);
        optionsTable.add(sizey).width(180);

        gridSizingY = new TextField(Constants.WORLD_DIM.y + "", textFieldStyle);
        gridSizingY.setTextFieldFilter(new DigitFilter());
        optionsTable.add(gridSizingY).width(180);

        optionsMenu.row();
        optionsMenu.add(new Label("'Escape' to close window", l2Style)).align(Align.bottom);
    }

    private void showHelpMenu() {
        hideOptionsMenu();
        helpMenu.setPosition((float) Gdx.graphics.getWidth() / 2 - helpMenu.getWidth() / 2, 100);
        Tools.slideIn(helpMenu, "down", 1f, Interpolation.exp10, 600);
        helpMenuShown = true;
    }

    private void hideHelpMenu() {
        Tools.slideOut(helpMenu, "down", 1f, Interpolation.exp10, 700);
        helpMenuShown = false;
    }

    private void showOptionsMenu() {
        hideHelpMenu();
        optionsMenu.setPosition((float) Gdx.graphics.getWidth() / 2 - helpMenu.getWidth() / 2, 100);
        Tools.slideIn(optionsMenu, "down", 1f, Interpolation.exp10, 600);
        optionsMenuShown = true;
    }

    private void hideOptionsMenu() {
        Tools.slideOut(optionsMenu, "down", 1f, Interpolation.exp10, 700);
        optionsMenuShown = false;

        try {
            if(Integer.parseInt(gridSpacing.getText()) < 10) {
                error.activate("Minimum spacing of 10 required");
            } else if (Integer.parseInt(gridSpacing.getText()) > 100) {
                error.activate("Maximum spacing of 100 required");
            } else {
                Constants.GRID_SIZE = Integer.parseInt(gridSpacing.getText());
            }
            if(Float.parseFloat(gridSizingX.getText()) > 15000) {
                error.activate("Maximum X of 15000 required");
            } else if (Float.parseFloat(gridSizingX.getText()) < 2000) {
                error.activate("Minimum X of 2000 required");
            } else if (Float.parseFloat(gridSizingY.getText()) < 2000) {
                error.activate("Minimum Y of 2000 required");
            } else if(Float.parseFloat(gridSizingY.getText()) > 15000) {
                error.activate("Maximum Y of 15000 required");
            } else {
                Constants.WORLD_DIM.set(Float.parseFloat(gridSizingX.getText()), Float.parseFloat(gridSizingY.getText()));
            }

        } catch (Exception e) {
            error.activate("Invalid input");
        }
    }

    private void hidePanel() {
//        Tools.slideOut(container, "right", 1f, Interpolation.exp10, 300);
        //propertiesBox.container.setPosition(Gdx.graphics.getWidth() - 210, Gdx.graphics.getHeight() - 210);
        Tools.sequenceSlideOut("right", 1f, Interpolation.exp5, 300, 0.2f, container, filters);
        if(propertiesBox.isVisible()) {
            Tools.sequenceSlideOut("right", 1f, Interpolation.exp5In, -210, 0.2f, propertiesBox.container);
        }
        panelShown = false;
    }

    private void showPanel() {
        container.setPosition(Gdx.graphics.getWidth() - 200, 0);
        filters.setPosition(Gdx.graphics.getWidth() - 190, Gdx.graphics.getHeight() - 140);
        Tools.sequenceSlideIn("right", 1f, Interpolation.exp5, 300, 0.2f, filters, container);
        if(propertiesBox.isVisible()) {
            propertiesBox.container.setPosition(Gdx.graphics.getWidth() - 420, Gdx.graphics.getHeight() - 210);
            Tools.sequenceSlideIn("right", 1f, Interpolation.exp5, -210, 0.2f, propertiesBox.container);
        }
        panelShown = true;
    }

    public static boolean isPanelShown() {
        return panelShown;
    }

    public void update(float delta) {


        if(helpMenuShown) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                hideHelpMenu();
            }
        }

        if(optionsMenuShown) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                hideOptionsMenu();
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.SLASH)) {
                showHelpMenu();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
                showOptionsMenu();
            }
        }

        if(filterChanged) {
            table.clearChildren();
            if(filtersMap.get(fil1)){
                //Wiring
                table.row();
                table.add(reg_cable).width(120);
                table.row();
                table.add(sandcrab).width(120);
                table.row();
                table.add(sandcrab3).width(120);
                table.row();
                table.add(battery).width(120);
                table.row();
                table.add(brk).width(120);
            }

            if(filtersMap.get(fil2)){
                //Control
                table.row();
                table.add(rbr).width(120);
                table.row();
                table.add(pdp).width(120);
                table.row();
                table.add(vrm).width(120);
                table.row();
                table.add(rad).width(120);
            }

            if(filtersMap.get(fil3)){
                //Motors
                table.row();
                table.add(tln).width(120);
                table.row();
                table.add(spk).width(120);
                table.row();
                table.add(m775).width(120);
                table.row();
                table.add(neo).width(120);
                table.row();
                table.add(fcn).width(120);
            }

            if(filtersMap.get(fil4)){
                //Pneumatics
                table.row();
                table.add(pcm).width(120);
            }
        }

        if(addAll) {
            addAll = false;
            table.clear();

            table.row();
            table.add(reg_cable).width(120);
            table.row();
            table.add(sandcrab).width(120);
            table.row();
            table.add(sandcrab3).width(120);
            table.row();
            table.add(battery).width(120);
            table.row();
            table.add(brk).width(120);
            table.row();
            table.add(rbr).width(120);
            table.row();
            table.add(pdp).width(120);
            table.row();
            table.add(vrm).width(120);
            table.row();
            table.add(tln).width(120);
            table.row();
            table.add(spk).width(120);
            table.row();
            table.add(m775).width(120);
            table.row();
            table.add(neo).width(120);
            table.row();
            table.add(fcn).width(120);
            table.row();
            table.add(pcm).width(120);
        }

        if(!filtersMap.containsValue(true) && filterChanged) {
            addAll = true;
        }

        if(filterChanged) {
            filterChanged = false;
        }

        stage.act(delta);
        stage.draw();
    }
}
