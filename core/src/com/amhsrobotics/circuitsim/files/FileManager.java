package com.amhsrobotics.circuitsim.files;

import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.hardware.devices.PowerDistributionPanel;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

public class FileManager {

    private static Json json = new Json();

    // testing
    public static void save() {
        System.out.println(json.prettyPrint(new PowerDistributionPanel(new Vector2(0, 0), HardwareType.PDP)));
    }

    public static void load() {

    }
}
