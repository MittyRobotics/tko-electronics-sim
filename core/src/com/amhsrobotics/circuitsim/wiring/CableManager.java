package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.Tuple;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class CableManager {

    public static Cable currentCable = null;
    private static int id = 1;

    private static DelayedRemovalArray<Cable> cables = new DelayedRemovalArray<>();

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        for(Cable c : cables) {
            c.update(renderer, cam);
        }
    }

    public static void addCable(Cable cable) {
        cable.setAppendingFromEnd(true);
        cables.add(cable);
    }

    public static DelayedRemovalArray<Cable> getCables() {
        return cables;
    }


    public static Tuple<Cable, Integer> wireHoveringWire(ClippedCameraController camera, Cable cable) {
        for (Cable c : cables) {
            if (c.getID() != cable.getID()) {
                int ans = c.hoveringOnEndpoint(camera);
                if (ans == 1) {
                    return new Tuple<>(c, 1);
                } else if (ans == 2) {
                    return new Tuple<>(c, 2);
                }
            }
        }
        return null;
    }

    public static void mergeCables(Cable cable1, Cable cable2, boolean cable2begin) {
        cable2.mergeCable(cable1, cable2begin);
        deleteCable(cable1);
    }

    public static void addCable(float startX, float startY) {
        CircuitGUIManager.propertiesBox.show();
        Cable temp = new Cable(new Vector2(startX, startY), id);
        id++;
        currentCable = temp;

        temp.setAppendingFromEnd(true);
        cables.add(temp);
    }

    public static void deleteCable(Cable cable) {
        cables.removeValue(cable, true);
//        cables.remove(cable);
    }
}