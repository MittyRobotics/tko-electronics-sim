package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Tank extends Flippable {

    public Tank() {}

    public Tank(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.TANK, addCrimped);


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

    public Hardware getOtherTankConn(Tank t) {
        if(getOther(0) == t) {
            if(getOther(1) instanceof Tank) {
                return ((Tank) getOther(1)).getOtherTankConn(this);
            }
            return getOther(1);
        }
        if(getOther(0) instanceof Tank) {
            return ((Tank) getOther(0)).getOtherTankConn(this);
        }
        return getOther(0);
    }

    public Vector2 calculate(int port) {
        if(port == 1) {
            return calculateDirection(cur+3, port, 100);
        } else {
            return calculateDirection(cur+1, port, 100);
        }
    }

    public String check() {
        if(!((getOther(0) instanceof Compressor || (getOther(0) instanceof Tank && ((Tank) getOther(0)).getOtherTankConn(this) instanceof Compressor)) ||
                getOther(1) instanceof Compressor || (getOther(1) instanceof Tank && ((Tank) getOther(1)).getOtherTankConn(this) instanceof Compressor))) {
            return "Tank not connected to compressor";
        }

        if(!((getOther(0) instanceof TConnector || (getOther(0) instanceof Tank && ((Tank) getOther(0)).getOtherTankConn(this) instanceof TConnector)) ||
                getOther(1) instanceof TConnector || (getOther(1) instanceof Tank && ((Tank) getOther(1)).getOtherTankConn(this) instanceof TConnector))) {
            return "Tank not connected to T Connector for relief valve";
        }

        return null;
    }
}
