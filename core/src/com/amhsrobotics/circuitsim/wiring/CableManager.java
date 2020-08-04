package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;

public class CableManager {

    public static Cable currentCable = null;

    private static DelayedRemovalArray<Cable> cables = new DelayedRemovalArray<>();

    public static void update(ModifiedShapeRenderer renderer, ClippedCameraController cam) {
        for(Cable c : cables) {
            c.update(renderer, cam);
        }
    }

    public static void addCable(Cable cable) {
        cable.setAppendingFromEnd(true);
        cables.add(cable);
    }

    public static void deleteCable(Cable cable) {
        cables.removeValue(cable, true);
//        cables.remove(cable);
    }
}