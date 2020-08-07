package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.ObjectType;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.utility.*;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
                if(Constants.placing_object == null && !HardwareManager.movingObject) {
                    float x = Gdx.input.getDeltaX();
                    float y = Gdx.input.getDeltaY();

                    camera.getCamera().translate(-x, y);
                }

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
    }

    @Override
    public void render(float delta) {

        camera.update();
        camera.calculateBounds();

        renderer.setProjectionMatrix(camera.getCamera().combined);
        SnapGrid.renderGrid(renderer, new Color(0/255f, 0/255f, 30/255f, 1), Constants.WORLD_DIM, Constants.GRID_SIZE, 0);

        if(Constants.placing_object != null) {
            HUDrenderer.setColor(Color.RED);
            HUDrenderer.begin(ShapeRenderer.ShapeType.Filled);
            HUDrenderer.rectLine(0, 0, 0, Gdx.graphics.getHeight(), 4);
            HUDrenderer.rectLine(0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 4);
            HUDrenderer.rectLine(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), 0, 4);
            HUDrenderer.rectLine(0, 0, Gdx.graphics.getWidth(), 0, 4);
            HUDrenderer.end();

            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                Constants.placing_object = null;
            }

            if(Constants.placing_object == ObjectType.WIRE) {
                handleCable();
            } else if(Constants.placing_object == ObjectType.WAGO2) {
                handleWago(1);
            } else if(Constants.placing_object == ObjectType.WAGO3) {
                handleWago(2);
            }
        }

        CableManager.update(renderer, batch, camera);
        HardwareManager.update(renderer, batch, camera);
        manager.update(delta);

//        HardwareManager.movingObject = false;
    }

    private void handleWago(int type) {

        Vector2 vec2 = Tools.mouseScreenToWorld(camera);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            if(HardwareManager.currentHardware == null) {
                if(type == 1) {
                    HardwareManager.addDoubleSandCrab(vec2.x, vec2.y);
                } else if(type == 2) {
                    HardwareManager.addTripleSandCrab(vec2.x, vec2.y);
                }
                Constants.placing_object = null;
            }
        }
    }


    private void handleCable() {
        Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.getCamera().unproject(vec);
        Vector2 vec2 = new Vector2(vec.x, vec.y);


        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            if(CableManager.currentCable == null) {
                CableManager.addCable(vec2.x, vec2.y);
                Constants.placing_object = null;
            }
        }
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
