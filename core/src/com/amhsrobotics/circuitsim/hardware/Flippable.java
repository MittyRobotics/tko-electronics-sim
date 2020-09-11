package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.amhsrobotics.circuitsim.wiring.EthernetCable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;

public class Flippable extends Hardware  {

    public Flippable() {}

    public Flippable(Vector2 position, HardwareType type, boolean... addCrimped) {
        super(position, type, addCrimped);

        for(JSONArray arr : pinDefs) {
            Sprite temp;
            if(connectors.size() == connNum) {
                break;
            }
            temp = new Sprite(new Texture(Gdx.files.internal("img/point.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            temp.setSize((Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(0) /2f, (Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(1) /2f);
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    @Override
    public void populateProperties() {

        CircuitGUIManager.propertiesBox.clearTable();

        TextButton flip = new TextButton("Rotate", CircuitGUIManager.propertiesBox.TBUTTON);

        CircuitGUIManager.propertiesBox.addElement(new Label(name, CircuitGUIManager.propertiesBox.LABEL), true, 2);
        CircuitGUIManager.propertiesBox.addElement(flip, true, 2);

        CircuitGUIManager.propertiesBox.addElement(new Label("E-Plate", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label(attached == null ? "None" : attached.hardwareID2+"", CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
        for (int x = 0; x < connectors.size(); x++) {
            CircuitGUIManager.propertiesBox.addElement(new Label("Conn. " + (x + 1), CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
            if(connections.get(x) == null) {
                CircuitGUIManager.propertiesBox.addElement(new Label("None", CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
            } else if(connections.get(x).getHardwareAtOtherEnd(this) != null) {
                if((connections.get(x).getHardwareAtOtherEnd(this).getName() + " " + connections.get(x).getHardwareAtOtherEnd(this).hardwareID2).length() > 10) {
                    CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(x).getHardwareAtOtherEnd(this).getName() + " " + connections.get(x).getHardwareAtOtherEnd(this).hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 2);
                } else {
                    CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(x).getHardwareAtOtherEnd(this).getName() + " " + connections.get(x).getHardwareAtOtherEnd(this).hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
                }
            } else if(connections.get(x) instanceof CrimpedCable) {
                CircuitGUIManager.propertiesBox.addElement(new Label("Crimped", CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
            } else if(connections.get(x) instanceof EthernetCable) {
                CircuitGUIManager.propertiesBox.addElement(new Label("Ethernet " + connections.get(x).getID(), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
            } else {
                CircuitGUIManager.propertiesBox.addElement(new Label("Cable " + connections.get(x).getID(), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
            }
        }

        flip.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                rotateThis();
            }
        });
    }

    private void rotateThis() {
        base.rotate(90);
        cur = (cur+1)%4;
    }

    @Override
    public void processFlip() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            rotateThis();
        }
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(new Color(156/255f,1f,150/255f,1f));

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(getSpriteBox().x - 7, getSpriteBox().y - 7, getSpriteBox().getWidth() + 14, getSpriteBox().getHeight() + 14, 5);
        renderer.end();
    }
}
