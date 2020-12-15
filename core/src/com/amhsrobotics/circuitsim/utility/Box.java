package com.amhsrobotics.circuitsim.utility;

import com.badlogic.gdx.math.Vector2;

public class Box {

    public float x, y, width, height;
    private Vector2[] resizePoints;

    public Box() {}

    public Box(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        resizePoints = new Vector2[] {
                new Vector2(x, y), // bottom left
                new Vector2(x + (width / 2), y), // bottom center
                new Vector2(x + width, y), // bottom right
                new Vector2(x, y + (height / 2)), // left center
                new Vector2(x, y + height), // top left
                new Vector2(x + (width / 2), y + height), // top middle
                new Vector2((x + width), (y + height)), // top right
                new Vector2((x + width), y + (height / 2)), // right center
                new Vector2(x + width / 2, y + height / 2) // middle
        };
    }


    public Vector2 getResizePointAtIndex(int index) {
        return resizePoints[index];
    }

    public boolean contains(float x, float y) {
        return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
    }
}
