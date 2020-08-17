package com.amhsrobotics.circuitsim.hardware.parts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class LED {

    private String color;
    private String type;
    private Vector2 position;
    private Vector2 size;
    private Sprite sprite;

    public LED(JSONArray position, JSONArray size, String type, String color) {

        this.position = new Vector2((long) position.get(0), (long) position.get(1));
        this.size = new Vector2((long) size.get(0), (long) size.get(1));
        this.color = color;
        this.type = type;

        sprite = new Sprite(new Texture(Gdx.files.internal("img/led/" + this.color + ".png")));
        sprite.setCenter(this.position.x, this.position.y);
        sprite.setSize(this.size.x, this.size.y);
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        sprite = new Sprite(new Texture(Gdx.files.internal("img/led/" + this.color + ".png")));
        sprite.setCenter(position.x, position.y);
        sprite.setSize(size.x, size.y);
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
