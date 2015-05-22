package pw.spn.lifegame.ui.component;

import javafx.scene.layout.GridPane;

public class GamePane extends GridPane {
    private boolean[][] state;

    public boolean[][] getState() {
        return state;
    }

    public void setState(boolean[][] state) {
        this.state = state;
    }
}
