package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class TripleSandCrab extends DoubleSandCrab {

    public TripleSandCrab(Vector2 position) {
        super(position);
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
