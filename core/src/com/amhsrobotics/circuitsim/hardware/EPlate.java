package com.amhsrobotics.circuitsim.hardware;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class EPlate extends Hardware {

    private Vector2 position;
    private int hardwareID;
    private HardwareType type;

    private ArrayList<Hardware> hardwareOnPlate = new ArrayList<>();


    public EPlate(Vector2 pos) {
        super(pos, HardwareType.EPLATE);
    }

    public void init() {

    }
}
