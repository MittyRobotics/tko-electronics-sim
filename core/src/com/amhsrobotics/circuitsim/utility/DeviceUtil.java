package com.amhsrobotics.circuitsim.utility;

import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class DeviceUtil {

    public static final LinkedHashMap<String, Color> COLORS = new LinkedHashMap<String, Color>() {{
        //WIRE COLORS
        put("Green", new Color(0/255f, 150/255f, 0/255f, 1));
        put("Yellow", new Color(255/255f, 255/255f, 0/255f, 1));
        put("Red", new Color(200/255f, 0/255f, 0/255f, 1));
        put("Black", new Color(15/255f, 14/255f, 14/255f, 1));
        put("White", new Color(243/255f, 237/255f, 236/255f, 1));
        put("Blue", new Color(61/255f, 58/255f, 234/255f, 1));
        put("Orange", new Color(255/255f, 130/255f, 0/255f, 1));
    }};

    public static final HashMap<String, String> GAUGETODEVICE = new HashMap<String, String>() {{
        put("22", "CAN / Sensor");
        put("12", "Motor");
        put("4", "Power");
        put("18", "VRM / PCM");
        put("9", "Ethernet");
    }};

    //WIRE GAUGES
    public static final int[] GAUGES = {22, 18, 12, 4};

    private static int curID = 0;

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static int getNewHardwareID() {
        curID++;
        return curID;
    }
}
