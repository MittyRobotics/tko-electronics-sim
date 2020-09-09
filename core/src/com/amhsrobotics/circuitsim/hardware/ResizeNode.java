package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.utility.Box;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.camera.CameraController;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.HashMap;

public class ResizeNode {

    public float x, y;
    public NodeType type;
    private boolean selected;
    private Circle circle;

    public enum NodeType {
        BOTTOM_LEFT, BOTTOM_MIDDLE, BOTTOM_RIGHT,
        TOP_LEFT, TOP_MIDDLE, TOP_RIGHT,
        LEFT_MIDDLE, RIGHT_MIDDLE
    }

    public static HashMap<Integer, NodeType> nodeMap = new HashMap<Integer, NodeType>() {{
        put(0, NodeType.BOTTOM_LEFT);
        put(1, NodeType.BOTTOM_MIDDLE);
        put(2, NodeType.BOTTOM_RIGHT);
        put(6, NodeType.LEFT_MIDDLE);
        put(3, NodeType.TOP_LEFT);
        put(4, NodeType.TOP_MIDDLE);
        put(5, NodeType.TOP_RIGHT);
        put(7, NodeType.RIGHT_MIDDLE);
    }};

    public ResizeNode(float x, float y, NodeType type) {
        this.x = x;
        this.y = y;
        this.type = type;

        circle = new Circle(x, y, 16);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void draw(ModifiedShapeRenderer renderer, CameraController camera) {
        Vector2 vec = Tools.mouseScreenToWorld(camera);
        if(circle.contains(vec)) {
            renderer.setColor(Color.WHITE);
            renderer.circle(circle.x, circle.y, circle.radius);
        } else {
            renderer.setColor(Color.SALMON);
            renderer.circle(circle.x, circle.y, circle.radius);
        }
    }

    public void movePosition(CameraController camera, Box box) {

        Vector2 vec = Tools.mouseScreenToWorld(camera);
        circle.setPosition(vec);
        if(isSelected()) {
            switch(type) {
                case BOTTOM_LEFT:
                    box.width += box.x - vec.x;
                    box.x = vec.x;
                    box.height = vec.y - box.y;
                    break;
                case BOTTOM_MIDDLE:
                    box.height = vec.y - box.y;
                    break;
                case BOTTOM_RIGHT:
                    box.width = vec.x - box.x;
                    box.height = vec.y - box.y;
                    break;
                case TOP_LEFT:
                    box.width += box.x - vec.x;
                    box.height += box.y - vec.y;
                    box.x = vec.x;
                    box.y = vec.y;
                    break;
                case TOP_MIDDLE:
                    box.height += box.y - vec.y;
                    box.y = vec.y;
                    break;
                case TOP_RIGHT:
                    box.width = vec.x - box.x;
                    box.height += box.y - vec.y;
                    box.y = vec.y;
                    break;
                case LEFT_MIDDLE:
                    box.width += box.x - vec.x;
                    box.x = vec.x;
                    break;
                case RIGHT_MIDDLE:
                    box.width = vec.x - box.x;
                    break;
            }
        }
    }

    public void updateIdlePos(Box box) {
        switch(type) {
            case BOTTOM_LEFT:
                circle.x = box.x;
                circle.y = box.y;
                break;
            case BOTTOM_MIDDLE:
                circle.x = box.x + box.width / 2;
                circle.y = box.y;
                break;
            case BOTTOM_RIGHT:
                circle.x = box.x + box.width;
                circle.y = box.y;
                break;
            case TOP_LEFT:
                circle.x = box.x;
                circle.y = box.y + box.height;
                break;
            case TOP_MIDDLE:
                circle.x = box.x + box.width / 2;
                circle.y = box.y + box.height;
                break;
            case TOP_RIGHT:
                circle.x = box.x + box.width;
                circle.y = box.y + box.height;
                break;
            case LEFT_MIDDLE:
                circle.x = box.x;
                circle.y = box.y + box.height / 2;
                break;
            case RIGHT_MIDDLE:
                circle.x = box.x + box.width;
                circle.y = box.y + box.height / 2;
                break;
        }
    }

    public boolean contains(Vector2 vec) {
        return circle.contains(vec);
    }
}
