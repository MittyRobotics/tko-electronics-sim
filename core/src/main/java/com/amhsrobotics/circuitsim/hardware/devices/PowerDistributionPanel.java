package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;


public class PowerDistributionPanel extends Flippable {

    public PowerDistributionPanel() {
    }

    public PowerDistributionPanel(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.PDP, addCrimped);


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
        if (port >= 0 && port <= 5) {
            return calculateDirection(cur + 3, port, 100);
        } else if (port >= 6 && port < 10) {
            return calculateDirection(cur + 2, port, 100);
        } else if (port >= 10 && port < 18) {
            return calculateDirection(cur, port, 50);
        } else if (port >= 18 && port < 26) {
            return calculateDirection(cur + 2, port, 50);
        } else if (port >= 26 && port < 34) {
            return calculateDirection(cur + 2, port, 50);
        } else if (port == 42 || port == 43) {
            return calculateDirection(cur + 1, port, 100);
        } else {
            return calculateDirection(cur, port, 50);
        }
    }

    public String check() {
        if (getNull(42) || getNull(43)) {
            if (simLED) {
                resetLEDs();
                simLED = false;
            }
            return "PDP is not connected to power";
        }

        if (!(getOther(42) instanceof Breaker)) {
            if (simLED) {
                resetLEDs();
                simLED = false;
            }
            return "Positive terminal should be connected to main breaker";
        }

        if (!(getOther(43) instanceof Battery)) {
            if (simLED) {
                resetLEDs();
                simLED = false;
            }
            return "Negative terminal should be connected to battery";
        }

        if (getNum(43) != 1) {
            if (simLED) {
                resetLEDs();
                simLED = false;
            }
            return "PDP is connected to the wrong battery terminal";
        }

        if (!simLED) {
            simLED = true;

            LEDs.get(0).setColor("green");
            LEDs.get(0).blink(30);
            LEDs.get(0).setStatus("No Fault - Robot Enabled");

        }

        if (((getOther(6) instanceof Talon || getOther(6) instanceof Falcon || getOther(6) instanceof NEO) && getOther(6).check() == null) || ((getOther(8) instanceof Talon || getOther(8) instanceof Falcon || getOther(8) instanceof NEO) && getOther(8).check() == null)) {
            LEDs.get(1).setColor("green");
            LEDs.get(1).setStatus("No Fault - Robot Enabled");
        } else {
            LEDs.get(1).setColor("red");
            LEDs.get(1).setStatus("No CAN Comm");
        }

        return null;

    }

}
