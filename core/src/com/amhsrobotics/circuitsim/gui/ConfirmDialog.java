package com.amhsrobotics.circuitsim.gui;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class ConfirmDialog {

    private static Window window;
    private static final Label.LabelStyle l2Style = new Label.LabelStyle();
    private static final Window.WindowStyle wStyle = new Window.WindowStyle();
    private static final TextButton.TextButtonStyle tbStyle = new TextButton.TextButtonStyle();

    private static boolean open = false;

    
    public static void init(Stage stage) {

        l2Style.font = Constants.FONT_SMALL;
        l2Style.fontColor = Color.BLACK;

        wStyle.background = Constants.SKIN.getDrawable("window_02");
        wStyle.titleFont = Constants.FONT_MEDIUM;
        wStyle.titleFontColor = Color.WHITE;

        tbStyle.font = Constants.FONT_SMALL;
        tbStyle.up = Constants.SKIN.getDrawable("button_03");
        tbStyle.down = Constants.SKIN.getDrawable("button_02");

        window = new Window("Are you sure? Your circuit will be cleared.", wStyle);
        window.setWidth(600);
        window.setHeight(140);
        window.setKeepWithinStage(false);
        window.setMovable(false);
        window.setPosition(-700, -700);

        stage.addActor(window);
    }

    public static void createWindow(Runnable runnable) {
        window.clear();

        Table saveTable = new Table();
        window.add(saveTable).expand().fill();

        saveTable.row();
        TextButton cancel_ = new TextButton("Cancel", tbStyle);
        saveTable.add(cancel_).width(90).colspan(1).align(Align.center).padTop(50).padRight(10);
        TextButton continue_ = new TextButton("Continue", tbStyle);
        saveTable.add(continue_).width(90).colspan(1).align(Align.center).padTop(50).padLeft(10);

        cancel_.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                closeWindow();
            }
        });
        continue_.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
                closeWindow();
            }
        });

        openWindow();
    }

    private static void openWindow() {
        window.setPosition(Gdx.graphics.getWidth()/2f - window.getWidth()/2f, Gdx.graphics.getHeight()/2f - window.getHeight()/2f);
        Tools.slideIn(window, "down", 1f, Interpolation.exp10, 600);
        open = true;
    }

    private static void closeWindow() {
        Tools.slideOut(window, "down", 1f, Interpolation.exp10, 700);
        open = false;
    }

    public static boolean isOpen() {
        return open;
    }
}
