package com.amhsrobotics.circuitsim.utility.scene;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class SnapGrid {

    public static boolean renderGridB = true;
    private static ImmediateModeRenderer20 lineRenderer = new ImmediateModeRenderer20(false, true, 0);

    public static void line(float x1, float y1,
                            float x2, float y2,
                            Color color) {
        lineRenderer.color(color);
        lineRenderer.vertex(x1, y1, 0);
        lineRenderer.color(color);
        lineRenderer.vertex(x2, y2, 0);
    }


    public static void renderGrid(ClippedCameraController camera, Color color, Vector2 dimensions, int gap, int startSpace) {
        if(renderGridB) {
//            renderer.setColor(color);
            for(int i = startSpace; i < dimensions.x; i += gap) {

//                renderer.begin(ShapeRenderer.ShapeType.Line);
                lineRenderer.begin(camera.getCamera().combined, GL30.GL_LINES);
                for (int j = startSpace; j < dimensions.y; j += gap) {
                    line(i, 0, i, dimensions.y, color);
                    line(0, j, dimensions.x, j, color);
//                    renderer.line(i, 0, i, dimensions.y);
//                    renderer.line(0, j, dimensions.x, j);
                }
                lineRenderer.end();
//                renderer.end();
            }
        }
    }

    public static void calculateSnap(Vector2 position) {
        position.x = Math.round(position.x / Constants.GRID_SIZE) * Constants.GRID_SIZE;
        position.y = Math.round(position.y / Constants.GRID_SIZE) * Constants.GRID_SIZE;
    }
}
