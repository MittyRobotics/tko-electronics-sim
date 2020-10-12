package com.amhsrobotics.circuitsim.files;

import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.amhsrobotics.circuitsim.wiring.EthernetCable;
import com.amhsrobotics.circuitsim.wiring.Tubing;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class CableModel {

    public CableModel() {}

    public float gauge;
    public int id;
    public int hardware1ID, hardware2ID;
    public float r, g, b, a;
    public ArrayList<Vector2> coordinates;
    public int port1, port2;
    public String cableType;

    public void load(Cable c) {
        coordinates = c.coordinates;
        gauge = c.gauge;

        hardware1ID = c.getConnection1().getHardwareID();
        port1 = c.connection1port;

        r = c.color.r;
        g = c.color.g;
        b = c.color.b;
        a = c.color.a;

        if(c instanceof EthernetCable) {
            cableType = "ethernet";
            hardware2ID = c.getConnection2().getHardwareID();
            port2 = c.connection2port;
        } else if(c instanceof Tubing) {
            cableType = "tubing";
            hardware2ID = c.getConnection2().getHardwareID();
            port2 = c.connection2port;
        } else if(c instanceof CrimpedCable) {
            cableType = "crimped";
            hardware2ID = 0;
            port2 = 0;
        } else {
            cableType = "regular";
            hardware2ID = c.getConnection2().getHardwareID();
            port2 = c.connection2port;
        }

        id = c.getID();
    }
}
