package com.amhsrobotics.circuitsim.files;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class CableModel {

    public CableModel() {}

    public float gauge;
    public float r, g, b, a;
    public boolean appendingFromEnd, appendingFromBegin, disableEnd, disableBegin;
    public ArrayList<Vector2> coordinates;

}
