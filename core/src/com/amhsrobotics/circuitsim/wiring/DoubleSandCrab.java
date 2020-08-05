package com.amhsrobotics.circuitsim.wiring;

import com.amhsrobotics.circuitsim.utility.ClippedCameraController;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import javax.sound.sampled.Clip;

public class DoubleSandCrab {

    private Sprite bottom, connector1, connector2;
    private Vector2 position;

    public DoubleSandCrab(Vector2 position) {
        this.position = position;

        bottom = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_white.png")));
        connector1 = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));
        connector2 = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));

        bottom.setCenter(position.x, position.y);
        connector1.setCenter(position.x - 30, position.y - 20);
        connector2.setCenter(position.x + 30, position.y - 20);
    }

    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {

        renderer.setProjectionMatrix(camera.getCamera().combined);
        Vector2 vec = Tools.mouseScreenToWorld(camera);

        if(bottom.getBoundingRectangle().contains(vec.x, vec.y)) {
            drawHover(renderer);
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                CableManager.currentConnector = this;
            }
        }

        if(CableManager.currentConnector == this) {
            drawHover(renderer);

            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                CableManager.currentConnector = null;
            }
        }

        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();
        bottom.draw(batch);
        connector1.draw(batch);
        connector2.draw(batch);
        batch.end();
    }

    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(Color.WHITE);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(position.x - (bottom.getWidth() / 2), position.y - (bottom.getHeight() / 2), bottom.getWidth(), bottom.getHeight(), 15);
        renderer.end();
    }
}
