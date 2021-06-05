package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class NEO extends Flippable {

    public NEO() {}

    public NEO(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.NEO, addCrimped);


        for(JSONArray arr : pinDefs) {
            Sprite temp;
            if(connectors.size() == connNum) {
                break;
            }
            temp = new Sprite(new Texture(Gdx.files.internal("img/point.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            temp.setSize((Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(0) /2f, (Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(1) /2f);
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }


    public Vector2 calculate(int port) {
        return calculateDirection(cur, port);
    }

    public String check() {
        if(getNull(0) || getNull(1) || getNull(2) || !(getOther(0) instanceof Spark && getOther(1) instanceof Spark && getOther(2) instanceof Spark)) {
            return "NEO is not connected to SPARK";
        }

        if(getNum(0) != 2 || getNum(1) != 1 || getNum(2) != 0 ) {
            return "NEO improperly connected to SPARK";
        }

        return null;
    }
}
