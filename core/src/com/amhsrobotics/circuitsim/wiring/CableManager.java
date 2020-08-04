package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;

public class CableManager {

    public static Cable currentCable = null;

    private static ArrayList<Cable> cables = new ArrayList<>();

    public static void update(ModifiedShapeRenderer renderer, ClippedCameraController cam) {
        for(Cable c : cables) {
            c.update(renderer, cam);
        }
    }

    public static void addCable(Cable cable) {
        cables.add(cable);
    }

    public static void deleteCable(Cable cable) {
        cables.remove(cable);
    }
}