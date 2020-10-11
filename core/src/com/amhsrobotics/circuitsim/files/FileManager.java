package com.amhsrobotics.circuitsim.files;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

public class FileManager {

    private static Json json = new Json();
    public static AppData appdata;
    public static String fileName = "";

    // testing
    public static void save(String filename) {
        fileName = filename;
        appdata = new AppData();
        ArrayList<Hardware> hardware = new ArrayList<>(HardwareManager.getHardwareAsList());
        ArrayList<Cable> cables = new ArrayList<>();
//        for(Cable c : CableManager.getCables()) {
//            cables.add(c);
//        }
//        appdata.setHardware(hardware);
//        appdata.setCables(cables);

        for(Hardware h : hardware) {
            HardwareModel hm = new HardwareModel();
            hm.type = h.type;
            hm.position = h.getPosition();
            for(Cable c : h.connections) {
                CableModel cm = new CableModel();
                cm.coordinates = c.coordinates;
                cm.appendingFromBegin = c.appendingFromBegin;
                cm.appendingFromEnd = c.appendingFromEnd;
                cm.disableBegin = c.disableBegin;
                cm.disableEnd = c.disableEnd;
                cm.gauge = c.gauge;
            }
        }

        appdata.setGridSize(Constants.WORLD_DIM);
        appdata.setGridSpacing(Constants.GRID_SIZE);

        if(!filename.contains(".tko")) {
            filename += ".tko";
        }
        FileHandle file = Gdx.files.absolute(filename);
        file.writeString(json.prettyPrint(appdata), false);
        Gdx.graphics.setTitle("TKO 1351 Circuit Simulator - " + filename);
    }

    public static void load(String filename) {
        fileName = filename;

        FileHandle file = Gdx.files.absolute(filename);
        appdata = json.fromJson(AppData.class, file.readString());
        Constants.WORLD_DIM = appdata.getGridSize();
        Constants.GRID_SIZE = Math.round(appdata.getGridSpacing());

        Gdx.graphics.setTitle("TKO 1351 Circuit Simulator - " + filename);

    }
}
