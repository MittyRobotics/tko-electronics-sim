package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class TConnector extends Flippable {

    public TConnector() {}

    public TConnector(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.TCONNECTOR, addCrimped);


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
        if(port == 0) {
            return calculateDirection(cur, port, 100);
        } else if (port == 1) {
            return calculateDirection(cur+1, port, 100);
        } else {
            return calculateDirection(cur+3, port, 100);
        }
    }

    public Hardware getOtherPiston() {
        return getOther(0);
    }

    public int getNumPiston() {
        return getNum(0);
    }

    public String check() {
        if(getNull(0) || getNull(1) || getNull(2)) {
            return "T Connector not connected";
        }
        return null;
    }
}
