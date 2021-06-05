package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Breaker extends Flippable {

    public Breaker() {}

    public Breaker(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.BREAKER, addCrimped);


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
        if(port == 1) {
            return calculateDirection(cur, port, 100);
        } else {
            return calculateDirection(cur+2, port, 100);
        }
    }

    public String check() {

        if(getNull(0) && getNull(1)) {
            return "Breaker is not connected";
        }

        if(!(getNotNull(0) && getOther(0) instanceof PowerDistributionPanel || getNotNull(1) && getOther(1) instanceof PowerDistributionPanel)) {
            return "Breaker is not connected to PDP";
        }

        if(!(getNotNull(0) && getOther(0) instanceof Battery || getNotNull(1) && getOther(1) instanceof Battery)) {
            return "Breaker is not connected to battery";
        }

        if(getNotNull(0) && getNum(0) != 42 && getNotNull(1) && getNum(1) != 42) {
            return "Breaker connected to wrong PDP port";
        }

        if(getNotNull(0) && getNum(0) != 0 && getNotNull(1) && getNum(1) != 0) {
            return "Breaker connected to wrong battery port";
        }

        return null;
    }

}
