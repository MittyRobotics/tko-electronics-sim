package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.HashMap;

public class TripleSandCrab extends DoubleSandCrab {

    private Sprite bottom, connector1, connector2;
    private String conn1, conn2;
    private Cable conn1c, conn2c;
    private boolean endOfWire1, endOfWire2;

    private HashMap<Cable, Hardware> connections;
    boolean canMove;

    public TripleSandCrab(Vector2 position) {
        super(position);

        connections = new HashMap<>();

        bottom = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_white.png")));
        connector1 = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange_2.png")));
        connector2 = new Sprite(new Texture(Gdx.files.internal("img/hardware/sandcrab_orange.png")));

        bottom.setCenter(position.x, position.y);
        connector1.setCenter(position.x - 30, position.y - 20);
        connector2.setCenter(position.x + 30, position.y - 20);

        conn1 = "None";
        conn2 = "None";
        conn1c = null;
        conn2c = null;

        canMove = false;
    }

    @Override
    public void attachWire(Cable cable, int port, boolean endOfWire) {
        super.attachWire(cable, port, endOfWire);
    }

    @Override
    public void drawHover(ModifiedShapeRenderer renderer) {
        super.drawHover(renderer);
    }

    @Override
    public void clearConnection(Cable cable) {
        super.clearConnection(cable);
    }
}
