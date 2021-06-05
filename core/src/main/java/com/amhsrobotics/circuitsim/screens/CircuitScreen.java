package com.amhsrobotics.circuitsim.screens;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.files.FileManager;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.camera.Rumble;
import com.amhsrobotics.circuitsim.utility.input.InputManager;
import com.amhsrobotics.circuitsim.utility.scene.ModifiedStage;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

    private static ModifiedStage stage;
    private final Game game;
    private final SpriteBatch batch;
    public static ModifiedShapeRenderer renderer;
    private final ModifiedShapeRenderer HUDrenderer;
    public static ClippedCameraController camera;
    private static BitmapFont hoverFont = Tools.renderFont("font/Abel-Regular.ttf", 40, true);
    private static String drawString = "";
    private static Vector2 drawLoc = new Vector2();
    private static GlyphLayout layout = new GlyphLayout();

    private Hardware currentPlacingHardware;

    private CircuitGUIManager manager;

    public static boolean selectMultiple, selectedMultiple;
    public Vector2 selectMultiple1, selectMultiple2;
    public static ArrayList<Hardware> selected;
    public static ArrayList<Cable> selectedC;

    static {
        hoverFont.setColor(Color.SALMON);
    }


    public CircuitScreen(final Game game, String... loading) {

        this.game = game;
        this.batch = new SpriteBatch();
        this.renderer = new ModifiedShapeRenderer();
        this.HUDrenderer = new ModifiedShapeRenderer();

        camera = new ClippedCameraController(true);
        camera.getCamera().translate(Constants.WORLD_DIM.x / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE-3, Constants.WORLD_DIM.y / 2 - (Constants.WORLD_DIM.x / 2) % Constants.GRID_SIZE-2);
        camera.attachCameraSequence(new ArrayList<CameraAction>() {{
            add(Actions.zoomCameraTo(3f, 1f, Interpolation.exp10));
        }});

        stage = new ModifiedStage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        manager = new CircuitGUIManager(stage, camera, game);

        InputMultiplexer plexer = new InputMultiplexer(stage, new InputManager() {
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                    Vector2 vec2 = Tools.mouseScreenToWorld(camera);

                    if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                        SnapGrid.calculateSnap(vec2);
                    }
                    if(selectMultiple) {
                        selectMultiple2 = vec2;
                    } else {
                        CableManager.currentCable = null;
                        HardwareManager.currentHardware = null;
                        Constants.placing_object = null;
                        CircuitGUIManager.propertiesBox.hide();

                        selectMultiple = true;
                        selectMultiple1 = vec2;
                        selectMultiple2 = vec2;
                    }
                } else {
                    float x = Gdx.input.getDeltaX() * camera.getCamera().zoom;
                    float y = Gdx.input.getDeltaY() * camera.getCamera().zoom;
                    if(selectedMultiple) {
                        selected = HardwareManager.getSelectedHardware(selectMultiple1, selectMultiple2);
                        selectedC = CableManager.getSelectedCables(selectMultiple1, selectMultiple2);
                        for(Cable c : selectedC) {
                            if(!selected.contains(c.connection1) && !selected.contains(c.connection2)) {
                                c.moveEntireCable(x, -y);
                            }
                        }
                        for(Hardware h : selected) {
                            h.move(x, -y);
                        }
                        selectMultiple1.add(x, -y);
                        selectMultiple2.add(x, -y);

                    } else if (Constants.placing_object == null && !HardwareManager.movingObject && CableManager.currentCable == null && HardwareManager.currentHardware == null) {
                        camera.getCamera().translate(-x, y);
                    }
                }

                return super.touchDragged(screenX, screenY, pointer);
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                if(CircuitGUIManager.welcomeMenuShown) {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                        camera.getCamera().translate(0, amountY > 0 ? 45f : -45f);
                    } else if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                        camera.getCamera().translate(amountY > 0 ? 45f : -45f, 0);
                    } else {

                        Vector3 screenCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

                        Vector3 worldCoordsBefore = camera.getCamera().unproject(new Vector3(screenCoords));

                        camera.getCamera().zoom += amountY * camera.getCamera().zoom * 0.1f;
                        camera.getCamera().update();

                        Vector3 worldCoordsAfter = camera.getCamera().unproject(new Vector3(screenCoords));

                        Vector3 diff = new Vector3(worldCoordsAfter).sub(worldCoordsBefore);
                        camera.getCamera().position.sub(diff);
                        camera.getCamera().update();

                        if (camera.getCamera().zoom > 3.55 * (Constants.WORLD_DIM.x / 5000)) {
                            camera.getCamera().zoom = 3.55f * (Constants.WORLD_DIM.x / 5000);
                        } else if (camera.getCamera().zoom < 0.2) {
                            camera.getCamera().zoom = 0.2f;
                        }

                    }
                }

                return true;
            }
        });
        Gdx.input.setInputProcessor(plexer);

        if(loading.length > 0) {
            FileManager.load(loading[0]);
        }
    }

    @Override
    public void render(float delta) {

        camera.update();
        camera.calculateBounds();

        if (Rumble.getRumbleTimeLeft() > 0){
            Rumble.tick(Gdx.graphics.getDeltaTime());
            camera.getCamera().translate(Rumble.getPos());
        }

        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                if(FileManager.fileName.equals("")) {
                    CircuitGUIManager.saveMenu();
                } else {
                    FileManager.save(FileManager.fileName);
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                    CircuitGUIManager.saveMenu();
                }
            }
        }

        renderer.setProjectionMatrix(camera.getCamera().combined);
        SnapGrid.renderGrid(camera, new Color(0/255f, 0/255f, 30/255f, 1), Constants.WORLD_DIM, Constants.GRID_SIZE, 0);

        Vector2 vec2 = Tools.mouseScreenToWorld(camera);

        if(selectMultiple || selectedMultiple) {
            if(selectMultiple && !(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT))) {
                selectMultiple = false;
                selectedMultiple = true;

                selected = HardwareManager.getSelectedHardware(selectMultiple1, selectMultiple2);
                selectedC = CableManager.getSelectedCables(selectMultiple1, selectMultiple2);
                CircuitGUIManager.popup.activatePrompt(selected.size() + " devices and " + selectedC.size() + " cables selected. DELETE to remove, ESC to deselect, DRAG to move.", 5);
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(new Color(156/255f,1f,150/255f,0.3f));
            renderer.rect(selectMultiple1.x, selectMultiple1.y, selectMultiple2.x- selectMultiple1.x, selectMultiple2.y- selectMultiple1.y);
            renderer.end();

            selected = HardwareManager.getSelectedHardware(selectMultiple1, selectMultiple2);
            selectedC = CableManager.getSelectedCables(selectMultiple1, selectMultiple2);

            for (Hardware h : selected) {
                h.drawHover(renderer);
            }

            for(Cable c : selectedC) {
                c.hover = true;
            }

            if(selectedMultiple) {
                if(Gdx.input.isKeyPressed(Input.Keys.DEL) || Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
                    for(Cable c : selectedC) {
                        HardwareManager.removeCableFromHardware(c, c.connection1);
                        HardwareManager.removeCableFromHardware(c, c.connection2);
                        CableManager.deleteCable(c);
                    }
                    for(Hardware h : selected) {
                        h.delete();
                    }
                    selectedMultiple = false;
                } else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                    selectedMultiple = false;
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            SnapGrid.calculateSnap(vec2);
        }

        if (Constants.placing_object != null) {

            HUDrenderer.setColor(Color.RED);
            HUDrenderer.begin(ShapeRenderer.ShapeType.Filled);
            HUDrenderer.rectLine(0, 0, 0, Gdx.graphics.getHeight(), 6);
            HUDrenderer.rectLine(0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 6);
            HUDrenderer.rectLine(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), 0, 6);
            HUDrenderer.rectLine(0, 0, Gdx.graphics.getWidth(), 0, 6);
            HUDrenderer.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                Constants.placing_object = null;
            }


            if (currentPlacingHardware != null && currentPlacingHardware.type == Constants.placing_object) {
                currentPlacingHardware.setPosition(vec2.x, vec2.y);
            } else {
                if (Constants.placing_object == HardwareType.WIRE || Constants.placing_object == HardwareType.ETHERNET || Constants.placing_object == HardwareType.TUBING || Constants.placing_object == HardwareType.CURVEDCABLE) {
                    drawPlacing(vec2.x, vec2.y);
                } else if (Constants.placing_object != null) {
                    currentPlacingHardware = HardwareManager.switchCaseHardware(Constants.placing_object, vec2.x, vec2.y, false);

                    DeviceUtil.curID.put(Constants.placing_object, DeviceUtil.curID.get(Constants.placing_object) - 1);
                    DeviceUtil.counter--;
                }
            }

            if (Constants.placing_object != HardwareType.WIRE && Constants.placing_object != HardwareType.ETHERNET && Constants.placing_object != HardwareType.TUBING && Constants.placing_object != HardwareType.CURVEDCABLE) {
                handleHardware(Constants.placing_object);
            } else {
                handleCable();
            }

        }


        HardwareManager.updateEplates(renderer, batch, camera);
        CableManager.update(renderer, camera);
        HardwareManager.update(renderer, batch, camera);

        if(CableManager.currentCable != null) {
            if(!CircuitGUIManager.propertiesBox.isVisible()) {
                CircuitGUIManager.propertiesBox.show();
            }
            CableManager.currentCable.render(renderer, camera);
            if(CableManager.currentCable.connection1 != null) {
                HardwareManager.moveToFront(CableManager.currentCable.connection1);
                batch.begin();
                CableManager.currentCable.connection1.getConnector(CableManager.currentCable.connection1.getConnectionPosition(CableManager.currentCable)).draw(batch);
                batch.end();
            }
            if(CableManager.currentCable.connection2 != null) {
                HardwareManager.moveToFront(CableManager.currentCable.connection2);
                batch.begin();
                CableManager.currentCable.connection2.getConnector(CableManager.currentCable.connection2.getConnectionPosition(CableManager.currentCable)).draw(batch);
                batch.end();
            }
        }


        if(Constants.placing_object != null) {
            if (Constants.placing_object == HardwareType.WIRE || Constants.placing_object == HardwareType.ETHERNET || Constants.placing_object == HardwareType.TUBING || Constants.placing_object == HardwareType.CURVEDCABLE) {
                drawPlacing(vec2.x, vec2.y);
            } else if (currentPlacingHardware != null && currentPlacingHardware.type == Constants.placing_object) {
                currentPlacingHardware.updatePosition(camera, renderer, batch);
            }
        }





        manager.update(delta);

        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();
        if(!drawString.equals("")) {
            hoverFont.draw(batch, drawString, drawLoc.x + (-layout.width) / 2, drawLoc.y + 60);
        }
        batch.end();
        drawString = "";

    }

    public String getError(Exception e) {
        StringBuilder ans = new StringBuilder();
        for(StackTraceElement i : e.getStackTrace()) {
            ans.append("Line ").append(i.getLineNumber()).append(" in ").append(i.getClassName()).append(": ").append(i.toString()).append("\n");
        }
        return ans.toString();
    }

    private void handleHardware(HardwareType type) {
        if(CableManager.currentCable != null) {
            CableManager.currentCable.appendingFromEnd = false;
            CableManager.currentCable.appendingFromBegin = false;
            CableManager.currentCable = null;
        }

        Vector2 vec2 = Tools.mouseScreenToWorld(camera);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && ((Gdx.input.getX() <= Gdx.graphics.getWidth() - 200) || !CircuitGUIManager.isPanelShown())) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            HardwareManager.currentHardware = null;
            Constants.placing_object = null;
            HardwareManager.movingObject = false;

            HardwareManager.addHardware(vec2.x, vec2.y, type);

        }
    }

    private void drawPlacing(float x, float y) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.RED);
        renderer.circle(x, y, 5);
        renderer.end();
    }

    public static void setHoverDraw(Vector2 loc, String string) {
        drawString = string;
        drawLoc = loc;
        layout.setText(hoverFont, string);
    }


    private void handleCable() {
        HardwareManager.currentHardware = null;

        Vector2 vec2 = Tools.mouseScreenToWorld(camera);

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && Gdx.input.getX() <= Gdx.graphics.getWidth() - 200) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            if(CableManager.currentCable != null) {
                CableManager.currentCable.appendingFromEnd = false;
                CableManager.currentCable.appendingFromBegin = false;
                CableManager.currentCable = null;
            }

            if(Constants.placing_object == HardwareType.ETHERNET) {
                CableManager.addEthernet(vec2.x, vec2.y);
            } else if (Constants.placing_object == HardwareType.TUBING) {
                CableManager.addTubing(vec2.x, vec2.y);
            } else if (Constants.placing_object == HardwareType.CURVEDCABLE) {
                    CableManager.addCurvedCable(vec2.x, vec2.y);
            } else {
                CableManager.addCable(vec2.x, vec2.y);
            }
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
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void hide() { }
}
