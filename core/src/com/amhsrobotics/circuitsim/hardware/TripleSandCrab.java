package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.files.JSONReader;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.SnapGrid;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;

public class TripleSandCrab extends Hardware {

    private Sprite bottom, connector1, connector2, connector3;

    boolean canMove;

    public TripleSandCrab(Vector2 position) {
        super(position);

        JSONReader.loadConfig("scripts/DoubleSandCrab.json");

        //CONNECTIONS & IF END OF EACH CABLE
        connections = new ArrayList<>();
        ends = new ArrayList<>();

        bottom = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_white_2.png")));
        connector1 = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange_2.png")));
        connector2 = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));
        connector3 = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));

        bottom.setCenter(position.x, position.y);
        connector1.setCenter(position.x - 80, position.y - 20);
        connector2.setCenter(position.x, position.y - 20);
        connector3.setCenter(position.x + 80, position.y - 20);

        connNum = 3;

        initConnections();
        initEnds();

        canMove = false;
    }

    @Override
    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(batch, renderer, camera);

        bottom.setCenter(getPosition().x, getPosition().y);
        connector1.setCenter(getPosition().x - 80, getPosition().y - 20);
        connector2.setCenter(getPosition().x, getPosition().y - 20);
        connector3.setCenter(getPosition().x + 80, getPosition().y - 20);

        Vector2 vec = Tools.mouseScreenToWorld(camera);


        //HOVERING MECHANICS
        //---------------------------------------------------

        if(bottom.getBoundingRectangle().contains(vec.x, vec.y)) {
            drawHover(renderer);

            //SELECTING
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                HardwareManager.currentHardware = this;
                CableManager.currentCable = null;
                CircuitGUIManager.propertiesBox.show();
                populateProperties();
            }

        }


        //SELECTED MECHANICS
        //---------------------------------------------------

        if(HardwareManager.currentHardware == this) {
            drawHover(renderer);

            //UNSELECT
            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                CircuitGUIManager.propertiesBox.hideAndClear();
                HardwareManager.currentHardware = null;
            }

            //MOVING
            if(Gdx.input.isTouched() && canMove) {
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    SnapGrid.calculateSnap(vec);
                }

                //SET OWN POSITION

                setPosition(vec.x, vec.y);


                //SET CONNECTED WIRE POSITIONS

                if(connections.get(0) != null) {
                    connections.get(0).editCoordinates(new Vector2(getConnector1().getX() + getConnector1().getWidth() / 2, getConnector1().getY() + 20), ends.get(0), false);
                    connections.get(0).editCoordinates(new Vector2(getConnector1().getX() + getConnector1().getWidth() / 2, getConnector1().getY() - 20), ends.get(0), true);
                }
                if(connections.get(1) != null) {
                    connections.get(1).editCoordinates(new Vector2(getConnector2().getX() + getConnector2().getWidth() / 2, getConnector2().getY() + 20), ends.get(1), false);
                    connections.get(1).editCoordinates(new Vector2(getConnector2().getX() + getConnector2().getWidth() / 2, getConnector2().getY() - 20), ends.get(1), true);
                }
                if(connections.get(2) != null) {
                    connections.get(2).editCoordinates(new Vector2(getConnector3().getX() + getConnector3().getWidth() / 2, getConnector3().getY() + 20), ends.get(2), false);
                    connections.get(2).editCoordinates(new Vector2(getConnector3().getX() + getConnector3().getWidth() / 2, getConnector3().getY() - 20), ends.get(2), true);
                }
            } else if (Gdx.input.isTouched()) {

                //BEING DRAGGED: ALLOW MOVE

                if(Gdx.input.getDeltaX() != 0 && Gdx.input.getDeltaY() != 0 && bottom.getBoundingRectangle().contains(vec.x, vec.y)) {
                    canMove = true;
                }
                HardwareManager.movingObject = true;

            } else {

                //STOP MOVING

                if(HardwareManager.movingObject) {
                    canMove = false;
                    HardwareManager.movingObject = false;
                }
            }

            if((Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL))) {
                this.delete();
            }

        }

        batch.begin();
        bottom.draw(batch);
        connector1.draw(batch);
        connector2.draw(batch);
        connector3.draw(batch);
        batch.end();
    }

    @Override
    public void delete() {
        HardwareManager.removeSandCrab(this);
        HardwareManager.currentHardware = null;
        CircuitGUIManager.propertiesBox.hide();
    }

    private void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("Sandcrab", CircuitGUIManager.propertiesBox.LABEL), true, 2);
        CircuitGUIManager.propertiesBox.addElement(new Label("Conn. 1", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(0) == null ? "None" : "Cable " + connections.get(0).getID(), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label("Conn. 2", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(1) == null ? "None" : "Cable " + connections.get(1).getID(), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label("Conn. 3", CircuitGUIManager.propertiesBox.LABEL_SMALL), true, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label(connections.get(2) == null ? "None" : "Cable " + connections.get(2).getID(), CircuitGUIManager.propertiesBox.LABEL_SMALL), false, 1);
    }

    public void clearConnection(Cable cable) {
        if(cable == connections.get(0)) {
            connections.set(0, null);
        } else if(cable == connections.get(1)) {
            connections.set(1, null);
        } else if(cable == connections.get(2)) {
            connections.set(2, null);
        }
    }


    public void reattachWire(Cable cable, int port, boolean endOfWire) {
        //REATTACH WIRE FOR MEGING
        connections.set(port, cable);
        ends.set(port, endOfWire);
        if(endOfWire) {
            cable.setConnection2(this);
        } else {
            cable.setConnection1(this);
        }
    }


    public void attachWireS(Cable cable, int port, boolean endOfWire) {

        //INITIAL ATTACH WIRE

        ends.set(port, endOfWire);
        connections.set(port, cable);


        if(endOfWire) {

            cable.setConnection2(this);

            // MOVE CABLE
            if(port == 0) {
                cable.addCoordinates(new Vector2(getConnector1().getX() + getConnector1().getWidth() / 2, getConnector1().getY() - 20), false);
                cable.addCoordinates(new Vector2(getConnector1().getX() + getConnector1().getWidth() / 2, getConnector1().getY() + 20), false);
            } else if(port == 1) {
                cable.addCoordinates(new Vector2(getConnector2().getX() + getConnector2().getWidth() / 2, getConnector2().getY() - 20), false);
                cable.addCoordinates(new Vector2(getConnector2().getX() + getConnector2().getWidth() / 2, getConnector2().getY() + 20), false);
            } else if(port == 2) {
                cable.addCoordinates(new Vector2(getConnector3().getX() + getConnector3().getWidth() / 2, getConnector3().getY() - 20), false);
                cable.addCoordinates(new Vector2(getConnector3().getX() + getConnector3().getWidth() / 2, getConnector3().getY() + 20), false);
            }

        } else {

            cable.setConnection1(this);

            // MOVE CABLE
            if(port == 0) {
                cable.addCoordinates(new Vector2(getConnector1().getX() + getConnector1().getWidth() / 2, getConnector1().getY() - 20), true);
                cable.addCoordinates(new Vector2(getConnector1().getX() + getConnector1().getWidth() / 2, getConnector1().getY() + 20), true);
            } else if(port == 1) {
                cable.addCoordinates(new Vector2(getConnector2().getX() + getConnector2().getWidth() / 2, getConnector2().getY() - 20), true);
                cable.addCoordinates(new Vector2(getConnector2().getX() + getConnector2().getWidth() / 2, getConnector2().getY() + 20), true);
            } else if(port == 2) {
                cable.addCoordinates(new Vector2(getConnector3().getX() + getConnector3().getWidth() / 2, getConnector3().getY() - 20), true);
                cable.addCoordinates(new Vector2(getConnector3().getX() + getConnector3().getWidth() / 2, getConnector3().getY() + 20), true);
            }

        }

        CableManager.currentCable = null;

    }

    public Sprite getConnector3() {
        return connector3;
    }

    public Sprite getConnector1() {
        return connector1;
    }

    public Sprite getConnector2() {
        return connector2;
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(Color.WHITE);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(getPosition().x - (bottom.getWidth() / 2), getPosition().y - (bottom.getHeight() / 2), bottom.getWidth()-1, bottom.getHeight(), 16);
        renderer.end();
    }
}
