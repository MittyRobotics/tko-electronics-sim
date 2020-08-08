package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.DoubleSandCrab;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.HashMap;

public class CableManager {

    public static Cable currentCable = null;
    private static int count = 1;

    private static DelayedRemovalArray<Cable> cables = new DelayedRemovalArray<>();

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        for(Cable c : cables) {
            c.update(renderer, cam);
        }
    }

    public static void wireHoveringWire(ClippedCameraController camera, Cable cable) {
        for (Cable c : cables) {
            if (c.getID() != cable.getID()) {
                Gdx.app.log(String.valueOf(c.getID()), Integer.toString(cable.getID()));
                int ans = c.hoveringOnEndpoint(camera);
                if (ans == 1) {
                    mergeCables(c, cable, true);
                    currentCable = cable;
                } else if (ans == 2) {
                    mergeCables(c, cable, false);
                    currentCable = cable;
                }
            }
        }
    }

    public static void addCable(Cable cable) {
        cable.setAppendingFromEnd(true);
        cables.add(cable);
    }

    public static void mergeCables(Cable cable1, Cable cable2, boolean cable2begin) {
        deleteCable(cable1);
        cable2.mergeCable(cable1, cable2begin);
    }

    public static void addCable(float startX, float startY) {
        CircuitGUIManager.propertiesBox.show();
        Cable temp = new Cable(new Vector2(startX, startY), count);
        count++;
        currentCable = temp;

        temp.setAppendingFromEnd(true);
        cables.add(temp);
    }

    public static void deleteCable(Cable cable) {
        cables.removeValue(cable, true);
//        cables.remove(cable);
    }
}