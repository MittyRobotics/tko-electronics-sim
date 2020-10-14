package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Falcon extends Flippable {

    public Falcon() {}

    public Falcon(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.FALCON, addCrimped);


        for(JSONArray arr : pinDefs) {
            Sprite temp;
            if(connectors.size() == connNum) {
                break;
            }
            temp = new Sprite(new Texture(Gdx.files.internal("img/point.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            temp.setSize((Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(0) /2f, (Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(1) /2f);
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    public Vector2 calculate(int port) {
        if(port == 0) {
            return calculateDirection(3+cur, port);
        } else if (port == 1) {
            return calculateDirection(1+cur, port);
        } else {
            return calculateDirection(cur, port);
        }
    }

    public String check() {
        if(getNull(0) || getNull(1) || !(getOther(0) instanceof PowerDistributionPanel && getOther(1) instanceof PowerDistributionPanel)) {
            return "Falcon is not connected to PDP";
        }

        if(getNum(0) <= 41 && getNum(0) >= 34) {
            if(getNum(0) % 2 != 1 || getNum(1) != getNum(0) - 1) {
                return "Falcon incorrectly connected to PDP";
            }
        }

        if(getNum(0) <= 17 && getNum(0) >= 10) {
            if(getNum(0) % 2 != 1 || getNum(1) != getNum(0) - 1) {
                return "Falcon incorrectly connected to PDP";
            }
        }

        if(getNum(0) <= 33 && getNum(0) >= 26) {
            if(getNum(0) % 2 != 0 || getNum(1) != getNum(0) + 1) {
                return "Falcon incorrectly connected to PDP";
            }
        }

        if(getNum(0) <= 25 && getNum(0) >= 18) {
            if(getNum(0) % 2 != 0 || getNum(1) != getNum(0) + 1) {
                return "Falcon incorrectly connected to PDP";
            }
        }


        return null;
    }
}
