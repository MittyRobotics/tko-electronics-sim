package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.ObjectType;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.*;
import com.amhsrobotics.circuitsim.utility.*;
import com.amhsrobotics.circuitsim.wiring.Cable;
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


    private Cable currentPlacingCable;
    private Hardware currentPlacingHardware;

    private CircuitGUIManager manager;


    public CircuitScreen(final Game game) {


        this.game = game;
        this.batch = new SpriteBatch();
        this.renderer = new ModifiedShapeRenderer();
        this.HUDrenderer = new ModifiedShapeRenderer();

        camera = new ClippedCameraController(true);
        camera.getCamera().translate(Constants.WORLD_DIM.x / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE-3, Constants.WORLD_DIM.y / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE-2);
        camera.attachCameraSequence(new ArrayList<CameraAction>() {{
            add(Actions.zoomCameraTo(2f, 1f, Interpolation.exp10));
        }});

        stage = new ModifiedStage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        manager = new CircuitGUIManager(stage, camera, game);

        InputMultiplexer plexer = new InputMultiplexer(stage, new InputManager() {
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if(Constants.placing_object == null && !HardwareManager.movingObject && CableManager.currentCable == null && HardwareManager.currentHardware == null) {
                    float x = Gdx.input.getDeltaX();
                    float y = Gdx.input.getDeltaY();

                    camera.getCamera().translate(-x, y);
                }

                return super.touchDragged(screenX, screenY, pointer);
            }

            @Override
            public boolean scrolled(int amount) {
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    camera.getCamera().translate(0, amount > 0 ? 45f : -45f);
                } else if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                    camera.getCamera().translate(amount > 0 ? 45f : -45f, 0);
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

            Vector2 vec2 = Tools.mouseScreenToWorld(camera);

            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

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


            if(Gdx.input.getX() <= Gdx.graphics.getWidth() - 200) {

                if (Constants.placing_object == ObjectType.WIRE) {
                    drawPlacing(vec2.x, vec2.y);
                    handleCable();
                } else if (Constants.placing_object == ObjectType.WAGO2) {
                    if(currentPlacingHardware != null && currentPlacingHardware.type == HardwareType.DOUBLESANDCRAB) {
                        currentPlacingHardware.setPosition(vec2.x, vec2.y);
                    } else {
                        currentPlacingHardware = new SandCrab(new Vector2(vec2.x, vec2.y), HardwareType.DOUBLESANDCRAB);
                    }
                    currentPlacingHardware.update(batch, renderer, camera);
                    handleWago(1);
                } else if (Constants.placing_object == ObjectType.WAGO3) {
                    if(currentPlacingHardware != null && currentPlacingHardware.type == HardwareType.TRIPLESANDCRAB) {
                        currentPlacingHardware.setPosition(vec2.x, vec2.y);
                    } else {
                        currentPlacingHardware = new SandCrab(new Vector2(vec2.x, vec2.y), HardwareType.TRIPLESANDCRAB);
                    }
                    currentPlacingHardware.update(batch, renderer, camera);
                    handleWago(2);
                } else if (Constants.placing_object == ObjectType.PDP) {
                    if(currentPlacingHardware != null && currentPlacingHardware.type == HardwareType.PDP) {
                        currentPlacingHardware.setPosition(vec2.x, vec2.y);
                    } else {
                        currentPlacingHardware = new PowerDistributionPanel(new Vector2(vec2.x, vec2.y), HardwareType.PDP);
                    }
                    currentPlacingHardware.update(batch, renderer, camera);
                    handlePDP();
                } else if (Constants.placing_object == ObjectType.VRM) {
                    if(currentPlacingHardware != null && currentPlacingHardware.type == HardwareType.VRM) {
                        currentPlacingHardware.setPosition(vec2.x, vec2.y);
                    } else {
                        currentPlacingHardware = new VoltageRegulatorModule(new Vector2(vec2.x, vec2.y), HardwareType.VRM);
                    }
                    currentPlacingHardware.update(batch, renderer, camera);
                    handleVRM();
                } else if (Constants.placing_object == ObjectType.ROBORIO) {
                    if(currentPlacingHardware != null && currentPlacingHardware.type == HardwareType.ROBORIO) {
                        currentPlacingHardware.setPosition(vec2.x, vec2.y);
                    } else {
                        currentPlacingHardware = new RoboRio(new Vector2(vec2.x, vec2.y), HardwareType.ROBORIO);
                    }
                    currentPlacingHardware.update(batch, renderer, camera);
                    handleRoboRio();
                } else if (Constants.placing_object == ObjectType.TALON) {
                    if(currentPlacingHardware != null && currentPlacingHardware.type == HardwareType.TALON) {
                        currentPlacingHardware.setPosition(vec2.x, vec2.y);
                    } else {
                        currentPlacingHardware = new Talon(new Vector2(vec2.x, vec2.y), HardwareType.TALON);
                    }
                    currentPlacingHardware.update(batch, renderer, camera);
                    handleTalon();
                }
            }

        }

        CableManager.update(renderer, batch, camera);
        HardwareManager.update(renderer, batch, camera);

        manager.update(delta);

    }

    private void handlePDP() {

        CableManager.currentCable = null;

        Vector2 vec2 = Tools.mouseScreenToWorld(camera);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            HardwareManager.currentHardware = null;
            HardwareManager.addPDP(vec2.x, vec2.y, HardwareType.PDP);
            Constants.placing_object = null;
        }
    }

    private void handleVRM() {

        CableManager.currentCable = null;

        Vector2 vec2 = Tools.mouseScreenToWorld(camera);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            HardwareManager.currentHardware = null;
            HardwareManager.addVRM(vec2.x, vec2.y, HardwareType.VRM);
            Constants.placing_object = null;
        }
    }

    private void handleWago(int type) {

        CableManager.currentCable = null;

        Vector2 vec2 = Tools.mouseScreenToWorld(camera);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            HardwareManager.currentHardware = null;
            if(type == 1) {
                HardwareManager.addSandCrab(vec2.x, vec2.y, HardwareType.DOUBLESANDCRAB);
            } else if(type == 2) {
                HardwareManager.addSandCrab(vec2.x, vec2.y, HardwareType.TRIPLESANDCRAB);
            }
            Constants.placing_object = null;

        }

    }

    private void handleRoboRio() {
        CableManager.currentCable = null;

        Vector2 vec2 = Tools.mouseScreenToWorld(camera);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            HardwareManager.currentHardware = null;
            HardwareManager.addRoboRio(vec2.x, vec2.y, HardwareType.ROBORIO);
            Constants.placing_object = null;
        }
    }

    private void handleTalon() {
        CableManager.currentCable = null;

        Vector2 vec2 = Tools.mouseScreenToWorld(camera);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            HardwareManager.currentHardware = null;
            HardwareManager.addTalon(vec2.x, vec2.y, HardwareType.TALON);
            Constants.placing_object = null;
        }
    }

    private void drawPlacing(float x, float y) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.RED);
        renderer.circle(x, y, 5);
        renderer.end();
    }


    private void handleCable() {
        HardwareManager.currentHardware = null;

        Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.getCamera().unproject(vec);
        Vector2 vec2 = new Vector2(vec.x, vec.y);


        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            CableManager.currentCable = null;
            CableManager.addCable(vec2.x, vec2.y);
            Constants.placing_object = null;

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
