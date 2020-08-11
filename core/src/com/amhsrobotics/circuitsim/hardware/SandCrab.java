package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.files.JSONReader;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SandCrab extends Hardware {


    public SandCrab(Vector2 position, HardwareType type, boolean... addCrimped) {
        super(position, addCrimped);

        this.type = type;

        if(type == HardwareType.DOUBLESANDCRAB) {
            JSONReader.loadConfig("scripts/DoubleSandCrab.json");
            base = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_white.png")));
        } else {
            JSONReader.loadConfig("scripts/TripleSandCrab.json");
            base = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_white_2.png")));
        }

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

        for(JSONArray arr : pinDefs) {
            Sprite temp;
            if(connectors.size() == connNum) {
                break;
            }
            if(connectors.size() == 0) {
                temp = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange_2.png")));
            } else {
                temp = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));
            }
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    @Override
    public void delete() {
        for(Cable cable : connections) {
            if(cable != null) {
                if(ends.get(connections.indexOf(cable))) {
                    cable.setConnection2(null);
                } else {
                    cable.setConnection1(null);
                }
            }
        }
        HardwareManager.removeHardware(this);
        HardwareManager.currentHardware = null;
        CircuitGUIManager.propertiesBox.hide();
    }


    @Override
    public void populateProperties() {
        super.populateProperties();
        for(int x = 0; x < connectors.size(); x++) {
            CircuitGUIManager.propertiesBox.addElement(new Label("Conn. " + (x + 1), CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
            CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(x) == null ? "None" : (connections.get(x) instanceof CrimpedCable ? "Crimped" : "Cable " + connections.get(x).getID()), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
        }
    }
}
