package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Solenoid extends Flippable {

    public Solenoid() {}

    public Solenoid(Vector2 position, HardwareType type, boolean... addCrimped) {
        super(position, type, addCrimped);


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
        if (port == 0 || port == 1) {
            return calculateDirection(3+cur, port, 100);
        } else {
            return calculateDirection(1+cur, port, 100);
        }


    }

    public String check() {
        if(!(getOther(0) instanceof PneumaticsControlModule && getOther(1) instanceof PneumaticsControlModule && getOther(2) instanceof PneumaticsControlModule && getOther(3) instanceof PneumaticsControlModule)) {
            return "Double solenoid not connected to PCM";
        }

        if(!((18 <= getNum(0) && getNum(0) <= 25 && getNum(0) % 2 == 0 && getNum(1) == getNum(0) + 1 && 10 <= getNum(2) && getNum(2) <= 17 && getNum(2) % 2 == 1 && getNum(3) == getNum(2) - 1 && getNum(0) == getNum(3) + 8)
                ||(18 <= getNum(2) && getNum(2) <= 25 && getNum(2) % 2 == 0 && getNum(3) == getNum(2) + 1 && 10 <= getNum(0) && getNum(0) <= 17 && getNum(0) % 2 == 1 && getNum(1) == getNum(0) - 1 && getNum(0) == getNum(3) - 8))) {
            return "Double solenoid incorrectly connected to PCM";
        }

        return null;
    }
}
