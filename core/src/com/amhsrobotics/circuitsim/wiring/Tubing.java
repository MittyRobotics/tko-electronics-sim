package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.LinkTimer;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.input.Tuple;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.HashMap;

public class Tubing extends Cable {
    public Tubing(Vector2 startPoint, int count) {
        super(startPoint, count);
        gauge = 2;
        color = new Color(0.8f, 0.8f, 0.8f, 0.7f);
        populateProperties();
    }

    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("Tubing - ID " + ID, CircuitGUIManager.propertiesBox.LABEL), true, 2);

    }

    public void render(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        limit = DeviceUtil.GAUGETOLIMIT.get(gauge);
        limit2 = DeviceUtil.GAUGETOLIMIT2.get(gauge);
        limit3 = DeviceUtil.GAUGETOLIMIT3.get(gauge);

        renderer.setProjectionMatrix(camera.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // DRAW CABLE
        // ---------------------------------------------------------------------
        renderer.setColor(color);
        for(int i = 0; i < coordinates.size() - 1; ++i) {
            if(CableManager.currentCable != null) {
                if(CableManager.currentCable == this) {
                    // draw cable selected
                    renderer.setColor(new Color(156/255f,1f,150/255f,0.4f));
                    renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit2);
                }
            }
            if(hoveringMouse(camera)) {
                // draw hovering on cable
                renderer.setColor(new Color(156/255f,1f,150/255f,0.4f));
                renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit2);
            }
            // draw actual cable
            renderer.setColor(color);
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit);

            renderer.circle(coordinates.get(i).x, coordinates.get(i).y, limit/2);
        }

        // ---------------------------------------------------------------------


        // CABLE SELECTED MECHANICS
        // ---------------------------------------------------------------------

        if(CableManager.currentCable == this) {

            Vector2 vec2 = Tools.mouseScreenToWorld(camera);

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            // RENDER POSSIBLE IF MERGING
            if(CableManager.merging) {
                if(appendingFromBegin) {
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit3);
                } else {
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit3);
                }
            } else {
                // DRAW NODES IF SELECTED
                drawNodes(renderer, camera, Color.SALMON);

                if (appendingFromEnd && !disableEnd) {
                    // draw potential cable wire
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit3);
                } else if (appendingFromBegin && !disableBegin){
                    // draw potential cable wire
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit3);
                }
            }

        }

        renderer.setColor(Color.SALMON);
        renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit3);
        renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit3);

        if(hoveringMouse(camera)) {
            drawNodes(renderer, camera, Color.SALMON);
        }

        renderer.end();
    }

    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        limit = DeviceUtil.GAUGETOLIMIT.get(gauge);
        limit2 = DeviceUtil.GAUGETOLIMIT2.get(gauge);
        limit3 = DeviceUtil.GAUGETOLIMIT3.get(gauge);

        renderer.setProjectionMatrix(camera.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // LOGIC
        // ---------------------------------------------------------------------
        if(nodeChanged) {
            nodeChanged = false;
        }
        disableBegin = connection1 != null;
        disableEnd = connection2 != null;
        // ---------------------------------------------------------------------


        // DRAW CABLE
        // ---------------------------------------------------------------------
        renderer.setColor(color);
        for(int i = 0; i < coordinates.size() - 1; ++i) {
            if(CableManager.currentCable != null) {
                if(CableManager.currentCable == this) {
                    // draw cable selected
                    renderer.setColor(new Color(156/255f,1f,150/255f,0.4f));
                    renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit2);
                }
            }
            if(hoveringMouse(camera)) {
                // draw hovering on cable
                renderer.setColor(new Color(156/255f,1f,150/255f,0.4f));
                renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit2);
            }
            // draw actual cable
            renderer.setColor(color);
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit);

            renderer.circle(coordinates.get(i).x, coordinates.get(i).y, limit/2);
        }
        renderer.setColor(Color.SALMON);
        drawEndpoints(renderer);
        // ---------------------------------------------------------------------



        // CABLE SELECTED MECHANICS
        // ---------------------------------------------------------------------

        if(CableManager.currentCable == this) {

            CableManager.moveToFront(this);

            Vector2 vec2 = Tools.mouseScreenToWorld(camera);

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            // RENDER POSSIBLE IF MERGING
            if(CableManager.merging) {
                if(appendingFromBegin) {
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit3);
                } else {
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit3);
                }

            }

            // IF MERGING WIRES
            Tuple<Cable, Integer> secondCable = CableManager.wireHoveringWire(camera, this);
            if (secondCable != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && ((Gdx.input.getX() <= Gdx.graphics.getWidth() - 200) || !CircuitGUIManager.isPanelShown())) {
                CableManager.mergeCables(this, secondCable.x, secondCable.y == 1, appendingFromBegin);
            } else {

                // UNSELECT
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    appendingFromBegin = false;
                    appendingFromEnd = false;
                    if (movingNode != null) {
                        if(coordinates.contains(movingNode)) {
                            coordinates.set(coordinates.indexOf(movingNode), backupNode);
                            movingNode = null;
                            backupNode = null;
                        } else {
                            movingNode = null;
                            backupNode = null;
                            CableManager.currentCable = null;
                            CircuitGUIManager.propertiesBox.hide();
                        }
                    } else {
                        CableManager.currentCable = null;
                        CircuitGUIManager.propertiesBox.hide();
                    }
                }

                // CLICK
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    if((Gdx.input.getX() <= Gdx.graphics.getWidth() - 200) || !CircuitGUIManager.isPanelShown()) {
                        HashMap<Hardware, Integer> hardware = HardwareManager.wireHoveringHardware(vec2);

                        if (hardware != null) {
                            // HARDWARE
                            processHardwareClick(hardware);
                        } else {
                            // ADD NEW POINT
                            if (appendingFromEnd && !disableEnd) {
                                if(!CircuitGUIManager.propertiesBox.hovering) {
                                    addCoordinates(new Vector2(vec2.x, vec2.y), false);
                                } else {
                                    appendingFromEnd = false;
                                }
                            } else if (appendingFromBegin && !disableBegin) {
                                if(!CircuitGUIManager.propertiesBox.hovering) {
                                    addCoordinates(new Vector2(vec2.x, vec2.y), true);
                                } else {
                                    appendingFromBegin = false;
                                }
                            } else if (movingNode != null && backupNode.x != movingNode.x && backupNode.y != movingNode.y) {
                                if(coordinates.contains(movingNode)) {
                                    coordinates.set(coordinates.indexOf(movingNode), new Vector2(vec2.x, vec2.y));
                                }
                                movingNode = null;
                                backupNode = null;
                                nodeChanged = true;
                            } else if (!hoveringMouse(camera)) {
                                appendingFromEnd = false;
                                appendingFromBegin = false;
                                movingNode = null;
                                backupNode = null;
                                LinkTimer.init((int) Gdx.graphics.getDeltaTime() + 3, () -> {
                                    if(!CircuitGUIManager.propertiesBox.hovering && CableManager.currentCable == this) {
                                        CircuitGUIManager.propertiesBox.hide();
                                        CableManager.currentCable = null;
                                    }
                                });
                            }
                        }
                    } else {
                        if(!appendingFromEnd && !appendingFromBegin) {
                            appendingFromEnd = false;
                            appendingFromBegin = false;
                            movingNode = null;
                            backupNode = null;
                            CircuitGUIManager.propertiesBox.hide();
                            CableManager.currentCable = null;
                        }
                    }

                }

                // DELETE
                if ((Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL)) && movingNode == null) {
                    if((appendingFromBegin && !disableBegin) || (appendingFromEnd && !disableEnd)) {
                        if(appendingFromBegin && coordinates.size() > 2) {
                            coordinates.remove(0);
                        } else if (appendingFromEnd && coordinates.size() > 2) {
                            coordinates.remove(coordinates.size()-1);
                        } else if (coordinates.size() > 1) {
                            CableManager.deleteCable(this);
                            CableManager.currentCable = null;
                            CircuitGUIManager.propertiesBox.hide();
                            HardwareManager.removeCableFromHardware(this, connection1);
                            HardwareManager.removeCableFromHardware(this, connection2);
                            connection2 = null;
                            connection1 = null;
                        }
                        appendingFromBegin = false;
                        appendingFromEnd = false;
                    } else {
                        CableManager.deleteCable(this);
                        CableManager.currentCable = null;
                        CircuitGUIManager.propertiesBox.hide();
                        HardwareManager.removeCableFromHardware(this, connection1);
                        HardwareManager.removeCableFromHardware(this, connection2);
                        connection2 = null;
                        connection1 = null;
                    }
                }

                // DRAW NODES IF SELECTED
                drawNodes(renderer, camera, Color.SALMON);

                if (appendingFromEnd && !disableEnd) {
                    // draw potential cable wire
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit3);

                } else if (appendingFromBegin && !disableBegin){
                    // draw potential cable wire
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit3);

                } else if (movingNode != null) {
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
