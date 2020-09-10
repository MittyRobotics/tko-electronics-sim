package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.MainObject;
import com.amhsrobotics.circuitsim.files.JSONReader;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.parts.LED;
import com.amhsrobotics.circuitsim.screens.CircuitScreen;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.input.Tuple;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.amhsrobotics.circuitsim.wiring.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.util.ArrayList;

public abstract class Hardware extends MainObject implements Json.Serializable {

    private Vector2 position;
    public int hardwareID, hardwareID2, cur,connNum, ledNum ;
    public DelayedRemovalArray<Cable> connections;
    public ArrayList<Boolean> ends;
    public ArrayList<Integer> crimpedPorts;
    public ArrayList<Color> crimpedPortColors;
    public HardwareType type;
    public String name;

    public ArrayList<JSONArray> pinSizeDefs = new ArrayList<>();
    public ArrayList<Sprite> connectors = new ArrayList<>();
    public ArrayList<JSONArray> pinDefs = new ArrayList<>();
    public ArrayList<String> portTypes = new ArrayList<>();
    public ArrayList<LED> LEDs = new ArrayList<>();

    public Sprite base;
    public boolean canMove, addCrimped;
    public EPlate attached;

    public float diffX, diffY;

    public Hardware() {}

    public Hardware(Vector2 pos, HardwareType type, boolean... addCrimped) {
        this.position = pos;
        this.type = type;
        this.hardwareID2 = DeviceUtil.getNewHardwareID(type);
        this.hardwareID = DeviceUtil.getNewHardwareID();

        if(type != HardwareType.EPLATE) {
            connections = new DelayedRemovalArray<>();
            ends = new ArrayList<>();
            crimpedPorts = new ArrayList<>();
            crimpedPortColors = new ArrayList<>();

            canMove = false;

            if(addCrimped.length > 0) {
                this.addCrimped = addCrimped[0];
            }

            if(Constants.placing_object == null) {
                populateProperties();
                CircuitGUIManager.propertiesBox.show();
            }

            loadThis();
        }
    }

    public void updatePosition(ClippedCameraController camera, ModifiedShapeRenderer renderer, SpriteBatch batch) {
        position = Tools.mouseScreenToWorld(camera);

        base.setCenter(getPosition().x, getPosition().y);
        batch.begin();
        base.draw(batch);
        batch.end();
    }

    public void loadThis() {
        JSONReader.loadConfig("scripts/" + type.toString().toLowerCase() + ".json");
        base = new Sprite(new Texture(Gdx.files.internal("img/hardware/" + type.toString().toLowerCase() + ".png")));

        connNum = ((Long) JSONReader.getCurrentConfig().get("totalPins")).intValue();
        ledNum = ((Long) JSONReader.getCurrentConfig().get("totalLeds")).intValue();
        name = (String) (JSONReader.getCurrentConfig().get("name"));
        JSONArray pins = (JSONArray) JSONReader.getCurrentConfig().get("pins");
        for(int x = 0; x < pins.size(); x++) {
            pinDefs.add((JSONArray) ((JSONObject) pins.get(x)).get("position"));
            pinSizeDefs.add((JSONArray) ((JSONObject) pins.get(x)).get("dimensions"));
            portTypes.add((String) ((JSONObject) pins.get(x)).get("type"));
        }

        JSONArray temp = (JSONArray) JSONReader.getCurrentConfig().get("crimped");

        if(temp != null) {
            for (Object o : temp) {
                crimpedPorts.add(((Long) o).intValue());
            }
        }

        JSONArray col = (JSONArray) JSONReader.getCurrentConfig().get("crimpedColors");

        if(col != null) {
            for (Object k : col) {
                crimpedPortColors.add(DeviceUtil.COLORS.get(k));
            }
        }

        JSONArray lights = (JSONArray) JSONReader.getCurrentConfig().get("leds");
        if(ledNum > 0) {
            for (Object light : lights) {
                LEDs.add(new LED(
                        (JSONArray) ((JSONObject) light).get("position"),
                        this,
                        (JSONArray) ((JSONObject) light).get("dimensions"),
                        (String) ((JSONObject) light).get("type"),
                        (String) ((JSONObject) light).get("color")
                ));
            }
        }

        base.setCenter(position.x, position.y);
    }

