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
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


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
    private Label.LabelStyle lStyle, l2Style;
    private GlyphLayout rohanL = new GlyphLayout(), andyL = new GlyphLayout();

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

        lStyle = new Label.LabelStyle();
        lStyle.font = Constants.FONT;
        lStyle.fontColor = Color.CYAN;

        l2Style = new Label.LabelStyle();
        l2Style.font = Constants.FONT;
        l2Style.fontColor = Color.WHITE;

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

        new_circuit = new TextButton("New Circuit", tStyle);
        new_circuit.setWidth(200);
        import_circuit = new TextButton("Import Circuit", tStyle);
        import_circuit.setWidth(200);
        credits = new TextButton("Credits", tStyle);
        credits.setWidth(200);
        contests = new TextButton("Contests", tStyle);
        contests.setWidth(200);

        rohan = new Label("Rohan Bansal", lStyle);
        rohan.setPosition((float) Gdx.graphics.getWidth() / 2 - rohan.getWidth() / 2, -100);
        rohanL.setText(Constants.FONT, "Rohan Bansal");
        andy = new Label("Andy Li", lStyle);
        andy.setPosition((float) Gdx.graphics.getWidth() / 2 - andy.getWidth() / 2, -100);
        andyL.setText(Constants.FONT, "Andy Li");

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
                new Thread(() -> {
                    JFileChooser chooser = new JFileChooser();
                    JFrame f = new JFrame();
                    f.setVisible(true);
                    f.toFront();
                    f.setVisible(false);
                    int res = chooser.showOpenDialog(f);
                    chooser.setDialogTitle("Import");
                    f.dispose();
                    if (res == JFileChooser.APPROVE_OPTION) {
                        fileLocation.setText(chooser.getSelectedFile().getAbsolutePath());
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
                game.setScreen(new CircuitScreen(game, fileLocation.getText()));
            }
        });
        Tools.slideOut(importMenu, "down", 1f, Interpolation.exp10, 700);
    }

    @Override
    public void render(float delta) {

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            hideImportMenu();
        }

        SnapGrid.renderGrid(renderer, new Color(0, 0, 30/255f, 1), new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), Constants.GRID_SIZE, 3);

        if(creditsShown) {

            if(new Rectangle(
                    Math.round(rohan.getX()),
                    Math.round(rohan.getY()),
                    Math.round(rohanL.width),
                    Math.round(rohanL.height)).contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                rohan.setStyle(l2Style);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                if(Gdx.input.isButtonJustPressed(0)) {
                    openWebpage("https://github.com/Rohan-Bansal");
                }
            } else {
                rohan.setStyle(lStyle);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }

            if(new Rectangle(
                    Math.round(andy.getX()),
                    Math.round(andy.getY()),
                    Math.round(andyL.width),
                    Math.round(andyL.height)).contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                andy.setStyle(l2Style);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                if(Gdx.input.isButtonJustPressed(0)) {
                    openWebpage("https://github.com/AndyLi23");
                }
            } else {
                andy.setStyle(lStyle);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        }

        stage.act(delta);
        stage.draw();
    }

    public static void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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