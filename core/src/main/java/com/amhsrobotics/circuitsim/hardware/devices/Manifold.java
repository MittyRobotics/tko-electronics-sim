package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
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

    public Manifold() {
    }

    public Manifold(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.MANIFOLD, addCrimped);

        int temp = 0;
        for (JSONArray arr : pinDefs) {
            if (temp == 0) {
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
        for (Sprite temp : solenoids) {
            temp.rotate(90);
        }
    }


    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(batch, renderer, camera);
    }

    public Vector2 calculate(int port) {
        if (port == 0) {
            return calculateDirection(cur + 3, port, 100);
        } else if (port > 8 && (port % 4 == 3 || port % 4 == 2)) {
            return calculateDirection(cur + 2, port, 50);
        }
        return calculateDirection(cur, port, 50);
    }

    public String checkSolenoid(int port) {
        int temp = (port + 1) * 4;

        if (!(getOther(temp) instanceof PneumaticsControlModule && getOther(temp + 1) instanceof PneumaticsControlModule && getOther(temp + 2) instanceof PneumaticsControlModule && getOther(temp + 3) instanceof PneumaticsControlModule)) {
            return "Double solenoid " + port + " not connected to PCM";
        }

        if (!((18 <= getNum(temp + 1) && getNum(temp + 1) <= 25 && getNum(temp + 1) % 2 == 0 && getNum(temp) == getNum(temp + 1) + 1 && 10 <= getNum(temp + 3) && getNum(temp + 3) <= 17 && getNum(temp + 3) % 2 == 1 && getNum(temp + 2) == getNum(temp + 3) - 1 && getNum(temp + 1) == getNum(temp + 2) + 8)
                || (18 <= getNum(temp + 3) && getNum(temp + 3) <= 25 && getNum(temp + 3) % 2 == 0 && getNum(temp + 2) == getNum(temp + 3) + 1 && 10 <= getNum(temp + 1) && getNum(temp + 1) <= 17 && getNum(temp + 1) % 2 == 1 && getNum(temp) == getNum(temp + 1) - 1 && getNum(temp + 1) == getNum(temp + 2) - 8))) {
            return "Double solenoid " + port + " incorrectly connected to PCM";
        }

        return null;
    }

    public String check() {
        if (!(getOther(0) instanceof Regulator)) {
            return "Manifold not connected to regulator";
        }

        for (int i = 1; i < 8; ++i) {
            if (getOther(i) != null && !(getOther(i) instanceof TConnector && getOther(i).getOther(1) instanceof Piston && getOther(i).getOther(2) instanceof Piston)) {
                return "Manifold port " + i + " not connected to piston";
            } else if (getOther(i) != null) {
                if (checkSolenoid(i) != null) {
                    return checkSolenoid(i);
                }
            }
        }

        return null;
    }
}
