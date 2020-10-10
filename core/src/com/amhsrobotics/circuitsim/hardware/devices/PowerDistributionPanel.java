package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;


public class PowerDistributionPanel extends Flippable {

    public PowerDistributionPanel() {}

    public PowerDistributionPanel(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.PDP, addCrimped);


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
        if(port >= 0 && port <= 5) {
            return calculateDirection(cur+3, port, 100);
        } else if (port >= 6 && port < 10) {
            return calculateDirection(cur+2, port, 100);
        } else if (port >= 10 && port < 18) {
            return calculateDirection(cur, port, 50);
        } else if (port >= 18 && port < 26) {
            return calculateDirection(cur+2, port, 50);
        } else if (port >= 26 && port < 34) {
            return calculateDirection(cur+2, port, 50);
        } else if (port == 42 || port == 43) {
            return calculateDirection(cur+1, port, 100);
        } else {
            return calculateDirection(cur, port, 50);
        }
    }

}
