package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class PneumaticsControlModule extends Flippable {

    public PneumaticsControlModule() {
    }

    public PneumaticsControlModule(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.PCM, addCrimped);

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
        if (port == 0 || port == 1) {
            return calculateDirection(cur + 3, port, 80);
        } else if (port >= 2 && port <= 5) {
            return calculateDirection(cur + 2, port, 80);
        } else if (port >= 6 && port <= 9) {
            return calculateDirection(cur, port, 80);
        } else if (port >= 10 && port <= 17) {
            return calculateDirection(cur + 2, port, 80);
        } else {
            return calculateDirection(cur, port, 80);
        }
    }

    public String check() {
        if (getNull(0) || getNull(1) || !(getOther(0) instanceof PowerDistributionPanel && getOther(1) instanceof PowerDistributionPanel)) {
            if (simLED) {
                resetLEDs();
                simLED = false;
            }
            return "PCM is not connected to PDP";
        }

        if (!((getNum(1) == 0 && getNum(0) == 1) || (getNum(1) == 2 && getNum(0) == 3))) {
            if (simLED) {
                resetLEDs();
                simLED = false;
            }
            return "PCM is improperly connected to PDP";
        }

        if (getOther(8) instanceof Compressor && getOther(9) instanceof Compressor) {
            if (getNum(8) == 2 && getNum(9) == 1 && getOther(9).check() == null) {
                LEDs.get(1).setColor("green");
                LEDs.get(1).setStatus("Compressor Connected");
            } else {
                LEDs.get(1).setColor("red");
                LEDs.get(1).setStatus("Compressor Error");
            }
        }

        if (!getAllNull(2, 5)) {
            if (!LEDs.get(0).blinking) {
                LEDs.get(0).setColor("red");
                LEDs.get(0).blink(50);
                LEDs.get(0).setStatus("No Can Comm");
            }

            if (!getAllNotNull(2, 5)) {
                return "CAN chain should reach RoboRIO";
            } else if (getOther(2) instanceof RoboRio && getOther(3) instanceof RoboRio && (getNum(2) != 3 || getNum(3) != 2)) {
                return "CAN chain improperly connected to RoboRIO";
            } else if (getOther(5) instanceof RoboRio && getOther(4) instanceof RoboRio && (getNum(4) != 3 || getNum(5) != 2)) {
                return "CAN chain improperly connected to RoboRIO";
            }

            LEDs.get(0).setColor("green");
            LEDs.get(0).setStatus("No Fault - Robot Enabled");

        } else {
            if (!LEDs.get(0).blinking) {
                LEDs.get(0).setColor("red");
                LEDs.get(0).blink(50);
                LEDs.get(0).setStatus("No Can Comm");
            }
        }

        return null;
    }

}
