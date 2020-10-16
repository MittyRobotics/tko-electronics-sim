package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Radio extends Flippable {

    public Radio() {}

    public Radio(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.RADIO, addCrimped);


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
        return calculateDirection(cur, port, 60);
    }

    public String check() {
        if(getAllNull(0, 2)) {
            return "Radio is not connected";
        }

        if(getNull(2) || !(getOther(2) instanceof RoboRio)) {
            return "Radio is not connected to RoboRIO";
        }

        if(getNull(0) || getNull(1) || !(getOther(0) instanceof VoltageRegulatorModule) || !(getOther(1) instanceof VoltageRegulatorModule)) {
            return "Radio is not connected to VRM";
        }

        if(getNum(0) < 2 || getNum(0) > 5 || getNum(1) < 2 || getNum(1) > 5) {
            return "Radio should be connected to 12V/2A on VRM";
        }

        if(!((getNum(0) == 4 && getNum(1) == 5) || (getNum(0) == 2 || getNum(1) == 3))) {
            return "Radio improperly connected to VRM";
        }

        return null;
    }
}
