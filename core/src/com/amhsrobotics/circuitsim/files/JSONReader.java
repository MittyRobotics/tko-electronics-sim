package com.amhsrobotics.circuitsim.files;

import com.badlogic.gdx.Gdx;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class JSONReader {

    private static JSONParser reader = new JSONParser();
    private static JSONObject currentConfig;

    public static void loadConfig(String path) {
        try {
            currentConfig = (JSONObject) reader.parse(new FileReader(Gdx.files.internal(path).file()));
        } catch (Exception e) {
            Gdx.app.exit();
        }
    }

    public static Object readString(String str) {
        return currentConfig.get(str);
    }

    public static Object getCurrentConfig() {
        return currentConfig;
    }
}