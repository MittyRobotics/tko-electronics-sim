package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private ArrayList<Vector2> coordinates;
    private HashMap<Float, Float> connections;
    private float x1, x2, y1, y2, a;


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

    public void setColor(Color color) {
        this.color = color;
    }

    public void addCoordinates(Vector2 point) {
        this.coordinates.add(point);
    }

    public void renderHover(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.getCamera().unproject(vec);

        renderer.setProjectionMatrix(camera.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(color);
        for (int i = 0; i < coordinates.size() - 1; ++i) {

            renderer.setColor(new Color(217 / 255f, 233 / 255f, 217 / 255f, 1));
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), 6f);

            renderer.setColor(color);
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), 3f);
        }
        renderer.end();
    }

    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        renderer.setProjectionMatrix(camera.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(color);
        for(int i = 0; i < coordinates.size() - 1; ++i) {
            if(CableManager.currentCable != null) {
                if(CableManager.currentCable == this) {
                    renderer.setColor(new Color(217/255f, 233/255f, 217/255f, 1));
                    renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), 6f);
                }
            }
            renderer.setColor(color);
            renderer.rectLine(coordinates.get(i), coordinates.get(i + 1), 3f);
        }
        if(Constants.placing_object) {
            Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.getCamera().unproject(vec);

            renderer.setColor(color);
            renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec.x, vec.y), 3f);
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
