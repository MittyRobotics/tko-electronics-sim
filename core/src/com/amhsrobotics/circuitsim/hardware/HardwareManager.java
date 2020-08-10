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
            if(h instanceof SandCrab) {

                for(int x = 0; x < ((SandCrab) h).getTotalConnectors(); x++) {
                    if(((SandCrab) h).getConnector(x).getBoundingRectangle().contains(vec.x, vec.y)) {
                        int finalX = x;
                        return new HashMap<Hardware, Integer>() {{
                            put(h, finalX);
                        }};
                    }
                }
            }
        }
        return null;
    }

    public static void removeCableFromHardware(Cable cable, Hardware hardware) {
        if(hardware instanceof SandCrab) {
            ((SandCrab) hardware).clearConnection(cable);
        }
    }

    public static void addSandCrab(float startX, float startY, String type) {
        CircuitGUIManager.propertiesBox.show();
        SandCrab temp = new SandCrab(new Vector2(startX, startY), type);
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }


    public static void removeSandCrab(SandCrab crab) {
        hardwares.removeValue(crab, true);
    }
}
