package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class Simulation {
    private DelayedRemovalArray<Hardware> h;

    public boolean isRunning = false;

    public Simulation() {
    }

    public void simulate() {
        isRunning = true;

        h = HardwareManager.getHardware();

        for (Hardware ha : h) {
            if (ha.check() != null) {
                ha.drawErrorHover();
                CircuitGUIManager.popup.addLabel(ha.check(), ha.getPositionProjected());
            } else {
                ha.drawGoodHover();
            }
        }

        //CircuitGUIManager.popup.activateError("Simulation Not Implemented");

        //return error;
    }
}
