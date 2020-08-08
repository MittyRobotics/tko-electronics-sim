package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.Tuple;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.Iterator;

public class CableManager {

    public static Cable currentCable = null;
    private static int id = 1;
    public static boolean merging = false;

    private static DelayedRemovalArray<Cable> cables = new DelayedRemovalArray<>();

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        Iterator<Cable> iterator = cables.iterator();
        while(iterator.hasNext()) {
            iterator.next().update(renderer, cam);
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
        for (int x = 0; x < cables.size; x++) {
            if (cables.get(x).getID() != cable.getID()) {
                int ans = cables.get(x).hoveringOnEndpoint(camera);
                Gdx.app.log(""+cables.get(x).getID(), ""+ans);
                if (ans == 1) {
                    merging = true;
                    return new Tuple<>(cables.get(x), 1);
                } else if (ans == 2) {
                    merging = true;
                    return new Tuple<>(cables.get(x), 2);
                }
            }
        }
        return null;
    }

    public static void mergeCables(Cable cable1, Cable cable2, boolean cable2begin, boolean cable1begin) {
        cable1.mergeCable(cable2, cable1begin, cable2begin);
        deleteCable(cable2);
        merging = false;
    }

    public static Cable getCableByID(int ID) {
        for(int x = 0; x < cables.size; x++) {
            if(cables.get(x).getID() == ID) {
                return cables.get(x);
            }
        }
        return null;
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