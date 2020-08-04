package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.InputManager;
import com.amhsrobotics.circuitsim.utility.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.SnapGrid;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
    private final ModifiedShapeRenderer HUDrenderer;
    private ClippedCameraController camera;

    Cable cable;

    private CircuitGUIManager manager;

    public CircuitScreen(final Game game) {

        this.game = game;
        this.batch = new SpriteBatch();
        this.renderer = new ModifiedShapeRenderer();
        this.HUDrenderer = new ModifiedShapeRenderer();

        camera = new ClippedCameraController(true);
        camera.getCamera().translate(Constants.WORLD_DIM.x / 2, Constants.WORLD_DIM.y / 2);
        camera.attachCameraSequence(new ArrayList<CameraAction>() {{
            add(Actions.zoomCameraTo(2f, 1f, Interpolation.exp10));
        }});

        stage = new ModifiedStage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        manager = new CircuitGUIManager(stage, camera, game);

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
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    camera.getCamera().translate(0, amount > 0 ? 12.1f : -12.1f);
                } else if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                    camera.getCamera().translate(amount > 0 ? 12.1f : -12.1f, 0);
                } else {
                    camera.getCamera().zoom *= amount > 0 ? 1.05f : 0.95f;
                    if(camera.getCamera().zoom > 3.55) {
                        camera.getCamera().zoom = 3.55f;
                    } else if(camera.getCamera().zoom < 0.2) {
                        camera.getCamera().zoom = 0.2f;
                    }
                }
                return super.scrolled(amount);
            }
        });
        Gdx.input.setInputProcessor(plexer);

        cable = new Cable(0, 0);
    }

    @Override
    public void render(float delta) {

        camera.update();
        camera.calculateBounds();

        renderer.setProjectionMatrix(camera.getCamera().combined);
        SnapGrid.renderGrid(renderer, new Color(0/255f, 0/255f, 30/255f, 1), Constants.WORLD_DIM, Constants.GRID_SIZE, 0);

//        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
//        camera.getCamera().unproject(mousePos);
//        Vector2 mpos = new Vector2(mousePos.x, mousePos.y);
//        SnapGrid.calculateSnap(mpos);
//
//        renderer.begin(ShapeRenderer.ShapeType.Filled);
//        renderer.circle(mpos.x, mpos.y, 10);
//        renderer.end();
        cable.update(renderer, camera, true);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.getCamera().unproject(vec);
            cable.addCoordinates(new Vector2(vec.x, vec.y));
        }

        Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.getCamera().unproject(vec);
        cable.renderHover(renderer, camera, vec.x, vec.y, true);


        if(cable.getPressed(vec.x, vec.y)) {
            Gdx.app.log("", "Stupid.");
        }

        manager.update(delta, HUDrenderer);
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
