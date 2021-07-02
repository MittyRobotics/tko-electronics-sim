package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.screens.CircuitScreen;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.camera.CameraController;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class EthernetCable extends Cable {

    boolean canDraw = false;

    public EthernetCable() {
    }

    public EthernetCable(Vector2 startPoint, int count) {
        super(startPoint, count);
        gauge = 13;
        color = DeviceUtil.COLORS.get("Orange");
        hoverColor = Color.GRAY;
        populateProperties();
        CircuitGUIManager.propertiesBox.show();

    }

    public void populateProperties() {
        super.populateProperties("Ethernet", true, false, true, ID);
    }

    @Override
    public void render(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        super.render(renderer, camera);

        if (CableManager.currentCable == this) {

            Vector2 vec2 = Tools.mouseScreenToWorld(camera);

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            if (appendingFromEnd && !disableEnd) {
                // draw potential cable wire
                renderer.setColor(color);
                renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), limit);
                renderer.setColor(DeviceUtil.END_COLORS.get("Plastic"));
                float angle, dx, dy;

                angle = (float) Math.atan2(coordinates.get(coordinates.size() - 1).x - vec2.x, vec2.y - coordinates.get(coordinates.size() - 1).y);

                dx = 40 * (float) Math.cos(angle);
                dy = 40 * (float) Math.sin(angle);

                renderer.rectLine(vec2.x + dx, vec2.y + dy, vec2.x - dx, vec2.y - dy, 50);

            } else if (appendingFromBegin && !disableBegin) {
                // draw potential cable wire
                renderer.setColor(color);
                renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                renderer.setColor(DeviceUtil.END_COLORS.get("Plastic"));

                float angle, dx, dy;

                angle = (float) Math.atan2(coordinates.get(0).x - vec2.x, vec2.y - coordinates.get(0).y);

                if (connection2 != null) {
                    angle = 0;
                }

                dx = 40 * (float) Math.cos(angle);
                dy = 40 * (float) Math.sin(angle);

                renderer.rectLine(vec2.x + dx, vec2.y + dy, vec2.x - dx, vec2.y - dy, 50);
            }

            renderer.end();

        }

    }

    @Override
    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        canDraw = true;

        super.update(renderer, camera);


    }


    @Override
    public void drawEndpoints(ShapeRenderer renderer) {
        if (canDraw) {
            renderer.setColor(DeviceUtil.END_COLORS.get("Plastic"));

            float angle, dx, dy;

            if (!appendingFromBegin) {

                if (hoveringOnEndpoint(CircuitScreen.camera) == 1) {
                    renderer.setColor(DeviceUtil.END_COLORS.get("SelectedPlastic"));
                }

                if (coordinates.size() == 1 || connection1 != null) {
                    angle = 0;
                } else {
                    angle = (float) Math.atan2(coordinates.get(1).x - coordinates.get(0).x, coordinates.get(0).y - coordinates.get(1).y);
                }

                dx = 40 * (float) Math.cos(angle);
                dy = 40 * (float) Math.sin(angle);

                renderer.rectLine(coordinates.get(0).x + dx, coordinates.get(0).y + dy, coordinates.get(0).x - dx, coordinates.get(0).y - dy, 50);
            }

            renderer.setColor(DeviceUtil.END_COLORS.get("Plastic"));

            if (!appendingFromEnd) {

                if (hoveringOnEndpoint(CircuitScreen.camera) == 2) {
                    renderer.setColor(DeviceUtil.END_COLORS.get("SelectedPlastic"));
                }

                if (coordinates.size() == 1 || connection2 != null) {
                    angle = 0;
                } else {
                    angle = (float) Math.atan2(coordinates.get(coordinates.size() - 2).x - coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y - coordinates.get(coordinates.size() - 2).y);
                }

                dx = 40 * (float) Math.cos(angle);
                dy = 40 * (float) Math.sin(angle);

                renderer.rectLine(coordinates.get(coordinates.size() - 1).x + dx, coordinates.get(coordinates.size() - 1).y + dy, coordinates.get(coordinates.size() - 1).x - dx, coordinates.get(coordinates.size() - 1).y - dy, 50);
            }
        }


    }

    public int hoveringOnEndpoint(CameraController cameraController) {
        // CHECK IF HOVERING ON ENDPOINT
        Vector2 vec = Tools.mouseScreenToWorld(cameraController);


        Vector2 c2 = coordinates.get(coordinates.size() - 1);
        Vector2 c = coordinates.get(0);

        if (new Circle(c2.x, c2.y, limit + 10f).contains(vec.x, vec.y)) {
            return 2;
        } else if (new Circle(c.x, c.y, limit + 10f).contains(vec.x, vec.y)) {
            return 1;
        }
        return 0;
    }

    @Override
    protected void drawNodes(ShapeRenderer renderer, ClippedCameraController cam, Color... color) {
        if (color.length > 0) {
            renderer.setColor(color[0]);
        }
        for (Vector2 coords : coordinates) {
            renderer.circle(coords.x, coords.y, limit3);
        }
        processNodes(renderer, cam);
    }

}