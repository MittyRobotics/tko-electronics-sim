package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.files.JSONReader;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Falcon extends Hardware {

    public Falcon(Vector2 position, HardwareType type, boolean... addCrimped) {
        super(position, addCrimped);

        this.type = type;

        JSONReader.loadConfig("scripts/Falcon.json");
        base = new Sprite(new Texture(Gdx.files.internal("img/hardware/Falcon.png")));
        base.setSize(base.getWidth()/2, base.getHeight()/2);

        connNum = ((Long) JSONReader.getCurrentConfig().get("totalPins")).intValue();
        name = (String) (JSONReader.getCurrentConfig().get("name"));
        JSONArray pins = (JSONArray) JSONReader.getCurrentConfig().get("pins");
        for(int x = 0; x < pins.size(); x++) {
            pinDefs.add((JSONArray) ((JSONObject) pins.get(x)).get("position"));
        }
        for(int x = 0; x < pins.size(); x++) {
            pinSizeDefs.add((JSONArray) ((JSONObject) pins.get(x)).get("dimensions"));
        }

        base.setCenter(position.x, position.y);

//        for(JSONArray arr : pinDefs) {
//            Sprite temp;
//            if(connectors.size() == connNum) {
//                break;
//            }
//            if(connectors.size() == 0) {
//                temp = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange_2.png")));
//            } else {
//                temp = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));
//            }
//            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
//            connectors.add(temp);
//        }

        initConnections();
        initEnds();
    }

    @Override
    public void populateProperties() {
        super.populateProperties();
        for(int x = 0; x < connectors.size(); x++) {
            CircuitGUIManager.propertiesBox.addElement(new Label("Conn. " + (x + 1), CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
            CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(x) == null ? "None" : (connections.get(x) instanceof CrimpedCable ? "Crimped" : "Cable " + connections.get(x).getID()), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
        }
    }

    @Override
    public void attachCrimpedCable(Cable cable, int port) {
        connections.set(port, cable);

        cable.removeCoordinates();

        cable.setConnection1(this);
        cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() - 20), true);
        cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + 20), true);

        CableManager.currentCable = null;
    }

    @Override
    public void attachWireLib(Cable cable, int port, boolean endOfWire) {
        cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() - 20), !endOfWire);
        cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + 20), !endOfWire);

        if(endOfWire) {cable.setConnection2(this);} else {cable.setConnection1(this);}
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(new Color(156/255f,1f,150/255f,1f));

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(getPosition().x - (base.getWidth() / 2)-7, getPosition().y - (base.getHeight() / 2)-7, base.getWidth()+16, base.getHeight()+13, 5);
        renderer.end();
    }
}
