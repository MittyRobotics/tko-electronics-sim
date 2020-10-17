package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class RoboRio extends Flippable {

    public RoboRio() {}

    public RoboRio(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.ROBORIO, addCrimped);

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
        if(port == 0 || port == 1) {
            return calculateDirection(cur+3, port, 80);
        } else if(port == 2 || port == 3) {
            return calculateDirection(cur+3, port, 40);
        } else {
            return calculateDirection(cur + 2, port, 120);
        }
    }

    public String check() {
        if(getNull(0) || getNull(1) || !(getOther(0) instanceof PowerDistributionPanel && getOther(1) instanceof PowerDistributionPanel)) {
            return "RoboRIO is not connected to PDP";
        }

        if(getNum(0) != 4 || getNum(1) != 5) {
            return "RoboRIO is improperly connected to PDP";
        }

        if(!simLED) {
            simLED = true;
            LEDs.get(0).setColor("green");
            LEDs.get(0).setStatus("Power is good");
            LEDs.get(1).setColor("green");
            LEDs.get(1).setStatus("On");
            LEDs.get(1).blinkTime(10, 80);
            LEDs.get(5).setColor("yellow");
            LEDs.get(5).setStatus("Robot Enabled");
        }

        if(getOther(4) instanceof Radio && getOther(4).check() == null) {
            LEDs.get(3).setColor("green");
            LEDs.get(3).setStatus("Connected");
            LEDs.get(4).setColor("green");
            LEDs.get(4).setStatus("Teleop Enabled");
        }

        /*
        if(getNull(36) || !(getOther(36) instanceof Radio)) {
            return "RoboRIO is not connected to radio";
        }*/

        return null;
    }

}
