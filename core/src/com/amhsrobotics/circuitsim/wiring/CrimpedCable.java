package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;


public class CrimpedCable extends Cable {

    public CrimpedCable(int gauge) {
        super(new Vector2(0, 0), CableManager.getCrimpedID());
        this.gauge = gauge;

        appendingFromBegin = false;
        appendingFromEnd = false;

        // Will be attached to hardware in hardware, not here
    }

    @Override
    public void populateProperties(String... name) {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("Crimped Cable", CircuitGUIManager.propertiesBox.LABEL), true, 2);
        CircuitGUIManager.propertiesBox.addElement(new Label("Color", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        final TextButton cb = new TextButton(DeviceUtil.getKeyByValue(DeviceUtil.COLORS, this.color), CircuitGUIManager.propertiesBox.TBUTTON);
        CircuitGUIManager.propertiesBox.addElement(cb, false, 1);

        CircuitGUIManager.propertiesBox.addElement(new Label("Conn. 1", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        if((connection1 == null ? "None" : connection1.name + " " + connection1.hardwareID2).length() > 10) {
            CircuitGUIManager.propertiesBox.addElement(new Label(connection1 == null ? "None" : connection1.name + " " + connection1.hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 2);
        } else {
            CircuitGUIManager.propertiesBox.addElement(new Label(connection1 == null ? "None" : connection1.name + " " + connection1.hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
        }

        CircuitGUIManager.propertiesBox.addElement(new Label("Conn. 2", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        if((connection2 == null ? "None" : connection2.name + " " + connection2.hardwareID2).length() > 10) {
            CircuitGUIManager.propertiesBox.addElement(new Label(connection2 == null ? "None" : connection2.name + " " + connection2.hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 2);
        } else {
            CircuitGUIManager.propertiesBox.addElement(new Label(connection2 == null ? "None" : connection2.name + " " + connection2.hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
        }

        cb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ArrayList<String> keys = new ArrayList<>(DeviceUtil.COLORS.keySet());
                for(String str : keys) {
                    if(str.contentEquals(cb.getText())) {
                        if(keys.indexOf(str) == keys.size() - 1) {
                            cb.setText(keys.get(0));
                            color = DeviceUtil.COLORS.get(keys.get(0));
                        } else {
                            cb.setText(keys.get(keys.indexOf(str) + 1));
                            color = DeviceUtil.COLORS.get(keys.get(keys.indexOf(str) + 1));
                        }
                        break;
                    }
                }
            }
        });
    }

}