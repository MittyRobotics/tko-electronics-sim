package com.amhsrobotics.circuitsim.utility.camera;

import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class Rumble {

    public static float time = 0, currentTime = 0, power = 0, currentPower = 0;
    private static Random random;
    private static final Vector3 pos = new Vector3();

    public static void rumble(float rumblePower, float rumbleLength) {
        random = new Random();
        power = rumblePower;
        time = rumbleLength;
        currentTime = 0;
    }

    public static Vector3 tick(float delta) {
        if (currentTime <= time) {
            currentPower = power * ((time - currentTime) / time);

            pos.x = (random.nextFloat() - 0.5f) * 2 * currentPower;
            pos.y = (random.nextFloat() - 0.5f) * 2 * currentPower;

            currentTime += delta;
        } else {
            time = 0;
        }
        return pos;
    }

    public static float getRumbleTimeLeft() {
        return time;
    }

    public static Vector3 getPos() {
        return pos;
    }
}
