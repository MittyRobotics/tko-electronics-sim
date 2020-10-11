package com.amhsrobotics.circuitsim.files;


import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class HardwareModel {

    public HardwareModel() {}

    public HardwareType type;
    public Vector2 position;
    public DelayedRemovalArray<CableModel> connections;
}
