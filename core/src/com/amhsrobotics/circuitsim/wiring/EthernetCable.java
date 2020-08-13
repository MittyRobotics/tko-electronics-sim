package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.input.Tuple;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;
import java.util.HashMap;

public class EthernetCable extends Cable {
    public EthernetCable(Vector2 startPoint, int count) {
        super(startPoint, count);
        gauge = 9;
        color = DeviceUtil.COLORS.get("Orange");
        populateProperties();
    }

    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("Ethernet - ID " + ID, CircuitGUIManager.propertiesBox.LABEL), true, 2);
        CircuitGUIManager.propertiesBox.addElement(new Label("Color", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        final TextButton cb = new TextButton(DeviceUtil.getKeyByValue(DeviceUtil.COLORS, this.color), CircuitGUIManager.propertiesBox.TBUTTON);
        CircuitGUIManager.propertiesBox.addElement(cb, false, 1);

        cb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ArrayList<String> keys = new ArrayList<>(DeviceUtil.COLORS.keySet());
                for(String str : keys) {
                    if(str.contentEquals(cb.getText())) {
                        if(keys.indexOf(str) == keys.size() - 1) {
                            cb.setText(keys.get(0));
                            color = DeviceUtil.COLORS.get(keys.get(0));
                        } else {
                            cb.setText(keys.get(keys.indexOf(str) + 1));
                            color = DeviceUtil.COLORS.get(keys.get(keys.indexOf(str) + 1));
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void drawEndpoints(ShapeRenderer renderer) {
        if(!appendingFromBegin) {
            renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit + 10f);
        }
        if(!appendingFromEnd) {
            renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit + 10f);
        }
//        renderer.rect(coordinates.get(0).x - 10, coordinates.get(0).y - 10, 20, 20);
//        renderer.rect(coordinates.get(coordinates.size() - 1).x - 10, coordinates.get(coordinates.size() - 1).y - 10, 20, 20);
    }

    @Override
    protected void drawNodes(ShapeRenderer renderer, ClippedCameraController cam, Color... color) {
        if(color.length > 0) {
            renderer.setColor(color[0]);
        }
        for(Vector2 coords : coordinates) {
            renderer.circle(coords.x, coords.y, limit+3f);
        }
        processNodes(renderer, cam);
    }

    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        limit = ((1/gauge)*100)/2;

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
                    renderer.setColor(new Color(156/255f,1f,150/255f,1f));
                    renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit*2+3f);
                }
            }
            if(hoveringMouse(camera)) {
                // draw hovering on cable
                renderer.setColor(new Color(156/255f,1f,150/255f,1f));
                renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit*2+3f);
            }
            // draw actual cable
            renderer.setColor(color);
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), (1/gauge)*100);

            renderer.circle(coordinates.get(i).x, coordinates.get(i).y, limit);
        }
        renderer.setColor(Color.WHITE);
        if(!appendingFromBegin) {
            renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit + 10f);
        }
        if(!appendingFromEnd) {
            renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit+10f);
        }
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
                    renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), (1/gauge)*100);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit+10f);
                } else {
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), (1/gauge)*100);
                    renderer.setColor(Color.SALMON);
                    renderer.circle(vec2.x, vec2.y, limit+10f);
                }
            }

            // IF MERGING WIRES
            Tuple<Cable, Integer> secondCable = CableManager.wireHoveringWire(camera, this);
            if (secondCable != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && ((Gdx.input.getX() <= Gdx.graphics.getWidth() - 200) || !CircuitGUIManager.isPanelShown())) {
                CableManager.mergeCables(this, secondCable.x, secondCable.y == 1, appendingFromBegin);
            } else {

                // UNSELECT
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    if((appendingFromBegin && !disableBegin) || (appendingFromEnd && !disableEnd)) {
                        appendingFromBegin = false;
                        appendingFromEnd = false;
                    } else if (movingNode != null) {
                        coordinates.set(coordinates.indexOf(movingNode), backupNode);
                        movingNode = null;
                        backupNode = null;
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
                                addCoordinates(new Vector2(vec2.x, vec2.y), false);
                            } else if (appendingFromBegin && !disableBegin) {
                                addCoordinates(new Vector2(vec2.x, vec2.y), true);
                            } else if (movingNode != null && backupNode.x != movingNode.x && backupNode.y != movingNode.y) {
                                coordinates.set(coordinates.indexOf(movingNode), new Vector2(vec2.x, vec2.y));
                                movingNode = null;
                                backupNode = null;
                                nodeChanged = true;
                            } else {
                                appendingFromEnd = false;
                                appendingFromBegin = false;
                                movingNode = null;
                                backupNode = null;
                                CableManager.currentCable = null;
                                CircuitGUIManager.propertiesBox.hide();
                            }
                        }
                    } else {
                        appendingFromEnd = false;
                        appendingFromBegin = false;
                        if (movingNode != null && backupNode.x != movingNode.x && backupNode.y != movingNode.y) {
                            coordinates.set(coordinates.indexOf(movingNode), new Vector2(vec2.x, vec2.y));
                            movingNode = null;
                            backupNode = null;
                            nodeChanged = true;
                        }
                        CircuitGUIManager.propertiesBox.hideAndClear();
                        CableManager.currentCable = null;
                    }

                }

                // DELETE
                if ((Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL)) && movingNode == null) {
                    if((appendingFromBegin && !disableBegin) || (appendingFromEnd && !disableEnd)) {
                        if(appendingFromBegin && coordinates.size() > 1) {
                            coordinates.remove(0);
                        } else if (appendingFromEnd && coordinates.size() > 1) {
                            coordinates.remove(coordinates.size()-1);
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
                    renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), (1/gauge)*100);
                    renderer.setColor(Color.WHITE);
                    renderer.circle(vec2.x, vec2.y, limit+10f);
                } else if (appendingFromBegin && !disableBegin){
                    // draw potential cable wire
                    renderer.setColor(color);
                    renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), (1/gauge)*100);
                    renderer.setColor(Color.WHITE);
                    renderer.circle(vec2.x, vec2.y, limit+10f);
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
