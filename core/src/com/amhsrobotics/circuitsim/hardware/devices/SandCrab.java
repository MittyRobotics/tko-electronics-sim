package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;

public class SandCrab extends Flippable {

    public SandCrab() {}

    public SandCrab(Vector2 position, HardwareType type, boolean... addCrimped) {
        super(position, type, addCrimped);

        this.type = type;

        for(JSONArray arr : pinDefs) {
            Sprite temp;
            if(connectors.size() == connNum) {
                break;
            }

            temp = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_connector.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    public void updatePosition(ClippedCameraController camera, ModifiedShapeRenderer renderer, SpriteBatch batch) {
        position = Tools.mouseScreenToWorld(camera);

        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            SnapGrid.calculateSnap(position);
        }
        base.setCenter(getPosition().x, getPosition().y);
        batch.begin();
        base.draw(batch);
        for(Sprite c : connectors) {
            c.setCenter(position.x + (Long) pinDefs.get(connectors.indexOf(c)).get(0), position.y + (Long) pinDefs.get(connectors.indexOf(c)).get(1));
            c.draw(batch);
        }
        batch.end();
    }

    public String check() {
        if(getNull(0) || getNull(1)) {
            return "Sandcrab is not connected";
        }

        return null;
    }

    public Hardware getWagoOther(Cable c) {
        if(c == connections.get(0)) {
            if (connections.get(1) == null) {
                return null;
            } else {
                if(connections.get(1).getOtherConnectionSimple(this) != this && connections.get(1).getOtherConnectionSimple(this) != connections.get(0).getOtherConnectionSimple(this)) {
                    return connections.get(1).getOtherConnection(this);
                } else {
                    return this;
                }
            }
        } else {
            if (connections.get(0) == null) {
                return null;
            } else {
                if(connections.get(0).getOtherConnectionSimple(this) != this && connections.get(1).getOtherConnectionSimple(this) != connections.get(0).getOtherConnectionSimple(this)) {
                    return connections.get(0).getOtherConnection(this);
                } else {
                    return this;
                }
            }
        }
    }

    public int getGauge() {
        if(connections.get(0) != null) {
            return (int) connections.get(0).gauge;
        }
        if(connections.get(1) != null) {
            return (int) connections.get(1).gauge;
        }
        if(type == HardwareType.TRIPLESANDCRAB) {
            if(connections.get(2) != null) {
                return (int) connections.get(2).gauge;
            }
        }
        return -1;
    }

    public String getGaugeString() {
        if(getGauge() == -1) {
            return "18/22";
        }
        return ""+getGauge();
    }

    public int getWagoOtherNum(Cable c) {
        if(c == connections.get(0)) {
            if (connections.get(1) == null) {
                return -1;
            } else {
                return connections.get(1).getOtherConnection(this).getConnNum(connections.get(1));
            }
        } else {
            if (connections.get(0) == null) {
                return -1;
            } else {
                return connections.get(0).getOtherConnection(this).getConnNum(connections.get(0));
            }
        }
    }


    public Vector2 calculate(int port) {
        return calculateDirection(cur, port, 120);
    }

}
