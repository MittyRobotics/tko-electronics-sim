package com.amhsrobotics.circuitsim.files;


import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class HardwareModel {

    public HardwareModel() {}

    public HardwareType type;
    public Vector2 position;
    public DelayedRemovalArray<CableModel> connections;

    public void load(Hardware h) {
        type = h.type;
        position = h.getPosition();
    }

    public void loadCableConnection(CableModel c) {
        if(connections == null) {
            connections = new DelayedRemovalArray<>();
        }
        connections.add(c);
    }
}
