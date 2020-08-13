package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;

public class Spark extends Hardware {

    public Spark(Vector2 position, HardwareType type, boolean... addCrimped) {
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

    public Vector2 calculate(int port) {
        if(port < 3) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight()/2 + 40);
        } else {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight()/2 - 40);
        }
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(new Color(156/255f,1f,150/255f,1f));

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(getPosition().x - (base.getWidth() / 2)-7, getPosition().y - (base.getHeight() / 2)-7, base.getWidth()+16, base.getHeight()+13, 5);
        renderer.end();
    }
}
