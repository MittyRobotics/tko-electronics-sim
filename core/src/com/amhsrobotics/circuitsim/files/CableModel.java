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
    public float r, g, b, a;
    public boolean appendingFromEnd, appendingFromBegin, disableEnd, disableBegin;
    public ArrayList<Vector2> coordinates;
    public String cableType;

    public void load(Cable c) {
        coordinates = c.coordinates;
        appendingFromBegin = c.appendingFromBegin;
        appendingFromEnd = c.appendingFromEnd;
        disableBegin = c.disableBegin;
        disableEnd = c.disableEnd;
        gauge = c.gauge;

        r = c.color.r;
        g = c.color.g;
        b = c.color.b;
        a = c.color.a;

        if(c instanceof EthernetCable) {
            cableType = "ethernet";
        } else if(c instanceof Tubing) {
            cableType = "tubing";
        } else if(c instanceof CrimpedCable) {
            cableType = "crimped";
        } else {
            cableType = "regular";
        }

        id = c.getID();
    }
}
