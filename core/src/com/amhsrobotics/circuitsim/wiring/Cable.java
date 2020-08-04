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

    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        renderer.setProjectionMatrix(camera.getCamera().combined);

        renderer.begin(ShapeRenderer.ShapeType.Line);

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

    @Override
    public void dispose() {

    }
}
