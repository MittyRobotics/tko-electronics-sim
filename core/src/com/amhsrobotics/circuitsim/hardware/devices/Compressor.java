package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Compressor extends Flippable {

    public Compressor() {
    }

    public Compressor(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.COMPRESSOR, addCrimped);


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
        return calculateDirection(cur + 1, port, 100);
    }

    public String check() {
        if ((!(getNotNull(1) && getOther(1) instanceof PneumaticsControlModule) || !(getNotNull(2) && getOther(2) instanceof PneumaticsControlModule))) {
            return "Compressor not connected to PCM";
        }

        if (getNum(1) != 9 || getNum(2) != 8) {
            return "Compressor incorrectly connected to PCM";
        }

        if (!(getNotNull(0) && getOther(0) instanceof Tank)) {
            return "Compressor not connected to tanks";
        }
        return null;
    }
}
