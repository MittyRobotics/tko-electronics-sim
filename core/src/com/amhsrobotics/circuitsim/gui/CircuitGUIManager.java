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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import me.rohanbansal.ricochet.camera.CameraAction;
import me.rohanbansal.ricochet.camera.CameraController;
import me.rohanbansal.ricochet.tools.Actions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.*;

public class CircuitGUIManager implements Disposable {

    private static final LinkedHashMap<String, String> UI_COLORS = new LinkedHashMap<String, String>() {{
        put("Blue", "skin/ui-blue.atlas");
        put("Gray", "skin/ui-gray.atlas");
        put("Green", "skin/ui-green.atlas");
        put("Orange", "skin/ui-orange.atlas");
        put("Red", "skin/ui-red.atlas");
        put("White", "skin/ui-white.atlas");
        put("Yellow", "skin/ui-yellow.atlas");
    }};
    public static Table container, table, table2, filters;
    public static PropertiesBox propertiesBox;
    public static Message popup;
    public static boolean saveMenuShown = false;
    public static boolean panelShown = true;
    public static boolean helpMenuShown, optionsMenuShown, welcomeMenuShown, showWelcomeMenu = true;
    private static Window saveMenu;
    private final ModifiedStage stage;
    private static Simulation sim;
    private final HashMap<TextButton, Boolean> filtersMap = new HashMap<>();
    private TextButton simulate;
    private TextButton back;
    private TextButton help;
    private TextButton options;
    private TextButton hidePanel;
    private TextButton save;
    private TextButton clear, temp;
    private TextButton.TextButtonStyle tStyle, t2Style;
    private TextButton fil1;
    private TextButton fil2;
    private TextButton fil3;
    private TextButton fil4;
    private Window helpMenu, optionsMenu, welcomeMenu;
    private Map<String, LinkedList<TextButton>> reverseMap;
    private TextField gridSizingX, gridSizingY, gridSpacing, fileLocation;
    private TextButton saveButton, fileSave, togGridButton, mColor, sColor;
    private boolean filterChanged = false;
    private boolean addAll = true;
    private Image easter;
    private boolean easterOn = false;
    private final CameraController camera;
    private final Game game;


    public CircuitGUIManager(ModifiedStage stage, final CameraController camera, final Game game) {
        this.stage = stage;

        sim = new Simulation();


        this.camera = camera;
        this.game = game;
        loadThis();

        stage.addActors(welcomeMenu, back, help, helpMenu, optionsMenu, saveMenu, options, hidePanel, save, clear, easter, simulate);

        ConfirmDialog.init(stage);
    }

    public static void saveMenu() {
        saveMenu.setPosition((float) Gdx.graphics.getWidth() / 2 - saveMenu.getWidth() / 2, 250);
        Tools.slideIn(saveMenu, "down", 1f, Interpolation.exp10, 600);
        saveMenuShown = true;
    }

    public static boolean isPanelShown() {
        return panelShown;
    }

