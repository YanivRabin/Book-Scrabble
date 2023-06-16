package view;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import model.data.Board;
import model.data.Tile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class BoardViewController implements Initializable {

    Board gameBoard;
    ArrayList<Tile> currentTiles;
    boolean blockingTiles;
    Tile selectedTile;
    ArrayList<Button> usedButtons;
    Button clickedButton;
    Pair<Integer, Integer>[] positions;
    int positionsIndex;

    @FXML
    private GridPane boardGrid;

    @FXML
    private Text score;

    @FXML
    private AnchorPane tilesContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gameBoard = new Board();

        currentTiles = new ArrayList<>();
        blockingTiles = false;
        score.setText("0");
        usedButtons = new ArrayList<>();
        positions = new Pair[8];
        positionsIndex = 0;

        for (int i = 0; i < 8; i++)
            currentTiles.add(Tile.Bag.getBagModel().getRand());

        ObservableList<Node> children = tilesContainer.getChildren();

        int tileIndex = 0;
        for (Node child : children) {
            if (child instanceof Button) {
                Button button = (Button) child;
                Tile tile = currentTiles.get(tileIndex);

                button.setText(String.valueOf(tile.letter));

                tileIndex++;
                if (tileIndex >= currentTiles.size()) {
                    break;
                }
            }
        }
    }

    // Define additional methods and event handlers as needed
    @FXML
    public void handleTileButtonClick(ActionEvent event) {

        clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();

        // Find the corresponding Tile object based on the button text
        selectedTile = null;
        for (Tile tile : currentTiles) {
            if (String.valueOf(tile.letter).equals(buttonText)) {
                selectedTile = tile;
                break;
            }
        }

        if (!blockingTiles) {

            disableButtons();

            if (selectedTile != null) {
                System.out.println("Selected tile: " + selectedTile.letter);
            }
        }
        else {

            enableButtons();
        }
    }

    // Example method to handle pane click
    @FXML
    public void handlePaneClick(MouseEvent event) {

        Pane clickedPane = (Pane) event.getSource();

        Integer rowIndex = GridPane.getRowIndex(clickedPane);
        Integer columnIndex = GridPane.getColumnIndex(clickedPane);

        // Perform actions based on the row and column
        int row = rowIndex.intValue();
        int column = columnIndex.intValue();
        System.out.println("Clicked pane at row: " + row + ", column: " + column);

        // check to see if a tile was selected
        if (blockingTiles && selectedTile != null) {

            // check if there is no tile
            if (gameBoard.getTiles()[row][column] == null) {

                gameBoard.placeTile(selectedTile, row, column);

                Label letter = new Label(String.valueOf(selectedTile.letter));
                letter.setAlignment(Pos.CENTER);
                // Set font size, weight and color of the letter
                letter.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: white;");
                // Set layout constraints to center the label within the pane
                letter.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                // adding the letter to the board
                boardGrid.add(letter, column, row);

                // adding the tile to list of used tiles for the blocking buttons
                usedButtons.add(clickedButton);

                // Enable all other tile buttons
                enableButtons();
                for (Node child : tilesContainer.getChildren()) {
                    if (child instanceof Button && child.equals(clickedButton)) {
                        Button button = (Button) child;
                        button.setDisable(true);
                    }
                }

                // for later if ill want to reset word
                positions[positionsIndex] = new Pair<>(row, column);
                // for next itr
                positionsIndex++;
            }
            else {
                System.out.println("Error in putting tile, there's already tile");
            }
        }
    }

    public void resetTilesButtonClick(ActionEvent actionEvent) {

        // remove the tiles from original board
        for (Pair<Integer, Integer> pair: positions) {
            if (pair != null) {
                int row = pair.getKey();
                int column = pair.getValue();
                gameBoard.removeTile(row, column);
            }
            else {
                break;
            }
        }

        // Remove the placed tiles from the boardGrid
        ObservableList<Node> children = boardGrid.getChildren();
        ArrayList<Node> tilesToRemove = new ArrayList<>();
        for (Node child : children) {
            if (child instanceof Label) {
                int row = GridPane.getRowIndex(child);
                int column = GridPane.getColumnIndex(child);

                // Check if the tile was placed during the current turn
                if (isTilePlacedDuringTurn(row, column)) {
                    tilesToRemove.add(child);
                }
            }
        }
        for (Node tile : tilesToRemove) {
            boardGrid.getChildren().remove(tile);
        }

        resetPositionsArray();
        usedButtons.clear();
        clickedButton = null;
        enableButtons();
    }

    public boolean isTilePlacedDuringTurn(int row, int column) {
        // Check if the tile position matches any of the positions stored in the positions array
        for (Pair<Integer, Integer> position : positions) {
            if (position != null && position.getKey() == row && position.getValue() == column) {
                return true;
            }
        }
        return false;
    }

    public void EndTurnButtonClick(ActionEvent actionEvent) {

        // check if the player didn't put tiles and then pressed end turn
        resetPositionsArray();
        usedButtons.clear();
        clickedButton = null;
        selectedTile = null;
        enableButtons();

        currentTiles.clear();
        for (int i = 0; i < 8; i++)
            currentTiles.add(Tile.Bag.getBagModel().getRand());

        ObservableList<Node> children = tilesContainer.getChildren();

        int tileIndex = 0;
        for (Node child : children) {
            if (child instanceof Button) {
                Button button = (Button) child;
                Tile tile = currentTiles.get(tileIndex);

                button.setText(String.valueOf(tile.letter));

                tileIndex++;
                if (tileIndex >= currentTiles.size()) {
                    break;
                }
            }
        }
    }

    public void EndGameButtonClick(ActionEvent actionEvent) {

        //call resetPositionsArray();
        System.out.println("End Game Clicked");

    }

    public void TryPlaceWordButtonClick(ActionEvent actionEvent) {

        //call resetPositionsArray();
        System.out.println("Try Place Word Clicked");
    }

    public void resetPositionsArray() {

        // Clear the array by setting elements to null
        Arrays.fill(positions, null);
        positionsIndex = 0;
    }

    public void enableButtons() {

        blockingTiles = false;
        for (Node child : tilesContainer.getChildren()) {
            if (child instanceof Button && !child.equals(clickedButton) && !usedButtons.contains(child)) {
                Button button = (Button) child;
                button.setDisable(false);
            }
        }
    }

    public void disableButtons() {

        blockingTiles = true;
        for (Node child : tilesContainer.getChildren()) {
            if (child instanceof Button && !child.equals(clickedButton)) {
                Button button = (Button) child;
                button.setDisable(true);
            }
        }
    }
}