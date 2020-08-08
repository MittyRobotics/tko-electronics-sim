package com.amhsrobotics.circuitsim.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class JSONReader {

    private static JsonReader reader = new JsonReader();

    public static void readConfig(String path) {
        JsonValue val = reader.parse(Gdx.files.internal(path));

    }
}
