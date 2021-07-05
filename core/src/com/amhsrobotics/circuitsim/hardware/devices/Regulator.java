package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;


public class Regulator extends Flippable {

    public Regulator() {
    }

    public Regulator(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.REGULATOR, addCrimped);

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
        if (port == 1) {
            return calculateDirection(cur + 3, port, 80);
        } else {
            return calculateDirection(cur + 1, port, 80);
        }
    }

    public String check() {

        if (!(getOther(0) instanceof Manifold || getOther(1) instanceof Manifold)) {
            return "Regulator not connected to manifold";
        }

        if (getOther(0) instanceof Manifold && getNum(0) != 0 || getOther(1) instanceof Manifold && getNum(1) != 0) {
            return "Regulator incorrectly connected to manifold";
        }

        if (!(getOther(0) instanceof TConnector || getOther(1) instanceof TConnector)) {
            return "Regulator not connected to T Connector for relief valve";
        }

        if ((getOther(0) instanceof TConnector && (getNum(0) == 0 || !((TConnector) getOther(0)).checkPS())) || (getOther(1) instanceof TConnector && (getNum(1) == 0 || !((TConnector) getOther(1)).checkPS()))) {
            return "Regulator incorrectly connected to T Connector for relief valve";
        }

        return null;
    }
}
