package com.amhsrobotics.circuitsim.gui;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.files.FileManager;
import com.amhsrobotics.circuitsim.files.JSONReader;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.screens.MenuScreen;
import com.amhsrobotics.circuitsim.utility.Simulation;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.input.DigitFilter;
import com.amhsrobotics.circuitsim.utility.scene.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CircuitGUIManager {

    public static Table container, table, table2, filters;
    public static PropertiesBox propertiesBox;
    public static Message popup;
    public static boolean saveMenuShown = false;
    public static boolean panelShown = true;
    private static Window saveMenu;
    private final ModifiedStage stage;
    private final Simulation sim;
    private final TextButton simulate, back, help, options, hidePanel, save, clear;
    private final TextButton.TextButtonStyle tStyle, t2Style;
    private final TextButton fil1, fil2, fil3, fil4;
    private final HashMap<TextButton, Boolean> filtersMap = new HashMap<>();
    public boolean helpMenuShown, optionsMenuShown;
    private Window helpMenu, optionsMenu;
    private Map<String, LinkedList<TextButton>> reverseMap;
    private TextField gridSizingX, gridSizingY, gridSpacing, fileLocation;
    private TextButton saveButton, fileSave, togGridButton;
    private boolean filterChanged = false;
    private boolean addAll = true;

    private Image easter;
    private boolean easterOn = false;

    public CircuitGUIManager(ModifiedStage stage, final CameraController camera, final Game game) {
        this.stage = stage;

        sim = new Simulation();
        ConfirmDialog.init(stage);

        propertiesBox = new PropertiesBox(stage);
        popup = new Message(stage);

        easter = new Image(new Texture(Gdx.files.internal("img/logo/robot.png")));
        easter.setPosition(100, -200);

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
        container.setPosition(Gdx.graphics.getWidth() - 210, 10);
        container.setHeight(Gdx.graphics.getHeight() - 160);
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
        container.add(scroll).expand().fill();

        JSONReader.loadConfig("scripts/gui/elements.json");

        reverseMap = new HashMap<String, LinkedList<TextButton>>() {{
            put("wiring", new LinkedList<>());
            put("control", new LinkedList<>());
            put("pneumatics", new LinkedList<>());
            put("motors", new LinkedList<>());
        }};

        JSONArray elementList = (JSONArray) JSONReader.getCurrentConfig().get("elements");

        for(Object o : elementList) {
            TextButton t = new TextButton((String) ((JSONObject) o).get("name"), tStyle);
            t.addListener(new TextTooltip((String) ((JSONObject) o).get("tooltip"), ttStyle));
            reverseMap.get((String) ((JSONObject) o).get("category")).add(t);
            HardwareType buttonType = null;
            for(HardwareType type : HardwareType.values()) {
                if(type.name().equalsIgnoreCase((String) ((JSONObject) o).get("objectName"))) {
                    buttonType = type;
                }
            }
            HardwareType finalButtonType = buttonType;
            t.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Constants.placing_object = finalButtonType;
                    buttonDecline();
                }
            });
        }

        filters = new Table();
        filters.setBackground(Constants.SKIN.getDrawable("textbox_01"));
        filters.setWidth(200);
        filters.setHeight(130);
        filters.setPosition(Gdx.graphics.getWidth() - 210, Gdx.graphics.getHeight() - 140);
        stage.addActor(filters);


        table2 = new Table();
        ScrollPane scrollFilters = new ScrollPane(table2, sStyle);
        scrollFilters.setScrollingDisabled(true,false);
        scrollFilters.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setScrollFocus(scrollFilters);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                stage.setScrollFocus(null);
            }
        });
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
                filterProcess(fil1);
            }
        });
        fil2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                filterProcess(fil2);
            }
        });
        fil3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                filterProcess(fil3);
            }
        });
        fil4.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                filterProcess(fil4);
            }
        });


        back = new TextButton("Quit", tStyle);
        back.setPosition(20, Gdx.graphics.getHeight() - 70);
        save = new TextButton("Save", tStyle);
        save.setPosition(100, Gdx.graphics.getHeight() - 70);
        help = new TextButton("Help", tStyle);
        help.setPosition(180, Gdx.graphics.getHeight() - 70);
        options = new TextButton("Options", tStyle);
        options.setPosition(260, Gdx.graphics.getHeight() - 70);
        hidePanel = new TextButton("Toggle Panel", tStyle);
        hidePanel.setPosition(420, Gdx.graphics.getHeight() - 70);
        simulate = new TextButton("Simulate", tStyle);
        simulate.setPosition(540, Gdx.graphics.getHeight() - 70);
        clear = new TextButton("Clear", tStyle);
        clear.setPosition(340, Gdx.graphics.getHeight() - 70);


        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ConfirmDialog.createWindow(new Runnable() {
                    @Override
                    public void run() {
                        camera.getCamera().translate(Constants.WORLD_DIM.x / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE-3, Constants.WORLD_DIM.y / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE-2);
                        camera.attachCameraSequence(new ArrayList<CameraAction>() {{
                            add(Actions.zoomCameraTo(1f, 1f, Interpolation.exp10));
                        }});
                        Tools.sequenceSlideOut("top", 0.5f, Interpolation.exp10, 100, 0.07f, simulate, hidePanel, clear, options, help, save);
                        Tools.slideOut(back, "left", 0.5f, Interpolation.exp10, 100, new Runnable() {
                            @Override
                            public void run() {
                                HardwareManager.clearHardware();
                                CableManager.clearCables();
                                game.setScreen(new MenuScreen(game));
                            }
                        });
                        Tools.sequenceSlideOut("right", 0.5f, Interpolation.exp10, 100, 0.2f, filters, container);
                    }
                });
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
                if(CableManager.currentCable != null) {
                    CableManager.currentCable.appendingFromEnd = false;
                    CableManager.currentCable.appendingFromBegin = false;
                    CableManager.currentCable = null;
                }
                HardwareManager.currentHardware = null;
                if(panelShown) {
                    hidePanel();
                } else {
                    showPanel();
                }
            }
        });
        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(FileManager.fileName.equals("")) {
                    CircuitGUIManager.saveMenu();
                } else {
                    FileManager.save(FileManager.fileName);
                }
            }
        });
        simulate.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(sim.isRunning) {
                    for(Hardware h : HardwareManager.getHardware()) {
                        h.stopDrawErrorHover();
                        h.stopDrawGoodHover();
                    }
                    popup.removeLabels();
                    HardwareManager.currentHardware = null;
                    CableManager.currentCable = null;
                    propertiesBox.hide();
                    simulate.setText("Simulate");
                    sim.isRunning = false;
                } else {

                    if(HardwareManager.getHardware() == null || HardwareManager.getHardware().size == 0) {
                        CircuitGUIManager.popup.activateError("Nothing to simulate");
                        return;
                    }
                    simulate.setText("Stop");
                    sim.simulate();
                }
            }
        });

        clear.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                ConfirmDialog.createWindow(new Runnable() {
                    @Override
                    public void run() {
                        HardwareManager.clearHardware();
                        CableManager.clearCables();
                    }
                });
            }
        });


        buildHelpMenu(wStyle, lStyle, l2Style);
        buildSaveMenu(wStyle, l2Style, textFieldStyle, tStyle);
        buildOptionsMenu(wStyle, l2Style, textFieldStyle, tStyle);

        Tools.slideIn(back, "left", 0.5f, Interpolation.exp10, 100);
        Tools.sequenceSlideIn("right", 1f, Interpolation.exp10, 100, 0.3f, filters, container);
        Tools.sequenceSlideIn("top", 1f, Interpolation.exp10, 100, 0.2f, save, help, options, clear, hidePanel, simulate);

        stage.addActors(back, help, helpMenu, optionsMenu, saveMenu, options, hidePanel, save, clear, easter);
    }

    public static void saveMenu() {
        saveMenu.setPosition((float) Gdx.graphics.getWidth() / 2 - saveMenu.getWidth() / 2, 250);
        Tools.slideIn(saveMenu, "down", 1f, Interpolation.exp10, 600);
        saveMenuShown = true;
    }

    public static boolean isPanelShown() {
        return panelShown;
    }

    private void buttonDecline() {
        if(CableManager.currentCable != null) {
            CableManager.currentCable.appendingFromEnd = false;
            CableManager.currentCable.appendingFromBegin = false;
            CableManager.currentCable = null;
        }
        HardwareManager.currentHardware = null;
        propertiesBox.hide();
    }

    private void filterProcess(TextButton fil) {
        if(filtersMap.get(fil)) {
            filtersMap.put(fil, false);
            fil.setStyle(tStyle);
        } else {
            filtersMap.put(fil, true);
            fil.setStyle(t2Style);
        }
        filterChanged = true;
    }

    private void buildHelpMenu(Window.WindowStyle wStyle, Label.LabelStyle lStyle, Label.LabelStyle l2Style) {
        helpMenu = new Window("Help", wStyle);
        helpMenu.setWidth(500);
        helpMenu.setHeight(600);
        helpMenu.setKeepWithinStage(false);
        helpMenu.setMovable(false);
        helpMenu.setPosition(-700, -700);
        helpMenu.row();
        helpMenu.add(new Label("", l2Style));
        helpMenu.row();

        JSONReader.loadConfig("scripts/gui/keybinds.json");


        Table helpTable = new Table();

        helpMenu.add(helpTable);

        helpTable.row();
        helpTable.add(new Label("Keybinds & Behaviors", l2Style)).width(180).colspan(2).align(Align.center).padBottom(5);

        JSONArray elementList = (JSONArray) JSONReader.getCurrentConfig().get("binds");

        for(Object o : elementList) {
            helpTable.row();
            String id = null;
            for(Object g : ((JSONObject) o).keySet()) {
                id = (String) g;
            }
            helpTable.add(new Label(id, l2Style)).width(180).align(Align.center);
            helpTable.add(new Label((String) ((JSONObject) o).get(id), lStyle)).width(180).align(Align.center);
        }

        helpMenu.row();
        helpMenu.add(new Label("'Escape' to close window", l2Style)).align(Align.bottom);
    }

    private void buildSaveMenu(Window.WindowStyle wStyle, Label.LabelStyle l2Style, TextField.TextFieldStyle textFieldStyle, TextButton.TextButtonStyle tbStyle) {
        saveMenu = new Window("Save", wStyle);
        saveMenu.setWidth(500);
        saveMenu.setHeight(400);
        saveMenu.setKeepWithinStage(false);
        saveMenu.setMovable(false);
        saveMenu.setPosition(-700, -700);

        Table saveTable = new   Table();
        saveMenu.add(saveTable).expand().fill();
        saveTable.row();
        saveTable.add(new Label("Save Project", l2Style)).width(90).colspan(2).padBottom(40).align(Align.center);

        saveTable.row();
        saveTable.add(new Label("File Location", l2Style)).width(100).align(Align.center);
        fileSave = new TextButton("Browse", tbStyle);
        saveTable.add(fileSave).width(90);
        saveTable.row();
        fileLocation = new TextField("", textFieldStyle);
        saveTable.add(fileLocation).width(180).colspan(2).align(Align.center).padTop(10);

        fileSave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JFileChooser chooser = new JFileChooser();
                        JFrame f = new JFrame();
                        f.setVisible(true);
                        f.toFront();
                        f.setVisible(false);
                        int res = chooser.showSaveDialog(f);
                        f.dispose();
                        if (res == JFileChooser.APPROVE_OPTION) {
                            fileLocation.setText(chooser.getSelectedFile().getAbsolutePath());
                        }
                    }
                }).start();
            }
        });

        saveTable.row();
        saveButton = new TextButton("Save", tbStyle);
        saveTable.add(saveButton).width(90).colspan(2).align(Align.center).padTop(60);

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!fileLocation.getText().contains(".tko")) {
                    fileLocation.setText(fileLocation.getText() + ".tko");
                }
                FileManager.save(fileLocation.getText());
                popup.activatePrompt("Successfully Saved!");
                hideSaveMenu();
            }
        });

        saveMenu.row();
        saveMenu.add(new Label("'Escape' to close window", l2Style)).align(Align.bottom);
    }

    private void hideSaveMenu() {
        Tools.slideOut(saveMenu, "down", 1f, Interpolation.exp10, 700);
        saveMenuShown = false;
    }

    private void buildOptionsMenu(Window.WindowStyle wStyle, Label.LabelStyle l2Style, TextField.TextFieldStyle textFieldStyle, TextButton.TextButtonStyle tbStyle) {
        optionsMenu = new Window("Options", wStyle);
        optionsMenu.setWidth(500);
        optionsMenu.setHeight(600);
        optionsMenu.setKeepWithinStage(false);
        optionsMenu.setMovable(false);
        optionsMenu.setPosition(-700, -700);

        Table optionsTable = new Table();
        optionsMenu.add(optionsTable).expand().fill();

        optionsTable.row();
        Label togglegrid = new Label("View Grid", l2Style);
        togglegrid.setAlignment(Align.center);
        optionsTable.add(togglegrid).width(180).padBottom(20);

        togGridButton = new TextButton("Toggle", tbStyle);
        optionsTable.add(togGridButton).width(180).padBottom(20);

        togGridButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SnapGrid.renderGridB = !SnapGrid.renderGridB;
                gridSpacing.setDisabled(!gridSpacing.isDisabled());
                gridSizingX.setDisabled(!gridSizingX.isDisabled());
                gridSizingY.setDisabled(!gridSizingY.isDisabled());
            }
        });

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
        helpMenu.setPosition((float) Gdx.graphics.getWidth() / 2 - helpMenu.getWidth() / 2, 80);
        Tools.slideIn(helpMenu, "down", 1f, Interpolation.exp10, 600);
        helpMenuShown = true;
    }

    private void hideHelpMenu() {
        Tools.slideOut(helpMenu, "down", 1f, Interpolation.exp10, 700);
        helpMenuShown = false;
    }

    private void showOptionsMenu() {
        hideHelpMenu();
        optionsMenu.setPosition((float) Gdx.graphics.getWidth() / 2 - helpMenu.getWidth() / 2, 80);
        Tools.slideIn(optionsMenu, "down", 1f, Interpolation.exp10, 600);
        optionsMenuShown = true;
    }

    private void hideOptionsMenu() {
        Tools.slideOut(optionsMenu, "down", 1f, Interpolation.exp10, 700);
        optionsMenuShown = false;

        try {
            if(Integer.parseInt(gridSpacing.getText()) < 10) {
                popup.activateError("Minimum spacing of 10 required");
            } else if (Integer.parseInt(gridSpacing.getText()) > 100) {
                popup.activateError("Maximum spacing of 100 required");
            } else {
                Constants.GRID_SIZE = Integer.parseInt(gridSpacing.getText());
            }
            if(Float.parseFloat(gridSizingX.getText()) > 15000) {
                popup.activateError("Maximum X of 15000 required");
            } else if (Float.parseFloat(gridSizingX.getText()) < 2000) {
                popup.activateError("Minimum X of 2000 required");
            } else if (Float.parseFloat(gridSizingY.getText()) < 2000) {
                popup.activateError("Minimum Y of 2000 required");
            } else if(Float.parseFloat(gridSizingY.getText()) > 15000) {
                popup.activateError("Maximum Y of 15000 required");
            } else {
                Constants.WORLD_DIM.set(Float.parseFloat(gridSizingX.getText()), Float.parseFloat(gridSizingY.getText()));
            }

        } catch (Exception e) {
            popup.activateError("Invalid input");
        }
    }

    private void hidePanel() {
        panelShown = false;

        Tools.sequenceSlideOut("right", 1f, Interpolation.exp5, 300, 0.2f, container, filters);
        Tools.sequenceSlideOut("left", 0.5f, Interpolation.exp5, 300, 0.1f, back, save, help, options, clear);
        Tools.slideOut(hidePanel, "left", 1.5f, Interpolation.exp5, -20);
        Tools.slideOut(simulate, "left", 2f, Interpolation.exp5, -150);
        propertiesBox.hide();
    }

    private void showPanel() {
        panelShown = true;
        container.setPosition(Gdx.graphics.getWidth() - 210, 10);
        filters.setPosition(Gdx.graphics.getWidth() - 210, Gdx.graphics.getHeight() - 140);
        hidePanel.setPosition(420, Gdx.graphics.getHeight() - 70);
        simulate.setPosition(540, Gdx.graphics.getHeight() - 70);
        back.setPosition(20, Gdx.graphics.getHeight() - 70);
        save.setPosition(100, Gdx.graphics.getHeight() - 70);
        help.setPosition(180, Gdx.graphics.getHeight() - 70);
        options.setPosition(260, Gdx.graphics.getHeight() - 70);
        clear.setPosition(340, Gdx.graphics.getHeight() - 70);
        if(propertiesBox.isVisible()) {
            propertiesBox.hide();
            propertiesBox.show();
        }
        Tools.sequenceSlideIn("right", 1f, Interpolation.exp5, 300, 0.2f, filters, container);
        Tools.slideIn(simulate, "left", 1f, Interpolation.exp5, -150);
        Tools.slideIn(hidePanel, "left", 1f, Interpolation.exp5, -20);
        Tools.sequenceSlideIn("left", 1f, Interpolation.exp5, 300, 0.2f, clear, options, help, save, back);
    }

    public void update(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.T) && Gdx.input.isKeyPressed(Input.Keys.K) && Gdx.input.isKeyPressed(Input.Keys.O)) {
            easter.setPosition(100, 0);
            if(!easterOn) {
                Tools.slideIn(easter, "down", 1f, Interpolation.smooth, 165);
                easterOn = true;
            }
        } else {
            if(easterOn) {
                Tools.slideOut(easter, "down", 1f, Interpolation.smooth, 165);
                easterOn = false;
            }
        }

        if(sim.isRunning) {
            for(Hardware h : HardwareManager.getHardware()) {
                h.stopDrawErrorHover();
                h.stopDrawGoodHover();
            }
            popup.removeLabels();
            sim.simulate();
        }


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

        if(saveMenuShown) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                hideSaveMenu();
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
                filterWiring();
            }

            if(filtersMap.get(fil2)) {
                //Control
                filterControl();
            }

            if(filtersMap.get(fil3)) {
                //Motors
                filterMotors();
            }

            if(filtersMap.get(fil4)){
                //Pneumatics
                filterPneumatics();
            }
        }

        if(addAll) {
            addAll = false;
            table.clear();
            filterWiring();
            filterControl();
            filterMotors();
            filterPneumatics();
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

    public void filterWiring() {
        for(TextButton t : reverseMap.get("wiring")) {
            table.row();
            table.add(t).width(150);
        }
        table.row();
    }

    public void filterControl() {
        for(TextButton t : reverseMap.get("control")) {
            table.row();
            table.add(t).width(150);
        }
        table.row();
    }

    public void filterMotors() {
        for(TextButton t : reverseMap.get("motors")) {
            table.row();
            table.add(t).width(150);
        }
        table.row();
    }

    public void filterPneumatics() {
        for(TextButton t : reverseMap.get("pneumatics")) {
            table.row();
            table.add(t).width(150);
        }
        table.row();
    }
}