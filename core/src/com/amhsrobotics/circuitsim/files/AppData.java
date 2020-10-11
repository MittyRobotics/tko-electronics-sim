package com.amhsrobotics.circuitsim.files;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class AppData {

    private ArrayList<HardwareModel> hardware = new ArrayList<>();
    private ArrayList<CableModel> cables = new ArrayList<>();
    private float gridSpacing;
    private Vector2 gridSize;

    public ArrayList<HardwareModel> getHardware() {
        return hardware;
    }

    public void setHardware(ArrayList<HardwareModel> hardware) {
        this.hardware = hardware;
    }

    public void addHardware(HardwareModel model) {
        hardware.add(model);
    }

    public void addCable(CableModel model) {
        cables.add(model);
    }

    public ArrayList<CableModel> getCables() {
        return cables;
    }

    public void setCables(ArrayList<CableModel> cables) {
        this.cables = cables;
    }

    public float getGridSpacing() {
        return gridSpacing;
    }

    public void setGridSpacing(float gridSpacing) {
        this.gridSpacing = gridSpacing;
    }

    public Vector2 getGridSize() {
        return gridSize;
    }

    public void setGridSize(Vector2 gridSize) {
        this.gridSize = gridSize;
    }
}
