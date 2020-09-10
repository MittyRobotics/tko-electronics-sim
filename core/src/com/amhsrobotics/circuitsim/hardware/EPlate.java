package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.Box;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.scene.SnapGrid;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;

public class EPlate extends Hardware {

    private Box box;
    private TextField.TextFieldStyle textFieldStyle;

    public ArrayList<Hardware> hardwareOnPlate = new ArrayList<>();
    private Color color;
    private ResizeNode[] nodes = new ResizeNode[9];

    private boolean frozen = false;

    private int dragging = -1;

    public EPlate(Vector2 pos) {
        super(pos, HardwareType.EPLATE);

        color = new Color(193/255f, 211/255f, 200/255f, 0.5f);

        textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = Constants.SKIN.getDrawable("textbox_02");
        textFieldStyle.cursor = Constants.SKIN.getDrawable("textbox_cursor_02");
        textFieldStyle.font = Constants.FONT_SMALL;
        textFieldStyle.fontColor = Color.BLACK;

        box = new Box(getPosition().x, getPosition().y, 300, 300);
        initNodes();
    }

    private void initNodes() {
        for(int x = 0; x < 9; x++) {
            nodes[x] = new ResizeNode(box.getResizePointAtIndex(x).x, box.getResizePointAtIndex(x).y, ResizeNode.nodeMap.get(x));
        }
    }

    public void updatePosition(ClippedCameraController camera, ModifiedShapeRenderer renderer, SpriteBatch batch) {
        box.x = Tools.mouseScreenToWorld(camera).x;
        box.y = Tools.mouseScreenToWorld(camera).y;

        renderer.setColor(color);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(box.x, box.y, box.width, box.height, 5);
        renderer.end();
    }

    @Override
    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(batch, renderer, camera);

        renderer.setColor(color);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(box.x, box.y, box.width, box.height, 5);
        renderer.end();

