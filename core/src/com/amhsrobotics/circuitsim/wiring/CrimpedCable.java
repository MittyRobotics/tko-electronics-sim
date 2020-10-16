package com.amhsrobotics.circuitsim.wiring;

import com.badlogic.gdx.math.Vector2;


public class CrimpedCable extends Cable {

    public CrimpedCable(int gauge, int id_) {
        super(new Vector2(0, 0), id_);
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