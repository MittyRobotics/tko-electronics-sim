package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.screens.CircuitScreen;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;


public class CrimpedCable extends Cable {

    public CrimpedCable(int gauge, int id_) {
        super(new Vector2(0, 0), id_);
        this.gauge = gauge;

        appendingFromBegin = false;
        appendingFromEnd = false;

        // Will be attached to hardware in hardware, not here
    }

    @Override
    public void populateProperties(String title, boolean enableColor, boolean enableGauge, boolean enableConnections, int ID) {
        super.populateProperties("Crimped", true, false, true, -ID);
    }

    @Override
    public void render(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        super.render(renderer, camera);

        if(CableManager.currentCable == this) {

            Vector2 vec2 = Tools.mouseScreenToWorld(camera);

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            if (appendingFromEnd && !disableEnd) {
                renderer.setColor(DeviceUtil.END_COLORS.get("DarkPlastic"));
                float angle, dx, dy;

                angle = (float) Math.atan2(coordinates.get(coordinates.size() - 1).x - vec2.x, vec2.y - coordinates.get(coordinates.size() - 1).y);

                dx = 20*(float) Math.cos(angle);
                dy = 20*(float) Math.sin(angle);

                renderer.rectLine(vec2.x + dx, vec2.y + dy, vec2.x - dx, vec2.y - dy, 30);

            }

            renderer.end();

        }

    }

    @Override
    public void drawEndpoints(ShapeRenderer renderer) {
            renderer.setColor(DeviceUtil.END_COLORS.get("DarkPlastic"));

            if(color2 != null) {
                renderer.rect(coordinates.get(color2n).x-20, coordinates.get(color2n).y-20, 40, 40);
            }


            float angle, dx, dy;

            if (!appendingFromEnd && !disableEnd) {

                if (hoveringOnEndpoint(CircuitScreen.camera) == 2) {
                    renderer.setColor(DeviceUtil.END_COLORS.get("SelectedDarkPlastic"));
                }

                if (coordinates.size() == 1) {
                    angle = 0;
                } else {
                    angle = (float) Math.atan2(coordinates.get(coordinates.size() - 2).x - coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y - coordinates.get(coordinates.size() - 2).y);
                }

                dx = 20 * (float) Math.cos(angle);
                dy = 20 * (float) Math.sin(angle);

                renderer.rectLine(coordinates.get(coordinates.size() - 1).x + dx, coordinates.get(coordinates.size() - 1).y + dy, coordinates.get(coordinates.size() - 1).x - dx, coordinates.get(coordinates.size() - 1).y - dy, 30);
            }


    }


}