package com.amhsrobotics.circuitsim.files;

import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.amhsrobotics.circuitsim.wiring.EthernetCable;
import com.amhsrobotics.circuitsim.wiring.Tubing;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class CableModel {

    public float gauge;
    public int id;
    public int hardware1ID, hardware2ID;
    public float r, g, b, a;
    public ArrayList<Vector2> coordinates;
    public int port1, port2;
    public String cableType;
    public CableModel() {
    }

    public void load(Cable c) {
        coordinates = c.coordinates;
        gauge = c.gauge;

        hardware1ID = c.getConnection1().getHardwareID();
        port1 = c.connection1port;
        hardware2ID = c.getConnection2().getHardwareID();
        port2 = c.connection2port;

        r = c.color.r;
        g = c.color.g;
        b = c.color.b;
        a = c.color.a;

        if (c instanceof EthernetCable) {
            cableType = "ethernet";
        } else if (c instanceof Tubing) {
            cableType = "tubing";
        } else if (c instanceof CrimpedCable) {
            cableType = "crimped";
        } else {
            cableType = "regular";
        }

        id = c.getID();
    }
}
