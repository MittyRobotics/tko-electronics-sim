package com.amhsrobotics.circuitsim.files;

import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class FileManager {

    private static Json json = new Json();

    // testing
    public static void save() {
        DelayedRemovalArray<JsonValue> hardware = new DelayedRemovalArray<>();
        System.out.println(json.prettyPrint(HardwareManager.getHardwareAsList().get(0)));
//        for(Hardware h : HardwareManager.getHardwareAsList()) {
//            JsonValue jsonHardware = new JsonValue(JsonValue.ValueType.object);
//            jsonHardware.addChild("name", new JsonValue(h.getName()));
//            jsonHardware.addChild("positionX", new JsonValue(h.getPosition().x));
//            jsonHardware.addChild("positionY", new JsonValue(h.getPosition().y));
//
//            hardware.add(jsonHardware);
//        }
//
//        JsonValue main = new JsonValue(JsonValue.ValueType.object);
//        for(JsonValue v : hardware) {
//            main.addChild(v);
//        }
//
//        FileHandle file = Gdx.files.local("save.tko");
//        file.writeString(json.prettyPrint(main), false);
    }

    public static void load() {

    }
}
