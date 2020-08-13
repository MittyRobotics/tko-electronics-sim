package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EthernetCable extends Cable {
    public EthernetCable(Vector2 startPoint, int count) {
        super(startPoint, count);
        gauge = 9;
        color = DeviceUtil.COLORS.get("White");
    }

    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("Ethernet - ID " + ID, CircuitGUIManager.propertiesBox.LABEL), true, 2);
        CircuitGUIManager.propertiesBox.addElement(new Label("Color", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        final TextButton cb = new TextButton(DeviceUtil.getKeyByValue(DeviceUtil.COLORS, this.color), CircuitGUIManager.propertiesBox.TBUTTON);
        CircuitGUIManager.propertiesBox.addElement(cb, false, 1);

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

    @Override
    public void drawEndpoints(ShapeRenderer renderer) {
        renderer.rect(coordinates.get(0).x - 10, coordinates.get(0).y - 10, 20, 20);
        renderer.rect(coordinates.get(coordinates.size() - 1).x - 10, coordinates.get(coordinates.size() - 1).y - 10, 20, 20);
    }

    @Override
    protected void drawNodes(ShapeRenderer renderer, ClippedCameraController cam, Color... color) {
        if(color.length > 0) {
            renderer.setColor(color[0]);
        }
        for(Vector2 coords : coordinates) {
            renderer.rect(coords.x - 10, coords.y - 10, 20, 20);
        }
        processNodes(renderer, cam);
    }
}
