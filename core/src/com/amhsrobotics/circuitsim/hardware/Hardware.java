package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class Hardware {

    private Vector2 position;
    private int hardwareID;

    public Hardware(Vector2 pos) {
        this.position = pos;
        this.hardwareID = DeviceUtil.getNewHardwareID();
    }

    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        renderer.setProjectionMatrix(camera.getCamera().combined);
        batch.setProjectionMatrix(camera.getCamera().combined);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public int getHardwareID() {
        return hardwareID;
    }
}
