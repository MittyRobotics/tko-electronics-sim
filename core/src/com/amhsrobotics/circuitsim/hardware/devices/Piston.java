package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Piston extends Flippable {

    public Piston() {
    }

    public Piston(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.PISTON, addCrimped);


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
        return calculateDirection(cur, port, 100);
    }

    public String check() {
        if (!(getOther(0) instanceof TConnector && getOther(1) instanceof TConnector)) {
            return "Piston not connected to T connector";
        }

        if (getNum(0) == 0 || getNum(1) == 0) {
            return "Piston incorrectly connected to T connector";
        }

        if (!(((TConnector) getOther(0)).getOtherPiston() instanceof Manifold)) {
            return "Piston not connected to manifold";
        }

        if ((((TConnector) getOther(0)).getNumPiston() == 0)) {
            return "Piston connected to wrong manifold port";
        }

        return null;
    }
}
