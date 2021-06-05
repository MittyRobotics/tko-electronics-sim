package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class PressureSwitch extends Flippable {

    public PressureSwitch() {}

    public PressureSwitch(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.PRESSURESWITCH, addCrimped);


        for(JSONArray arr : pinDefs) {
            Sprite temp;
            if(connectors.size() == connNum) {
                break;
            }
            temp = new Sprite(new Texture(Gdx.files.internal("img/point.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            temp.setSize((Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(0), (Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(1));
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    public Vector2 calculate(int port) {
        if(port < 2) {
            return calculateDirection(cur+1, port, 100);
        } else if (port == 2) {
            return calculateDirection(cur, port, 100);
        } else {
            return calculateDirection(cur+2, port, 100);
        }
    }

    public String check() {
        if(!(getOther(0) instanceof PneumaticsControlModule && getOther(1) instanceof PneumaticsControlModule)) {
            return "Pressure switch not connected to PCM";
        }

        if(!(getNum(0) == 6 && getNum(1) == 7)) {
            return "Pressure switch incorrectly connected to PCM";
        }

        if(!(getOther(2) instanceof ReliefValve || getOther(3) instanceof ReliefValve)) {
            return "Pressure switch not connected to manual relief valve";
        }

        if(!(getOther(2) instanceof TConnector || getOther(3) instanceof TConnector)) {
            return "Pressure switch not connected to T Connector";
        }

        if(!(getNum(2) == 0 && getNum(3) == 0)) {
            return "Pressure switch incorrectly connected to T Connector";
        }

        return null;
    }
}