        Vector2 vec = Tools.mouseScreenToWorld(camera);

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            SnapGrid.calculateSnap(vec);
        }

        if(box.contains(vec.x, vec.y)) {
            drawHover(renderer);

            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                HardwareManager.currentHardware = this;
                CableManager.currentCable = null;
                populateProperties();
                CircuitGUIManager.propertiesBox.show();
            }
        }

        for(int i = 0; i < HardwareManager.hardwares.size; ++i) {
            if(!(HardwareManager.hardwares.get(i) instanceof EPlate)) {
                if (hardwareOnPlate.contains(HardwareManager.hardwares.get(i))) {
                    if (!box.contains(HardwareManager.hardwares.get(i).getPosition().x, HardwareManager.hardwares.get(i).getPosition().y)) {
                        hardwareOnPlate.remove(HardwareManager.hardwares.get(i));
                        HardwareManager.hardwares.get(i).attached = null;
                    }
                } else {
                    if (!frozen && box.contains(HardwareManager.hardwares.get(i).getPosition().x, HardwareManager.hardwares.get(i).getPosition().y)) {
                        hardwareOnPlate.add(HardwareManager.hardwares.get(i));
                        HardwareManager.hardwares.get(i).attached = this;

                    }
                }
            }
        }

        if (HardwareManager.currentHardware == this) {
            drawHover(renderer);
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            for(ResizeNode c : nodes) {
                c.draw(renderer, camera);
            }
            renderer.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                HardwareManager.currentHardware = null;
                CircuitGUIManager.propertiesBox.hide();
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.DEL) || Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
                for(Hardware h : hardwareOnPlate) {
                    h.attached = null;
                }
                HardwareManager.removeHardware(this);
                HardwareManager.currentHardware = null;
                CircuitGUIManager.propertiesBox.hide();
            }

            if(dragging != -1) {
                setSelectedNode(dragging);
                nodes[dragging].movePosition(camera, box, hardwareOnPlate);
            }


            if(!canMove) {
                for (int x = 0; x < nodes.length; x++) {
                    if (Gdx.input.isTouched()) {
                        if (nodes[x].contains(vec)) {
                            dragging = x;
                        }

                    } else {
                        dragging = -1;
                    }

                    if (!nodes[x].isSelected()) {
                        nodes[x].updateIdlePos(box);
                    }
                }
            }

            if(Gdx.input.isTouched() && dragging == -1) {

                if (box.contains(vec.x, vec.y) && HardwareManager.getCurrentlyHovering(camera) == null) {
                    HardwareManager.currentHardware = this;
                    CableManager.currentCable = null;
                    populateProperties();
                    CircuitGUIManager.propertiesBox.show();

                    if (!HardwareManager.movingObject) {
                        HardwareManager.movingObject = true;
                        canMove = true;
                        HardwareManager.currentHardware = this;
                        diffX = box.x - vec.x;
                        diffY = box.y - vec.y;
                    }
                }

                if(canMove) {
                    if ((Gdx.input.getX() <= Gdx.graphics.getWidth() - 200) || !CircuitGUIManager.isPanelShown()) {
                        for(Hardware h : hardwareOnPlate) {
                            h.setPosition(h.getPosition().x + vec.x + diffX - box.x, h.getPosition().y + vec.y + diffY - box.y);
                        }

                        box.x = vec.x + diffX;
                        box.y = vec.y + diffY;

                        for (ResizeNode node : nodes) {
                            node.updateIdlePos(box);
                        }
                    }
                }

                if(!canMove && !box.contains(vec.x, vec.y) && !(CircuitGUIManager.panelShown && Gdx.input.getX() >= Gdx.graphics.getWidth() - 420 && Gdx.input.getY() <= 210) && !(!CircuitGUIManager.panelShown && Gdx.input.getX() >= Gdx.graphics.getWidth() - 210 && Gdx.input.getY() <= 210)) {
                    HardwareManager.currentHardware = null;
                    CircuitGUIManager.propertiesBox.hide();
                }
            } else {
                canMove = false;
                HardwareManager.movingObject = false;
            }

        }
    }

    private void setSelectedNode(int index) {
        for(int i = 0; i < nodes.length; i++) {
            if(i == index) {
                nodes[i].setSelected(true);
            } else {
                nodes[i].setSelected(false);
            }
        }
    }

    @Override
    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("E-Plate " + hardwareID2, CircuitGUIManager.propertiesBox.LABEL), true, 2);
        TextButton freeze = new TextButton("Freeze", CircuitGUIManager.propertiesBox.TBUTTON);
        CircuitGUIManager.propertiesBox.addElement(freeze, true, 2);
        freeze.setWidth(120);

        CircuitGUIManager.propertiesBox.addElement(new Label("Color", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        final TextButton cb = new TextButton(DeviceUtil.getKeyByValue(DeviceUtil.COLORS_EPLATE, this.color), CircuitGUIManager.propertiesBox.TBUTTON);
        CircuitGUIManager.propertiesBox.addElement(cb, false, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label("Width", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        TextField width = new TextField(box.width + "", textFieldStyle);
        width.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        CircuitGUIManager.propertiesBox.addElement(width, false, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label("Height", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        TextField height = new TextField(box.height + "", textFieldStyle);
        height.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        CircuitGUIManager.propertiesBox.addElement(height, false, 1);

        CircuitGUIManager.propertiesBox.addElement(new Label("", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 2);

        width.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                box.width = Float.parseFloat(width.getText());
            }
        });
        height.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                box.height = Float.parseFloat(height.getText());
            }
        });

        cb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ArrayList<String> keys = new ArrayList<>(DeviceUtil.COLORS_EPLATE.keySet());
                for(String str : keys) {
                    if(str.contentEquals(cb.getText())) {
                        if(keys.indexOf(str) == keys.size() - 1) {
                            cb.setText(keys.get(0));
                            color = DeviceUtil.COLORS_EPLATE.get(keys.get(0));
                        } else {
                            cb.setText(keys.get(keys.indexOf(str) + 1));
                            color = DeviceUtil.COLORS_EPLATE.get(keys.get(keys.indexOf(str) + 1));
                        }
                        break;
                    }
                }
            }
        });


        freeze.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!frozen) {
                    frozen = true;
                    freeze.setText("Unfreeze");
                } else {
                    frozen = false;
                    freeze.setText("Freeze");
                }

            }
        });


        CircuitGUIManager.propertiesBox.addElement(new Label("Connections", CircuitGUIManager.propertiesBox.LABEL), true, 2);
        for(Hardware h : hardwareOnPlate) {
            CircuitGUIManager.propertiesBox.addElement(new Label(h.name + " " + h.hardwareID2, CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 2);
        }
    }


    @Override
    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(255/255f, 255/255f, 255/255f, 0.2f);

        Gdx.gl.glLineWidth(5);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.roundedRect(box.x - 5, box.y - 5, box.width + 10, box.height + 10, 5);
        renderer.end();
        Gdx.gl.glLineWidth(1);
    }


    public Box getBox() {
        return box;
    }
}
