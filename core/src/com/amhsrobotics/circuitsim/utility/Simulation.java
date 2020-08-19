package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.hardware.devices.Battery;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import java.util.HashMap;

public class Simulation {
    DelayedRemovalArray<Hardware> h;
    DelayedRemovalArray<Cable> c;
    HashMap<Object, String> error;
    Battery battery;

    public Simulation() {
        error = new HashMap<>();
    }

    public HashMap<Object, String> simulate() {
        error.clear();
        battery = null;

        h = HardwareManager.getHardware();
        c = CableManager.getCables();

        for(Hardware ha : h) {
            if(ha.type == HardwareType.BATTERY) {
                if(battery == null) {
                    battery = (Battery) ha;
                } else {
                    error.put(ha, "Only one battery is valid");
                    return error;
                }
            }
        }

        if (battery == null) {
            error.put(null, "A battery is required");
            return error;
        }

        if(battery.connections.get(0) == null || battery.connections.get(1) == null) {
            error.put(battery, "Battery is not connected correctly");
            return error;
        }

        return error;
    }
}
