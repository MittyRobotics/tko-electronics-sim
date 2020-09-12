package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import me.rohanbansal.ricochet.camera.CameraController;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;

public class EthernetCable extends Cable {

    public EthernetCable() {}

    public EthernetCable(Vector2 startPoint, int count) {
        super(startPoint, count);
        gauge = 13;
        color = DeviceUtil.COLORS.get("Orange");
        hoverColor = Color.GRAY;
        populateProperties();
    }

    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("Ethernet - ID " + ID, CircuitGUIManager.propertiesBox.LABEL), true, 2);
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

    @Override
    public void render(ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        if(CableManager.currentCable == this) {

            Vector2 vec2 = Tools.mouseScreenToWorld(camera);

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                SnapGrid.calculateSnap(vec2);
            }

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            if (appendingFromEnd && !disableEnd) {
                // draw potential cable wire
                renderer.setColor(color);
                renderer.rectLine(coordinates.get(coordinates.size() - 1), new Vector2(vec2.x, vec2.y), limit);
                renderer.setColor(Color.WHITE);
                renderer.circle(vec2.x, vec2.y, limit + 5f);
            } else if (appendingFromBegin && !disableBegin) {
                // draw potential cable wire
                renderer.setColor(color);
                renderer.rectLine(coordinates.get(0), new Vector2(vec2.x, vec2.y), limit);
                renderer.setColor(Color.WHITE);
                renderer.circle(vec2.x, vec2.y, limit + 5f);
            }

            renderer.end();

        }

        super.render(renderer, camera);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        drawEndpoints(renderer);
        renderer.end();
    }

    @Override
    public void update(ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(renderer, camera);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        drawEndpoints(renderer);
        renderer.end();
    }


    @Override
    public void drawEndpoints(ShapeRenderer renderer) {
        renderer.setColor(DeviceUtil.COLORS.get("White"));
        if(!appendingFromBegin) {
            renderer.circle(coordinates.get(0).x, coordinates.get(0).y, limit + 5f);
        }
        if(!appendingFromEnd) {
            renderer.circle(coordinates.get(coordinates.size() - 1).x, coordinates.get(coordinates.size() - 1).y, limit + 5f);
        }

    }

    public int hoveringOnEndpoint(CameraController cameraController) {
        // CHECK IF HOVERING ON ENDPOINT
        Vector2 vec = Tools.mouseScreenToWorld(cameraController);


        Vector2 c2 = coordinates.get(coordinates.size() - 1);
        Vector2 c = coordinates.get(0);

        if(new Circle(c2.x, c2.y, limit + 5f).contains(vec.x, vec.y)) {
            return 2;
        } else if(new Circle(c.x, c.y, limit + 5f).contains(vec.x, vec.y)) {
            return 1;
        }
        return 0;
    }

    @Override
    protected void drawNodes(ShapeRenderer renderer, ClippedCameraController cam, Color... color) {
        if(color.length > 0) {
            renderer.setColor(color[0]);
        }
        for(Vector2 coords : coordinates) {
            renderer.circle(coords.x, coords.y, limit3);
        }
        processNodes(renderer, cam);
    }
}
