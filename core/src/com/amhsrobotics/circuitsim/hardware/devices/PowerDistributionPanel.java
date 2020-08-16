package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;


public class PowerDistributionPanel extends Hardware {

    public PowerDistributionPanel(Vector2 position, HardwareType type, boolean... addCrimped) {
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
    public boolean acceptPortConnection(Cable cable, int port) {
        Gdx.app.log(port + "", "");
        if(port >= 10 && port <= 41) {
            if (cable instanceof CrimpedCable) {
                return true;
            } else {
                CircuitGUIManager.popup.activateError("PDP motor ports only accept crimped cables");
                return false;
            }
        } else {
            return true;
        }
    }

    public Vector2 calculate(int port) {
        Sprite connector = getConnector(port);
        if(port >= 0 && port <= 5) {
            return new Vector2(connector.getX() + connector.getWidth() / 2 - 50, connector.getY() + connector.getHeight()/2);
        } else if (port >= 6 && port <= 10) {
            return new Vector2(connector.getX() + connector.getWidth() / 2, connector.getY() + connector.getHeight() / 2 + 50);
        } else if (port >= 11 && port <= 18) {
                return new Vector2(connector.getX() + connector.getWidth() / 2, connector.getY() + connector.getHeight() / 2 - 50);
        } else if (port >= 19 && port <= 26) {
            return new Vector2(connector.getX() + connector.getWidth() / 2, connector.getY() + connector.getHeight() / 2 + 50);
        } else if (port >= 27 && port <= 34) {
            return new Vector2(connector.getX() + connector.getWidth() / 2, connector.getY() + connector.getHeight() / 2 + 50);
        } else if (port >= 28 && port <= 35) {
            return new Vector2(connector.getX() + connector.getWidth() / 2, connector.getY() + connector.getHeight() / 2 + 50);
        } else if (port == 42 || port == 43) {
            return new Vector2(connector.getX() + connector.getWidth() / 2 + 50, connector.getY() + connector.getHeight() / 2);
        } else {
            return new Vector2(connector.getX() + connector.getWidth() / 2, connector.getY() + connector.getHeight()/2 - 50);
        }
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(new Color(156/255f,1f,150/255f,1f));

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(getPosition().x - (base.getWidth() / 2)-7, getPosition().y - (base.getHeight() / 2)-7, base.getWidth()+12, base.getHeight()+13, 100);
        renderer.end();
    }
}
