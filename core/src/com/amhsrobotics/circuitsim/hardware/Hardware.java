package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.files.JSONReader;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.screens.CircuitScreen;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.amhsrobotics.circuitsim.wiring.EthernetCable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.util.ArrayList;

public abstract class Hardware {

    private Vector2 position;
    private int hardwareID;
    public ArrayList<Cable> connections;
    public ArrayList<Boolean> ends;
    public ArrayList<Integer> crimpedPorts;
    public int connNum;
    public HardwareType type;
    public String name;

    public ArrayList<JSONArray> pinSizeDefs = new ArrayList<>();
    public ArrayList<Sprite> connectors = new ArrayList<>();
    public ArrayList<JSONArray> pinDefs = new ArrayList<>();
    public ArrayList<String> portTypes = new ArrayList<>();

    public Sprite base;
    public boolean canMove;
    public boolean addCrimped;
    public boolean facingUp = true;

    private float prevBaseRotation = 0;

    public Hardware(Vector2 pos, HardwareType type, boolean... addCrimped) {
        this.position = pos;
        this.type = type;
        this.hardwareID = DeviceUtil.getNewHardwareID();

        connections = new ArrayList<>();
        ends = new ArrayList<>();
        crimpedPorts = new ArrayList<>();

        canMove = false;

        if(addCrimped.length > 0) {
            this.addCrimped = addCrimped[0];
        }

        /*if(this.addCrimped) {
            checkCrimpedCables();
        }*/

        if(Constants.placing_object == null) {
            populateProperties();
            CircuitGUIManager.propertiesBox.show();
        }

        loadThis();
        prevBaseRotation = base.getRotation();
    }

    public void loadThis() {
        Gdx.app.log("scripts/" + type.toString().toLowerCase() + ".json", "");
        JSONReader.loadConfig("scripts/" + type.toString().toLowerCase() + ".json");
        base = new Sprite(new Texture(Gdx.files.internal("img/hardware/" + type.toString().toLowerCase() + ".png")));

        connNum = ((Long) JSONReader.getCurrentConfig().get("totalPins")).intValue();
        name = (String) (JSONReader.getCurrentConfig().get("name"));
        JSONArray pins = (JSONArray) JSONReader.getCurrentConfig().get("pins");
        for(int x = 0; x < pins.size(); x++) {
            pinDefs.add((JSONArray) ((JSONObject) pins.get(x)).get("position"));
            pinSizeDefs.add((JSONArray) ((JSONObject) pins.get(x)).get("dimensions"));
            portTypes.add((String) ((JSONObject) pins.get(x)).get("type"));
        }

        base.setCenter(position.x, position.y);
    }

    public void checkCrimpedCables() {
        for(int i : crimpedPorts) {
            if(connections.get(i) == null) {
                CrimpedCable c = new CrimpedCable(Integer.parseInt(portTypes.get(i)));
                CableManager.addCable(c);
                attachCrimpedCable(c, i);
            }
        }
    }

    public Vector2 getDim() {
        return new Vector2(base.getWidth(), base.getHeight());
    }

    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        renderer.setProjectionMatrix(camera.getCamera().combined);
        batch.setProjectionMatrix(camera.getCamera().combined);

        base.setCenter(getPosition().x, getPosition().y);

        for(Sprite temp : connectors) {
            temp.setCenter(getPosition().x + (Long) pinDefs.get(connectors.indexOf(temp)).get(0), getPosition().y + (Long) pinDefs.get(connectors.indexOf(temp)).get(1));
            Vector2 pos = new Vector2(temp.getX(), temp.getY());
            pos.rotateAround(new Vector2(base.getX() + base.getWidth() / 2, base.getY() + base.getHeight() / 2), base.getRotation());
            temp.setPosition(pos.x, pos.y);
        }


        //MOVE CABLES

        for (JSONArray arr : pinDefs) {
            int index = pinDefs.indexOf(arr);
            if (connections.get(index) != null) {
                editWire(connections.get(index), index, ends.get(index));
            }
        }

        Vector2 vec = Tools.mouseScreenToWorld(camera);

        if(base.getBoundingRectangle().contains(vec.x, vec.y)) {

            drawHover(renderer);

            for(Cable c : connections) {
                if(c != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && c.hoveringMouse(camera)) {
                    CableManager.currentCable = c;
                    HardwareManager.currentHardware = null;
                    break;
                }
            }

        }

        for(Sprite s : connectors) {
            if(s.getBoundingRectangle().contains(vec.x, vec.y)) {
                CircuitScreen.setHoverDraw(vec, DeviceUtil.GAUGETODEVICE.get((portTypes.get(connectors.indexOf(s)))) + " (" + portTypes.get(connectors.indexOf(s)) + "g)");
            }
        }

