package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;

public abstract class Hardware {

    private Vector2 position;
    private int hardwareID;
    public ArrayList<Cable> connections;
    public ArrayList<Boolean> ends;
    public int connNum;

    public Hardware(Vector2 pos) {
        this.position = pos;
        this.hardwareID = DeviceUtil.getNewHardwareID();

        connections = new ArrayList<>();
        ends = new ArrayList<>();
    }

    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        renderer.setProjectionMatrix(camera.getCamera().combined);
        batch.setProjectionMatrix(camera.getCamera().combined);
    }

    public void reattachWire(Cable cable, int port, boolean endOfWire) { }

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

    public void clearConnection(Cable cable) { }

    public int getConnectionPosition(Cable cable) {
        for(int i = 0; i < connections.size(); ++i) {
            if(connections.get(i) == cable) {
                return i;
            }
        }
        return -1;
    }


    public int getHardwareID() {
        return hardwareID;
    }
}
