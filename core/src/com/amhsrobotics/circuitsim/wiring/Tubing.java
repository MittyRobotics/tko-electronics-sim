package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class Tubing extends Cable {

    public Tubing() {}

    public Tubing(Vector2 startPoint, int count) {
        super(startPoint, count);
        gauge = 2;
        color = new Color(0.8f, 0.8f, 0.8f, 0.7f);
        hoverColor = Color.GRAY;
        nodeColor = Color.WHITE;
        populateProperties();
    }

    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("Tubing - ID " + ID, CircuitGUIManager.propertiesBox.LABEL), true, 2);
    }

    @Override
    public void render(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        if(nodeColor == null) {
            nodeColor = Color.WHITE;
        }

        if(CableManager.currentCable == this) {

            Vector2 vec2 = Tools.mouseScreenToWorld(camera);

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            if (appendingFromEnd && !disableEnd) {
                // draw potential cable wire
                renderer.setColor(color);
                renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), limit);
                renderer.setColor(Color.WHITE);
                renderer.circle(vec2.x, vec2.y, limit + 2f);
            } else if (appendingFromBegin && !disableBegin) {
                // draw potential cable wire
                renderer.setColor(color);
                renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                renderer.setColor(Color.WHITE);
                renderer.circle(vec2.x, vec2.y, limit + 2f);
            }

            renderer.end();

        }

        super.render(renderer, camera);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.WHITE);
        drawEndpoints(renderer);
        renderer.end();
    }


    @Override
    public void drawEndpoints(ShapeRenderer renderer) {
        renderer.setColor(DeviceUtil.COLORS.get("White"));
        if(!appendingFromBegin) {
            renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit + 2f);
        }
        if(!appendingFromEnd) {
            renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit + 2f);
        }

    }

    @Override
    protected void drawNodes(ShapeRenderer renderer, ClippedCameraController cam, Color... color) {
        renderer.setColor(Color.WHITE);
        for(Vector2 coords : coordinates) {
            renderer.circle(coords.x, coords.y, limit3);
        }
        processNodes(renderer, cam);
    }

}
