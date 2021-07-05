package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import java.util.HashMap;

public class Simulation {
    public boolean isRunning = false;
    private DelayedRemovalArray<Hardware> h;
    public HashMap<Hardware, String> temp;

    public Simulation() {
        temp = new HashMap<>();
    }

    public void simulate() {
        temp.clear();
        isRunning = true;

        h = HardwareManager.getHardware();

        for (Hardware ha : h) {
            String temp_s = ha.check();
            if (temp_s != null) {
                ha.drawErrorHover();
                CircuitGUIManager.popup.addLabel(temp_s, ha.getPositionProjected());
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
