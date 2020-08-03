package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.Constants;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class SnapGrid {

    public static void renderGrid(ModifiedShapeRenderer renderer, Color color, Vector2 dimensions, int gap, int startSpace) {
        renderer.setColor(color);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        for(int i = startSpace; i < dimensions.x; i += gap) {
            for (int j = startSpace; j < dimensions.y; j += gap) {
                renderer.line(i, 0, i, dimensions.y);
                renderer.line(0, j, dimensions.x, j);
            }
        }
        renderer.end();
    }

    public static void calculateSnap(Vector2 position) {
        position.x = Math.round(position.x / Constants.GRID_SIZE) * Constants.GRID_SIZE;
        position.y = Math.round(position.y / Constants.GRID_SIZE) * Constants.GRID_SIZE;
    }
}
