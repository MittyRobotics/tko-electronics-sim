package com.amhsrobotics.circuitsim.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import java.util.HashMap;

public class LinkTimer {

    private static final HashMap<Integer, Float> timeLeft = new HashMap<>();
    private static final HashMap<Integer, Float> timerLengths = new HashMap<>();
    private static final HashMap<Integer, Runnable> runnables = new HashMap<>();

    private static final DelayedRemovalArray<Integer> currentActiveTimers = new DelayedRemovalArray<>();

    private static int additiveTimerID = 0;

    public static void init(float seconds, Runnable runnable) {
        additiveTimerID++;

        timeLeft.put(additiveTimerID, 0f);
        timerLengths.put(additiveTimerID, seconds);
        runnables.put(additiveTimerID, runnable);

        currentActiveTimers.add(additiveTimerID);
    }

    public static void tick() {

        try {
            if (currentActiveTimers.size > 0) {
                for (int timerID : currentActiveTimers) {
                    timeLeft.put(timerID, timeLeft.get(timerID) + Gdx.graphics.getRawDeltaTime());

                    if (timeLeft.get(timerID) >= timerLengths.get(timerID)) {
                        runnables.get(timerID).run();
                        timeLeft.remove(timerID);
                        timerLengths.remove(timerID);
                        currentActiveTimers.removeValue(timerID, true);
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
