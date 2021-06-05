package com.amhsrobotics.circuitsim.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import me.rohanbansal.ricochet.camera.CameraController;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class Tools {


    public static BitmapFont renderFont(String fontfile, int size, boolean... bold) {
        FileHandle fontFile = Gdx.files.internal(fontfile);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        if(bold.length > 0) {
            parameter.borderWidth = 2;
        }
        BitmapFont fnt = generator.generateFont(parameter);
        generator.dispose();
        return fnt;
    }

    public static Vector2 mouseScreenToWorld(CameraController cam) {
        Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.getCamera().unproject(vec);
        return new Vector2(vec.x, vec.y);
    }

    public static boolean collide(Vector2 r1p1, Vector2 r1p2, Vector2 r2p1, Vector2 r2p2) {
        return !(r1p2.x <= r2p1.x ||
                r1p2.y <= r2p1.y ||
                r1p1.x >= r2p2.x ||
                r1p1.y >= r2p2.y);
    }

    public static void slideIn(Actor actor, String location, float duration, Interpolation interp, int offset, Runnable... runnable) {
        Vector2 actorXY = new Vector2(actor.getX(), actor.getY());

        Runnable toRun = null;
        if(runnable.length > 0) {
            toRun = runnable[0];
        }

        if(location.equals("left")) {
            actor.setPosition(-offset, actorXY.y);
            if(toRun != null) {
                actor.addAction(sequence(moveTo(actorXY.x, actorXY.y, duration, interp), run(toRun)));
            } else {
                actor.addAction(moveTo(actorXY.x, actorXY.y, duration, interp));
            }
        } else if(location.equals("right")) {
            actor.setPosition(Gdx.graphics.getWidth() + offset, actorXY.y);
            if(toRun != null) {
                actor.addAction(sequence(moveTo(actorXY.x, actorXY.y, duration, interp), run(toRun)));
            } else {
                actor.addAction(moveTo(actorXY.x, actorXY.y, duration, interp));
            }
        } else if(location.equals("top")) {
            actor.setPosition(actorXY.x, Gdx.graphics.getHeight() + offset);
            if(toRun != null) {
                actor.addAction(sequence(moveTo(actorXY.x, actorXY.y, duration, interp), run(toRun)));
            } else {
                actor.addAction(moveTo(actorXY.x, actorXY.y, duration, interp));
            }
        } else if(location.equals("down")) {
            actor.setPosition(actorXY.x, -offset);
            if(toRun != null) {
                actor.addAction(sequence(moveTo(actorXY.x, actorXY.y, duration, interp), run(toRun)));
            } else {
                actor.addAction(moveTo(actorXY.x, actorXY.y, duration, interp));
            }
        }

        if(runnable.length > 0) {
            actor.addAction(run(runnable[0]));
        }
    }

    public static void slideOut(Actor actor, String location, float duration, Interpolation interp, int offset, Runnable... runnable) {
        Vector2 actorXY = new Vector2(actor.getX(), actor.getY());

        Runnable toRun = null;
        if(runnable.length > 0) {
            toRun = runnable[0];
        }

        if(location.equals("left")) {
            if(toRun != null) {
                actor.addAction(sequence(moveTo(-offset, actorXY.y, duration, interp), run(toRun)));
            } else {
                actor.addAction(moveTo(-offset, actorXY.y, duration, interp));
            }
        } else if(location.equals("right")) {
            if(toRun != null) {
                actor.addAction(sequence(moveTo(Gdx.graphics.getWidth() + offset, actorXY.y, duration, interp), run(toRun)));
            } else {
                actor.addAction(moveTo(Gdx.graphics.getWidth() + offset, actorXY.y, duration, interp));
            }
        } else if(location.equals("top")) {
            if(toRun != null) {
                actor.addAction(sequence(moveTo(actorXY.x, Gdx.graphics.getHeight() + offset, duration, interp), run(toRun)));
            } else {
                actor.addAction(moveTo(actorXY.x, Gdx.graphics.getHeight() + offset, duration, interp));
            }
        } else if(location.equals("down")) {
            if(toRun != null) {
                actor.addAction(sequence(moveTo(actorXY.x, -offset, duration, interp), run(toRun)));
            } else {
                actor.addAction(moveTo(actorXY.x, -offset, duration, interp));
            }        }
    }

    public static void sequenceSlideIn(String location, float duration, Interpolation interp, int offset, float delay, Actor... actors) {

        float currentDelay = 0f;

        for(int x = 0; x < actors.length; x++) {
            Actor actor = actors[x];

            if(x != 0) {
                currentDelay += delay;
            }

            Vector2 actorXY = new Vector2(actor.getX(), actor.getY());

            if(location.equals("left")) {
                actor.setPosition(-offset, actorXY.y);
                actor.addAction(sequence(delay(currentDelay), moveTo(actorXY.x, actorXY.y, duration, interp)));
            } else if(location.equals("right")) {
                actor.setPosition(Gdx.graphics.getWidth() + offset, actorXY.y);
                actor.addAction(sequence(delay(currentDelay), moveTo(actorXY.x, actorXY.y, duration, interp)));
            } else if(location.equals("top")) {
                actor.setPosition(actorXY.x, Gdx.graphics.getHeight() + offset);
                actor.addAction(sequence(delay(currentDelay), moveTo(actorXY.x, actorXY.y, duration, interp)));
            } else if(location.equals("down")) {
                actor.setPosition(actorXY.x, -offset);
                actor.addAction(sequence(delay(currentDelay), moveTo(actorXY.x, actorXY.y, duration, interp)));
            }
        }
    }

    public static void sequenceSlideOut(String location, float duration, Interpolation interp, int offset, float delay, Actor... actors) {

        float currentDelay = 0f;

        for(int x = 0; x < actors.length; x++) {
            Actor actor = actors[x];

            if(x != 0) {
                currentDelay += delay;
            }

            Vector2 actorXY = new Vector2(actor.getX(), actor.getY());

            if(location.equals("left")) {
                actor.addAction(sequence(delay(currentDelay), moveTo(-offset, actorXY.y, duration, interp)));
            } else if(location.equals("right")) {
                actor.addAction(sequence(delay(currentDelay), moveTo(Gdx.graphics.getWidth() + offset, actorXY.y, duration, interp)));
            } else if(location.equals("top")) {
                actor.addAction(sequence(delay(currentDelay), moveTo(actorXY.x, Gdx.graphics.getHeight() + offset, duration, interp)));
            } else if(location.equals("down")) {
                actor.addAction(sequence(delay(currentDelay), moveTo(actorXY.x, -offset, duration, interp)));
            }
        }
    }
}