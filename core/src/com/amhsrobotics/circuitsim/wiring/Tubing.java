package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class Tubing extends Cable {

    public Tubing() {}

    public Tubing(Vector2 startPoint, int count) {
        super(startPoint, count);
        gauge = 2;
        color = new Color(0.8f, 0.8f, 0.8f, 0.7f);
        hoverColor = Color.GRAY;
        nodeColor = Color.WHITE;
        //populateProperties();
    }

    public void populateProperties() {
        super.populateProperties("Tubing", false, false, true, ID);
    }



    @Override
    public void drawEndpoints(ShapeRenderer renderer) {
        renderer.setColor(DeviceUtil.COLORS.get("White"));
        if(!appendingFromBegin) {
            renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit3);
        }
        if(!appendingFromEnd) {
            renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit3);
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
