package com.amhsrobotics.circuitsim.utility;

import com.badlogic.gdx.math.Vector2;

public class Box {

    public float x, y, width, height;
    private Vector2[] resizePoints;

//    public Box(Vector2 bl, Vector2 br, Vector2 tl, Vector2 tr) {
//        vertices = new Vector2[] {bl, br, tl, tr};
//        resizePoints = new Vector2[] {
//                bl, // bottom left
//                new Vector2((br.x + bl.x) / 2, bl.y), // bottom middle
//                br, // bottom right
//                new Vector2(bl.x, (tl.y - bl.y) / 2), // left middle
//                tr, // top left
//                new Vector2((br.x + bl.x) / 2, tr.y), // top middle
//                tl, // top right
//                new Vector2(br.x, (tr.y - br.y) / 2), // right middle
//                new Vector2((br.x - bl.x) / 2, (tr.y - br.y) / 2), // center
//        };
//    }

    public Box(float x, float y, float width, float height) {
//        vertices = new Vector2[] {new Vector2(x, y), new Vector2(x + width, y), new Vector2(x, y + height), new Vector2(x + width, y + height)};
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
                new Vector2((x + width), y + (height / 2)), // right middle
        };
    }

    public void initResize() {

    }

//    public Vector2 getVerticeAtIndex(int index) {
//        return vertices[index];
//    }

    public Vector2 getResizePointAtIndex(int index) {
        return resizePoints[index];
    }

//    public void updateVertice(int vertice, Vector2 value) {
//        vertices[vertice] =  new Vector2(value);
//    }

    public float getX() {
//        return vertices[0].x;
        return x;
    }

    public float getY() {
//        return vertices[0].y;
        return y;
    }

    public float getWidth() {
//        return vertices[1].x - vertices[0].x;
        return width;
    }

    public float getHeight() {
//        return vertices[2].y - vertices[0].y;
        return height;
    }

    public boolean contains(float x, float y) {
        return getX() <= x && getX() + getWidth() >= x && getY() <= y && getY() + getHeight() >= y;
    }
}
