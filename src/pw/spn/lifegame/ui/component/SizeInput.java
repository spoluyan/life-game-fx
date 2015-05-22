package pw.spn.lifegame.ui.component;

import javafx.scene.control.TextField;

public class SizeInput extends TextField {
    private static final String VALID_CHARS = "[0-9]*";

    @Override
    public void replaceText(int start, int end, String text) {
        if (text.matches(VALID_CHARS)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (text.matches(VALID_CHARS)) {
            super.replaceSelection(text);
        }
    }
}
