package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.ObjectType;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

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

    public Hardware(Vector2 pos) {
        this.position = pos;
        this.hardwareID = DeviceUtil.getNewHardwareID();

        connections = new ArrayList<>();
        ends = new ArrayList<>();
        crimpedPorts = new ArrayList<>();
    }

    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        renderer.setProjectionMatrix(camera.getCamera().combined);
        batch.setProjectionMatrix(camera.getCamera().combined);
    }

    public void clearConnection(Cable cable) {
        for(int i = 0; i < connNum; i++) {
            if(cable == connections.get(i)) {
                connections.set(i, null);
            }
        }

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

    public void delete() { }

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

    public void checkCrimpedCables() {
        for(int i : crimpedPorts) {
            if(connections.get(i) == null) {
                CrimpedCable c = new CrimpedCable();
                CableManager.addCable(c);
                attachCrimpedCable(c, i);
            }
        }
    }

    public void attachCrimpedCable(Cable cable, int port) {}

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

    public void attachWire(Cable cable, int port, boolean endOfWire) {
        connections.set(port, cable);
        ends.set(port, endOfWire);

        attachWireLib(cable, port, endOfWire);

        CableManager.currentCable = null;
    }

    public void attachWireLib(Cable cable, int port, boolean endOfWire) {};

    public int getHardwareID() {
        return hardwareID;
    }

    public void firstClickAttach(Cable cable, int port, boolean endOfWire) {
        connections.set(port, cable);
        ends.set(port, endOfWire);

        cable.removeCoordinates();

        attachWireLib(cable, port, endOfWire);

        cable.setAppendingFromEnd(false);
        cable.setAppendingFromBegin(false);

    }

}