    private void loadThis() {

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

        Label.LabelStyle l3Style = new Label.LabelStyle();
        l3Style.font = Constants.FONT_SMALL;
        l3Style.fontColor = Color.RED;

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
        scroll.setScrollingDisabled(true, false);
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

        for (Object o : elementList) {
            TextButton t = new TextButton((String) ((JSONObject) o).get("name"), tStyle);
            t.addListener(new TextTooltip((String) ((JSONObject) o).get("tooltip"), ttStyle));
            reverseMap.get(((JSONObject) o).get("category")).add(t);
            HardwareType buttonType = null;
            for (HardwareType type : HardwareType.values()) {
                if (type.name().equalsIgnoreCase((String) ((JSONObject) o).get("objectName"))) {
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
        scrollFilters.setScrollingDisabled(true, false);
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
                        camera.getCamera().translate(Constants.WORLD_DIM.x / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE - 3, Constants.WORLD_DIM.y / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE - 2);
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
                        welcomeMenuShown = false;
                    }
                });
            }
        });

        help.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (helpMenuShown) {
                    hideHelpMenu();
                } else {
                    showHelpMenu();
                }
            }
        });
        options.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (optionsMenuShown) {
                    hideOptionsMenu();
                } else {
                    showOptionsMenu();
                }
            }
        });
        hidePanel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (CableManager.currentCable != null) {
                    CableManager.currentCable.appendingFromEnd = false;
                    CableManager.currentCable.appendingFromBegin = false;
                    CableManager.currentCable = null;
                }
                HardwareManager.currentHardware = null;
                if (panelShown) {
                    hidePanel();
                } else {
                    showPanel();
                }
            }
        });
        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (FileManager.fileName.equals("")) {
                    CircuitGUIManager.saveMenu();
                } else {
                    FileManager.save(FileManager.fileName);
                }
            }
        });

        simulate.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (sim.isRunning) {
                    for (Hardware h : HardwareManager.getHardware()) {
                        h.stopDrawErrorHover();
                        h.stopDrawGoodHover();
                        h.resetLEDs();
                        h.simLED = false;
                    }
                    popup.removeLabels();
                    HardwareManager.currentHardware = null;
                    CableManager.currentCable = null;
                    propertiesBox.hide();
                    simulate.setText("Simulate");
                    sim.isRunning = false;
                    sim.changed = false;
                    System.gc();
                } else {

                    if (HardwareManager.getHardware() == null || HardwareManager.getHardware().size == 0) {
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
                if (HardwareManager.getHardware().size == 0 && CableManager.getCables().size == 0) {
                    popup.activateError("Nothing to clear");
                } else {
                    ConfirmDialog.createWindow(() -> {
                        HardwareManager.clearHardware();
                        CableManager.clearCables();
                    });
                }
            }
        });


        buildWelcomePanel();
        buildHelpMenu(wStyle, lStyle, l2Style);
        buildSaveMenu(wStyle, l2Style, textFieldStyle, tStyle);
        buildOptionsMenu(wStyle, l2Style, l3Style, textFieldStyle, tStyle);


        Tools.slideIn(back, "left", 0.5f, Interpolation.exp10, 100);
        Tools.sequenceSlideIn("right", 1f, Interpolation.exp10, 100, 0.3f, filters, container);
        Tools.sequenceSlideIn("top", 1f, Interpolation.exp10, 100, 0.2f, save, help, options, clear, hidePanel, simulate);

        if (!welcomeMenuShown && showWelcomeMenu) {
            showWelcomeMenu();
        }
    }

    private void buttonDecline() {
        if (CableManager.currentCable != null) {
            CableManager.currentCable.appendingFromEnd = false;
            CableManager.currentCable.appendingFromBegin = false;
            CableManager.currentCable = null;
        }
        HardwareManager.currentHardware = null;
        propertiesBox.hide();
    }

    private void filterProcess(TextButton fil) {
        if (filtersMap.get(fil)) {
            filtersMap.put(fil, false);
            fil.setStyle(tStyle);
        } else {
            filtersMap.put(fil, true);
            fil.setStyle(t2Style);
        }
        filterChanged = true;
    }

    public void buildWelcomePanel() {
        Window.WindowStyle w5Style = new Window.WindowStyle();
        w5Style.background = Constants.SKIN.getDrawable("window_02");
        w5Style.titleFont = Constants.FONT_MEDIUM;
        w5Style.titleFontColor = Color.WHITE;
        welcomeMenu = new Window("Welcome!", w5Style);
        welcomeMenu.setWidth(800);
        welcomeMenu.setHeight(600);
        welcomeMenu.setKeepWithinStage(false);
        welcomeMenu.setMovable(false);
        welcomeMenu.setPosition(-700, -700);

        Label.LabelStyle lStyle = new Label.LabelStyle();
        lStyle.font = Constants.FONT_MEDIUM;
        lStyle.fontColor = Color.BLACK;

        Label.LabelStyle l2Style = new Label.LabelStyle();
        l2Style.font = Constants.FONT_SMALL;
        l2Style.fontColor = Color.BLACK;

        Label.LabelStyle l3Style = new Label.LabelStyle();
        l3Style.font = Constants.FONT_SMALL;
        l3Style.fontColor = Color.RED;

        Table welcomeTable = new Table();

        ScrollPane.ScrollPaneStyle sStyle = new ScrollPane.ScrollPaneStyle();
        sStyle.vScrollKnob = Constants.SKIN.getDrawable("scroll_back_ver");

        welcomeMenu.row();
        welcomeMenu.add(new Label("", l3Style)).colspan(2);
        welcomeMenu.row();
        welcomeMenu.add(new Label("", l3Style)).colspan(2);
        welcomeMenu.row();

        ScrollPane scroll = new ScrollPane(welcomeTable, sStyle);
        scroll.setScrollingDisabled(true, false);
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


        welcomeTable.add(new Label("Welcome to TKO Circuit Sim!", lStyle)).colspan(2).align(Align.center).padBottom(5);
        welcomeTable.row();
        welcomeTable.add(new Label("(Scroll down)", lStyle)).colspan(2).align(Align.center).padBottom(5);
        welcomeTable.row();
        welcomeTable.add(new Label("Menu Usage: ", lStyle)).colspan(2).align(Align.center).padBottom(5);
        welcomeTable.row();
        Image img = new Image(new Texture("img/main.png"));
        img.setScaling(Scaling.fit);
        welcomeTable.add(img).colspan(2).align(Align.center).padBottom(20).height(320);

        welcomeTable.row();
        welcomeTable.add(new Label("To add components, use the menu on the right. \n" +
                "Click on a button for a component, and click on the grid when you\n" +
                "want to place it. To filter components, use the menu on the top. ", lStyle)).colspan(2).align(Align.left).padBottom(20);

        welcomeTable.row();
        welcomeTable.add(new Label("Many functional buttons are on the top. \n" +
                "You can quit, save your design to a file, open a help menu (with\n" +
                "keybinds), open a settings menu, clear your design, toggle the panel,\n" +
                "and run a wiring/LED simulation to find any problems.", lStyle)).colspan(2).align(Align.left).padBottom(40);

        welcomeTable.row();
        welcomeTable.add(new Label("Hardware Usage: ", lStyle)).colspan(2).align(Align.center).padBottom(5);
        welcomeTable.row();
        Image img2 = new Image(new Texture("img/device.png"));
        img2.setScaling(Scaling.fit);
        welcomeTable.add(img2).colspan(2).align(Align.center).padBottom(20).height(320);

        welcomeTable.row();
        welcomeTable.add(new Label("Once you place a device, click on it to select it.\n" +
                "A menu should appear on the left. You can rotate the device and\n" +
                "look at its connections. You can also drag the device to move it.\n" +
                "Press delete to remove a device and esc to unselect it.", lStyle)).colspan(2).align(Align.left).padBottom(20);

        welcomeTable.row();
        welcomeTable.add(new Label("The salmon-colored dots are wire connectors. \n" +
                "Some already have crimped wires attached. Otherwise, you can\n" +
                "double click to create a wire there. Hover to see details\n" +
                "about the connector.", lStyle)).colspan(2).align(Align.left).padBottom(20);


        welcomeTable.row();
        welcomeTable.add(new Label("Wiring Usage: ", lStyle)).colspan(2).align(Align.center).padBottom(5);
        welcomeTable.row();
        Image img3 = new Image(new Texture("img/wire.png"));
        img3.setScaling(Scaling.fit);
        welcomeTable.add(img3).colspan(2).align(Align.center).padBottom(20).height(320);

        welcomeTable.row();
        welcomeTable.add(new Label("You can double click a node or place one from the menu to create\n" +
                "a wire. You can drag it, extend it by clicking the endpoints, and\n" +
                "delete (with key) or move individual nodes by clicking on them.", lStyle)).colspan(2).align(Align.left).padBottom(20);

        welcomeTable.row();
        welcomeTable.add(new Label("On the top right, there is a menu with many useful functions. \n" +
                "You can change the color and gauge of the wire (disabled for\n" +
                "connected wires), and disconnect either end.\n", lStyle)).colspan(2).align(Align.left).padBottom(20);


        welcomeTable.row();
        welcomeTable.add(new Label("Simulation Usage: ", lStyle)).colspan(2).align(Align.center).padBottom(5);
        welcomeTable.row();
        Image img4 = new Image(new Texture("img/sim.png"));
        img4.setScaling(Scaling.fit);
        welcomeTable.add(img4).colspan(2).align(Align.center).padBottom(20).height(320);

        welcomeTable.row();
        welcomeTable.add(new Label("Click the Simulate button on the top to simulate your diagram.\n" +
                "A green outline will appear around correctly wired components, and\n" +
                "a red one around incorrectly wired ones. Error messages appear\n" +
                "on top. This also simulates LEDs - hover over them for details.", lStyle)).colspan(2).align(Align.left).padBottom(20);


        welcomeTable.row();
        welcomeTable.add(new Label("That's it for the basic usage. If you have any questions or issues,\n" +
                "open an issue on the Github.", lStyle)).colspan(2).align(Align.left).padBottom(5);

        welcomeMenu.add(scroll).colspan(2).expand().fill();
        welcomeMenu.row();
        welcomeMenu.add(new Label(" ", l3Style)).colspan(2);
        welcomeMenu.row();

        TextButton.TextButtonStyle tbStyle = new TextButton.TextButtonStyle();
        tbStyle.font = Constants.FONT_SMALL;
        tbStyle.up = Constants.SKIN.getDrawable("button_03");
        tbStyle.down = Constants.SKIN.getDrawable("button_02");

        TextButton dontshow = new TextButton(" Don't Show Again ", tbStyle);
//        welcomeMenu.add(dontshow).colspan(1).padRight(10).align(Align.right);
        TextButton close = new TextButton(" Close ", tbStyle);
        welcomeMenu.add(close).colspan(2).align(Align.center);

        dontshow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showWelcomeMenu = false;
                hideWelcomeMenu();
            }
        });

        close.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hideWelcomeMenu();
            }
        });
    }

    public void showWelcomeMenu() {
        hideOptionsMenu();
        welcomeMenu.setPosition((float) Gdx.graphics.getWidth() / 2 - welcomeMenu.getWidth() / 2 - 100, 60);
        Tools.slideIn(welcomeMenu, "down", 1f, Interpolation.exp10, 600);
        welcomeMenuShown = true;
    }

    public void hideWelcomeMenu() {
        Tools.slideOut(welcomeMenu, "down", 1f, Interpolation.exp10, 700);
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

        for (Object o : elementList) {
            helpTable.row();
            String id = null;
            for (Object g : ((JSONObject) o).keySet()) {
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

        Table saveTable = new Table();
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
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("TKO Simulator File", "tko");
                        chooser.setAcceptAllFileFilterUsed(false);
                        chooser.setFileFilter(filter);
                        chooser.setSelectedFile(new File("defaultsave.tko"));
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
                if (!fileLocation.getText().contains(".tko")) {
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

    private void buildOptionsMenu(Window.WindowStyle wStyle, Label.LabelStyle l2Style, Label.LabelStyle l3Style, TextField.TextFieldStyle textFieldStyle, TextButton.TextButtonStyle tbStyle) {
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
        optionsTable.add(togglegrid).width(180).padBottom(10);

        togGridButton = new TextButton("Toggle", tbStyle);
        optionsTable.add(togGridButton).width(180).padBottom(10);

        /*optionsTable.row();
        Label warning = new Label("Color schemes are experimental", l3Style);
        warning.setAlignment(Align.center);
        optionsTable.add(warning).width(180).padBottom(10).colspan(2);

        optionsTable.row();
        Label mainColor = new Label("Main Color", l2Style);
        mainColor.setAlignment(Align.center);
        optionsTable.add(mainColor).width(180).padBottom(10);

        mColor = new TextButton("Gray", tbStyle);
        optionsTable.add(mColor).width(180).padBottom(10);

        optionsTable.row();
        Label alternateColor = new Label("Second Color", l2Style);
        alternateColor.setAlignment(Align.center);
        //optionsTable.add(alternateColor).width(180).padBottom(20);

        sColor = new TextButton("Blue", tbStyle);
        //optionsTable.add(sColor).width(180).padBottom(20);*/

        togGridButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SnapGrid.renderGridB = !SnapGrid.renderGridB;
                gridSpacing.setDisabled(!gridSpacing.isDisabled());
                gridSizingX.setDisabled(!gridSizingX.isDisabled());
                gridSizingY.setDisabled(!gridSizingY.isDisabled());
            }
        });

        /*mColor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ArrayList<String> keys = new ArrayList<>(UI_COLORS.keySet());
                for(String str : keys) {
                    if(str.contentEquals(mColor.getText())) {
                        if(keys.indexOf(str) == keys.size() - 1) {
                            mColor.setText(keys.get(0));
//                            Constants.ATLAS = new TextureAtlas(Gdx.files.internal(UI_COLORS.get(keys.get(0))));
                        } else {
                            mColor.setText(keys.get(keys.indexOf(str) + 1));
//                            Constants.ATLAS = new TextureAtlas(Gdx.files.internal(UI_COLORS.get(keys.get(keys.indexOf(str) + 1))));
                        }
//                        Constants.reloadAssets();
//                        removeThis();
//                        loadThis();
                        break;
                    }
                }
            }
        });

        sColor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ArrayList<String> keys = new ArrayList<>(UI_COLORS.keySet());
                for(String str : keys) {
                    if(str.contentEquals(sColor.getText())) {
                        if(keys.indexOf(str) == keys.size() - 1) {
                            sColor.setText(keys.get(0));
//                            Constants.ATLAS_ALTERNATE = new TextureAtlas(Gdx.files.internal(UI_COLORS.get(keys.get(0))));
                        } else {
                            sColor.setText(keys.get(keys.indexOf(str) + 1));
//                            Constants.ATLAS_ALTERNATE = new TextureAtlas(Gdx.files.internal(UI_COLORS.get(keys.get(keys.indexOf(str) + 1))));
                        }
//                        Constants.reloadAssets();
//                        removeThis();
//                        loadThis();
                        break;
                    }
                }
            }
        });*/

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

    private void removeThis() {
        container.remove();
        table.remove();
        table2.remove();
        filters.remove();
        saveMenu.remove();
        simulate.remove();
        back.remove();
        help.remove();
        options.remove();
        hidePanel.remove();
        save.remove();
        clear.remove();
        fil1.remove();
        fil2.remove();
        fil3.remove();
        fil4.remove();
        helpMenu.remove();
        optionsMenu.remove();
        gridSizingX.remove();
        gridSizingY.remove();
        gridSpacing.remove();
        fileLocation.remove();
        saveButton.remove();
        fileSave.remove();
        togGridButton.remove();
        mColor.remove();
        sColor.remove();
        easter.remove();
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
            if (Integer.parseInt(gridSpacing.getText()) < 10) {
                popup.activateError("Minimum spacing of 10 required");
            } else if (Integer.parseInt(gridSpacing.getText()) > 100) {
                popup.activateError("Maximum spacing of 100 required");
            } else {
                Constants.GRID_SIZE = Integer.parseInt(gridSpacing.getText());
            }
            if (Float.parseFloat(gridSizingX.getText()) > 15000) {
                popup.activateError("Maximum X of 15000 required");
            } else if (Float.parseFloat(gridSizingX.getText()) < 2000) {
                popup.activateError("Minimum X of 2000 required");
            } else if (Float.parseFloat(gridSizingY.getText()) < 2000) {
                popup.activateError("Minimum Y of 2000 required");
            } else if (Float.parseFloat(gridSizingY.getText()) > 15000) {
                popup.activateError("Maximum Y of 15000 required");
            } else {
                Constants.WORLD_DIM.set(Float.parseFloat(gridSizingX.getText()), Float.parseFloat(gridSizingY.getText()));
            }

        } catch (Exception e) {
            popup.activateError("Invalid input");
        }
    }

    public void hidePanel() {
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
        if (propertiesBox.isVisible()) {
            propertiesBox.hide();
            propertiesBox.show();
        }
        Tools.sequenceSlideIn("right", 1f, Interpolation.exp5, 300, 0.2f, filters, container);
        Tools.slideIn(simulate, "left", 1f, Interpolation.exp5, -150);
        Tools.slideIn(hidePanel, "left", 1f, Interpolation.exp5, -20);
        Tools.sequenceSlideIn("left", 1f, Interpolation.exp5, 300, 0.2f, clear, options, help, save, back);
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.T) && Gdx.input.isKeyPressed(Input.Keys.K) && Gdx.input.isKeyPressed(Input.Keys.O)) {
            easter.setPosition(100, 0);
            if (!easterOn) {
                Tools.slideIn(easter, "down", 1f, Interpolation.smooth, 165);
                easterOn = true;
            }
        } else {
            if (easterOn) {
                Tools.slideOut(easter, "down", 1f, Interpolation.smooth, 165);
                easterOn = false;
            }
        }

        if (sim.isRunning) {
            if(sim.changed) {
                for (Hardware h : HardwareManager.getHardware()) {
                    h.stopDrawErrorHover();
                    h.stopDrawGoodHover();
                    //h.resetLEDs();
                }
                popup.removeLabels();
                sim.simulate();
            } else {
                popup.removeLabels();
                for (Hardware h : HardwareManager.getHardware()) {
                    if(sim.store.containsKey(h)) {
                        CircuitGUIManager.popup.addLabel(sim.store.get(h), h.getPositionProjected());
                    }
                }
            }
        }


        if (helpMenuShown) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                hideHelpMenu();
            }
        }

        if (optionsMenuShown) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                hideOptionsMenu();
            }
        }

        if (saveMenuShown) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                hideSaveMenu();
            }
        }

        if (welcomeMenuShown) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                hideWelcomeMenu();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SLASH)) {
                showHelpMenu();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
                showOptionsMenu();
            }
        }

        if (filterChanged) {
            table.clearChildren();
            if (filtersMap.get(fil1)) {
                //Wiring
                filterWiring();
            }

            if (filtersMap.get(fil2)) {
                //Control
                filterControl();
            }

            if (filtersMap.get(fil3)) {
                //Motors
                filterMotors();
            }

            if (filtersMap.get(fil4)) {
                //Pneumatics
                filterPneumatics();
            }
        }

        if (addAll) {
            addAll = false;
            table.clear();
            filterWiring();
            filterControl();
            filterMotors();
            filterPneumatics();
        }

        if (!filtersMap.containsValue(true) && filterChanged) {
            addAll = true;
        }

        if (filterChanged) {
            filterChanged = false;
        }

        stage.act(delta);
        stage.draw();
    }

    public void filterWiring() {
        for (TextButton t : reverseMap.get("wiring")) {
            table.row();
            table.add(t).width(150);
        }
        table.row();
    }

    public void filterControl() {
        for (TextButton t : reverseMap.get("control")) {
            table.row();
            table.add(t).width(150);
        }
        table.row();
    }

    public void filterMotors() {
        for (TextButton t : reverseMap.get("motors")) {
            table.row();
            table.add(t).width(150);
        }
        table.row();
    }

    public void filterPneumatics() {
        for (TextButton t : reverseMap.get("pneumatics")) {
            table.row();
            table.add(t).width(150);
        }
        table.row();
    }

    public static Simulation getSim() {
        return sim;
    }

    @Override
    public void dispose() {

    }
}