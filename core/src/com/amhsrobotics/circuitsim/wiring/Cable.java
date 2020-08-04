package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.SnapGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import me.rohanbansal.ricochet.camera.CameraController;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;
import java.util.HashMap;

public class Cable implements Disposable {

    private float voltage;
    private float gauge;
    private Color color;
    private Color hoverColor = Color.WHITE;
    private ArrayList<Vector2> coordinates;
    private HashMap<Float, Float> connections;
    private float x1, x2, y1, y2, a;

    private boolean appendingFromEnd, appendingFromBegin;
    private boolean nodeChanged = false;
    private Vector2 movingNode, backupNode;


    public Cable(float voltage, float gauge, ArrayList<Vector2> coordinates, HashMap<Float, Float> connections) {
        this.voltage = voltage;
        this.gauge = gauge;
        this.coordinates = coordinates;
        this.connections = connections;
        this.color = new Color(158/255f, 205/255f, 158/255f, 1);
    }

    public Cable(float voltage, float gauge) {
        this(voltage, gauge, new ArrayList<Vector2>(), new HashMap<Float, Float>());
    }

    public Cable(Vector2 startPoint) {
        voltage = 0;
        gauge = 0;
        coordinates = new ArrayList<>();
        connections = new HashMap<>();
        this.color = new Color(158/255f, 205/255f, 158/255f, 1);

        coordinates.add(startPoint);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addCoordinates(Vector2 point, boolean begin) {
        if(begin) {
            this.coordinates.add(0, point);
        } else {
            this.coordinates.add(point);
        }
    }

    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        renderer.setProjectionMatrix(camera.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        if(nodeChanged) {
            nodeChanged = false;
        }

        // DRAW CABLE
        // ---------------------------------------------------------------------
        renderer.setColor(color);
        for(int i = 0; i < coordinates.size() - 1; ++i) {
            if(CableManager.currentCable != null) {
                if(CableManager.currentCable == this) {
                    // draw cable selected
                    renderer.setColor(new Color(217/255f, 233/255f, 217/255f, 1));
                    renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), 6f);
                }
            }
            if(hoveringMouse(camera)) {
                // draw hovering on cable
                renderer.setColor(new Color(217 / 255f, 233 / 255f, 217 / 255f, 1));
                renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), 6f);
            }
            // draw actual cable
            renderer.setColor(color);
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), 3f);
        }
        // ---------------------------------------------------------------------

        // CABLE SELECTED MECHANICS
        // ---------------------------------------------------------------------
        if(CableManager.currentCable == this) {
            Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.getCamera().unproject(vec);
            Vector2 vec2 = new Vector2(vec.x, vec.y);

            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                CableManager.currentCable = null;
                appendingFromBegin = false;
                appendingFromEnd = false;
                if(movingNode != null) {
                    coordinates.set(coordinates.indexOf(movingNode), backupNode);
                    movingNode = null;
                    backupNode = null;
                }
            }

            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if(appendingFromEnd) {
                    addCoordinates(new Vector2(vec2.x, vec2.y), false);
                } else if(appendingFromBegin) {
                    addCoordinates(new Vector2(vec2.x, vec2.y), true);
                } else if(movingNode != null && backupNode.x != movingNode.x && backupNode.y != movingNode.y) {
                    coordinates.set(coordinates.indexOf(movingNode), new Vector2(vec2.x, vec2.y));
                    movingNode = null;
                    backupNode = null;
                    nodeChanged = true;
                }
            }

            drawNodes(renderer, camera, Color.SALMON);
//            checkForClick(camera);

            if(appendingFromEnd) {
                // draw potential cable wire
                renderer.setColor(color);
                renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), 3f);
                renderer.circle(vec2.x, vec2.y, 5);
            } else if(appendingFromBegin) {
                // draw potential cable wire
                renderer.setColor(color);
                renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), 3f);
                renderer.circle(vec2.x, vec2.y, 5);
            } else if(movingNode != null) {
                movingNode.set(vec2.x, vec2.y);
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

    private void checkForClick(ClippedCameraController camera) {
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(hoveringOnEndpoint(camera) == 1) {
                appendingFromBegin = true;
            } else if(hoveringOnEndpoint(camera) == 2) {
                appendingFromEnd = true;
            } else if(hoveringOnNode(camera) != null && movingNode == null && !nodeChanged) {
                movingNode = hoveringOnNode(camera);
                backupNode = new Vector2(hoveringOnNode(camera));
            }

            CableManager.currentCable = this;
        }
    }

    private Vector2 hoveringOnNode(ClippedCameraController camera) {
        Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.getCamera().unproject(vec);

        for(Vector2 coord : coordinates) {
            if(coordinates.indexOf(coord) != 0 && coordinates.indexOf(coord) != coordinates.size() - 1) {
                if(new Circle(coord.x, coord.y, 5).contains(vec.x, vec.y)) {
                    return coord;
                }
            }
        }
        return null;
    }

    private void drawNodes(ShapeRenderer renderer, ClippedCameraController cam, Color... color) {

        if(color.length > 0) {
            renderer.setColor(color[0]);
        }
        for(Vector2 coords : coordinates) {
            renderer.circle(coords.x, coords.y, 6);
        }
        if(hoveringOnEndpoint(cam) == 1) {
            renderer.setColor(hoverColor);
            renderer.circle(coordinates.get(0).x, coordinates.get(0).y, 6);
        } else if(hoveringOnEndpoint(cam) == 2) {
            renderer.setColor(hoverColor);
            renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, 6);
        } else if(hoveringOnNode(cam) != null) {
            if(movingNode == null) {
                renderer.setColor(hoverColor);
                renderer.circle(hoveringOnNode(cam).x, hoveringOnNode(cam).y, 6);
            }
        }
    }

    public int hoveringOnEndpoint(CameraController cameraController) {
        Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cameraController.getCamera().unproject(vec);

        Vector2 c2 = coordinates.get(coordinates.size() - 1);
        Vector2 c = coordinates.get(0);

        if(new Circle(c2.x, c2.y, 5).contains(vec.x, vec.y)) {
            return 2;
        } else if(new Circle(c.x, c.y, 5).contains(vec.x, vec.y)) {
            return 1;
        }
        return 0;
    }

    public float getVoltage() {
        return voltage;
    }

    public float getGauge() {
        return gauge;
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

    public boolean hoveringMouse(CameraController cameraController) {
        Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cameraController.getCamera().unproject(vec);
        float x = vec.x;
        float y = vec.y;

        for(int i = 0; i < coordinates.size() - 1; ++i) {
            x1 = coordinates.get(i).x;
            x2 = coordinates.get(i + 1).x;
            y1 = coordinates.get(i).y;
            y2 = coordinates.get(i + 1).y;

            if(((x1 < x2 && x >= x1 && x <= x2)||(x1 > x2 && x <= x1 && x >= x2))&&((y1 < y2 && y >= y1 && y <= y2)||(y1 > y2 && y <= y1 && y >= y2))) {

                a = -1 * ((y2 - y1) / (x2 - x1));

                if ((float) Math.abs(x * a + y + (((y2 - y1) / (x2 - x1)) * x1 - y1)) / Math.sqrt(a * a + 1) < 5) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void dispose() {
    }
}
