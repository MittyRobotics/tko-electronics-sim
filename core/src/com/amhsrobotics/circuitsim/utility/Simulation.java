package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class Simulation {
    public boolean isRunning = false;
    private DelayedRemovalArray<Hardware> h;

    public Simulation() {
    }

    public void simulate() {
        isRunning = true;

        h = HardwareManager.getHardware();

        for (Hardware ha : h) {
            if (ha.check() != null) {
                ha.drawErrorHover();
                CircuitGUIManager.popup.addLabel(ha.check(), ha.getPositionProjected());
                //ha.resetLEDs();
            } else {
                ha.drawGoodHover();
            }
        }

        //CircuitGUIManager.popup.activateError("Simulation Not Implemented");

        //return error;
    }
}
