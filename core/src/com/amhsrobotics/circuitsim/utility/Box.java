package com.amhsrobotics.circuitsim.utility;

import com.badlogic.gdx.math.Vector2;

public class Box {

    public Vector2 bl, br, tl, tr;
    private Vector2[] vertices;

    public Box(Vector2 bl, Vector2 br, Vector2 tl, Vector2 tr) {
        this.bl = bl;
        this.br = br;
        this.tl = tl;
        this.tr = tr;
        vertices = new Vector2[] {bl, br, tl, tr};
    }

    public Vector2 getAtIndex(int index) {
        return vertices[index];
    }

    public void updateVertice(int vertice, Vector2 value) {
        vertices[vertice] =  new Vector2(value);
    }

    public float getX() {
        return bl.x;
    }

    public float getY() {
        return bl.y;
    }

    public float getWidth() {
        return br.x - bl.x;
    }

    public float getHeight() {
        return tr.y - br.y;
    }

    public boolean contains(float x, float y) {
        return getX() <= x && getX() + getWidth() >= x && getY() <= y && getY() + getHeight() >= y;
    }
}
