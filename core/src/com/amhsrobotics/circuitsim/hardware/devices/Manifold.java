package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;

import java.util.ArrayList;

public class Manifold extends Flippable {

    public ArrayList<Sprite> solenoids = new ArrayList<>();

    public Manifold() {}

    public Manifold(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.MANIFOLD, addCrimped);

        int temp = 0;
        for(JSONArray arr : pinDefs) {
            if(temp == 0) {
                temp = 1;
            } else {
                Sprite tempw;
                tempw = new Sprite(new Texture(Gdx.files.internal("img/hardware/doublesolenoid.png")));
                tempw.setCenter(position.x + (Long) arr.get(0), position.y);
                tempw.rotate(90);
                solenoids.add(tempw);
            }
        }


        initConnections();
        initEnds();
    }

    public void rotateThis() {
        super.rotateThis();
        for(Sprite temp : solenoids) {
            temp.rotate(90);
        }
    }


    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(batch, renderer, camera);
    }

    public Vector2 calculate(int port) {
        if(port == 0) {
            return calculateDirection(cur+3, port, 100);
        } else if (port > 8 && (port % 4 == 3 || port % 4 == 2)) {
            return calculateDirection(cur+2, port, 50);
        }
        return calculateDirection(cur, port, 50);
    }
}
