package com.amhsrobotics.circuitsim.files;

import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class CableModel {

    public CableModel() {}

    public float gauge;
    public float r, g, b, a;
    public boolean appendingFromEnd, appendingFromBegin, disableEnd, disableBegin;
    public ArrayList<Vector2> coordinates;

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
    }
}
