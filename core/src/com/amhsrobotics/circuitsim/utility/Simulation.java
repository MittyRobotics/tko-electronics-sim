package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import java.util.HashMap;

public class Simulation {
    DelayedRemovalArray<Hardware> h;
    DelayedRemovalArray<Cable> c;
    HashMap<Object, String> errors;

    public Simulation() {
        errors = new HashMap<>();
    }

    public HashMap<Object, String> simulate() {
        errors.clear();

        h = HardwareManager.getHardware();
        c = CableManager.getCables();

        return errors;
    }
}
