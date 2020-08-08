package com.amhsrobotics.circuitsim.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class JSONReader {

    private static JsonReader reader = new JsonReader();
    private static JsonValue currentConfig;

    public static void loadConfig(String path) {
        currentConfig = reader.parse(Gdx.files.internal(path));
    }

    public static String readString(String str) {
        return currentConfig.getString(str);
    }

    public static JsonValue getCurrentConfig() {
        return currentConfig;
    }
}
