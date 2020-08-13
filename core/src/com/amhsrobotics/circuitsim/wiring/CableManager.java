package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.input.Tuple;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.Iterator;

public class CableManager {

    public static Cable currentCable = null;
    public static int id = 1;
    public static boolean merging = false;

    private static DelayedRemovalArray<Cable> temp;

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

    public static void moveToFront(Cable cable) {
        temp = new DelayedRemovalArray<>();
        for(int i = 0; i < cables.size; i++) {
            if(cables.get(i).getID() != cable.getID()) {
                temp.add(cables.get(i));
            }
        }

        temp.add(cable);
        cables = temp;

    }

    public static DelayedRemovalArray<Cable> getCables() {
        return cables;
    }


    public static Tuple<Cable, Integer> wireHoveringWire(ClippedCameraController camera, Cable cable) {
        // CHECK FOR MERGING WIRES
        if (cable.getCoordinates().size() > 1) {
            for (int x = 0; x < cables.size; x++) {
                if (cables.get(x).getID() != cable.getID() && !(cable instanceof CrimpedCable) && cable.getID() != 9 && cables.get(x).getID() != 9) {
                    int ans = cables.get(x).hoveringOnEndpoint(camera);
                    if (ans == 1) {
                        merging = true;
                        return new Tuple<>(cables.get(x), 1);
                    } else if (ans == 2) {
                        merging = true;
                        return new Tuple<>(cables.get(x), 2);
                    }
                }
            }
        }
        return null;
    }

    public static void mergeCables(Cable cable1, Cable cable2, boolean cable1begin, boolean cable2begin) {
        //MERGE TWO CABLES
        cable1.mergeCable(cable2, cable2begin, cable1begin);
        deleteCable(cable2);
        merging = false;
        currentCable = null;
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
        boolean good = true;
        Iterator<Cable> iterator = cables.iterator();
        while(iterator.hasNext()) {
            if (iterator.next().pointIsOnEndpoint(startX, startY) != 0) {
                good = false;
                break;
            }
        }
        if(good) {
            CircuitGUIManager.propertiesBox.show();
            Cable temp = new Cable(new Vector2(startX, startY), id);
            id++;
            currentCable = temp;

            temp.setAppendingFromEnd(true);
            cables.add(temp);
        }
    }

    public static void addEthernet(float startX, float startY) {
        boolean good = true;
        Iterator<Cable> iterator = cables.iterator();
        while(iterator.hasNext()) {
            if (iterator.next().pointIsOnEndpoint(startX, startY) != 0) {
                good = false;
                break;
            }
        }
        if(good) {
            CircuitGUIManager.propertiesBox.show();
            EthernetCable temp = new EthernetCable(new Vector2(startX, startY), id);
            id++;
            currentCable = temp;

            temp.setAppendingFromEnd(true);
            cables.add(temp);
        }
    }

    public static void deleteCable(Cable cable) {
        cables.removeValue(cable, true);
//        cables.remove(cable);
    }
}