    public void checkCrimpedCables() {
        int j = 0;
        for(int i : crimpedPorts) {
            if(connections.get(i) == null) {
                CrimpedCable c = new CrimpedCable(Integer.parseInt(portTypes.get(i)));
                c.setColor(crimpedPortColors.get(j));
                CableManager.addCable(c);
                attachCrimpedCable(c, i);
            }
            j++;
        }
    }

    public Vector2 getDim() {
        return new Vector2(base.getWidth(), base.getHeight());
    }

    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        renderer.setProjectionMatrix(camera.getCamera().combined);
        batch.setProjectionMatrix(camera.getCamera().combined);

        if(type == HardwareType.EPLATE) return;

        base.setCenter(getPosition().x, getPosition().y);

        for(Sprite temp : connectors) {
            temp.setCenter(getPosition().x + (Long) pinDefs.get(connectors.indexOf(temp)).get(0), getPosition().y + (Long) pinDefs.get(connectors.indexOf(temp)).get(1));
            Vector2 pos = new Vector2(temp.getX() + temp.getWidth()/2, temp.getY() + temp.getHeight()/2);
            pos.rotateAround(new Vector2(base.getX() + base.getWidth() / 2, base.getY() + base.getHeight() / 2), base.getRotation());
            temp.setCenter(pos.x, pos.y);
        }

        for(LED led : LEDs) {
            led.setPosition();
        }

        //MOVE CABLES

        for (JSONArray arr : pinDefs) {
            int index = pinDefs.indexOf(arr);
            if (connections.get(index) != null) {
                editWire(connections.get(index), index, ends.get(index));
            }
        }

        Vector2 vec = Tools.mouseScreenToWorld(camera);


