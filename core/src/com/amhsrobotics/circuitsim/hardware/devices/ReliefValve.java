package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class ReliefValve extends Flippable {

    public ReliefValve() {
    }

    public ReliefValve(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.RELIEFVALVE, addCrimped);


        for (JSONArray arr : pinDefs) {
            Sprite temp;
            if (connectors.size() == connNum) {
                break;
            }
            temp = new Sprite(new Texture(Gdx.files.internal("img/point.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            temp.setSize((Long) pinSizeDefs.get(pinDefs.indexOf(arr)).get(0), (Long) pinSizeDefs.get(pinDefs.indexOf(arr)).get(1));
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    public Vector2 calculate(int port) {
        return calculateDirection(cur + 1, port, 100);
    }

    public String check() {
        if (!(getOther(0) instanceof PressureSwitch)) {
            return "Manual relief valve not conected to pressure switch";
        }

        return null;
    }
}
