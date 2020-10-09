package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
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
        if(port == 0) {
            return calculateDirection(cur+2, port, 40);
        } else if (port == 1) {
            return calculateDirection(cur+1, port, 40);
        } else if(port >= 2 && port <= 15) {
            return calculateDirection(cur+3, port, 40);
        } else if(port >= 16 && port <= 24) {
            return calculateDirection(cur, port, 40);
        } else if(port == 36) {
            return calculateDirection(cur+2, port, 120);
        } else {
            return calculateDirection(cur+1, port, 40);
        }
    }

}
