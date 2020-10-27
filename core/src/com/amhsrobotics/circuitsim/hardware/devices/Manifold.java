package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import org.json.simple.JSONArray;

import java.util.ArrayList;

public class Manifold extends Flippable {

    public ArrayList<Sprite> solenoids = new ArrayList<>();

    public Manifold() {}

    public Manifold(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.MANIFOLD, addCrimped);

        int temp = 0;
        for(JSONArray arr : pinDefs) {
            if(temp == 0) {
                temp = 1;
            } else {
                Sprite tempw;
                tempw = new Sprite(new Texture(Gdx.files.internal("img/hardware/doublesolenoid.png")));
                tempw.setCenter(position.x + (Long) arr.get(0), position.y);
                tempw.rotate(90);
                solenoids.add(tempw);
            }
        }

        initConnections();
        initEnds();
    }

    public void rotateThis() {
        super.rotateThis();
        for(Sprite temp : solenoids) {
            temp.rotate(90);
        }
    }


    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        batch.begin();
        for(Sprite temp : solenoids) {
            temp.setCenter(getPosition().x + (Long) pinDefs.get(solenoids.indexOf(temp)+1).get(0), getPosition().y);
            Vector2 pos = new Vector2(temp.getX() + temp.getWidth()/2, temp.getY() + temp.getHeight()/2);
            pos.rotateAround(new Vector2(base.getX() + base.getWidth() / 2, base.getY() + base.getHeight() / 2), base.getRotation());
            temp.setCenter(pos.x, pos.y);
            temp.draw(batch);
        }
        batch.end();
        super.update(batch, renderer, camera);
    }

    public Vector2 calculate(int port) {
       return calculateDirection(cur, port, 100);
    }
}
