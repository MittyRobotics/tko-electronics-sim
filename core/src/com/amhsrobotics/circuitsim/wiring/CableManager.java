package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.files.CableModel;
import com.amhsrobotics.circuitsim.files.HardwareModel;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.input.Tuple;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.Iterator;

public class CableManager {

    public static Cable currentCable = null;
    public static int id = 1;
    public static int cId = 0;

    private static DelayedRemovalArray<Cable> temp;

    private static DelayedRemovalArray<Cable> cables = new DelayedRemovalArray<>();

    public static Cable toBeMovedForward;

    public static boolean movingCable = false;

    public static void update(ModifiedShapeRenderer renderer, ClippedCameraController cam) {
        if(toBeMovedForward != null) {
            int i = cables.indexOf(toBeMovedForward, true);
            if(i != cables.size-1) {
                Cable temp = cables.get(i+1);
                cables.set(i+1, toBeMovedForward);
                cables.set(i, temp);
            }
            toBeMovedForward = null;
        }

        Iterator<Cable> iterator = cables.iterator();
        while(iterator.hasNext()) {
            iterator.next().update(renderer, cam);
        }
    }

    public static int getCrimpedID() {
        cId--;
        return cId;
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

    public static void moveToBack(Cable cable) {
        temp = new DelayedRemovalArray<>();
        temp.add(cable);
        for(int i = 0; i < cables.size; i++) {
            if(cables.get(i).getID() != cable.getID()) {
                temp.add(cables.get(i));
            }
        }

        cables = temp;

    }

    public static void moveBack(Cable cable) {
        int i = cables.indexOf(cable, true);
        if(i != 0) {
            Cable temp = cables.get(i-1);
            cables.set(i-1, cable);
            cables.set(i, temp);
        }
    }

    public static void moveForward(Cable cable) {
        toBeMovedForward = cable;
    }

    public static DelayedRemovalArray<Cable> getCables() {
        return cables;
    }


    public static Tuple<Cable, Integer> wireHoveringWire(ClippedCameraController camera, Cable cable) {
        // CHECK FOR MERGING WIRES
        if (cable.getCoordinates().size() > 1) {
            for (int x = 0; x < cables.size; x++) {
                if (cables.get(x).getID() != cable.getID()) {
                    int ans = cables.get(x).hoveringOnEndpoint(camera);
                    if (ans == 1) {
                        return new Tuple<>(cables.get(x), 1);
                    } else if (ans == 2) {
                        return new Tuple<>(cables.get(x), 2);
                    }
                }
            }
        }

        return null;
    }

    public static void mergeCables(Cable cable1, Cable cable2, boolean cable1begin, boolean cable2begin) {
        if(cable1.getConnection(!cable1begin) == null || cable2.getConnection(!cable2begin) == null || cable1.getConnection(!cable1begin).hardwareID != cable2.getConnection(!cable2begin).hardwareID) {
            if(cable1.getConnection(cable1begin) == null && cable2.getConnection(cable2begin) == null) {
                if(cable1.gauge == cable2.gauge) {
                    //MERGE TWO CABLES
                    if (cable2 instanceof CrimpedCable) {
                        if (cable1 instanceof CrimpedCable) {
                            cable2.color2n = cable2.coordinates.size() - 1;
                            cable2.mergeCable(cable1, cable1begin, cable2begin);
                            cable2.color2 = cable1.color;
                            deleteCable(cable1);
                        } else {
                            CircuitGUIManager.popup.activateError("A crimped cable cannot be connected to a regular cable");
                        }
                    } else {
                        if (!(cable1 instanceof CrimpedCable)) {
                            cable1.mergeCable(cable2, cable2begin, cable1begin);
                            deleteCable(cable2);
                        } else {
                            CircuitGUIManager.popup.activateError("A crimped cable cannot be connected to a regular cable");
                        }
                    }
                } else {
                    CircuitGUIManager.popup.activateError("Only cables with the same gauge may be merged");
                }
            } else {
                CircuitGUIManager.popup.activateError("Connection is already taken");
            }
        } else {
            CircuitGUIManager.popup.activateError("A device cannot be connected to itself");
        }
        if(currentCable != null) {
            currentCable.appendingFromEnd = false;
            currentCable.appendingFromBegin = false;
            currentCable = null;
        }
    }

    public static Cable getCableByID(int ID) {
        for(int x = 0; x < cables.size; x++) {
            if(cables.get(x).getID() == ID) {
                return cables.get(x);
            }
        }
        return null;
    }

    private static void addCableLib(int type, float startX, float startY) {
        boolean good = true;
        Iterator<Cable> iterator = cables.iterator();
        while(iterator.hasNext()) {
            if (iterator.next().pointIsOnEndpoint(startX, startY) != 0) {
                good = false;
                break;
            }
        }
        if(good) {
            Cable temp = null;
            switch(type) {
                case 0:
                    temp = new Cable(new Vector2(startX, startY), id);
                    break;
                case 1:
                    temp = new EthernetCable(new Vector2(startX, startY), id);
                    break;
                case 2:
                    temp = new Tubing(new Vector2(startX, startY), id);
                    break;
                case 3:
                    temp = new CurvedCable(new Vector2(startX, startY), id);
                    break;
            }
            id++;

            currentCable = temp;

            temp.setAppendingFromEnd(true);
            cables.add(temp);
        }
    }

    public static void addCable(float startX, float startY) {
        addCableLib(0, startX, startY);
    }

    public static void addEthernet(float startX, float startY) {
        addCableLib(1, startX, startY);

    }

    public static void addTubing(float startX, float startY) {
        addCableLib(2, startX, startY);

    }

    public static void addCurvedCable(float x, float y) {
        addCableLib(3, x, y);
    }

    public static void deleteCable(Cable cable) {
        cables.removeValue(cable, true);
    }

    public static void clearCables() {
        cables.clear();
        currentCable = null;
        id = 1;
        cId = 0;
        toBeMovedForward = null;
         movingCable = false;
    }

    private static Cable loadHardwareCableType(String type, Vector2 coord, int id) {
        if(type.equals("ethernet")) {
            return new EthernetCable(coord, id);
        } else if(type.equals("tubing")) {
            return new Tubing(coord, id);
        } else if(type.equals("regular")) {
            return new Cable(coord, id);
        }
        return null;
    }

    public static void loadCable(CableModel cm) {
        if(cm != null) {
            if(!cm.cableType.equals("crimped")) {
                Cable c = loadHardwareCableType(cm.cableType, new Vector2(0, 0), cm.id);
                assert c != null;
                addCable(c);
                HardwareManager.getHardwareByID(cm.hardware1ID).attachWire(c, cm.port1, false);
                HardwareManager.getHardwareByID(cm.hardware2ID).attachWire(c, cm.port2, true);
                c.coordinates = cm.coordinates;
                c.gauge = cm.gauge;
                c.color = new Color(cm.r, cm.g, cm.b, cm.a);
                HardwareManager.getHardwareByID(cm.hardware1ID).reattachWire(c, cm.port1, false);
                HardwareManager.getHardwareByID(cm.hardware2ID).reattachWire(c, cm.port2, true);
            }
        }

    }
}