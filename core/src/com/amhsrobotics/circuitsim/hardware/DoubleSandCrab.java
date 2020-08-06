package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
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
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class DoubleSandCrab extends Hardware {

    private Sprite bottom, connector1, connector2;

    public DoubleSandCrab(Vector2 position) {
        super(position);

        bottom = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_white.png")));
        connector1 = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));
        connector2 = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));

        bottom.setCenter(position.x, position.y);
        connector1.setCenter(position.x - 30, position.y - 20);
        connector2.setCenter(position.x + 30, position.y - 20);
    }

    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(batch, renderer, camera);

        Vector2 vec = Tools.mouseScreenToWorld(camera);

        if(bottom.getBoundingRectangle().contains(vec.x, vec.y)) {
            drawHover(renderer);
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                HardwareManager.currentHardware = this;
            }
        }

        if(HardwareManager.currentHardware == this) {
            drawHover(renderer);

            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                HardwareManager.currentHardware = null;
            }
        }

        batch.begin();
        bottom.draw(batch);
        connector1.draw(batch);
        connector2.draw(batch);
        batch.end();
    }

    public void attachWire(Cable cable, int port, boolean endOfWire) {
        if(endOfWire) {
            cable.setConnection2(getHardwareID());
            if(port == 1) {
                cable.addCoordinates(new Vector2(getConnector1().getX() + getConnector1().getWidth() / 2, getConnector1().getY() + 20), false);
            } else if(port == 2) {
                cable.addCoordinates(new Vector2(getConnector2().getX() + getConnector2().getWidth() / 2, getConnector2().getY() + 20), false);
            }
        } else {
            cable.setConnection1(getHardwareID());
            if(port == 1) {
                cable.addCoordinates(new Vector2(getConnector1().getX() + getConnector1().getWidth() / 2, getConnector1().getY() + 20), true);
            } else if(port == 2) {
                cable.addCoordinates(new Vector2(getConnector2().getX() + getConnector2().getWidth() / 2, getConnector2().getY() + 20), true);
            }

        }
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
        renderer.roundedRect(getPosition().x - (bottom.getWidth() / 2), getPosition().y - (bottom.getHeight() / 2), bottom.getWidth(), bottom.getHeight(), 15);
        renderer.end();
    }
}
