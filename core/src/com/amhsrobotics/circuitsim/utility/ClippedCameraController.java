package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.Constants;
import com.badlogic.gdx.math.MathUtils;
import me.rohanbansal.ricochet.camera.CameraController;

public class ClippedCameraController extends CameraController {

    public ClippedCameraController(boolean isBox2D) {
        super(isBox2D, 1f);
    }

    public void calculateBounds() {

        float worldWidth = Constants.WORLD_DIM.x;
        float worldHeight = Constants.WORLD_DIM.y;
        float zoom = getCamera().zoom;
        float zoomedHalfWorldWidth = zoom * getCamera().viewportWidth / 2;
        float zoomedHalfWorldHeight = zoom * getCamera().viewportHeight / 2;

        float minX = zoomedHalfWorldWidth;
        float maxX = worldWidth - zoomedHalfWorldWidth;

        float minY = zoomedHalfWorldHeight;
        float maxY = worldHeight - zoomedHalfWorldHeight;

        getCamera().position.x = MathUtils.clamp(getCamera().position.x, minX, maxX);
        getCamera().position.y = MathUtils.clamp(getCamera().position.y, minY, maxY);
    }

}
