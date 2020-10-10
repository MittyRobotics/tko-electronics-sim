package com.amhsrobotics.circuitsim.files;

import com.badlogic.gdx.Gdx;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JSONReader {

    private static JSONParser reader = new JSONParser();
    private static JSONObject currentConfig;

    public static void loadConfig(String path) {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(path);
            assert inputStream != null;
            currentConfig = (JSONObject) reader.parse(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            Gdx.app.exit();
        }
    }

    public static JSONObject getCurrentConfig() {
        return currentConfig;
    }
}
