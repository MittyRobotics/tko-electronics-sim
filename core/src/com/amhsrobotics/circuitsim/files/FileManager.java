package com.amhsrobotics.circuitsim.files;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {

    private static Json json = new Json();
    public static AppData appdata;
    public static String fileName = "";

    // testing
    public static void save(String filename) {
        fileName = filename;
        appdata = new AppData();
        ArrayList<Hardware> hardware = new ArrayList<>(HardwareManager.getHardwareAsList());
        DelayedRemovalArray<Cable> cables = CableManager.getCables();

        for(Hardware h : hardware) {
            HardwareModel hm = new HardwareModel();
            hm.load(h);

            appdata.addHardware(hm);
        }

        for(Cable c : cables) {
            CableModel cm = new CableModel();
            if((c.connection1port != -1 && c.connection2port != -1) && !(c instanceof CrimpedCable)) {
                cm.load(c);
                appdata.addCable(cm);
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
//        fileName = filename;

        FileHandle file = Gdx.files.absolute(filename);
        appdata = json.fromJson(AppData.class, file.readString());
        Constants.WORLD_DIM = appdata.getGridSize();
        Constants.GRID_SIZE = Math.round(appdata.getGridSpacing());

        for(HardwareModel hm : appdata.getHardware()) {
            HardwareManager.loadHardware(hm);
        }

        for(CableModel cm : appdata.getCables()) {
            CableManager.loadCable(cm);
        }

        Gdx.graphics.setTitle("TKO 1351 Circuit Simulator - " + filename);

    }
}
