package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.InputManager;
import com.amhsrobotics.circuitsim.utility.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import me.rohanbansal.ricochet.camera.CameraAction;
import me.rohanbansal.ricochet.tools.Actions;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;

public class CircuitScreen implements Screen {

    private final ModifiedStage stage;
    private final Game game;
    private final SpriteBatch batch;
    private final ModifiedShapeRenderer renderer;
    private ClippedCameraController camera;

    private TextButton back;

    public CircuitScreen(final Game game) {

        //Setup stage

        this.game = game;
        this.batch = new SpriteBatch();
        this.renderer = new ModifiedShapeRenderer();

        camera = new ClippedCameraController(true);

        camera.getCamera().translate(Constants.WORLD_DIM.x / 2, Constants.WORLD_DIM.y / 2);
        camera.attachCameraSequence(new ArrayList<CameraAction>() {{
            add(Actions.zoomCameraTo(2f, 1f, Interpolation.exp10));
        }});

        stage = new ModifiedStage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);

        TextButton.TextButtonStyle tStyle = new TextButton.TextButtonStyle();
        tStyle.font = Constants.FONT_SMALL;
        tStyle.up = Constants.SKIN.getDrawable("button_03");
        tStyle.down = Constants.SKIN.getDrawable("button_02");

        //Add back button

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
            }
        });

        Tools.slideIn(back, "left", 0.5f, Interpolation.exp10, 100);

        stage.addActor(back);

        InputMultiplexer plexer = new InputMultiplexer(stage, new InputManager() {
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                float x = Gdx.input.getDeltaX();
                float y = Gdx.input.getDeltaY();

                camera.getCamera().translate(-x, y);

                return super.touchDragged(screenX, screenY, pointer);
            }

            @Override
            public boolean scrolled(int amount) {
                camera.getCamera().zoom *= amount > 0 ? 1.05f : 0.95f;
                if(camera.getCamera().zoom > 3.55) {
                    camera.getCamera().zoom = 3.55f;
                } else if(camera.getCamera().zoom < 0.2) {
                    camera.getCamera().zoom = 0.2f;
                }

                return super.scrolled(amount);
            }
        });
        Gdx.input.setInputProcessor(plexer);
    }

    @Override
    public void render(float delta) {

        camera.update();
        camera.calculateBounds();

        renderer.setProjectionMatrix(camera.getCamera().combined);
        this.renderer.setColor(0, 0, 30/255f, 1);

        this.renderer.begin(ShapeRenderer.ShapeType.Line);
        for(int i = 3; i < Constants.WORLD_DIM.x; i += 40) {
            for (int j = 3; j < Constants.WORLD_DIM.y; j += 40) {
                this.renderer.line(i, 0, i, Constants.WORLD_DIM.y);
                this.renderer.line(0, j, Constants.WORLD_DIM.x, j);
            }
        }
        this.renderer.end();


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