        if (Gdx.input.isTouched()) {

            if (base.getBoundingRectangle().contains(vec.x, vec.y) || canMove) {
                HardwareManager.currentHardware = this;
                populateProperties();
                CircuitGUIManager.propertiesBox.show();

                if(Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0) {
                    HardwareManager.movingObject = true;
                    canMove = true;
                }
            } else {
                Timer timer = new Timer((int)Gdx.graphics.getDeltaTime()+3, arg0 -> {
                    if(!CircuitGUIManager.propertiesBox.hovering && HardwareManager.currentHardware == this) {
                        CircuitGUIManager.propertiesBox.hide();
                        HardwareManager.currentHardware = null;
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }

        } else {

            canMove = false;
            HardwareManager.movingObject = false;

        }

        //SELECTED MECHANICS
        //---------------------------------------------------

        if(HardwareManager.currentHardware == this) {
            HardwareManager.moveToFront(this);
            drawHover(renderer);

            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                HardwareManager.currentHardware = null;
                CircuitGUIManager.propertiesBox.hide();
            }

            if((Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL))) {
                this.delete();
            }

            if(Gdx.input.isTouched() && canMove) {
                if ((Gdx.input.getX() <= Gdx.graphics.getWidth() - 200) || !CircuitGUIManager.isPanelShown()) {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                        SnapGrid.calculateSnap(vec);
                    }

                    //SET OWN POSITION
                    setPosition(vec.x, vec.y);
                }

            }
        }

        if(this.addCrimped) {
            checkCrimpedCables();
        }

        batch.begin();
        base.draw(batch);
        batch.end();

        for(Cable c : connections) {
            if(c != null) {
                c.render(renderer, camera);
            }
        }

        batch.begin();
        for(Sprite conn : connectors) {
            conn.draw(batch);
        }
        batch.end();
    }

    public void clearConnection(Cable cable) {
        for(int i = 0; i < connNum; i++) {
            if(cable == connections.get(i)) {
                connections.set(i, null);
            }
        }

    }

    public Sprite getConnector(int conn) {
        return connectors.get(conn);
    }

    public Vector2 calculate(int port) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void attachCrimpedCable(Cable cable, int port) {
        connections.set(port, cable);

        cable.removeCoordinates();

        cable.setConnection1(this);

        cable.addCoordinates(calculate(port), true);
        cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2), true);

        CableManager.currentCable = null;
    }

    public void attachWire(Cable cable, int port, boolean endOfWire) {
        if (connections.get(port) != null) {
            CircuitGUIManager.error.activate("Port already occupied by Cable " + connections.get(port).getID());
        } else if(cable.getGauge() == Integer.parseInt(portTypes.get(port))) {
            connections.set(port, cable);
            ends.set(port, endOfWire);

            attachWireLib(cable, port, endOfWire);

            CableManager.currentCable = null;
        } else {
            CircuitGUIManager.error.activate("Wrong gauge - must be gauge: " + portTypes.get(port));
        }
    }


    public void firstClickAttach(Cable cable, int port, boolean endOfWire) {
        if (connections.get(port) != null) {
            CircuitGUIManager.error.activate("Port already occupied by Cable " + connections.get(port).getID());
        } else if(portTypes.get(port).equals("13") && cable.getGauge() != 13) {
            CircuitGUIManager.error.activate("Port requires ethernet cable");
        } else {
            cable.setGauge(Integer.parseInt(portTypes.get(port)));
            connections.set(port, cable);
            ends.set(port, endOfWire);

            cable.removeCoordinates();

            attachWireLib(cable, port, endOfWire);

            cable.setAppendingFromEnd(false);
            cable.setAppendingFromBegin(false);
        }
    }

    public void attachWireLib(Cable cable, int port, boolean endOfWire) {
        cable.addCoordinates(calculate(port), !endOfWire);
        cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2), !endOfWire);

        if(endOfWire) {cable.setConnection2(this);} else {cable.setConnection1(this);}
        CableManager.currentCable = null;
    }


    public void editWire(Cable cable, int port, boolean endOfWire) {
        cable.editCoordinates(calculate(port), endOfWire,true);
        cable.editCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight()/2), endOfWire, false);
    }

    public void renderConnectors(Batch batch) {
        batch.begin();
        for(Sprite s : connectors) {
            if(s != null) {
                s.draw(batch);
            }
        }
        batch.end();

    }

    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label(name, CircuitGUIManager.propertiesBox.LABEL), true, 2);
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
    }

    public void initConnections() {
        for(int i = 0; i < connNum; ++i) {
            connections.add(null);
        }
    }

    public void initEnds() {
        for(int i = 0; i < connNum; ++i) {
            ends.add(false);
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getSpriteBox() {
        return base.getBoundingRectangle();
    }

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

    public void setPosition(float x, float y) {
        position.set(x, y);
    }


    public int getConnectionPosition(Cable cable) {
        for(int i = 0; i < connections.size(); ++i) {
            if(connections.get(i) == cable) {
                return i;
            }
        }
        return -1;
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(Color.WHITE);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(getPosition().x - (base.getWidth() / 2), getPosition().y - (base.getHeight() / 2), base.getWidth()-1, base.getHeight(), 15);
        renderer.end();
    }


    public int getTotalConnectors() {
        return connNum;
    }

    public void reattachWire(Cable cable, int port, boolean endOfWire) {
        connections.set(port, cable);
        ends.set(port, endOfWire);
        if(endOfWire) {
            cable.setConnection2(this);
        } else {
            cable.setConnection1(this);
        }
    }

    public int getHardwareID() {
        return hardwareID;
    }
}
