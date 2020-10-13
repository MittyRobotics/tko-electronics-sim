package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Battery extends Flippable {

    public Battery() {}

    public Battery(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.BATTERY, addCrimped);


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
        return calculateDirection(cur+2, port, 100);
    }

    public String check() {

        if(getNull(0) && getNull(1)) {
            return "Battery is not connected";
        }

        if(getNull(0) || !(getOther(0) instanceof Breaker)) {
            return "Positive terminal is not connected to main breaker";
        }

        if(getNull(1)  || !(getOther(1) instanceof PowerDistributionPanel)) {
            return "Negative terminal is not connected to PDP";
        }

        if(getNum(1) != 43) {
            return "Negative terminal should be connected to the negative port on the PDP";
        }

        return null;
    }

}
