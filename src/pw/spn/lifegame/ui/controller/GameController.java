package pw.spn.lifegame.ui.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import pw.spn.lifegame.ui.component.Cell;
import pw.spn.lifegame.ui.component.GamePane;
import pw.spn.lifegame.ui.component.SizeInput;

public class GameController implements Initializable {
    private static final int MAX_HEIGHT = 35;
    private static final int MAX_WIDTH = 59;

    @FXML
    private BorderPane root;

    @FXML
    private HBox menu;

    @FXML
    private SizeInput heightInput;

    @FXML
    private SizeInput widthInput;

    @FXML
    private Button createButton;

    @FXML
    private GamePane gamePane;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    private Task<Void> updateTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            String heightText = heightInput.getText();
            String widthText = widthInput.getText();

            if (heightText == null || widthText == null) {
                return;
            }

            int height = parseInt(heightInput.getText(), MAX_HEIGHT);
            int width = parseInt(widthInput.getText(), MAX_WIDTH);

            if (height == 0 || width == 0) {
                return;
            }

            heightInput.setText(String.valueOf(height));
            widthInput.setText(String.valueOf(width));

            fillGamePane(height, width);
        });

        startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            startButton.setVisible(false);
            startGame(gamePane);
            stopButton.setVisible(true);
        });

        stopButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            stopGame();
        });
    }

    private int parseInt(String text, int maxValue) {
        int i = 0;
        try {
            i = Integer.parseInt(text);
            if (i > maxValue) {
                i = maxValue;
            }
        } catch (NumberFormatException e) {
        }
        return i;
    }

    private void fillGamePane(int height, int width) {
        startButton.setVisible(true);

        gamePane.setState(new boolean[height][width]);
        gamePane.getChildren().clear();

        IntStream.range(0, height).forEachOrdered(i -> IntStream.range(0, width).forEachOrdered(j -> {
            Cell cell = new Cell(false);

            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
                cell.changeState();
                gamePane.getState()[i][j] = !gamePane.getState()[i][j];
            });

            gamePane.add(cell, j, i);
        }));
    }

    private void startGame(GamePane gamePane) {
        menu.setVisible(false);
        gamePane.getChildrenUnmodifiable().forEach(node -> node.setDisable(true));

        updateTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                boolean stopGame = false;
                while (!stopGame && !isCancelled()) {
                    boolean[][] current = gamePane.getState();
                    boolean[][] newState = new boolean[current.length][current[0].length];

                    gamePane.getChildren().forEach(node -> {
                        int row = GridPane.getRowIndex(node);
                        int column = GridPane.getColumnIndex(node);
                        newState[row][column] = calculateNewState(current, row, column);
                    });
                    gamePane.setState(newState);

                    Platform.runLater(() -> updateState(gamePane));

                    stopGame = isGameStopped(current, newState);

                    Thread.sleep(500);
                }
                Platform.runLater(() -> stopGame());
                return null;
            }

        };

        Thread thread = new Thread(updateTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void stopGame() {
        if (updateTask != null && updateTask.isRunning()) {
            updateTask.cancel(false);
        }

        menu.setVisible(true);
        stopButton.setVisible(false);
        startButton.setVisible(true);
        gamePane.getChildrenUnmodifiable().forEach(node -> node.setDisable(false));
    }

    private boolean isGameStopped(boolean[][] currentState, boolean[][] newState) {
        boolean hasLiveCells = false;
        for (int i = 0; i < newState.length; i++) {
            for (int j = 0; j < newState[i].length; j++) {
                if (newState[i][j]) {
                    hasLiveCells = true;
                    break;
                }
            }
            if (hasLiveCells) {
                break;
            }
        }

        if (!hasLiveCells) {
            return true;
        }

        for (int i = 0; i < newState.length; i++) {
            boolean equalsState = Arrays.equals(currentState[i], newState[i]);
            if (!equalsState) {
                return false;
            }
        }

        return true;
    }

    private boolean calculateNewState(boolean[][] currentState, int currentRow, int currentColumn) {
        int liveNeighbors = 0;

        int rowLimit = currentState.length;
        int columnLimit = currentState[0].length;

        int topRow = currentRow - 1;
        int bottomRow = currentRow + 1;

        // top row neighbors
        if (topRow >= 0) {
            liveNeighbors += getLiveNeighbors(currentState, topRow, currentColumn, columnLimit, true);
        }

        // current row neighbors
        liveNeighbors += getLiveNeighbors(currentState, currentRow, currentColumn, columnLimit, false);

        // bottom row neighbors
        if (bottomRow < rowLimit) {
            liveNeighbors += getLiveNeighbors(currentState, bottomRow, currentColumn, columnLimit, true);
        }

        boolean currentValue = currentState[currentRow][currentColumn];
        if (!currentValue && liveNeighbors == 3) {
            return true;
        }
        if (currentValue && !(liveNeighbors == 3 || liveNeighbors == 2)) {
            return false;
        }

        return currentValue;
    }

    private int getLiveNeighbors(boolean[][] state, int row, int column, int columnLimit, boolean includeCurrentColumn) {
        int liveNeighbors = 0;
        if (column - 1 >= 0 && state[row][column - 1]) {
            liveNeighbors++;
        }

        if (includeCurrentColumn && state[row][column]) {
            liveNeighbors++;
        }

        if (column + 1 < columnLimit && state[row][column + 1]) {
            liveNeighbors++;
        }

        return liveNeighbors;
    }

    private void updateState(GamePane gamePane) {
        Iterator<Node> nodeIterator = gamePane.getChildren().iterator();
        boolean[][] state = gamePane.getState();
        IntStream.range(0, state.length).forEachOrdered(i -> IntStream.range(0, state[i].length).forEachOrdered(j -> {
            Cell node = (Cell) nodeIterator.next();
            node.setLive(state[i][j]);
        }));
    }
}
