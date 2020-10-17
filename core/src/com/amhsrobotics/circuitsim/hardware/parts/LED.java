package com.amhsrobotics.circuitsim.hardware.parts;

import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class LED {

    private String color, origColor;
    private String type;
    private Vector2 position;
    private Vector2 size;
    public Sprite sprite;
    private Hardware hardware;
    public boolean blinking;
    private boolean on;
    private int speed;
    private int cnt, et;
    public String status = " | Off";

    public LED(JSONArray position, Hardware hardware, JSONArray size, String type, String color) {

        this.hardware = hardware;
        this.position = new Vector2((long) position.get(0), (long) position.get(1));
        this.size = new Vector2((long) size.get(0), (long) size.get(1));
        this.color = color;
        this.origColor = color;
        this.type = type;

        sprite = new Sprite(new Texture(Gdx.files.internal("img/led/" + this.color + ".png")));
        setPosition();
        sprite.setSize(this.size.x, this.size.y);
    }

    public void reset() {
        blinking = false;
        this.color = origColor;
        sprite.setTexture(new Texture(Gdx.files.internal("img/led/" + this.color + ".png")));
        status = " | Off";
    }

    public void setStatus(String s) {
        status = " | " + s;
    }

    public void render(SpriteBatch batch) {
        if(et != 0) {
            et--;
            if(et == 0) {
                if(blinking) {
                    blinking = false;
                    setColorTemp(color);
                } else {
                    reset();
                }
            }
        }

        if (blinking) {
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

    public void endTime(int time) {
        et = time;
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

    public void blinkTime(int speed, int time) {
        blinking = true;
        this.speed = speed;
        on = true;
        cnt = speed;
        et = time;
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
