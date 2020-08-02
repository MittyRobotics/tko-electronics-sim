package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.utility.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class ImportScreen implements Screen {

    private final ModifiedStage stage;
    private final Game game;
    private final SpriteBatch batch;
    private final ModifiedShapeRenderer renderer;

    private BitmapFont font;
    private TextureAtlas atlas;
    private Skin skin;

    private TextButton back;
    FreeTypeFontGenerator generator;

    public ImportScreen(final Game game) {

        //Setup stage

        this.game = game;
        this.batch = new SpriteBatch();
        this.renderer = new ModifiedShapeRenderer();
        this.font = renderFont("font/Abel-Regular.ttf");

        atlas = new TextureAtlas(Gdx.files.internal("skin/ui-gray.atlas"));
        skin = new Skin(atlas);

        stage = new ModifiedStage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        Gdx.input.setInputProcessor(stage);

        TextButton.TextButtonStyle tStyle = new TextButton.TextButtonStyle();
        tStyle.font = this.font;
        tStyle.up = skin.getDrawable("button_03");
        tStyle.down = skin.getDrawable("button_02");

        //Add back button

        back = new TextButton(" Back ", tStyle);
        back.setPosition(20, Gdx.graphics.getHeight() - 70);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tools.slideOut(back, "left", 0.2f, Interpolation.exp10, 100, new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MenuScreen(game));
                    }
                });
            }
        });

        Tools.slideIn(back, "left", 0.2f, Interpolation.exp10, 100);

        stage.addActor(back);
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
        //Render Grid

        this.renderer.begin(ShapeRenderer.ShapeType.Line);

        this.renderer.setColor(0, 0, 30/255f, 1);

        for(int i = 10; i < Gdx.graphics.getWidth(); i+=40) {
            for (int j = 10; j < Gdx.graphics.getHeight(); j += 40) {
                this.renderer.line(i, 0, i, Gdx.graphics.getHeight());
                this.renderer.line(0, j, Gdx.graphics.getWidth(), j);
            }
        }
        this.renderer.end();


        //Stage

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        font.dispose();
        stage.dispose();
        generator.dispose();

    }

    @Override
    public void show() { }
    @Override
    public void resize(int width, int height) { }
    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void hide() { }
}