        if(HardwareManager.attachWireOnDoubleClick != null) {
            if(HardwareManager.attachWireOnDoubleClick.x == this && !canMove) {
                if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && connectors.get(HardwareManager.attachWireOnDoubleClick.y).getBoundingRectangle().contains(vec.x, vec.y)) {
                    Cable c;
                    if(Integer.parseInt(portTypes.get(HardwareManager.attachWireOnDoubleClick.y)) == 13) {
                        c = new EthernetCable(new Vector2(0, 0), CableManager.id);
                    } else if (Integer.parseInt(portTypes.get(HardwareManager.attachWireOnDoubleClick.y)) == 2) {
                        c = new Tubing(new Vector2(0, 0), CableManager.id);
                    } else {
                        c = new Cable(CableManager.id);
                    }
                    CableManager.id++;
                    c.setGauge(Integer.parseInt(portTypes.get(HardwareManager.attachWireOnDoubleClick.y)));
                    firstClickAttach(c, HardwareManager.attachWireOnDoubleClick.y, false);
                    CableManager.addCable(c);
                    CircuitGUIManager.propertiesBox.select(HardwareManager.attachWireOnDoubleClick.y);
                    HardwareManager.attachWireOnDoubleClick = null;
                }
            }
        }

        for(Sprite s : connectors) {
            if(connections.get(connectors.indexOf(s)) == null) {
                if (s.getBoundingRectangle().contains(vec.x, vec.y) && HardwareManager.getCurrentlyHovering(camera) == this) {
                    CircuitScreen.setHoverDraw(vec, DeviceUtil.GAUGETODEVICE.get((portTypes.get(connectors.indexOf(s)))) + " (" + portTypes.get(connectors.indexOf(s)) + "g)");
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && CableManager.currentCable == null && HardwareManager.attachWireOnDoubleClick == null) {
                        HardwareManager.attachWireOnDoubleClick = new Tuple<>(this, connectors.indexOf(s));
//                        LinkTimer.init(3, () -> HardwareManager.attachWireOnDoubleClick = null);
                        Timer timer = new Timer(500, arg0 -> {
                            HardwareManager.attachWireOnDoubleClick = null;
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
            }
        }


        if(HardwareManager.getCurrentlyHovering(camera) == this) {

            drawHover(renderer);

            for (Cable c : connections) {
                if (c != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && c.hoveringMouse(camera)) {
                    CableManager.currentCable = c;
                    HardwareManager.currentHardware = null;
                    break;
                }
            }
        }

        if(!(CableManager.currentCable != null && connections.contains(CableManager.currentCable, true) && CableManager.currentCable.hoveringMouse(camera)) && (CableManager.currentCable == null || (CableManager.currentCable != null && !(CableManager.currentCable.appendingFromBegin || CableManager.currentCable.appendingFromEnd)))) {
            if (Gdx.input.isTouched()) {

                if (HardwareManager.getCurrentlyHovering(camera) == this || canMove) {
                    HardwareManager.currentHardware = this;
                    CableManager.currentCable = null;
                    populateProperties();
                    CircuitGUIManager.propertiesBox.show();

                    if (!HardwareManager.movingObject) {
                        HardwareManager.movingObject = true;
                        canMove = true;
                        HardwareManager.currentHardware = this;
                        diffX = position.x - vec.x;
                        diffY = position.y - vec.y;
                    }
                } else {
//                    LinkTimer.init((int) Gdx.graphics.getDeltaTime() + 3, () -> {
//                        if (!CircuitGUIManager.propertiesBox.hovering && HardwareManager.currentHardware == Hardware.this) {
//                            CircuitGUIManager.propertiesBox.hide();
//                            HardwareManager.currentHardware = null;
//                        }
//                    });
                    if (HardwareManager.currentHardware == Hardware.this && (!(CircuitGUIManager.panelShown && Gdx.input.getX() >= Gdx.graphics.getWidth() - 420 && Gdx.input.getY() <= 210) && !(!CircuitGUIManager.panelShown && Gdx.input.getX() >= Gdx.graphics.getWidth() - 210 && Gdx.input.getY() <= 210))) {
                        CircuitGUIManager.propertiesBox.hide();
                        HardwareManager.currentHardware = null;
                    }

                }

            } else {

                canMove = false;
                HardwareManager.movingObject = false;

            }
        }

        //SELECTED MECHANICS
        //---------------------------------------------------

        if (HardwareManager.currentHardware == this) {
            HardwareManager.moveToFront(this);
            drawHover(renderer);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                HardwareManager.currentHardware = null;
                CircuitGUIManager.propertiesBox.hide();
            }

            if ((Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL))) {
                this.delete();
            }

            if (Gdx.input.isTouched() && canMove) {
                if ((Gdx.input.getX() <= Gdx.graphics.getWidth() - 200) || !CircuitGUIManager.isPanelShown()) {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                        SnapGrid.calculateSnap(vec);
                    }

                    //SET OWN POSITION
                    setPosition(vec.x + diffX, vec.y + diffY);
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
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                c.render(renderer, camera);
            }
        }

        batch.begin();
        for(Sprite conn : connectors) {
            conn.draw(batch);
        }
        for(LED led : LEDs) {
            led.render(batch);
        }
        batch.end();

        processFlip();
    }

    public boolean getHoveringMouse(ClippedCameraController camera) {
        Vector2 vec = Tools.mouseScreenToWorld(camera);
        if(this instanceof EPlate) {
            return ((EPlate) this).getBox().contains(vec.x, vec.y);
        }
        return base.getBoundingRectangle().contains(vec.x, vec.y);
    }

    public void processFlip() {}

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

        if(CableManager.currentCable != null) {
            CableManager.currentCable.appendingFromEnd = false;
            CableManager.currentCable.appendingFromBegin = false;
            CableManager.currentCable = null;
        }
    }

    public boolean acceptPortConnection(Cable cable, int port) {
        return true;
    }

    public void attachWire(Cable cable, int port, boolean endOfWire) {
        if(!acceptPortConnection(cable, port)) {
            return;
        }
        if (connections.get(port) != null) {
            CircuitGUIManager.popup.activateError("Port already occupied by Cable " + connections.get(port).getID());
        } else if(cable.getGauge() == Integer.parseInt(portTypes.get(port))) {
            connections.set(port, cable);
            ends.set(port, endOfWire);

            attachWireLib(cable, port, endOfWire);

            if(CableManager.currentCable != null) {
                CableManager.currentCable.appendingFromEnd = false;
                CableManager.currentCable.appendingFromBegin = false;
                CableManager.currentCable = null;
            }
        } else {
            CircuitGUIManager.popup.activateError("Wrong gauge - must be gauge: " + portTypes.get(port));
        }
    }

    public void firstClickAttach(Cable cable, int port, boolean endOfWire) {
        if (connections.get(port) != null) {
            CircuitGUIManager.popup.activateError("Port already occupied by Cable " + connections.get(port).getID());
        } else if(portTypes.get(port).equals("13") && cable.getGauge() != 13) {
            CircuitGUIManager.popup.activateError("Port requires ethernet cable");
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
        if(CableManager.currentCable != null) {
            CableManager.currentCable.appendingFromEnd = false;
            CableManager.currentCable.appendingFromBegin = false;
            CableManager.currentCable = null;
        }
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
        CircuitGUIManager.propertiesBox.addElement(new Label(name + " " + hardwareID2, CircuitGUIManager.propertiesBox.LABEL), true, 2);
        CircuitGUIManager.propertiesBox.addElement(new Label("E-Plate", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label(attached == null ? "None" : attached.hardwareID2+"", CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
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
                    if((connections.get(x).getHardwareAtOtherEnd(this).getName() + " " + connections.get(x).getHardwareAtOtherEnd(this).hardwareID2).length() > 10) {
                        CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(x).getHardwareAtOtherEnd(this).getName() + " " + connections.get(x).getHardwareAtOtherEnd(this).hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 2);
                    } else {
                        CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(x).getHardwareAtOtherEnd(this).getName() + " " + connections.get(x).getHardwareAtOtherEnd(this).hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
                    }
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
                if(cable instanceof CrimpedCable) {
                    CableManager.deleteCable(cable);
                } else if(ends.get(connections.indexOf(cable, true))) {
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
        for(int i = 0; i < connections.size; ++i) {
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

    public Vector2 calculateDirection(int dir, int port, int... length) {
        dir = (dir)%4;
        int useLength = length.length > 0 ? length[0] : 40;
        if(dir == 0) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2 - useLength);
        } else if (dir == 1) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2 + useLength, getConnector(port).getY() + getConnector(port).getHeight() / 2);
        } else if (dir == 2) {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2 + useLength);
        } else {
            return new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2 - useLength, getConnector(port).getY() + getConnector(port).getHeight() / 2);
        }
    }

    public int getHardwareID() {
        return hardwareID;
    }

    @Override
    public void write(Json json) {
        json.writeValue("id", this.hardwareID);
        json.writeValue("position", this.position);
//        json.writeValue("connections", this.connections);
        json.writeValue("ends", this.ends);
        json.writeValue("connNum", this.connNum);
        json.writeValue("ledNum", this.ledNum);
        json.writeValue("type", this.type);
        json.writeValue("name", this.name);
        json.writeValue("ledNum", this.ledNum);
        json.writeValue("canMove", this.canMove);
        json.writeValue("addCrimped", this.addCrimped);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }
}
