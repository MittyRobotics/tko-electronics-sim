package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.SnapGrid;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;

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

    public Hardware(Vector2 pos, boolean... addCrimped) {
        this.position = pos;
        this.hardwareID = DeviceUtil.getNewHardwareID();

        connections = new ArrayList<>();
        ends = new ArrayList<>();
        crimpedPorts = new ArrayList<>();

        canMove = false;

        if(addCrimped.length > 0) {
            this.addCrimped = addCrimped[0];
        }

        if(this.addCrimped) {
            checkCrimpedCables();
        }
    }

    public void checkCrimpedCables() {
        for(int i : crimpedPorts) {
            if(connections.get(i) == null) {
                CrimpedCable c = new CrimpedCable();
                CableManager.addCable(c);
                attachCrimpedCable(c, i);
            }
        }
    }


    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        renderer.setProjectionMatrix(camera.getCamera().combined);
        batch.setProjectionMatrix(camera.getCamera().combined);

        base.setCenter(getPosition().x, getPosition().y);
        for(Sprite temp : connectors) {
            temp.setCenter(getPosition().x + (Long) pinDefs.get(connectors.indexOf(temp)).get(0), getPosition().y + (Long) pinDefs.get(connectors.indexOf(temp)).get(1));
        }

        Vector2 vec = Tools.mouseScreenToWorld(camera);

        if(base.getBoundingRectangle().contains(vec.x, vec.y)) {

            drawHover(renderer);

            boolean good = true;

            for(Cable c : connections) {
                if(c != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && c.hoveringMouse(camera)) {
                    good = false;
                    CableManager.currentCable = c;
                    HardwareManager.currentHardware = null;
                    break;
                }
            }

            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && good) {
                HardwareManager.currentHardware = this;
                CableManager.currentCable = null;

                populateProperties();
                CircuitGUIManager.propertiesBox.show();
            }

        }

        if((Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL))) {
            this.delete();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            CircuitGUIManager.propertiesBox.hide();
            HardwareManager.currentHardware = null;
        }

        if (Gdx.input.isTouched()) {

            if(Gdx.input.getDeltaX() != 0 && Gdx.input.getDeltaY() != 0 && base.getBoundingRectangle().contains(vec.x, vec.y)) {
                canMove = true;
            }
            if(HardwareManager.currentHardware == this) {
                HardwareManager.movingObject = true;
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


            if(Gdx.input.isTouched() && canMove) {
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    SnapGrid.calculateSnap(vec);
                }

                //SET OWN POSITION
                setPosition(vec.x, vec.y);


                //MOVE CABLES

                for (JSONArray arr : pinDefs) {
                    int index = pinDefs.indexOf(arr);
                    if (connections.get(index) != null) {
                        editWire(connections.get(index), index, ends.get(index));
                        /*connections.get(index).editCoordinates(
                                new Vector2(getConnector(index).getX() + getConnector(index).getWidth() / 2, getConnector(index).getY() + 20),
                                ends.get(index), false);
                        connections.get(index).editCoordinates(
                                new Vector2(getConnector(index).getX() + getConnector(index).getWidth() / 2, getConnector(index).getY() - 20),
                                ends.get(index), true);*/
                    }
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


    public void attachCrimpedCable(Cable cable, int port) {
        connections.set(port, cable);

        cable.removeCoordinates();

        cable.setConnection1(this);

        cable.addCoordinates(calculate(port), true);
        cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + getConnector(port).getHeight() / 2), true);

        CableManager.currentCable = null;
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

    public void attachWire(Cable cable, int port, boolean endOfWire) {
        connections.set(port, cable);
        ends.set(port, endOfWire);

        attachWireLib(cable, port, endOfWire);

        CableManager.currentCable = null;
    }


    public void firstClickAttach(Cable cable, int port, boolean endOfWire) {
        connections.set(port, cable);
        ends.set(port, endOfWire);

        cable.removeCoordinates();

        attachWireLib(cable, port, endOfWire);

        cable.setAppendingFromEnd(false);
        cable.setAppendingFromBegin(false);

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
