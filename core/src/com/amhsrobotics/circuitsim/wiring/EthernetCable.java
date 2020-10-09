package com.amhsrobotics.circuitsim.wiring;

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

    public EthernetCable() {}

    public EthernetCable(Vector2 startPoint, int count) {
        super(startPoint, count);
        gauge = 13;
        color = DeviceUtil.COLORS.get("Orange");
        hoverColor = Color.GRAY;
        //populateProperties();
    }

    public void populateProperties() {
        super.populateProperties("Ethernet", true, false, true, ID);
    }

    @Override
    public void render(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

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
                renderer.circle(vec2.x, vec2.y, limit + 5f);
            } else if (appendingFromBegin && !disableBegin) {
                // draw potential cable wire
                renderer.setColor(color);
                renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                renderer.setColor(Color.WHITE);
                renderer.circle(vec2.x, vec2.y, limit + 5f);
            }

            renderer.end();

        }

        super.render(renderer, camera);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        drawEndpoints(renderer);
        renderer.end();
    }

    @Override
    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(renderer, camera);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        drawEndpoints(renderer);
        renderer.end();
    }


    @Override
    public void drawEndpoints(ShapeRenderer renderer) {
        renderer.setColor(DeviceUtil.COLORS.get("White"));
        if(!appendingFromBegin) {
            renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit + 5f);
        }
        if(!appendingFromEnd) {
            renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit + 5f);
        }

    }

    public int hoveringOnEndpoint(CameraController cameraController) {
        // CHECK IF HOVERING ON ENDPOINT
        Vector2 vec = Tools.mouseScreenToWorld(cameraController);


        Vector2 c2 = coordinates.get(coordinates.size() - 1);
        Vector2 c = coordinates.get(0);

        if(new Circle(c2.x, c2.y, limit + 5f).contains(vec.x, vec.y)) {
            return 2;
        } else if(new Circle(c.x, c.y, limit + 5f).contains(vec.x, vec.y)) {
            return 1;
        }
        return 0;
    }

    @Override
    protected void drawNodes(ShapeRenderer renderer, ClippedCameraController cam, Color... color) {
        if(color.length > 0) {
            renderer.setColor(color[0]);
        }
        for(Vector2 coords : coordinates) {
            renderer.circle(coords.x, coords.y, limit3);
        }
        processNodes(renderer, cam);
    }
}
