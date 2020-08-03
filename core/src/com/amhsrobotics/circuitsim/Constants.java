package com.amhsrobotics.circuitsim;

import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Constants {

    public static final BitmapFont FONT = Tools.renderFont("font/Abel-Regular.ttf", 30);
    public static final BitmapFont FONT_SMALL = Tools.renderFont("font/Abel-Regular.ttf", 20);
    public static final TextureAtlas ATLAS = new TextureAtlas(Gdx.files.internal("skin/ui-gray.atlas"));
    public static final Skin SKIN = new Skin(ATLAS);

    public static final Vector2 WORLD_DIM = new Vector2(5000, 5000);

    public static void disposeAll() {
        FONT.dispose();
        FONT_SMALL.dispose();
        ATLAS.dispose();
        SKIN.dispose();
    }
}
