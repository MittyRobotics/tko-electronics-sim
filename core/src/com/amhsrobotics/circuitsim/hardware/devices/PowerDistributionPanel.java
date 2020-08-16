package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.amhsrobotics.circuitsim.wiring.EthernetCable;
import com.badlogic.gdx.Gdx;
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
    public void populateProperties() {

        CircuitGUIManager.propertiesBox.clearTable();

        TextButton disableCrimp = new TextButton("Toggle Crimped", CircuitGUIManager.propertiesBox.TBUTTON);

        CircuitGUIManager.propertiesBox.addElement(new Label(name, CircuitGUIManager.propertiesBox.LABEL), true, 2);
        CircuitGUIManager.propertiesBox.addElement(disableCrimp, true, 2, 120);
        for (int x = 0; x < connectors.size(); x++) {
            CircuitGUIManager.propertiesBox.addElement(new Label("Conn. " + (x + 1), CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
            if(connections.get(x) == null) {
                CircuitGUIManager.propertiesBox.addElement(new Label("None", CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
            } else if(connections.get(x) instanceof CrimpedCable) {
                CircuitGUIManager.propertiesBox.addElement(new Label("Crimped", CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
            } else if(connections.get(x) instanceof EthernetCable) {
                CircuitGUIManager.propertiesBox.addElement(new Label("Ethernet " + connections.get(x).getID(), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
            } else {
                if(connections.get(x).getHardwareAtOtherEnd(this) == null) {
                    CircuitGUIManager.propertiesBox.addElement(new Label("Cable " + connections.get(x).getID(), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
                } else {
                    CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(x).getHardwareAtOtherEnd(this).getName() + " " + connections.get(x).getHardwareAtOtherEnd(this).getHardwareID(), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
                }
            }
        }

        disableCrimp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                if(addCrimped) {
//                    addCrimped = false;
//                    for(Cable c : connections) {
//                        if(c instanceof CrimpedCable) {
//                            CableManager.deleteCable(c);
//                            connections.removeValue(c, true);
//                        }
//                    }
//                } else {
//                    addCrimped = true;
//                }
            }
        });

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
        if(port >= 0 && port <= 5) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2 - 50, getConnector(port).getY() + getConnector(port).getHeight()/2);
        } else if (port >= 6 && port < 10) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2 + 50);
        } else if (port >= 10 && port < 18) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2 - 50);
        } else if (port >= 18 && port < 26) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2 + 50);
        } else if (port >= 26 && port < 34) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2 + 50);
        } else if (port == 42 || port == 43) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2 + 50, getConnector(port).getY() + getConnector(port).getHeight() / 2);
        } else {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight()/2 - 50);
        }
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(new Color(156/255f,1f,150/255f,1f));

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(getPosition().x - (base.getWidth() / 2)-7, getPosition().y - (base.getHeight() / 2)-7, base.getWidth()+12, base.getHeight()+13, 100);
        renderer.end();
    }
}
