package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;
import java.util.HashMap;

public class Cable implements Disposable {

    private float voltage;
    private float gauge;
    private Color color;
    private ArrayList<Vector2> coordinates;
    private HashMap<Float, Float> connections;
    private float x1, x2, y1, y2, a;


    public Cable(float voltage, float gauge, ArrayList<Vector2> coordinates, HashMap<Float, Float> connections) {
        this.voltage = voltage;
        this.gauge = gauge;
        this.coordinates = coordinates;
        this.connections = connections;
        this.color = Color.GREEN;
    }

    public Cable(float voltage, float gauge) {
        this(voltage, gauge, new ArrayList<Vector2>(), new HashMap<Float, Float>());
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addCoordinates(Vector2 point) {
        this.coordinates.add(point);
    }

    public void renderHover(ModifiedShapeRenderer renderer, ClippedCameraController camera, float x, float y) {
        if(coordinates.size() > 0) {
            renderer.setProjectionMatrix(camera.getCamera().combined);
            renderer.begin(ShapeRenderer.ShapeType.Filled);

            renderer.setColor(color);

            Vector2 vec = new Vector2(x, y);
            renderer.rectLine(coordinates.get(coordinates.size() - 1), vec, 3f);
            renderer.end();
        }
    }

    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        renderer.setProjectionMatrix(camera.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(color);
        for(int i = 0; i < coordinates.size() - 1; ++i) {
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), 3f);
        }
        renderer.end();
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

    public boolean getPressed(float x, float y) {
        for(int i = 0; i < coordinates.size() - 1; ++i) {
            x1 = coordinates.get(i).x;
            x2 = coordinates.get(i + 1).x;
            y1 = coordinates.get(i).y;
            y2 = coordinates.get(i + 1).y;

            a = -1*((y2-y1)/(x2-x1));

            if((float)(Math.abs(x*a+y+(((y2-y1)/(x2-x1))*x1 - y1))/Math.sqrt(a*a+1)) < 5) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void dispose() {

    }
}
