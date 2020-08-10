package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.HashMap;

public class HardwareManager {

    public static Hardware currentHardware = null;
    public static boolean movingObject = false;

    private static DelayedRemovalArray<Hardware> hardwares = new DelayedRemovalArray<>();

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        for(Hardware h : hardwares) {
            h.update(batch, renderer, cam);
        }
    }

    public static HashMap<Hardware, Integer> wireHoveringHardware(Vector2 vec) {

        //GET IF WIRE IS CLICKED ON HARDWARE

        for(Hardware h : hardwares) {
            if(h instanceof DoubleSandCrab) {

                //DOUBLE SAND CRAB

                if(((DoubleSandCrab) h).getConnector1().getBoundingRectangle().contains(vec.x, vec.y)) {
                    return new HashMap<Hardware, Integer>() {{
                        put(h, 0);
                    }};
                } else if(((DoubleSandCrab) h).getConnector2().getBoundingRectangle().contains(vec.x, vec.y)) {
                    return new HashMap<Hardware, Integer>() {{
                        put(h, 1);
                    }};
                }
            } else if (h instanceof TripleSandCrab) {

                //TRIPLE SAND CRAB

                if(((TripleSandCrab) h).getConnector1().getBoundingRectangle().contains(vec.x, vec.y)) {
                    return new HashMap<Hardware, Integer>() {{
                        put(h, 0);
                    }};
                } else if(((TripleSandCrab) h).getConnector2().getBoundingRectangle().contains(vec.x, vec.y)) {
                    return new HashMap<Hardware, Integer>() {{
                        put(h, 1);
                    }};
                } else if(((TripleSandCrab) h).getConnector3().getBoundingRectangle().contains(vec.x, vec.y)) {
                    return new HashMap<Hardware, Integer>() {{
                        put(h, 2);
                    }};
                }
            }

        }
        return null;
    }

    public static void removeCableFromHardware(Cable cable, Hardware hardware) {
        if(hardware instanceof DoubleSandCrab) {
            ((DoubleSandCrab) hardware).clearConnection(cable);
        }
    }

    public static void addDoubleSandCrab(float startX, float startY) {
        CircuitGUIManager.propertiesBox.show();
        DoubleSandCrab temp = new DoubleSandCrab(new Vector2(startX, startY));
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }


    public static void removeSandCrab(DoubleSandCrab crab) {
        hardwares.removeValue(crab, true);
    }

    public static void removeSandCrab(TripleSandCrab crab) {
        hardwares.removeValue(crab, true);
    }

    public static void addTripleSandCrab(float x, float y) {
        CircuitGUIManager.propertiesBox.show();
        TripleSandCrab temp = new TripleSandCrab(new Vector2(x, y));
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }
}
