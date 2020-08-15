package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.devices.*;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
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

    public static DelayedRemovalArray<Hardware> hardwares = new DelayedRemovalArray<>();

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        for(Hardware h : hardwares) {
            h.update(batch, renderer, cam);
        }
    }

    public static DelayedRemovalArray<Hardware> getHardware() {
        return hardwares;
    }

    public static void moveToFront(Hardware hardware) {
        DelayedRemovalArray<Hardware> temp = new DelayedRemovalArray<>();
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

    public static Hardware addHardware(float startX, float startY, HardwareType type) {
        Hardware temp;

        switch(type) {
            case PDP:
                temp = new PowerDistributionPanel(new Vector2(startX, startY), type, true);
                break;
            case VRM:
                temp = new VoltageRegulatorModule(new Vector2(startX, startY), type, true);
                break;
            case PCM:
                temp = new PneumaticsControlModule(new Vector2(startX, startY), type, true);
                break;
            case DOUBLESANDCRAB:
                temp = new SandCrab(new Vector2(startX, startY), type, true);
                break;
            case TRIPLESANDCRAB:
                temp = new SandCrab(new Vector2(startX, startY), type, true);
                break;
            case ROBORIO:
                temp = new RoboRio(new Vector2(startX, startY), type, true);
                break;
            case TALON:
                temp = new Talon(new Vector2(startX, startY), type, true);
                break;
            case SPARK:
                temp = new Spark(new Vector2(startX, startY), type, true);
                break;
            case FALCON:
                temp = new Falcon(new Vector2(startX, startY), type, true);
                break;
            case MOTOR775:
                temp = new Motor775(new Vector2(startX, startY), type, true);
                break;
            case NEO:
                temp = new NEO(new Vector2(startX, startY), type, true);
                break;
            case BREAKER:
                temp = new Breaker(new Vector2(startX, startY), type, true);
                break;
            case BATTERY:
                temp = new Battery(new Vector2(startX, startY), type, true);
                break;
            case RADIO:
                temp = new Radio(new Vector2(startX, startY), type, true);
                break;
            default:
                temp = new SandCrab(new Vector2(startX, startY), type, true);
                break;
        }

        if(type == HardwareType.PDP) {
            Hardware breaker = new Breaker(new Vector2(startX + temp.getDim().x - 200, startY), HardwareType.BREAKER);
            hardwares.add(breaker);
            Cable c = new Cable(new Vector2(startX + temp.getDim().x / 2 + 200, startY + 250), CableManager.id);
            Cable c2 = new Cable(new Vector2(startX + temp.getDim().x / 2 + 200, startY - 225), CableManager.id+1);
            CableManager.id += 2;
            c.setGauge(4);
            c.setColor(DeviceUtil.COLORS.get("Red"));
            c2.setGauge(4);
            c2.setColor(DeviceUtil.COLORS.get("Black"));
            CableManager.addCable(c);
            CableManager.addCable(c2);
            breaker.attachWire(c, 0, true);
            breaker.attachWire(c2, 1, true);
            temp.attachWire(c, 42, false);
            temp.attachWire(c2, 43, false);

        }

        temp.populateProperties();
        CircuitGUIManager.propertiesBox.show();

        currentHardware = temp;
        CableManager.currentCable = null;
        hardwares.add(temp);

        return temp;
    }



    public static void removeHardware(Hardware ha) {
        hardwares.removeValue(ha, true);
    }
}
