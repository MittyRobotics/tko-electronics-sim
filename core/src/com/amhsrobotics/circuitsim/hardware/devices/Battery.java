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
        //Gdx.app.log(connections.get(0).getOtherConnectionNum(this)+"", connections.get(1).getOtherConnectionNum(this)+"");

        if(connections.get(0) == null || connections.get(1) == null || connections.get(0).getOtherConnection(this) == null || connections.get(1).getOtherConnection(this) == null) {
            return "Battery is not connected";
        }

        if(!(connections.get(0).getOtherConnection(this) instanceof Breaker)) {
            return "Positive terminal should be connected to the main breaker";
        }
        if(!(connections.get(1).getOtherConnection(this) instanceof PowerDistributionPanel)) {
            return "Negative terminal should be connected to the PDP";
        }

        if(connections.get(1).getOtherConnectionNum(this) != 43) {
            return "Negative terminal should be connected to the negative port on the PDP";
        }

        return null;
    }

}
