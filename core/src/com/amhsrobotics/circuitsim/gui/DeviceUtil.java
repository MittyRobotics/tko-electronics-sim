package com.amhsrobotics.circuitsim.gui;

import com.badlogic.gdx.graphics.Color;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class DeviceUtil {

    public static final LinkedHashMap<String, Color> COLORS = new LinkedHashMap<String, Color>() {{
        put("Green", new Color(158/255f, 205/255f, 158/255f, 1));
        put("Blue", new Color(142/255f, 190/255f, 209/255f, 1));
        put("Red", new Color(209/255f, 142/255f, 166/255f, 1));
        put("Yellow", new Color(231/255f, 224/255f, 110/255f, 1));
        put("Orange", new Color(231/255f, 180/255f, 110/255f, 1));
        put("Purple", new Color(212/255f, 140/255f, 225/255f, 1));
    }};

    public static final int[] GAUGES = IntStream.iterate(10, n -> n + 2).limit(11).toArray();

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
