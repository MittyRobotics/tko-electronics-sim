package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.Box;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;

public class EPlate extends Hardware {

    private Box box;
    private TextField.TextFieldStyle textFieldStyle;

    private ArrayList<Hardware> hardwareOnPlate = new ArrayList<>();
    private Circle[] nodes = new Circle[4];


    public EPlate(Vector2 pos) {
        super(pos, HardwareType.EPLATE);

        textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = Constants.SKIN.getDrawable("textbox_02");
        textFieldStyle.cursor = Constants.SKIN.getDrawable("textbox_cursor_02");
        textFieldStyle.font = Constants.FONT_SMALL;
        textFieldStyle.fontColor = Color.BLACK;
    }

    public void init() {
        box = new Box(getPosition(), getPosition().add(100, 0), getPosition().add(0, 100), getPosition().add(100, 100));
        initNodes();
    }

    private void initNodes() {
        for(int x = 0; x < 4; x++) {
            nodes[x] = new Circle(box.getAtIndex(x), 15);
        }
    }

    private void updateBox(int vertice, Vector2 val) {
        box.updateVertice(vertice, val);
        initNodes();
    }

    @Override
    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(batch, renderer, camera);

        renderer.setColor(193/255f, 211/255f, 200/255f, 0.5f);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(box.getX(), box.getY(), box.getWidth(), box.getHeight(), 5);
        renderer.end();

        Vector2 vec = Tools.mouseScreenToWorld(camera);

        if(box.contains(vec.x, vec.y)) {
            drawHover(renderer);

            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                HardwareManager.currentHardware = this;
                CableManager.currentCable = null;
                populateProperties();
                CircuitGUIManager.propertiesBox.show();
            }
        }

        if (HardwareManager.currentHardware == this) {
            drawHover(renderer);
            drawDragNodes(vec, renderer);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                HardwareManager.currentHardware = null;
                CircuitGUIManager.propertiesBox.hide();
            }

            for(int x = 0; x < nodes.length; x++) {
                if(Gdx.input.isTouched()) {
                    if(nodes[x].contains(vec)) {
                        if ((Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0)) {
                            nodes[x].setPosition(vec.x, vec.y);
                            updateBox(x, new Vector2(nodes[x].x, nodes[x].y));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("E-Plate", CircuitGUIManager.propertiesBox.LABEL), true, 2);
        CircuitGUIManager.propertiesBox.addElement(new Label("Width", CircuitGUIManager.propertiesBox.LABEL), true, 1);
        TextField width = new TextField(box.getWidth() + "", textFieldStyle);
        width.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        CircuitGUIManager.propertiesBox.addElement(width, false, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label("Height", CircuitGUIManager.propertiesBox.LABEL), true, 1);
        TextField height = new TextField(box.getHeight() + "", textFieldStyle);
        height.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        CircuitGUIManager.propertiesBox.addElement(height, false, 1);

    }

    private void drawDragNodes(Vector2 vec, ModifiedShapeRenderer renderer) {

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for(Circle c : nodes) {
            if(c.contains(vec)) {
                renderer.setColor(Color.WHITE);
                renderer.circle(c.x, c.y, c.radius);
            } else {
                renderer.setColor(Color.SALMON);
                renderer.circle(c.x, c.y, c.radius);
            }
        }

        renderer.end();
    }

    @Override
    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(255/255f, 255/255f, 255/255f, 0.2f);

        Gdx.gl.glLineWidth(5);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.roundedRect(box.getX() - 5, box.getY() - 5, box.getWidth() + 10, box.getHeight() + 10, 5);
        renderer.end();
        Gdx.gl.glLineWidth(1);
    }
}
