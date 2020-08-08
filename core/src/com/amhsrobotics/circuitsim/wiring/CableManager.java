package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;
import sun.util.resources.be.CalendarData_be;

public class CableManager {

    public static Cable currentCable = null;
    private static int id = 1;

    private static DelayedRemovalArray<Cable> cables = new DelayedRemovalArray<>();

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        for(Cable c : cables) {
            c.update(renderer, cam);
        }
    }

    public static Cable wireHoveringWire(ClippedCameraController camera, Cable cable) {
        for (Cable c : cables) {
            if (c.getID() != cable.getID()) {
                Gdx.app.log(String.valueOf(c.getID()), Integer.toString(cable.getID()));
                int ans = c.hoveringOnEndpoint(camera);
                if (ans == 1) {
                    mergeCables(c, cable, true);
                } else if (ans == 2) {
                    mergeCables(c, cable, false);
                }
            }
        }
    }

    public static void addCable(Cable cable) {
        cable.setAppendingFromEnd(true);
        cables.add(cable);
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