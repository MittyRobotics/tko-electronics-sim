package com.amhsrobotics.circuitsim.hardware.parts;

import com.amhsrobotics.circuitsim.hardware.Hardware;
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
    public Sprite sprite;
    private Hardware hardware;
    public boolean blinking;
    private boolean on;
    private int speed;
    private int cnt;

    public LED(JSONArray position, Hardware hardware, JSONArray size, String type, String color) {

        this.hardware = hardware;
        this.position = new Vector2((long) position.get(0), (long) position.get(1));
        this.size = new Vector2((long) size.get(0), (long) size.get(1));
        this.color = color;
        this.type = type;

        sprite = new Sprite(new Texture(Gdx.files.internal("img/led/" + this.color + ".png")));
        setPosition();
        sprite.setSize(this.size.x, this.size.y);
    }

    public void render(SpriteBatch batch) {
        if(blinking) {
            cnt--;
            if(cnt == 0) {
                cnt = speed;
                if(on) {
                    on = false;
                    setColorTemp("off");
                } else {
                    on = true;
                    setColorTemp(color);
                }
            }
        }
        sprite.draw(batch);
    }

    public void setPosition() {
        sprite.setCenter(hardware.getPosition().x + this.position.x, hardware.getPosition().y + this.position.y);
        Vector2 pos = new Vector2(sprite.getX() + sprite.getWidth()/2, sprite.getY() + sprite.getHeight()/2);
        pos.rotateAround(new Vector2(hardware.base.getX() + hardware.base.getWidth() / 2, hardware.base.getY() + hardware.base.getHeight() / 2), hardware.base.getRotation());
        sprite.setCenter(pos.x, pos.y);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        sprite.setTexture(new Texture(Gdx.files.internal("img/led/" + this.color + ".png")));
    }

    public void setColorTemp(String color) {
        sprite.setTexture(new Texture(Gdx.files.internal("img/led/" + color + ".png")));
    }

    public void blink(int speed) {
        blinking = true;
        this.speed = speed;
        on = true;
        cnt = speed;
    }

    public void stopBink() {
        blinking = false;
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
