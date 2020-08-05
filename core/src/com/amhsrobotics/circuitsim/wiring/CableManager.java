package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class CableManager {

    public static Cable currentCable = null;
    public static DoubleSandCrab currentConnector = null;

    private static DelayedRemovalArray<Cable> cables = new DelayedRemovalArray<>();
    private static DelayedRemovalArray<DoubleSandCrab> sandcrabs = new DelayedRemovalArray<>();

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        for(Cable c : cables) {
            c.update(renderer, cam);
        }
        for(DoubleSandCrab crabs : sandcrabs) {
            crabs.update(batch, renderer, cam);
        }
    }

    public static void addCable(Cable cable) {
        cable.setAppendingFromEnd(true);
        cables.add(cable);
    }

    public static void addDoubleSandCrab(float startX, float startY) {
        CircuitGUIManager.propertiesBox.show();
        DoubleSandCrab temp = new DoubleSandCrab(new Vector2(startX, startY));
        CableManager.currentConnector = temp;

        sandcrabs.add(temp);
    }

    public static void addCable(float startX, float startY) {
        CircuitGUIManager.propertiesBox.show();
        Cable temp = new Cable(new Vector2(startX, startY));
        CableManager.currentCable = temp;

        temp.setAppendingFromEnd(true);
        cables.add(temp);
    }

    public static void deleteCable(Cable cable) {
        cables.removeValue(cable, true);
//        cables.remove(cable);
    }
}