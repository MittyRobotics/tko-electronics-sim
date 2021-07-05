package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import java.util.HashMap;

public class Simulation {
    public boolean isRunning = false;
    public boolean changed = false;
    private DelayedRemovalArray<Hardware> h;
    public HashMap<Hardware, String> temp, store;

    public Simulation() {
        temp = new HashMap<>();
        store = new HashMap<>();
    }

    public void simulate() {
        temp.clear();
        store.clear();
        isRunning = true;
        changed = false;

        h = HardwareManager.getHardware();

        for (Hardware ha : h) {
            String temp_s = ha.check();
            if (temp_s != null) {
                ha.drawErrorHover();
                CircuitGUIManager.popup.addLabel(temp_s, ha.getPositionProjected());

                store.put(ha, temp_s);
                //ha.resetLEDs();
            } else {
                ha.drawGoodHover();
            }
        }

        System.gc();

        //CircuitGUIManager.popup.activateError("Simulation Not Implemented");

        //return error;
    }


}
