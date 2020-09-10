package com.amhsrobotics.circuitsim.files;

import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class AppData {

    private ArrayList<Hardware> hardware;
    private ArrayList<Cable> cables;
    private float gridSpacing;
    private Vector2 gridSize;

    public ArrayList<Hardware> getHardware() {
        return hardware;
    }

    public void setHardware(ArrayList<Hardware> hardware) {
        this.hardware = hardware;
    }

    public ArrayList<Cable> getCables() {
        return cables;
    }

    public void setCables(ArrayList<Cable> cables) {
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
