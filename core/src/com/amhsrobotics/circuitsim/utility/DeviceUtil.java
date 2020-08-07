package com.amhsrobotics.circuitsim.utility;

import com.badlogic.gdx.graphics.Color;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class DeviceUtil {

    public static final LinkedHashMap<String, Color> COLORS = new LinkedHashMap<String, Color>() {{
        put("Green", new Color(158/255f, 205/255f, 158/255f, 1));
        put("Red", new Color(232/255f, 101/255f, 88/255f, 1));
        put("Yellow", new Color(231/255f, 224/255f, 110/255f, 1));
        put("Orange", new Color(231/255f, 180/255f, 110/255f, 1));
        put("Purple", new Color(212/255f, 140/255f, 225/255f, 1));
        put("White", new Color(243/255f, 237/255f, 236/255f, 1));
        put("Black", new Color(15/255f, 14/255f, 14/255f, 1));
    }};

    public static final int[] GAUGES = IntStream.iterate(10, n -> n + 2).limit(11).toArray();

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
