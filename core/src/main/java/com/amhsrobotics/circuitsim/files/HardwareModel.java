package com.amhsrobotics.circuitsim.files;


import com.amhsrobotics.circuitsim.hardware.EPlate;
import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.utility.Box;
import com.badlogic.gdx.math.Vector2;

public class HardwareModel {

    public HardwareType type;
    public Vector2 position;
    public int id;
    public Box box;
    public int rotation;
    public HardwareModel() {
    }

    public void load(Hardware h) {
        type = h.type;
        id = h.getHardwareID();
        position = h.getPosition();

        if (h instanceof EPlate) {
            box = ((EPlate) h).getBox();
        }

        if (h instanceof Flippable) {
            rotation = ((Flippable) h).getRotation();
        }
    }
}
