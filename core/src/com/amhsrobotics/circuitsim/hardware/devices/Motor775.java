package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Motor775 extends Flippable {

    public Motor775() {}

    public Motor775(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.MOTOR775, addCrimped);

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
        return calculateDirection(cur, port);
    }

    public String check() {
        if(getNull(0) || getNull(1) || !(getOther(0) instanceof Talon && getOther(1) instanceof Talon)) {
            return "775 Motor is not connected to talon";
        }

        if(getNum(0) != 3 || getNum(1) != 2) {
            return "775 Motor improperly connected to talon";
        }

        return null;
    }

}
