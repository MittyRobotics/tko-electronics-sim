package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;


public class VoltageRegulatorModule extends Flippable {

    public VoltageRegulatorModule() {
    }

    public VoltageRegulatorModule(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.VRM, addCrimped);

        for (JSONArray arr : pinDefs) {
            Sprite temp;
            if (connectors.size() == connNum) {
                break;
            }
            temp = new Sprite(new Texture(Gdx.files.internal("img/point.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            temp.setSize((Long) pinSizeDefs.get(pinDefs.indexOf(arr)).get(0), (Long) pinSizeDefs.get(pinDefs.indexOf(arr)).get(1));
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    public Vector2 calculate(int port) {
        if (port < 2) {
            return calculateDirection(cur + 2, port, 80);
        } else if (port < 10) {
            return calculateDirection(cur + 3, port, 80);
        } else {
            return calculateDirection(cur + 1, port, 80);
        }
    }

    public String check() {
        if (getNull(0) || getNull(1) || !(getOther(0) instanceof PowerDistributionPanel && getOther(1) instanceof PowerDistributionPanel)) {
            if (simLED) {
                resetLEDs();
                simLED = false;
            }
            return "VRM is not connected to PDP";
        }

        if (!((getNum(0) == 0 && getNum(1) == 1) || (getNum(0) == 2 && getNum(1) == 3))) {
            if (simLED) {
                resetLEDs();
                simLED = false;
            }
            return "VRM is improperly connected to PDP";
        }

        if (!simLED) {
            simLED = true;

            LEDs.get(0).setColor("green");
            LEDs.get(0).setStatus("Power On");
            LEDs.get(1).setColor("green");
            LEDs.get(1).setStatus("Power On");

        }

        return null;
    }


}
