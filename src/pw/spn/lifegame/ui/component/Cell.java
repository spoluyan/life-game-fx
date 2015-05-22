package pw.spn.lifegame.ui.component;

import javafx.scene.control.Button;

public class Cell extends Button {
    private boolean isLive;

    public Cell(boolean isLive) {
        setLive(isLive);
        getStyleClass().add("cell");
    }

    public void changeState() {
        setLive(!isLive);
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
        if (isLive) {
            setText("\u25CF");
        } else {
            setText("\u25CB");
        }
    }
}
