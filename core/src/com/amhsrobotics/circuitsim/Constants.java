package com.amhsrobotics.circuitsim;

import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Constants {

    public static final BitmapFont FONT = Tools.renderFont("font/Abel-Regular.ttf", 30);
    public static final BitmapFont FONT_MEDIUM = Tools.renderFont("font/Abel-Regular.ttf", 25);
    public static final BitmapFont FONT_SMALL = Tools.renderFont("font/Abel-Regular.ttf", 20);
    public static TextureAtlas ATLAS = new TextureAtlas(Gdx.files.internal("skin/ui-gray.atlas"));
    public static TextureAtlas ATLAS_ALTERNATE = new TextureAtlas(Gdx.files.internal("skin/ui-blue.atlas"));
    public static Skin SKIN = new Skin(ATLAS);
    public static Skin SKIN_ALTERNATE = new Skin(ATLAS_ALTERNATE);

    public static HardwareType placing_object = null;

    public static Vector2 WORLD_DIM = new Vector2(8000, 8000);
    public static int GRID_SIZE = 60;

    public static void reloadAssets() {
        SKIN = new Skin(ATLAS);
        SKIN_ALTERNATE = new Skin(ATLAS_ALTERNATE);
    }

    public static void disposeAll() {
        FONT.dispose();
        FONT_MEDIUM.dispose();
        FONT_SMALL.dispose();
        ATLAS.dispose();
        ATLAS_ALTERNATE.dispose();
        SKIN_ALTERNATE.dispose();
        SKIN.dispose();
    }
}
