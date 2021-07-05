package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.hardware.devices.SandCrab;
import com.amhsrobotics.circuitsim.screens.CircuitScreen;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.input.Tuple;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import me.rohanbansal.ricochet.camera.CameraController;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Cable implements Json.Serializable {

    public float voltage, gauge, x1, x2, y1, y2, a, limit, limit2, limit3;
    public Color color, nodeColor, hoverColor = Color.WHITE, color2 = null;
    public ArrayList<Vector2> coordinates;
    public Hardware connection1, connection2;
    public int connection1port = -1, connection2port = -1;

    public boolean appendingFromEnd, appendingFromBegin, disableEnd, disableBegin, canMove, nodeChanged, enableGauge, enableConn, enableColor, hover;
    public Vector2 movingNode, backupNode, prevPos;

    public int ID, color2n = -1;

    private String title;

    public Cable() {
    }

    public Cable(Vector2 startPoint, int count) {
        voltage = 0;
        gauge = DeviceUtil.GAUGES[0];
        coordinates = new ArrayList<>();
        this.color = DeviceUtil.COLORS.get("Green");
        this.ID = count;

        coordinates.add(startPoint);

        populateProperties("Cable", true, true, true, ID);
        CircuitGUIManager.propertiesBox.show();
    }

    public Cable(int count) {
        voltage = 0;
        gauge = DeviceUtil.GAUGES[0];
        coordinates = new ArrayList<>();
        this.color = DeviceUtil.COLORS.get("Green");
        this.ID = count;

        CircuitGUIManager.propertiesBox.show();
    }

    public int getID() {
        return ID;
    }

    public void populateProperties() {
        populateProperties("Cable", true, true, true, ID);
    }

    public void populateProperties(String title, boolean enableColor, boolean enableGauge, boolean enableConnections, int ID) {
        this.title = title;
        this.enableColor = enableColor;
        this.enableGauge = enableGauge;
        this.enableConn = enableConnections;

        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label(title + " - ID " + ID, CircuitGUIManager.propertiesBox.LABEL), true, 2);

        if (enableColor) {
            CircuitGUIManager.propertiesBox.addElement(new Label("Color", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
            final TextButton cb = new TextButton(DeviceUtil.getKeyByValue(DeviceUtil.COLORS, this.color), CircuitGUIManager.propertiesBox.TBUTTON);
            CircuitGUIManager.propertiesBox.addElement(cb, false, 1);
            cb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ArrayList<String> keys = new ArrayList<>(DeviceUtil.COLORS.keySet());
                    for (String str : keys) {
                        if (str.contentEquals(cb.getText())) {
                            if (keys.indexOf(str) == keys.size() - 1) {
                                cb.setText(keys.get(0));
                                color = DeviceUtil.COLORS.get(keys.get(0));
                                color2 = null;
                            } else {
                                cb.setText(keys.get(keys.indexOf(str) + 1));
                                color = DeviceUtil.COLORS.get(keys.get(keys.indexOf(str) + 1));
                                color2 = null;
                            }
                            break;
                        }
                    }
                }
            });
        }
        if (enableGauge) {
            CircuitGUIManager.propertiesBox.addElement(new Label("Gauge", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
            final TextButton ga = new TextButton(this.gauge + "", CircuitGUIManager.propertiesBox.TBUTTON);
            CircuitGUIManager.propertiesBox.addElement(ga, false, 1);
            ga.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    List<Integer> gauges = Arrays.stream(DeviceUtil.GAUGES).boxed().collect(Collectors.toList());
                    if ((connection1 != null && !(connection1 instanceof SandCrab) || connection2 != null && !(connection2 instanceof SandCrab))) {
                        CircuitGUIManager.popup.activateError("Gauge cannot be modified with hardware attached");
                    } else {
                        for (int gau : gauges) {
                            if (gau == gauge) {
                                if (gauges.indexOf(gau) == gauges.size() - 1) {
                                    gauge = gauges.get(0);
                                    ga.setText(gauge + "");
                                } else {
                                    gauge = gauges.get(gauges.indexOf(gau) + 1);
                                    ga.setText(gauge + "");
                                }
                                break;
                            }
                        }
                    }
                }
            });
        }
        if (enableConnections) {
            CircuitGUIManager.propertiesBox.addElement(new Label("Conn. 1", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
            if ((connection1 == null ? "None" : connection1.name + " " + connection1.hardwareID2).length() > 10) {
                CircuitGUIManager.propertiesBox.addElement(new Label(connection1.name + " " + connection1.hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 2);
            } else {
                CircuitGUIManager.propertiesBox.addElement(new Label(connection1 == null ? "None" : connection1.name + " " + connection1.hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
            }

            if (!(this instanceof CrimpedCable)) {

                final TextButton dis = new TextButton("Remove", CircuitGUIManager.propertiesBox.TBUTTON);
                Cable cable = this;
                CircuitGUIManager.propertiesBox.addElement(dis, true, 2);
                dis.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (connection1 != null) {
                            coordinates.remove(0);
                            HardwareManager.removeCableFromHardware(cable, connection1);
                            connection1 = null;
                            populateProperties();
                        }
                    }
                });
            }

            CircuitGUIManager.propertiesBox.addElement(new Label("Conn. 2", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
            if ((connection2 == null ? "None" : connection2.name + " " + connection2.hardwareID2).length() > 10) {
                CircuitGUIManager.propertiesBox.addElement(new Label(connection2.name + " " + connection2.hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 2);
            } else {
                CircuitGUIManager.propertiesBox.addElement(new Label(connection2 == null ? "None" : connection2.name + " " + connection2.hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
            }

            if (!(this instanceof CrimpedCable)) {

                final TextButton dis2 = new TextButton("Remove", CircuitGUIManager.propertiesBox.TBUTTON);
                Cable cable = this;
                CircuitGUIManager.propertiesBox.addElement(dis2, true, 2);
                dis2.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (connection2 != null) {
                            coordinates.remove(coordinates.size() - 1);
                            HardwareManager.removeCableFromHardware(cable, connection2);
                            connection2 = null;
                            populateProperties();
                        }
                    }
                });
            }
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addCoordinates(Vector2 point, boolean begin) {
        // ADD TO ENDS OF CABLE
        // ---------------------------------------------------------------------
        if (begin) {
            this.coordinates.add(0, point);
        } else {
            this.coordinates.add(point);
        }
    }

    public void editCoordinates(Vector2 point, boolean endOfWire, boolean second) {
        // UPDATE COORDINATES FOR HARDWARE
        // ---------------------------------------------------------------------
        if (endOfWire) {
            if (second) {
                this.coordinates.set(this.coordinates.size() - 2, point);
            } else {
                this.coordinates.set(this.coordinates.size() - 1, point);
            }
        } else {
            if (second) {
                this.coordinates.set(1, point);
            } else {
                this.coordinates.set(0, point);
            }
        }

    }

    public void render(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        limit = DeviceUtil.GAUGETOLIMIT.get(gauge);
        limit2 = DeviceUtil.GAUGETOLIMIT2.get(gauge);
        limit3 = DeviceUtil.GAUGETOLIMIT3.get(gauge);

        renderer.setProjectionMatrix(camera.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        if (nodeColor == null) {
            nodeColor = Color.SALMON;
        }

        // DRAW CABLE
        // ---------------------------------------------------------------------
        renderer.setColor(color);
        for (int i = 0; i < coordinates.size() - 1; ++i) {
            if (CableManager.currentCable != null) {
                if (CableManager.currentCable == this) {
                    // draw cable selected
                    renderer.setColor(new Color(156 / 255f, 1f, 150 / 255f, 1f));
                    renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit2);
                }
            }
            if (hoveringMouse(camera) || hover) {
                // draw hovering on cable
                renderer.setColor(new Color(156 / 255f, 1f, 150 / 255f, 1f));
                renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit2);
            }
            // draw actual cable
            if (i >= color2n && color2 != null) {
                renderer.setColor(color2);
            } else {
                renderer.setColor(color);
            }
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit);

            renderer.circle(coordinates.get(i).x, coordinates.get(i).y, limit / 2);
        }

        // ---------------------------------------------------------------------


        // CABLE SELECTED MECHANICS
        // ---------------------------------------------------------------------

        if (CableManager.currentCable == this) {

            Vector2 vec2 = Tools.mouseScreenToWorld(camera);

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            // DRAW NODES IF SELECTED
            drawNodes(renderer, camera, nodeColor);

            if (appendingFromEnd && !disableEnd) {
                // draw potential cable wire
                if (coordinates.size() - 1 >= color2n && color2 != null) {
                    renderer.setColor(color2);
                } else {
                    renderer.setColor(color);
                }
                renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), limit);
                renderer.setColor(nodeColor);
                renderer.circle(vec2.x, vec2.y, limit3);
            } else if (appendingFromBegin && !disableBegin) {
                // draw potential cable wire
                if (0 >= color2n && color2 != null) {
                    renderer.setColor(color2);
                } else {
                    renderer.setColor(color);
                }
                renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                renderer.setColor(nodeColor);
                renderer.circle(vec2.x, vec2.y, limit3);
            }

        }

        renderer.setColor(nodeColor);
        renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit3);
        renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit3);

        if (hoveringMouse(camera) || hover) {
            drawNodes(renderer, camera, nodeColor);
        }

        drawEndpoints(renderer);

        renderer.end();
    }

    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        limit = DeviceUtil.GAUGETOLIMIT.get(gauge);
        limit2 = DeviceUtil.GAUGETOLIMIT2.get(gauge);
        limit3 = DeviceUtil.GAUGETOLIMIT3.get(gauge);

        renderer.setProjectionMatrix(camera.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        if (nodeColor == null) {
            nodeColor = Color.SALMON;
        }

        // LOGIC
        // ---------------------------------------------------------------------
        if (nodeChanged) {
            nodeChanged = false;
        }
        disableBegin = connection1 != null;
        disableEnd = connection2 != null;
        // ---------------------------------------------------------------------


        // DRAW CABLE
        // ---------------------------------------------------------------------
        if (0 >= color2n && color2 != null) {
            renderer.setColor(color2);
        } else {
            renderer.setColor(color);
        }
        for (int i = 0; i < coordinates.size() - 1; ++i) {
            if (CableManager.currentCable != null) {
                if (CableManager.currentCable == this) {
                    // draw cable selected
                    renderer.setColor(new Color(156 / 255f, 1f, 150 / 255f, 1f));
                    renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit2);
                }
            } else if (hoveringMouse(camera) || hover) {
                // draw hovering on cable
                renderer.setColor(new Color(156 / 255f, 1f, 150 / 255f, 1f));
                renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit2);
            }
            // draw actual cable
            if (i >= color2n && color2 != null) {
                renderer.setColor(color2);
            } else {
                renderer.setColor(color);
            }
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), limit);

            renderer.circle(coordinates.get(i).x, coordinates.get(i).y, limit / 2);
        }

        // ---------------------------------------------------------------------


        // CABLE SELECTED MECHANICS
        // ---------------------------------------------------------------------

        if (CableManager.currentCable == this) {

            Vector2 vec2 = Tools.mouseScreenToWorld(camera);

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }


            // IF MERGING WIRES
            Tuple<Cable, Integer> secondCable = CableManager.wireHoveringWire(camera, this);
            if ((appendingFromBegin || appendingFromEnd) && secondCable != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && checkGood()) {
                CableManager.mergeCables(this, secondCable.x, appendingFromBegin, secondCable.y == 1);
            } else {

                // UNSELECT
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {

                    if (movingNode != null) {
                        if (coordinates.contains(movingNode)) {
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
                        if (!appendingFromEnd && !appendingFromBegin) {
                            CableManager.currentCable = null;
                            CircuitGUIManager.propertiesBox.hide();
                        }
                    }
                    appendingFromBegin = false;
                    appendingFromEnd = false;

                }

                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && checkGood() && canMove) {
                    Vector2 diff = Tools.mouseScreenToWorld(camera).sub(prevPos);
                    prevPos = Tools.mouseScreenToWorld(camera);
                    moveEntireCable(diff.x, diff.y);
                }


                // CLICK
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && checkGood()) {
                    if (!appendingFromEnd && !appendingFromBegin && movingNode == null) {
                        backupNode = null;
                        CircuitGUIManager.propertiesBox.hide();
                        CableManager.currentCable = null;
                    }
                    HashMap<Hardware, Integer> hardware = HardwareManager.wireHoveringHardware(vec2);

                    if (hardware != null) {
                        // HARDWARE
                        processHardwareClick(hardware);
                        disableBegin = connection1 != null;
                        disableEnd = connection2 != null;
                        if (disableBegin) {
                            appendingFromBegin = false;
                        }
                        if (disableEnd) {
                            appendingFromEnd = false;
                        }
                    } else {
                        // ADD NEW POINT
                        if (checkBeforeClick(camera) && appendingFromEnd && !disableEnd) {
                            if (!CircuitGUIManager.propertiesBox.hovering) {
                                addCoordinates(new Vector2(vec2.x, vec2.y), false);
                            } else {
                                appendingFromEnd = false;
                            }
                        } else if (checkBeforeClick(camera) && appendingFromBegin && !disableBegin) {
                            if (!CircuitGUIManager.propertiesBox.hovering) {
                                addCoordinates(new Vector2(vec2.x, vec2.y), true);
                            } else {
                                appendingFromBegin = false;
                            }
                        } else if (movingNode != null && backupNode.x != movingNode.x && backupNode.y != movingNode.y) {
                            if (coordinates.contains(movingNode)) {
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
                        }
                    }

                }

                // DELETE
                if ((Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL)) && movingNode == null) {
                    if ((appendingFromBegin && !disableBegin) || (appendingFromEnd && !disableEnd)) {
                        if (appendingFromBegin && coordinates.size() > 2) {
                            coordinates.remove(0);
                            color2n--;
                        } else if (appendingFromEnd && coordinates.size() > 2) {
                            coordinates.remove(coordinates.size() - 1);
                        } else {
                            CableManager.deleteCable(this);
                            CableManager.currentCable = null;
                            CircuitGUIManager.propertiesBox.hide();
                            HardwareManager.removeCableFromHardware(this, connection1);
                            HardwareManager.removeCableFromHardware(this, connection2);
                        }
                        appendingFromBegin = false;
                        appendingFromEnd = false;
                    } else {
                        CableManager.deleteCable(this);
                        CableManager.currentCable = null;
                        CircuitGUIManager.propertiesBox.hide();
                        HardwareManager.removeCableFromHardware(this, connection1);
                        HardwareManager.removeCableFromHardware(this, connection2);
                    }
                }

                // DRAW NODES IF SELECTED
                drawNodes(renderer, camera, nodeColor);

                if (appendingFromEnd && !disableEnd) {
                    // draw potential cable wire
                    if (coordinates.size() - 1 >= color2n && color2 != null) {
                        renderer.setColor(color2);
                    } else {
                        renderer.setColor(color);
                    }
                    renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(nodeColor);
                    renderer.circle(vec2.x, vec2.y, limit3);

                } else if (appendingFromBegin && !disableBegin) {
                    // draw potential cable wire
                    if (0 >= color2n && color2 != null) {
                        renderer.setColor(color2);
                    } else {
                        renderer.setColor(color);
                    }
                    renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                    renderer.setColor(nodeColor);
                    renderer.circle(vec2.x, vec2.y, limit3);

                } else if (movingNode != null) {
                    movingNode.set(vec2.x, vec2.y);
                    if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL)) {
                        if (coordinates.indexOf(movingNode) < color2n) {
                            color2n--;
                        }
                        if (!(this instanceof CrimpedCable && coordinates.indexOf(movingNode) == 1)) {
                            coordinates.remove(movingNode);
                        } else {
                            coordinates.set(coordinates.indexOf(movingNode), backupNode);
                            color2n++;
                        }
                        movingNode = null;
                        backupNode = null;
                    }
                }
            }

        } else {
            canMove = false;
            CableManager.movingCable = false;
        }
        // ---------------------------------------------------------------------

        // HOVERING OVER CABLE
        // ---------------------------------------------------------------------


        if (hoveringMouse(camera) || hover) {
            drawNodes(renderer, camera, nodeColor);
            checkForClick(camera);
            if (!appendingFromEnd && !appendingFromBegin && movingNode == null) {
                canMove = true;
                CableManager.movingCable = true;
                prevPos = Tools.mouseScreenToWorld(camera);
            }
        } else {
            if (!Gdx.input.isButtonPressed(0)) {
                canMove = false;
                CableManager.movingCable = false;
            }
        }

        renderer.setColor(nodeColor);

        drawEndpoints(renderer);

        // ---------------------------------------------------------------------

        renderer.end();
    }

    public void drawEndpoints(ShapeRenderer renderer) {
        if (coordinates.size() > 0) {
            renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit3);
            renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit3);
        }
    }

    public Hardware getHardwareAtOtherEnd(Hardware hardware) {
        if (hardware == connection1) {
            return connection2;
        } else if (hardware == connection2) {
            return connection1;
        } else {
            return null;
        }
    }

    public boolean checkGood() {
        return (!(CircuitGUIManager.panelShown && Gdx.input.getX() >= Gdx.graphics.getWidth() - 420 && Gdx.input.getY() <= 210) && !(!CircuitGUIManager.panelShown &&
                Gdx.input.getX() >= Gdx.graphics.getWidth() - 210 && Gdx.input.getY() <= 210) && ((Gdx.input.getX() <= Gdx.graphics.getWidth() - 210) || !CircuitGUIManager.isPanelShown()) && !HardwareManager.movingObject && (!CircuitScreen.selectMultiple && !CircuitScreen.selectedMultiple))
                && !CircuitGUIManager.helpMenuShown && !CircuitGUIManager.optionsMenuShown && !CircuitGUIManager.saveMenuShown;
    }


    protected void processHardwareClick(HashMap<Hardware, Integer> hardware) {
        ArrayList<Hardware> clist = new ArrayList<>(hardware.keySet());

        // FIRST CLICK

        if (coordinates.size() == 1) {
            if (appendingFromEnd) {
                clist.get(0).firstClickAttach(this, hardware.get(clist.get(0)), true);
            } else if (appendingFromBegin) {
                clist.get(0).firstClickAttach(this, hardware.get(clist.get(0)), false);
            }
        } else {
            if ((getConnectionSimple(!appendingFromBegin) == clist.get(0) || getConnection(!appendingFromBegin) == clist.get(0)) && (appendingFromBegin || appendingFromEnd)) {
                CircuitGUIManager.popup.activateError("A device cannot be connected to itself");
                appendingFromEnd = false;
                appendingFromBegin = false;
                CableManager.currentCable = null;
            } else {
                if (appendingFromEnd) {
                    clist.get(0).attachWire(this, hardware.get(clist.get(0)), true);
                } else if (appendingFromBegin) {
                    clist.get(0).attachWire(this, hardware.get(clist.get(0)), false);
                }
                appendingFromEnd = false;
                appendingFromBegin = false;
            }

        }
    }

    protected void checkForClick(ClippedCameraController camera) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && checkGood() &&
                ((CableManager.currentCable == null || !(CableManager.currentCable.appendingFromBegin || CableManager.currentCable.appendingFromEnd)) || CableManager.currentCable == this)) {
            // CLICKED ON END

            if (hoveringOnEndpoint(camera) == 1 && !disableBegin) {

                if (CableManager.currentCable != null && CableManager.currentCable != this && CableManager.currentCable.gauge != this.gauge && (CableManager.currentCable.appendingFromBegin || CableManager.currentCable.appendingFromEnd)) {
                    CircuitGUIManager.popup.activateError("Only cables with the same gauge may be merged");
                } else {
                    appendingFromBegin = true;
                    appendingFromEnd = false;
                    HardwareManager.currentHardware = null;
                }
            } else if (hoveringOnEndpoint(camera) == 2 && !disableEnd) {

                if (CableManager.currentCable != null && CableManager.currentCable != this && CableManager.currentCable.gauge != this.gauge && (CableManager.currentCable.appendingFromBegin || CableManager.currentCable.appendingFromEnd)) {
                    CircuitGUIManager.popup.activateError("Only cables with the same gauge may be merged");
                } else {
                    appendingFromEnd = true;
                    appendingFromBegin = false;
                    HardwareManager.currentHardware = null;
                }

            } else if (hoveringOnNode(camera) != null) {
                HardwareManager.currentHardware = null;
                if (movingNode == null && !nodeChanged) {
                    // MOVE NODE
                    movingNode = hoveringOnNode(camera);
                    backupNode = new Vector2(hoveringOnNode(camera));
                }
            }

            if (CableManager.currentCable != this) {

                //SELECT THIS CABLE
                if (CableManager.currentCable != null) {
                    CableManager.currentCable.appendingFromBegin = false;
                    CableManager.currentCable.appendingFromEnd = false;
                }
                CableManager.currentCable = this;
                CableManager.moveToFront(this);
                if (connection1 != null) {
                    HardwareManager.moveToFront(connection1);
                }
                if (connection2 != null) {
                    HardwareManager.moveToFront(connection2);
                }
                HardwareManager.currentHardware = null;
                populateProperties();
                CircuitGUIManager.propertiesBox.show();
            }

            populateProperties();
        }
    }

    protected boolean checkBeforeClick(ClippedCameraController camera) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && CableManager.currentCable == this && (appendingFromBegin && hoveringOnEndpoint(camera) == 2 || appendingFromEnd && hoveringOnEndpoint(camera) == 1)) {
            appendingFromEnd = false;
            appendingFromBegin = false;
            CircuitGUIManager.popup.activateError("A cable cannot be attached to itself");
            return false;
        }
        return true;
    }

    protected Vector2 hoveringOnNode(ClippedCameraController camera) {
        Vector2 vec = Tools.mouseScreenToWorld(camera);

        //RETURN NODE THAT MOUSE IS HOVERING ON

        for (Vector2 coord : coordinates) {
            if (coordinates.indexOf(coord) != 0 && coordinates.indexOf(coord) != coordinates.size() - 1) {
                if (new Circle(coord.x, coord.y, limit3).contains(vec.x, vec.y)) {
                    return coord;
                }
            }
        }
        return null;
    }

    protected void drawNodes(ShapeRenderer renderer, ClippedCameraController cam, Color... color) {
        if (color.length > 0) {
            renderer.setColor(color[0]);
        }
        for (Vector2 coords : coordinates) {
            // DRAW POINTS
            renderer.circle(coords.x, coords.y, limit3);
        }
        processNodes(renderer, cam);
    }

    public void processNodes(ShapeRenderer renderer, ClippedCameraController cam) {
        //DRAW IF HOVERING ON ENDPOINTS
        if (hoveringOnEndpoint(cam) == 1) {
            renderer.setColor(hoverColor);
            renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit3);
        } else if (hoveringOnEndpoint(cam) == 2) {
            renderer.setColor(hoverColor);
            renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit3);
        } else if (hoveringOnNode(cam) != null) {
            //DRAW IF HOVERING ON OTHER NODE
            if (movingNode == null) {
                renderer.setColor(hoverColor);
                renderer.circle(hoveringOnNode(cam).x, hoveringOnNode(cam).y, limit3);
            }
        }
    }

    public int hoveringOnEndpoint(CameraController cameraController) {
        // CHECK IF HOVERING ON ENDPOINT
        Vector2 vec = Tools.mouseScreenToWorld(cameraController);


        Vector2 c2 = coordinates.get(coordinates.size() - 1);
        Vector2 c = coordinates.get(0);

        if (new Circle(c2.x, c2.y, limit3).contains(vec.x, vec.y)) {
            return 2;
        } else if (new Circle(c.x, c.y, limit3).contains(vec.x, vec.y)) {
            return 1;
        }
        return 0;
    }

    public int pointIsOnEndpoint(float x, float y) {
        Vector2 c2 = coordinates.get(coordinates.size() - 1);
        Vector2 c = coordinates.get(0);
        if (new Circle(c2.x, c2.y, limit3).contains(x, y)) {
            return 2;
        } else if (new Circle(c.x, c.y, limit3).contains(x, y)) {
            return 1;
        }
        return 0;
    }

    public Hardware getConnection1() {
        return connection1;
    }

    public void setConnection1(Hardware connection1) {
        if (connection1 == null) {
            disableBegin = false;
            this.connection1 = null;
        } else {
            this.connection1 = connection1;
        }
    }

    public Hardware getConnection2() {
        return connection2;
    }

    public void setConnection2(Hardware connection2) {
        if (connection2 == null) {
            disableEnd = false;
            this.connection2 = null;
        } else {
            this.connection2 = connection2;
        }
    }

    public Hardware getOtherConnection(Hardware h) {
        if (connection1 == h) {
            if (connection2 instanceof SandCrab) {
                Hardware temp = ((SandCrab) connection2).getWagoOther(this);
                connection2.seen = false;
                return temp;

            } else {
                return connection2;
            }
        } else {
            if (connection1 instanceof SandCrab) {
                Hardware temp = ((SandCrab) connection1).getWagoOther(this);
                connection1.seen = false;
                return temp;
            } else {
                return connection1;
            }
        }
    }

    public Hardware getOtherConnectionSimple(Hardware h) {
        return connection1 == h ? connection2 : connection1;
    }

    public int getOtherConnectionNum(Hardware h) {
        if (connection1 == h) {
            if (connection2 instanceof SandCrab) {
                connection2.seen = false;
                int i = ((SandCrab) connection2).getWagoOtherNum(this);
                return i;
            } else {
                return connection2.getConnNum(this);
            }
        } else {
            if (connection1 instanceof SandCrab) {
                connection1.seen = false;
                int i = ((SandCrab) connection1).getWagoOtherNum(this);
                return i;
            } else {
                return connection1.getConnNum(this);
            }
        }
    }

    public Hardware getConnection(boolean begin) {
        return begin ? getOtherConnection(connection2) : getOtherConnection(connection1);
    }

    public Hardware getConnectionSimple(boolean begin) {
        return begin ? getOtherConnectionSimple(connection2) : getOtherConnectionSimple(connection1);
    }

    public float getVoltage() {
        return voltage;
    }

    public float getGauge() {
        return gauge;
    }

    public void setGauge(float gauge) {
        this.gauge = gauge;
    }

    public ArrayList<Vector2> getCoordinates() {
        return coordinates;
    }

    public void setAppendingFromEnd(boolean appendingFromEnd) {
        this.appendingFromEnd = appendingFromEnd;
    }

    public void setAppendingFromBegin(boolean appendingFromBegin) {
        this.appendingFromBegin = appendingFromBegin;
    }

    public void mergeCable(Cable cable2, boolean cable1begin, boolean begin) {

        // MERGE TWO CABLES


        ArrayList<Vector2> l = cable2.getCoordinates();

        if (cable1begin) {
            // AGGREGATE COORDINATES
            for (int i = 0; i < l.size(); i++) {
                this.addCoordinates(l.get(i), begin);
            }

            // CARRY OVER HARDWARE CONNECTIONS (DON'T CHANGE)
            Hardware temp = cable2.getConnection2();
            if (temp != null) {
                temp.reattachWire(this, temp.getConnectionPosition(cable2), !begin);
            }

        } else {
            // AGGREGATE COORDINATES
            for (int i = l.size() - 1; i >= 0; i--) {
                this.addCoordinates(l.get(i), begin);
            }

            // CARRY OVER HARDWARE CONNECTIONS (DON'T CHANGE)
            Hardware temp = cable2.getConnection1();
            if (temp != null) {
                temp.reattachWire(this, temp.getConnectionPosition(cable2), !begin);
            }

        }


        // DESELECT & RESET

        CableManager.currentCable = null;
        appendingFromBegin = false;
        appendingFromEnd = false;
        movingNode = null;
        backupNode = null;
    }

    public boolean intersect(Vector2 vec1, Vector2 vec2) {
        for (int i = 0; i < coordinates.size() - 1; ++i) {
            if (Intersector.intersectLinePolygon(coordinates.get(i), coordinates.get(i + 1), new Polygon(new float[]{vec1.x, vec1.y, vec1.x, vec2.y, vec2.x, vec2.y, vec2.x, vec1.y}))) {
                return true;
            }
        }
        return false;
    }

    public boolean hoveringMouse(CameraController cameraController) {

        Vector2 vec = Tools.mouseScreenToWorld(cameraController);
        float x = vec.x;
        float y = vec.y;

        if (hoveringOnEndpoint(cameraController) != 0) {
            return true;
        }

        for (Vector2 coord : coordinates) {
            if (new Circle(coord.x, coord.y, limit3).contains(vec.x, vec.y)) {
                return true;
            }
        }


        // CHECK IF HOVERING ON CABLE


        for (int i = 0; i < coordinates.size() - 1; ++i) {
            x1 = coordinates.get(i).x;
            x2 = coordinates.get(i + 1).x;
            y1 = coordinates.get(i).y;
            y2 = coordinates.get(i + 1).y;

            //VERTICAL LINES

            if (x1 == x2 && x > x1 - limit2 / 2 && x < x1 + limit2 / 2 && ((y1 < y2 && y >= y1 && y <= y2) || (y1 > y2 && y <= y1 && y >= y2))) {
                return true;
            }

            //HORIZONTAL LINES

            if (y1 == y2 && y > y1 - limit2 / 2 && y < y1 + limit2 / 2 && ((x1 < x2 && x >= x1 && x <= x2) || (x1 > x2 && x <= x1 && x >= x2))) {
                return true;
            }

            //SIDEWAYS LINES
            if (((x1 < x2 && x >= x1 && x <= x2) || (x1 > x2 && x <= x1 && x >= x2)) && ((y1 < y2 && y >= y1 && y <= y2) || (y1 > y2 && y <= y1 && y >= y2))) {

                a = -1 * ((y2 - y1) / (x2 - x1));

                if (Math.abs(x * a + y + (((y2 - y1) / (x2 - x1)) * x1 - y1)) / Math.sqrt(a * a + 1) < limit2 / 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public void moveEntireCable(float x, float y) {
        if (movingNode == null) {
            for (int i = 0; i < coordinates.size(); i++) {
                if ((connection1 == null || (i != 0 && i != 1)) && (connection2 == null || (i != coordinates.size() - 1 && i != coordinates.size() - 2))) {
                    coordinates.set(i, new Vector2(coordinates.get(i).x + x, coordinates.get(i).y + y));
                }
            }
        }
    }

    public void moveEntireCable(float x, float y, boolean endOfWire) {
        if (movingNode == null) {
            for (int i = 0; i < coordinates.size(); i++) {
                if ((!endOfWire || connection1 == null || (i != 0 && i != 1)) && (endOfWire || connection2 == null || (i != coordinates.size() - 1 && i != coordinates.size() - 2))) {
                    coordinates.set(i, new Vector2(coordinates.get(i).x + x, coordinates.get(i).y + y));
                }
            }
        }
    }

    public Vector2 getCoordinate(boolean endOfWire) {
        if (endOfWire) {
            return coordinates.get(coordinates.size() - 1);
        }
        return coordinates.get(0);
    }

    public void removeCoordinates() {
        coordinates.clear();
    }

    @Override
    public void write(Json json) {
        json.writeValue("id", this.ID);
        json.writeValue("gauge", this.gauge);
        json.writeValue("color", this.color);
        json.writeValue("nodeColor", this.nodeColor);
        json.writeValue("appendingFromEnd", this.appendingFromEnd);
        json.writeValue("appendingFromBegin", this.appendingFromBegin);
        json.writeValue("disableEnd", this.disableEnd);
        json.writeValue("disableBegin", this.disableBegin);
//        json.writeValue("connection1", this.connection1);
//        json.writeValue("connection2", this.connection2);
        json.writeValue("coordinates", this.coordinates);
    }

    private void parseColor(Color var, JsonValue jsonData, String identifier) {
        try {
            var.r = jsonData.get(identifier).getFloat("r");
        } catch (Exception e) {
            var.r = 0 / 255f;
        }
        try {
            var.g = jsonData.get(identifier).getFloat("g");
        } catch (Exception e) {
            var.g = 0 / 255f;
        }
        try {
            var.b = jsonData.get(identifier).getFloat("b");
        } catch (Exception e) {
            var.b = 0 / 255f;
        }
        var.a = jsonData.get(identifier).getFloat("a");
    }

    @Override
    public void read(Json json, JsonValue jsonData) {

        this.ID = jsonData.getInt("id");
        this.gauge = jsonData.getInt("gauge");
        this.color = new Color();
        parseColor(this.color, jsonData, "color");
        this.nodeColor = new Color();
        parseColor(this.nodeColor, jsonData, "nodeColor");
    }
}
