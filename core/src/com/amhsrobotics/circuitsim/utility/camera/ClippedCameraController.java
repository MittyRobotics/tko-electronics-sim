package com.amhsrobotics.circuitsim.utility.camera;

import com.amhsrobotics.circuitsim.Constants;
import com.badlogic.gdx.math.MathUtils;
import me.rohanbansal.ricochet.camera.CameraController;

public class ClippedCameraController extends CameraController {

    public ClippedCameraController(boolean isBox2D) {
        super(isBox2D, 1f);
    }

    public void calculateBounds() {

        float zoom = getCamera().zoom;
        float zoomedHalfWorldWidth = zoom * getCamera().viewportWidth / 2;
        float zoomedHalfWorldHeight = zoom * getCamera().viewportHeight / 2;

        getCamera().position.x = MathUtils.clamp(getCamera().position.x, zoomedHalfWorldWidth, Constants.WORLD_DIM.x - zoomedHalfWorldWidth);
        getCamera().position.y = MathUtils.clamp(getCamera().position.y, zoomedHalfWorldHeight, Constants.WORLD_DIM.y - zoomedHalfWorldHeight);
    }

}
