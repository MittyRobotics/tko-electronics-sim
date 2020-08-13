package com.amhsrobotics.circuitsim.utility.input;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class DigitFilter implements TextField.TextFieldFilter {

    private char[] accepted;
    private boolean dotPressed = false;

    public DigitFilter() {
        accepted = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
    }

    @Override
    public boolean acceptChar(TextField textField, char c) {
        if(c == '.') {
            dotPressed = true;
        }
        for (char a : accepted)
            if (a == c) return true;
        return false;
    }
}
