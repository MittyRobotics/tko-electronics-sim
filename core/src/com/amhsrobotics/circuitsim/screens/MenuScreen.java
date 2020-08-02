package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.ModifiedStage;
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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import javax.swing.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


public class MenuScreen implements Screen {

    private final ModifiedStage stage;
    private final Game game;
    private final SpriteBatch batch;
    private final ModifiedShapeRenderer renderer;
    private BitmapFont font, font_alternate;
    private TextureAtlas atlas;
    private Skin skin;

    private TextButton new_circuit, import_circuit, credits, contests;
    private Image title;
    FreeTypeFontGenerator generator;

    public MenuScreen(final Game game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.renderer = new ModifiedShapeRenderer();

        this.font = renderFont("font/Abel-Regular.ttf");
        this.font_alternate = new BitmapFont(Gdx.files.internal("font/ari2.fnt"));
        font_alternate.getData().setScale(0.8f);

        stage = new ModifiedStage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        Gdx.input.setInputProcessor(stage);

        atlas = new TextureAtlas(Gdx.files.internal("skin/ui-orange.atlas"));
        skin = new Skin(atlas);

        TextButton.TextButtonStyle tStyle = new TextButton.TextButtonStyle();
        tStyle.font = this.font;
        tStyle.up = skin.getDrawable("button_03");
        tStyle.down = skin.getDrawable("button_02");

        new_circuit = new TextButton("New Circuit", tStyle);
        import_circuit = new TextButton("Import Circuit", tStyle);
        credits = new TextButton("Credits", tStyle);
        contests = new TextButton("Contests", tStyle);

        title = new Image(new Texture(Gdx.files.internal("img/circuitsim.png")));
        title.setPosition((float) Gdx.graphics.getWidth() / 2 - title.getWidth() / 2, Gdx.graphics.getHeight() - 250);

        new_circuit.setPosition((float) Gdx.graphics.getWidth() / 2 - new_circuit.getWidth() / 2, 400);
        import_circuit.setPosition((float) Gdx.graphics.getWidth() / 2 - import_circuit.getWidth() / 2, 330);
        contests.setPosition((float) Gdx.graphics.getWidth() / 2 - contests.getWidth() / 2, 260);
        credits.setPosition((float) Gdx.graphics.getWidth() / 2 - credits.getWidth() / 2, 160);

        new_circuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tools.sequenceSlideOut("right", 1, Interpolation.pow3, 300, 0.2f, contests, import_circuit, new_circuit);
                Tools.sequenceSlideOut("down", 1, Interpolation.pow3, 300, 0.2f, credits);
                Tools.slideOut(title, "top", 2, Interpolation.exp10, 100, new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new CircuitScreen(game));
                    }
                });
            }
        });

        import_circuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tools.sequenceSlideOut("right", 1, Interpolation.pow3, 300, 0.2f, contests, import_circuit, new_circuit);
                Tools.sequenceSlideOut("down", 1, Interpolation.pow3, 300, 0.2f, credits);
                Tools.slideOut(title, "top", 2, Interpolation.exp10, 100, new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new ImportScreen(game));
                    }
                });
            }
        });

        Tools.sequenceSlideIn("left", 1.5f, Interpolation.exp10, 300, 0.4f, new_circuit, import_circuit, contests);
        Tools.slideIn(credits, "down", 1.5f, Interpolation.exp5, 50);
        Tools.slideIn(title, "top", 1.0f, Interpolation.exp5, 300);

        stage.addActors(new_circuit, import_circuit, contests, credits, title);
    }

    private BitmapFont renderFont(String fontfile) {
        FileHandle fontFile = Gdx.files.internal(fontfile);
        generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        return generator.generateFont(parameter);
    }

    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();

//        if(new Rectangle(title.getX(), title.getY(), title.getWidth(), title.getHeight()).contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {;
//        }
    }

    @Override
    public void dispose() {
        renderer.dispose();
        font.dispose();
        stage.dispose();
        generator.dispose();
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