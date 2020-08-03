package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.utility.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.awt.*;
import java.net.URL;


public class MenuScreen implements Screen {

    private final ModifiedStage stage;
    private final Game game;
    private final SpriteBatch batch;
    private final ModifiedShapeRenderer renderer;

    private int height, width;

    private TextButton new_circuit, import_circuit, credits, contests;
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

        Label.LabelStyle lStyle = new Label.LabelStyle();
        lStyle.font = Constants.FONT;
        lStyle.fontColor = Color.SALMON;

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
                Tools.sequenceSlideOut("right", 0.5f, Interpolation.pow3, 300, 0.2f, contests, import_circuit, new_circuit);
                Tools.sequenceSlideOut("down", 0.5f, Interpolation.pow3, 300, 0.2f, credits);
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
                    rohan.setPosition((float) Gdx.graphics.getWidth() / 2 - rohan.getWidth() / 2, 50);
                    andy.setPosition((float) Gdx.graphics.getWidth() / 2 - andy.getWidth() / 2, 20);
                    Tools.sequenceSlideIn("down", 1f, Interpolation.pow3, 100, 0.4f, rohan, andy);
                } else {
                    Tools.sequenceSlideOut("down", 1f, Interpolation.pow3, 100, 0.4f, andy, rohan);
                }
            }
        });

        import_circuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tools.sequenceSlideOut("right", 0.5f, Interpolation.pow3, 300, 0.2f, contests, import_circuit, new_circuit);
                Tools.sequenceSlideOut("down", 0.5f, Interpolation.pow3, 300, 0.2f, credits);
                Tools.slideOut(title, "top", 1.0f, Interpolation.exp10, 100, new Runnable() {
                    @Override
                    public void run() {
                        dispose();
                        game.setScreen(new ImportScreen(game));
                    }
                });
            }
        });

        Tools.sequenceSlideIn("left", 1.0f, Interpolation.exp10, 300, 0.2f, new_circuit, import_circuit, contests);
        Tools.slideIn(credits, "down", 1.0f, Interpolation.exp5, 50);
        Tools.slideIn(title, "top", 0.5f, Interpolation.exp5, 300);

        stage.addActors(new_circuit, import_circuit, credits, title, andy, rohan);
    }

    @Override
    public void render(float delta) {

        this.renderer.begin(ShapeRenderer.ShapeType.Line);

        this.renderer.setColor(0, 0, 30/255f, 1);

        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();

        for(int i = 3; i < width; i+=40) {
            for (int j = 3; j < height; j += 40) {
                this.renderer.line(i, 0, i, height);
                this.renderer.line(0, j, width, j);
            }
        }
        this.renderer.end();

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