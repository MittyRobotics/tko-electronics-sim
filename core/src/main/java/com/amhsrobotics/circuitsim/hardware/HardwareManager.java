package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.files.HardwareModel;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.devices.*;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.input.Tuple;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HardwareManager {

    public static Hardware currentHardware = null;
    public static boolean movingObject = false;

    public static Tuple<Hardware, Integer> attachWireOnDoubleClick = null;

    public static DelayedRemovalArray<Hardware> hardwares = new DelayedRemovalArray<>();

    public static Hardware toBeMovedForward;

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        if(toBeMovedForward != null) {
            int i = hardwares.indexOf(toBeMovedForward, true);
            if(i != hardwares.size-1) {
                Hardware temp = hardwares.get(i+1);
                hardwares.set(i+1, toBeMovedForward);
                hardwares.set(i, temp);
            }
            toBeMovedForward = null;
        }


        for(Hardware h : hardwares) {
            if(!(h instanceof EPlate)) {
                h.update(batch, renderer, cam);
            }
        }
    }

    public static void updateEplates(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        for(Hardware h : hardwares) {
            if(h instanceof EPlate) {
                h.update(batch, renderer, cam);
            }
        }
    }

    public static ArrayList<Hardware> getSelectedHardware(Vector2 v1, Vector2 v2) {
        ArrayList<Hardware> ans = new ArrayList<>();

        Vector2 vec1 = new Vector2(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y));
        Vector2 vec2 = new Vector2(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y));

        for(Hardware h : hardwares) {
            if(!(h instanceof EPlate) && h.intersect(vec1, vec2)) {
                ans.add(h);
            }
        }

        return ans;
    }

    public static DelayedRemovalArray<Hardware> getHardware() {
        return hardwares;
    }


    public static Hardware getCurrentlyHovering(ClippedCameraController camera) {
        for(int i = hardwares.size-1; i >= 0; i--) {
            if((!(hardwares.get(i) instanceof EPlate)) && hardwares.get(i).getHoveringMouse(camera)) {
                return hardwares.get(i);
            }
        }
        return null;

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

    public static void moveToBack(Hardware hardware) {
        DelayedRemovalArray<Hardware> temp = new DelayedRemovalArray<>();
        temp.add(hardware);
        for(int i = 0; i < hardwares.size; i++) {
            if(hardwares.get(i).getHardwareID() != hardware.getHardwareID()) {
                temp.add(hardwares.get(i));
            }
        }

        hardwares = temp;
    }

    public static void moveBack(Hardware hardware) {
        int i = hardwares.indexOf(hardware, true);
        if(i != 0) {
            Hardware temp = hardwares.get(i-1);
            hardwares.set(i-1, hardware);
            hardwares.set(i, temp);
        }
    }

    public static void moveForward(Hardware hardware) {
        toBeMovedForward = hardware;
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

    public static Hardware switchCaseHardware(HardwareType type, float startX, float startY, boolean addCrimped) {
        Hardware temp;
        switch(type) {
            case PDP:
                temp = new PowerDistributionPanel(new Vector2(startX, startY), addCrimped);
                break;
            case VRM:
                temp = new VoltageRegulatorModule(new Vector2(startX, startY), addCrimped);
                break;
            case PCM:
                temp = new PneumaticsControlModule(new Vector2(startX, startY), addCrimped);
                break;
            case DOUBLESANDCRAB:
                temp = new SandCrab(new Vector2(startX, startY), HardwareType.DOUBLESANDCRAB, addCrimped);
                break;
            case TRIPLESANDCRAB:
                temp = new SandCrab(new Vector2(startX, startY), HardwareType.TRIPLESANDCRAB, addCrimped);
                break;
            case ROBORIO:
                temp = new RoboRio(new Vector2(startX, startY), addCrimped);
                break;
            case TALON:
                temp = new Talon(new Vector2(startX, startY), addCrimped);
                break;
            case SPARK:
                temp = new Spark(new Vector2(startX, startY), addCrimped);
                break;
            case FALCON:
                temp = new Falcon(new Vector2(startX, startY), addCrimped);
                break;
            case MOTOR775:
                temp = new Motor775(new Vector2(startX, startY), addCrimped);
                break;
            case NEO:
                temp = new NEO(new Vector2(startX, startY), addCrimped);
                break;
            case BREAKER:
                temp = new Breaker(new Vector2(startX, startY), addCrimped);
                break;
            case BATTERY:
                temp = new Battery(new Vector2(startX, startY), addCrimped);
                break;
            case RADIO:
                temp = new Radio(new Vector2(startX, startY), addCrimped);
                break;
            case DOUBLESOLENOID:
                temp = new Solenoid(new Vector2(startX, startY), HardwareType.DOUBLESOLENOID, addCrimped);
                break;
            case SINGLESOLENOID:
                temp = new Solenoid(new Vector2(startX, startY), HardwareType.SINGLESOLENOID, addCrimped);
                break;
            case PRESSURESWITCH:
                temp = new PressureSwitch(new Vector2(startX, startY), addCrimped);
                break;
            case PISTON:
                temp = new Piston(new Vector2(startX, startY), addCrimped);
                break;
            case MANIFOLD:
                temp = new Manifold(new Vector2(startX, startY), addCrimped);
                break;
            case TANK:
                temp = new Tank(new Vector2(startX, startY), addCrimped);
                break;
            case COMPRESSOR:
                temp = new Compressor(new Vector2(startX, startY));
                break;
            case EPLATE:
                temp = new EPlate(new Vector2(startX, startY));
                break;
            case GAUGE:
                temp = new Gauge(new Vector2(startX, startY));
                break;
            case RELIEFVALVE:
                temp = new ReliefValve(new Vector2(startX, startY));
                break;
            case TCONNECTOR:
                temp = new TConnector(new Vector2(startX, startY));
                break;
            case REGULATOR:
                temp = new Regulator(new Vector2(startX, startY));
                break;
            default:
                temp = new SandCrab(new Vector2(startX, startY), type, addCrimped);
                break;
        }
        return temp;
    }

    public static Hardware addHardware(float startX, float startY, HardwareType type) {
        Hardware temp = switchCaseHardware(type, startX, startY, true);

        if (type == HardwareType.EPLATE) {
            DeviceUtil.curID.put(HardwareType.EPLATE, DeviceUtil.curID.get(HardwareType.EPLATE) - 1);
        }

        temp.populateProperties();
        CircuitGUIManager.propertiesBox.show();

        currentHardware = temp;
        if (CableManager.currentCable != null) {
            CableManager.currentCable.appendingFromEnd = false;
            CableManager.currentCable.appendingFromBegin = false;
            CableManager.currentCable = null;
        }
        hardwares.add(temp);

        return temp;
    }

    public static void loadHardware(HardwareModel model) {
        Hardware temp = switchCaseHardware(model.type, model.position.x, model.position.y, true);
        temp.setHardwareID(model.id);

        if(temp instanceof EPlate) {
            ((EPlate) temp).setBox(model.box);
        }
        if(temp instanceof Flippable) {
            for(int x = 0; x < model.rotation / 90; x++) {
                ((Flippable) temp).rotateThis();
            }
        }

        temp.populateProperties();
//        CircuitGUIManager.propertiesBox.show();
        hardwares.add(temp);
    }

    public static Hardware getHardwareByID(int id) {
        for(Hardware h : hardwares) {
            if(h.getHardwareID() == id) {
                return h;
            }
        }
        return null;
    }

    public static void removeHardware(Hardware ha) {
        hardwares.removeValue(ha, true);
    }

    public static void clearHardware() {
        currentHardware = null;
        movingObject = false;
        attachWireOnDoubleClick = null;
        hardwares.clear();
        toBeMovedForward = null;
    }
}
