package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class PneumaticsControlModule extends Flippable {

    public PneumaticsControlModule() {}

    public PneumaticsControlModule(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.PCM, addCrimped);

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
        if (port == 0 || port == 1) {
            return calculateDirection(cur+3, port, 100);
        } else if (port >= 2 && port <= 5) {
            return calculateDirection(cur+2, port, 100);
        } else if (port >= 6 && port <= 9) {
            return calculateDirection(cur, port, 100);
        } else if (port >= 10 && port < 18) {
            return calculateDirection(cur+2, port, 100);
        } else {
            return calculateDirection(cur, port, 100);
        }
    }

    public String check() {
        if(getNull(0) || getNull(1) || !(getOther(0) instanceof PowerDistributionPanel && getOther(1) instanceof PowerDistributionPanel)) {
            return "PCM is not connected to PDP";
        }

        if(!((getNum(1) == 0 && getNum(0) == 1) || (getNum(1) == 2 && getNum(0) == 3))) {
            return "PCM is improperly connected to PDP";
        }

        if(!getAllNull(2, 5)) {
            if(!getAllNotNull(2, 5)) {
                return "CAN chain should reach RoboRIO";
            } else if(getOther(2) instanceof RoboRio && getOther(3) instanceof RoboRio && (getNum(2) != 3 || getNum(3) != 2)) {
                return "CAN chain improperly connected to RoboRIO";
            } else if (getOther(5) instanceof RoboRio && getOther(4) instanceof RoboRio && (getNum(5) != 3 || getNum(4) != 2)) {
                return "CAN chain improperly connected to RoboRIO";
            }
        }

        return null;
    }

}
