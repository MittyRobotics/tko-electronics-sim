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
    private static DelayedRemovalArray<Hardware> temp;

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        for(Hardware h : hardwares) {
            h.update(batch, renderer, cam);
        }
    }

    public static DelayedRemovalArray<Hardware> getHardware() {
        return hardwares;
    }

    public static void moveToFront(Hardware hardware) {
        temp = new DelayedRemovalArray<>();
        for(int i = 0; i < hardwares.size; i++) {
            if(hardwares.get(i).getHardwareID() != hardware.getHardwareID()) {
                temp.add(hardwares.get(i));
            }
        }

        temp.add(hardware);
        hardwares = temp;

    }

    public static HashMap<Hardware, Integer> wireHoveringHardware(Vector2 vec) {

        //GET IF WIRE IS CLICKED ON HARDWARE

        for(Hardware h : hardwares) {
            if(h.getTotalConnectors() != 0) {
                for(int x = 0; x < h.getTotalConnectors(); x++) {
                    if(h.getConnector(x).getBoundingRectangle().contains(vec.x, vec.y)) {
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
        if(hardware != null) {
            hardware.clearConnection(cable);
        }
    }

    public static void addSandCrab(float startX, float startY, HardwareType type) {
        CircuitGUIManager.propertiesBox.show();
        SandCrab temp = new SandCrab(new Vector2(startX, startY), type, false);
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }

    public static void addPDP(float startX, float startY, HardwareType type) {
        CircuitGUIManager.propertiesBox.show();
        PowerDistributionPanel temp = new PowerDistributionPanel(new Vector2(startX, startY), type, false);
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }

    public static void addPCM(float startX, float startY, HardwareType type) {
        CircuitGUIManager.propertiesBox.show();
        PneumaticsControlModule temp = new PneumaticsControlModule(new Vector2(startX, startY), type, false);
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }

    public static void addVRM(float startX, float startY, HardwareType type) {
        CircuitGUIManager.propertiesBox.show();
        VoltageRegulatorModule temp = new VoltageRegulatorModule(new Vector2(startX, startY), type, false);
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }

    public static void addRoboRio(float startX, float startY, HardwareType type) {
        CircuitGUIManager.propertiesBox.show();
        RoboRio temp = new RoboRio(new Vector2(startX, startY), type, false);
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }

    public static void addTalon(float startX, float startY, HardwareType type) {
        CircuitGUIManager.propertiesBox.show();
        Talon temp = new Talon(new Vector2(startX, startY), type, false);
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }

    public static void addSpark(float startX, float startY, HardwareType type) {
        CircuitGUIManager.propertiesBox.show();
        Spark temp = new Spark(new Vector2(startX, startY), type, false);
        currentHardware = temp;
        CableManager.currentCable = null;

        hardwares.add(temp);
    }


    public static void removeHardware(Hardware ha) {
        hardwares.removeValue(ha, true);
    }
}
