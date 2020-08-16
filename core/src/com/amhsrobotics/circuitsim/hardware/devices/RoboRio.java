package com.amhsrobotics.circuitsim.hardware.devices;

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

public class RoboRio extends Hardware {

    public RoboRio(Vector2 position, HardwareType type, boolean... addCrimped) {
        super(position, type, addCrimped);



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

    @Override
    public void delete() {
        for(Cable cable : connections) {
            if(cable != null) {
                if(ends.get(connections.indexOf(cable, true))) {
                    cable.setConnection2(null);
                } else {
                    cable.setConnection1(null);
                }
            }
        }
        HardwareManager.removeHardware(this);
        HardwareManager.currentHardware = null;
    }

    public Vector2 calculate(int port) {
        if(port == 0) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight()/2 + 40);
        } else if (port == 1) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2 + 40, getConnector(port).getY() + getConnector(port).getHeight()/2);
        } else if(port >= 2 && port <= 15) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2 - 40, getConnector(port).getY() + getConnector(port).getHeight()/2);
        } else if(port >= 16 && port <= 24) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2 - 40);
        } else if(port == 36) {
                return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight()/2 + 120);
        } else {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2 + 40, getConnector(port).getY() + getConnector(port).getHeight()/2);
        }
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(new Color(156/255f,1f,150/255f,1f));

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(getPosition().x - (base.getWidth() / 2)-5, getPosition().y - (base.getHeight() / 2)-5, base.getWidth()+12, base.getHeight()+10, 45);
        renderer.end();
    }
}
