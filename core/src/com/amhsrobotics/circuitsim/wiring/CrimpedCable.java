package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.SnapGrid;
import com.amhsrobotics.circuitsim.utility.Tuple;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.HashMap;


public class CrimpedCable extends Cable {

    public CrimpedCable(Vector2 startPoint, int id) {
        super(startPoint, id);
        disableEnd = true;
    }

    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        limit = ((1/gauge)*100 + 3f)/2;

        renderer.setProjectionMatrix(camera.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // LOGIC
        // ---------------------------------------------------------------------
        if(nodeChanged) {
            nodeChanged = false;
        }
        disableBegin = connection1 != null;
        disableEnd = true;
        appendingFromBegin = false;
        appendingFromEnd = false;
        // ---------------------------------------------------------------------



        // DRAW CABLE
        // ---------------------------------------------------------------------
        renderer.setColor(color);
        for(int i = 0; i < coordinates.size() - 1; ++i) {
            if(CableManager.currentCable != null) {
                if(CableManager.currentCable == this) {
                    // draw cable selected
                    renderer.setColor(new Color(217/255f, 233/255f, 217/255f, 1));
                    renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), (1/gauge)*140);
                }
            }
            if(hoveringMouse(camera)) {
                // draw hovering on cable
                renderer.setColor(new Color(217 / 255f, 233 / 255f, 217 / 255f, 1));
                renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), (1/gauge)*140);
            }
            // draw actual cable
            renderer.setColor(color);
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), (1/gauge)*100);

            renderer.circle(coordinates.get(i).x, coordinates.get(i).y, limit);
        }
        renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit);
        // ---------------------------------------------------------------------



        // CABLE SELECTED MECHANICS
        // ---------------------------------------------------------------------

        if(CableManager.currentCable == this) {

            // GET X AND Y OF MOUSE
            Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.getCamera().unproject(vec);
            Vector2 vec2 = new Vector2(vec.x, vec.y);

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            // IF MERGING WIRES
            Tuple<Cable, Integer> secondCable = CableManager.wireHoveringWire(camera, this);
            if (secondCable != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                CableManager.mergeCables(this, secondCable.x, secondCable.y == 1, appendingFromBegin);
            } else {

                // UNSELECT
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    CableManager.currentCable = null;
                    appendingFromBegin = false;
                    if (movingNode != null) {
                        coordinates.set(coordinates.indexOf(movingNode), backupNode);
                        movingNode = null;
                        backupNode = null;
                    }
                    CircuitGUIManager.propertiesBox.hide();
                }

                // CLICK
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !CableManager.merging) {
                    HashMap<Hardware, Integer> hardware = HardwareManager.wireHoveringHardware(vec2);

                    if (hardware != null) {
                        // HARDWARE
                        processHardwareClick(hardware);
                    }

                }

                // DRAW NODES IF SELECTED
                drawNodes(renderer, camera, Color.SALMON);

                if (movingNode != null) {
                    movingNode.set(vec2.x, vec2.y);
                    if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL)) {
                        coordinates.remove(movingNode);
                        movingNode = null;
                        backupNode = null;
                    }
                }
            }

        }
        // ---------------------------------------------------------------------

        // HOVERING OVER CABLE
        // ---------------------------------------------------------------------

        if(hoveringMouse(camera)) {
            drawNodes(renderer, camera, Color.SALMON);
            checkForClick(camera);
        }

        // ---------------------------------------------------------------------

        renderer.end();
    }
}