package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.files.FileManager;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.scene.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import javax.swing.*;


public class MenuScreen implements Screen {

    private final ModifiedStage stage;
    private final Game game;
    private final SpriteBatch batch;
    private final ModifiedShapeRenderer renderer;

    private TextButton new_circuit, import_circuit, credits, contests;
    private static TextField fileLocation;
    private static Window importMenu;
    private static TextButton importButton, fileSave;
    private Label rohan, andy;
    private Image title;

    private boolean creditsShown = false;

    public MenuScreen(final Game game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.renderer = new ModifiedShapeRenderer();

        stage = new ModifiedStage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        Gdx.input.setInputProcessor(stage);


        TextButton.TextButtonStyle tStyle = new TextButton.TextButtonStyle();
        tStyle.font = Constants.FONT;
        tStyle.up = Constants.SKIN.getDrawable("button_03");
        tStyle.down = Constants.SKIN.getDrawable("button_02");

        TextButton.TextButtonStyle t2Style = new TextButton.TextButtonStyle();
        t2Style.font = Constants.FONT_SMALL;
        t2Style.up = Constants.SKIN.getDrawable("button_03");
        t2Style.down = Constants.SKIN.getDrawable("button_02");

        Label.LabelStyle lStyle = new Label.LabelStyle();
        lStyle.font = Constants.FONT;
        lStyle.fontColor = Color.CYAN;

        final Window.WindowStyle wStyle = new Window.WindowStyle();
        wStyle.background = Constants.SKIN.getDrawable("window_02");
        wStyle.titleFont = Constants.FONT_MEDIUM;
        wStyle.titleFontColor = Color.WHITE;

        final TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = Constants.SKIN.getDrawable("textbox_02");
        textFieldStyle.cursor = Constants.SKIN.getDrawable("textbox_cursor_02");
        textFieldStyle.font = Constants.FONT_SMALL;
        textFieldStyle.fontColor = Color.BLACK;

        Label.LabelStyle l2Style = new Label.LabelStyle();
        l2Style.font = Constants.FONT_SMALL;
        l2Style.fontColor = Color.BLACK;

        new_circuit = new TextButton("   New Circuit   ", tStyle);
        import_circuit = new TextButton(" Import Circuit ", tStyle);
        credits = new TextButton("      Credits      ", tStyle);
        contests = new TextButton("    Contests    ", tStyle);

        rohan = new Label(" Rohan Bansal ", lStyle);
        rohan.setPosition((float) Gdx.graphics.getWidth() / 2 - rohan.getWidth() / 2, -100);
        andy = new Label(" Andy Li ", lStyle);
        andy.setPosition((float) Gdx.graphics.getWidth() / 2 - andy.getWidth() / 2, -100);

        title = new Image(new Texture(Gdx.files.internal("img/logo/circuitsim.png")));
        title.setPosition((float) Gdx.graphics.getWidth() / 2 - title.getWidth() / 2, Gdx.graphics.getHeight() - 250);

        new_circuit.setPosition((float) Gdx.graphics.getWidth() / 2 - new_circuit.getWidth() / 2, 400);
        import_circuit.setPosition((float) Gdx.graphics.getWidth() / 2 - import_circuit.getWidth() / 2, 330);
        contests.setPosition((float) Gdx.graphics.getWidth() / 2 - contests.getWidth() / 2, 260);
        credits.setPosition((float) Gdx.graphics.getWidth() / 2 - credits.getWidth() / 2, 160);

        new_circuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.graphics.setTitle("TKO 1351 Circuit Simulator - Unsaved *");
                Tools.sequenceSlideOut("right", 0.5f, Interpolation.pow3, 300, 0.2f, contests, import_circuit, new_circuit);
                Tools.sequenceSlideOut("down", 0.5f, Interpolation.pow3, 300, 0.2f, credits);
                if(creditsShown) Tools.sequenceSlideOut("down", 1f, Interpolation.pow3, 100, 0.4f, andy, rohan);
                Tools.slideOut(title, "top", 1.0f, Interpolation.exp10, 100, new Runnable() {
                    @Override
                    public void run() {
                        dispose();
                        game.setScreen(new CircuitScreen(game));
                    }
                });
            }
        });

        credits.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                creditsShown = !creditsShown;
                if(creditsShown) {
                    rohan.setPosition((float) Gdx.graphics.getWidth() / 2 - rohan.getWidth() / 2, 46);
                    andy.setPosition((float) Gdx.graphics.getWidth() / 2 - andy.getWidth() / 2, 10);
                    Tools.sequenceSlideIn("down", 1f, Interpolation.pow3, 100, 0.4f, rohan, andy);
                } else {
                    Tools.sequenceSlideOut("down", 1f, Interpolation.pow3, 100, 0.4f, andy, rohan);
                }
            }
        });

        import_circuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                importMenu();
            }
        });

        importMenu = new Window("Import", wStyle);
        importMenu.setWidth(500);
        importMenu.setHeight(400);
        importMenu.setKeepWithinStage(false);
        importMenu.setMovable(false);
        importMenu.setPosition(-700, -700);

        Table importTable = new   Table();
        importMenu.add(importTable).expand().fill();
        importTable.row();
        importTable.add(new Label("Import Project", l2Style)).width(90).colspan(2).padBottom(40).align(Align.center);

        importTable.row();
        importTable.add(new Label("File Location", l2Style)).width(100).align(Align.center);
        fileSave = new TextButton("Browse", t2Style);
        importTable.add(fileSave).width(90);
        importTable.row();
        fileLocation = new TextField("", textFieldStyle);
        importTable.add(fileLocation).width(180).colspan(2).align(Align.center).padTop(10);

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
                        chooser.setDialogTitle("Import");
                        f.dispose();
                        if (res == JFileChooser.APPROVE_OPTION) {
                            fileLocation.setText(chooser.getSelectedFile().getAbsolutePath());
                        }
                    }
                }).start();
            }
        });

        importTable.row();
        importButton = new TextButton("Import", t2Style);
        importTable.add(importButton).width(90).colspan(2).align(Align.center).padTop(60);

        importButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(fileLocation.getText().contains(".")) {
                    hideAndLoadImportMenu();
                }
            }
        });

        importMenu.row();
        importMenu.add(new Label("'Escape' to close window", l2Style)).align(Align.bottom);

        Tools.sequenceSlideIn("left", 1.0f, Interpolation.exp10, 300, 0.2f, new_circuit, import_circuit);
        Tools.slideIn(credits, "down", 1.0f, Interpolation.exp5, 50);
        Tools.slideIn(title, "top", 0.5f, Interpolation.exp5, 300);

        stage.addActors(new_circuit, import_circuit, credits, title, andy, rohan, importMenu);
    }

    public static void importMenu() {
        importMenu.setPosition((float) Gdx.graphics.getWidth() / 2 - importMenu.getWidth() / 2, 250);
        Tools.slideIn(importMenu, "down", 1f, Interpolation.exp10, 600);
    }

    private void hideImportMenu() {
        Tools.slideOut(importMenu, "down", 1f, Interpolation.exp10, 700);
    }

    private void hideAndLoadImportMenu() {
        Tools.sequenceSlideOut("right", 0.5f, Interpolation.pow3, 300, 0.2f, contests, import_circuit, new_circuit);
        Tools.sequenceSlideOut("down", 0.5f, Interpolation.pow3, 300, 0.2f, credits);
        if(creditsShown) Tools.sequenceSlideOut("down", 1f, Interpolation.pow3, 100, 0.4f, andy, rohan);
        Tools.slideOut(title, "top", 1.0f, Interpolation.exp10, 100, new Runnable() {
            @Override
            public void run() {
                dispose();
            }
        });
        Tools.slideOut(importMenu, "down", 1f, Interpolation.exp10, 700);
        FileManager.load(fileLocation.getText());
    }

    @Override
    public void render(float delta) {

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            hideImportMenu();
        }

        SnapGrid.renderGrid(renderer, new Color(0, 0, 30/255f, 1), new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), Constants.GRID_SIZE, 3);

        stage.act(delta);
        stage.draw();
    }

//    public static void openWebpage(String urlString) {
//        try {
//            Desktop.getDesktop().browse(new URL(urlString).toURI());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void dispose() {
        renderer.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) { }
    @Override
    public void show() { }
    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void hide() { }
}