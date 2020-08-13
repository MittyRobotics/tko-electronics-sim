package com.amhsrobotics.circuitsim.hardware.parts;

import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class LED {

    private Color color;
    private String type;
    private Vector2 position;

    public LED(JSONArray position, String type, String color) {

        this.position = new Vector2((long) position.get(0), (long) position.get(1));
//        this.color = DeviceUtil.COLORS.get(color.substring(0, 1).toUpperCase() + color.substring(1));
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
