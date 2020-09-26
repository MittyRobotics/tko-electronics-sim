package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.camera.CameraController;


public class CrimpedCable extends Cable {

    public CrimpedCable(int gauge) {
        super(new Vector2(0, 0), CableManager.getCrimpedID());
        this.gauge = gauge;

        appendingFromBegin = false;
        appendingFromEnd = false;

        // Will be attached to hardware in hardware, not here
    }

    @Override
    public void populateProperties(String title, boolean enableColor, boolean enableGauge, boolean enableConnections, int ID) {
        super.populateProperties("Crimped", true, false, true, -ID);
    }


}