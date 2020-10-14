package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Spark extends Flippable {

    public Spark() {}

    public Spark(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.SPARK, addCrimped);


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
        if(port < 3) {
            return calculateDirection(cur+2, port);
        } else if (port < 5) {
            return calculateDirection(cur, port);
        } else {
            return calculateDirection(cur+3, port);
        }
    }

    public String check() {
        if(getNull(4) || getNull(3) || !(getOther(4) instanceof PowerDistributionPanel && getOther(3) instanceof PowerDistributionPanel)) {
            return "SPARK is not connected to PDP";
        }

        if(getNum(3) <= 41 && getNum(3) >= 34) {
            if(getNum(3) % 2 != 1 || getNum(4) != getNum(3) - 1) {
                return "SPARK incorrectly connected to PDP";
            }
        }

        if(getNum(3) <= 17 && getNum(3) >= 10) {
            if(getNum(3) % 2 != 1 || getNum(4) != getNum(3) - 1) {
                return "SPARK incorrectly connected to PDP";
            }
        }

        if(getNum(3) <= 33 && getNum(3) >= 26) {
            if(getNum(3) % 2 != 0 || getNum(4) != getNum(3) + 1) {
                return "SPARK incorrectly connected to PDP";
            }
        }

        if(getNum(3) <= 25 && getNum(3) >= 18) {
            if(getNum(3) % 2 != 0 || getNum(4) != getNum(3) + 1) {
                return "SPARK incorrectly connected to PDP";
            }
        }


        return null;
    }
}
