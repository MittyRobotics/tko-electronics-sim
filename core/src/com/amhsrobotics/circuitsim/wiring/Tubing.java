package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Tubing extends Cable {
    public Tubing(Vector2 startPoint, int count) {
        super(startPoint, count);
        gauge = 2;
        color = Color.LIGHT_GRAY;
        populateProperties();
    }

    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("Tubing - ID " + ID, CircuitGUIManager.propertiesBox.LABEL), true, 2);

    }

}
