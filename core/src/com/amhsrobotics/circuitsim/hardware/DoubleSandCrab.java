package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.files.JSONReader;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.SnapGrid;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.amhsrobotics.circuitsim.wiring.CrimpedCable;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class DoubleSandCrab extends Hardware {

    private Sprite bottom;
    ArrayList<JSONArray> pinDefs = new ArrayList<>();
    ArrayList<JSONArray> pinSizeDefs = new ArrayList<>();
    ArrayList<Sprite> connectors = new ArrayList<>();

    boolean canMove;

    public DoubleSandCrab(Vector2 position) {
        super(position);

        JSONReader.loadConfig("scripts/DoubleSandCrab.json");
        connNum = ((Long) JSONReader.getCurrentConfig().get("totalPins")).intValue();
        JSONArray pins = (JSONArray) JSONReader.getCurrentConfig().get("pins");
        for(int x = 0; x < pins.size(); x++) {
            pinDefs.add((JSONArray) ((JSONObject) pins.get(x)).get("position"));
        }
        for(int x = 0; x < pins.size(); x++) {
            pinSizeDefs.add((JSONArray) ((JSONObject) pins.get(x)).get("dimensions"));
        }

        bottom = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_white.png")));
        bottom.setCenter(position.x, position.y);


        for(JSONArray arr : pinDefs) {
            Sprite temp;
            if(connectors.size() == connNum) {
                break;
            }
            if(connectors.size() == 0) {
                temp = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange_2.png")));
            } else {
                temp = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));
            }
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            connectors.add(temp);
        }

        initConnections();
        initEnds();

        canMove = false;

//        CrimpedCable c = new CrimpedCable(new Vector2(500, 500), -1);
//        c.addCoordinates(new Vector2(200, 200), false);
//        attachWire(c, 1, true);
    }

    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(batch, renderer, camera);

        bottom.setCenter(getPosition().x, getPosition().y);
        for(Sprite temp : connectors) {
            temp.setCenter(getPosition().x + (Long) pinDefs.get(connectors.indexOf(temp)).get(0), getPosition().y + (Long) pinDefs.get(connectors.indexOf(temp)).get(1));
        }

        Vector2 vec = Tools.mouseScreenToWorld(camera);


        //HOVERING MECHANICS
        //---------------------------------------------------

        if(bottom.getBoundingRectangle().contains(vec.x, vec.y)) {

            //HOVERING
            drawHover(renderer);

            //SELECTING
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                HardwareManager.currentHardware = this;
                CableManager.currentCable = null;

                populateProperties();
            }

        }


        //SELECTED MECHANICS
        //---------------------------------------------------

        if(HardwareManager.currentHardware == this) {
            drawHover(renderer);

            if((Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL))) {
                this.delete();
            }

            //UNSELECT
            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                CircuitGUIManager.propertiesBox.hide();
                HardwareManager.currentHardware = null;
            }


            //MOVING
            if (Gdx.input.isTouched()) {

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

            if(Gdx.input.isTouched() && canMove) {
                if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    SnapGrid.calculateSnap(vec);
                }

                //SET OWN POSITION
                setPosition(vec.x, vec.y);


                //MOVE CABLES

                for(JSONArray arr : pinDefs) {
                    int index = pinDefs.indexOf(arr);
                    if(connections.get(index) != null) {
                        connections.get(index).editCoordinates(
                                new Vector2(getConnector(index).getX() + getConnector(index).getWidth() / 2, getConnector(index).getY() + 20),
                                ends.get(index), false);
                        connections.get(index).editCoordinates(
                                new Vector2(getConnector(index).getX() + getConnector(index).getWidth() / 2, getConnector(index).getY() - 20),
                                ends.get(index), true);
                    }
                }
            }
        }

        batch.begin();
        bottom.draw(batch);
//        connector1.draw(batch);
//        connector2.draw(batch);
        for(Sprite conn : connectors) {
            conn.draw(batch);
        }
        batch.end();
    }

    @Override
    public void delete() {
        for(Cable cable : connections) {
            if(cable != null) {
                if(ends.get(connections.indexOf(cable))) {
                    cable.setConnection2(null);
                } else {
                    cable.setConnection1(null);
                }
            }
        }
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
    }

    public void clearConnection(Cable cable) {
        if(cable == connections.get(0)) {
            connections.set(0, null);
        } else if(cable == connections.get(1)) {
            connections.set(1, null);
        }
    }

    public void reattachWire(Cable cable, int port, boolean endOfWire) {
        connections.set(port, cable);
        ends.set(port, endOfWire);
        if(endOfWire) {
            cable.setConnection2(this);
        } else {
            cable.setConnection1(this);
        }
    }


    public void attachWire(Cable cable, int port, boolean endOfWire) {
        connections.set(port, cable);
        ends.set(port, endOfWire);

        if(endOfWire) {
            cable.setConnection2(this);
            cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() - 20), false);
            cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + 20), false);
        } else {
            cable.setConnection1(this);
            cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() - 20), true);
            cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + 20), true);
        }

        CableManager.currentCable = null;
    }

    public void firstClickAttach(Cable cable, int port, boolean endOfWire) {
        connections.set(port, cable);
        ends.set(port, endOfWire);

        cable.removeCoordinates();

        if(endOfWire) {
            cable.setConnection2(this);
            cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() - 20), false);
            cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + 20), false);
        } else {
            cable.setConnection1(this);
            cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() - 20), true);
            cable.addCoordinates(new Vector2(getConnector(port).getX() + getConnector(port).getWidth() / 2, getConnector(port).getY() + 20), true);
        }

        cable.setAppendingFromEnd(false);
        cable.setAppendingFromBegin(false);

    }

    public Sprite getConnector(int conn) {
        return connectors.get(conn);
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(Color.WHITE);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(getPosition().x - (bottom.getWidth() / 2), getPosition().y - (bottom.getHeight() / 2), bottom.getWidth()-1, bottom.getHeight(), 15);
        renderer.end();
    }
}
