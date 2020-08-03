package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.utility.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class ImportScreen implements Screen {

    private final ModifiedStage stage;
    private final Game game;
    private final SpriteBatch batch;
    private final ModifiedShapeRenderer renderer;

    private TextButton back;

    public ImportScreen(final Game game) {

        this.game = game;
        this.batch = new SpriteBatch();
        this.renderer = new ModifiedShapeRenderer();

        stage = new ModifiedStage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        Gdx.input.setInputProcessor(stage);

        TextButton.TextButtonStyle tStyle = new TextButton.TextButtonStyle();
        tStyle.font = Constants.FONT;
        tStyle.up = Constants.SKIN.getDrawable("button_03");
        tStyle.down = Constants.SKIN.getDrawable("button_02");

        //Add back button

        back = new TextButton(" Back ", tStyle);
        back.setPosition(20, Gdx.graphics.getHeight() - 70);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tools.slideOut(back, "left", 0.5f, Interpolation.exp10, 100, new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MenuScreen(game));
                    }
                });
            }
        });

        Tools.slideIn(back, "left", 0.5f, Interpolation.exp10, 100);

        stage.addActor(back);
    }

    @Override
    public void render(float delta) {
        //Render Grid


        //Stage

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        stage.dispose();
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
