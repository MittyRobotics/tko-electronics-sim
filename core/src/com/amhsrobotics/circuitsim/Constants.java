package com.amhsrobotics.circuitsim;

import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Constants {

    public static final BitmapFont FONT = Tools.renderFont("font/Abel-Regular.ttf", 30);
    public static final TextureAtlas ATLAS = new TextureAtlas(Gdx.files.internal("skin/ui-gray.atlas"));
    public static final Skin SKIN = new Skin(ATLAS);

    public static void disposeAll() {
        FONT.dispose();
        ATLAS.dispose();
        SKIN.dispose();
    }
}
