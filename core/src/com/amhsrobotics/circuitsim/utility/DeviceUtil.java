package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.hardware.HardwareType;
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

    public static final LinkedHashMap<String, Color> END_COLORS = new LinkedHashMap<String, Color>() {{
        put("Plastic", new Color(200f / 255f, 200f / 255f, 200f / 255f, 1f));
        put("SelectedPlastic", new Color(150f / 255f, 150f / 255f, 150f / 255f, 1f));
        put("DarkPlastic", new Color(20f / 255f, 20f / 255f, 20f / 255f, 1f));
        put("SelectedDarkPlastic", new Color(50f / 255f, 50f / 255f, 50f / 255f, 1f));
    }};

    public static final LinkedHashMap<String, Color> COLORS_EPLATE = new LinkedHashMap<String, Color>() {{
        //WIRE COLORS
        put("Plastic", new Color(255f/255f, 255f/255f, 255f/255f, 0.5f));
        put("Lime", new Color(193/255f, 211/255f, 200/255f, 0.5f));
        put("Amber", new Color(240/255f, 214/255f, 166/255f, 0.7f));
        put("Sky", new Color(166/255f, 205/255f, 240/255f, 0.5f));
    }};

    public static final HashMap<Float, Float> GAUGETOLIMIT = new HashMap<Float, Float>() {{
        put(22f, 5f);
        put(18f, 8f);
        put(13f, 15f);
        put(12f, 19f);
        put(4f, 30f);
        put(2f, 30f);
    }};

    public static final HashMap<Float, Float> GAUGETOLIMIT2 = new HashMap<Float, Float>() {{
        put(22f, 7f);
        put(18f, 12f);
        put(13f, 18f);
        put(12f, 22f);
        put(4f, 38f);
        put(2f, 30f);
    }};

    public static final HashMap<Float, Float> GAUGETOLIMIT3 = new HashMap<Float, Float>() {{
        put(22f, 6f);
        put(18f, 8f);
        put(13f, 10f);
        put(12f, 13f);
        put(4f, 25f);
        put(2f, 17f);
    }};

    //WIRE GAUGES
    public static final int[] GAUGES = {22, 18, 12, 4};

    public static int counter = 0;

    //public static int curID = 0;
    public static final HashMap<HardwareType, Integer> curID = new HashMap<HardwareType, Integer>() {{
        for(HardwareType h : HardwareType.values()) {
            put(h, 0);
        }
    }};


    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static int getNewHardwareID(HardwareType type) {
        curID.put(type, curID.get(type)+1);
        return curID.get(type);
    }

    public static int getNewHardwareID() {
        counter++;
        return counter;
    }

    public static final LinkedHashMap<String, Color> BACKGROUND_COLORS = new LinkedHashMap<String, Color>() {{
        //WIRE COLORS
        put("Nord", new Color(46 / 255f, 46 / 255f, 64 / 255f,1));
        put("Midnight", new Color(5/255f, 5/255f, 10/255f, 1));
        put("Light", new Color(230/255f, 230/255f, 235/255f, 1));
        put("Dark", new Color(30/255f, 30/255f, 35/255f, 1));
    }};

    public static final LinkedHashMap<String, Color> SNAPGRID_COLORS = new LinkedHashMap<String, Color>() {{
        //WIRE COLORS
        put("Nord", new Color(0 / 255f, 0 / 255f, 30 / 255f,1));
        put("Midnight", new Color(35/255f, 35/255f, 40/255f, 1));
        put("Light", new Color(185/255f, 185/255f, 195/255f, 1));
        put("Dark", new Color(60/255f, 60/255f, 65/255f, 1));
    }};
}
