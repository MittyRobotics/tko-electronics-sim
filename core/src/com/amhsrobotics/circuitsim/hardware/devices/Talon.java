package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Talon extends Flippable {

    public Talon() {}

    public Talon(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.TALON, addCrimped);

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
        if(port >= 2 && port < 4) {
            return calculateDirection(cur, port);
        } else {
            return calculateDirection(2+cur, port);
        }
    }

    public String check() {
        if(getNull(0) || getNull(1) || !(getOther(0) instanceof PowerDistributionPanel && getOther(1) instanceof PowerDistributionPanel)) {
            return "Talon is not connected to PDP";
        }

        if(getNum(1) <= 41 && getNum(1) >= 34) {
            if(getNum(1) % 2 != 1 || getNum(0) != getNum(1) - 1) {
                return "Talon incorrectly connected to PDP";
            }
        }

        if(getNum(1) <= 17 && getNum(1) >= 10) {
            if(getNum(1) % 2 != 1 || getNum(0) != getNum(1) - 1) {
                return "Talon incorrectly connected to PDP";
            }
        }

        if(getNum(1) <= 33 && getNum(1) >= 26) {
            if(getNum(1) % 2 != 0 || getNum(0) != getNum(1) + 1) {
                return "Talon incorrectly connected to PDP";
            }
        }

        if(getNum(1) <= 25 && getNum(1) >= 18) {
            if(getNum(1) % 2 != 0 || getNum(0) != getNum(1) + 1) {
                return "Talon incorrectly connected to PDP";
            }
        }


        return null;
    }

}
