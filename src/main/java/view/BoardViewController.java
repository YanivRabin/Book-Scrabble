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
import model.data.Word;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class BoardViewController implements Initializable {

    Board gameBoard;

    ArrayList<Tile> currentTiles;       // the tiles in the hand
    ArrayList<Button> usedButtons;      // the tiles button that used during turn

    Pair<Integer, Integer>[] positions; // the used tiles positions
    int positionsIndex;                 // an index to put in the position array, also for checking

    Tile selectedTile;    // the tile that selected
    Button clickedButton; // the tile button that selected

    boolean blockingTiles; // if selected tile then block other buttons

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

        generateHand(8);
    }

    public void generateHand(int num) {

        for (int i = 0; i < num; i++)
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

    public void resetTilesButtonClick() {

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

        // reset other things
        resetPositionsArray();
        usedButtons.clear();
        clickedButton = null;
        selectedTile = null;
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



    public void TryPlaceWordButtonClick() {

        ArrayList<Tile> tilesForWord = new ArrayList<>();
        Tile[][] gameBoardTiles = gameBoard.getTiles();

        int startRow = 0;
        int startCol = 0;
        boolean vertical = false;
        boolean oneTileCheck = false;

        // if only placed one tile to continue word from other tiles
        if (positionsIndex == 1) {

            startRow = positions[0].getKey();
            startCol = positions[0].getValue();

            // check vertical
            if (gameBoardTiles[startRow - 1][startCol] != null || gameBoardTiles[startRow + 1][startCol] != null) {

                vertical = true;
                oneTileCheck = true;
            }

            // check not vertical
            if (gameBoardTiles[startRow][startCol - 1] != null || gameBoardTiles[startRow][startCol + 1] != null) {

                vertical = false;
                oneTileCheck = true;
            }

            if (!oneTileCheck) {

                System.out.println("not legal");
                resetTilesButtonClick();
                return;
            }
        }

        if (positionsIndex > 1 || oneTileCheck) {

            if (!oneTileCheck) {

                // checking to see if all is on the same row/col and find the first tile location the player put
                // not vertical ( from left to right )
                if (positions[0].getKey().intValue() == positions[1].getKey().intValue()) {

                    System.out.println("not vertical");
                    vertical = false;
                    startRow = positions[0].getKey();
                    startCol = positions[0].getValue();

                    for (int i = 0; i < positionsIndex - 1; i++) {

                        if (positions[i].getKey().intValue() != positions[i + 1].getKey().intValue()) {
                            System.out.println("Word placed incorrect");
                            // not really need action event ( maybe later in the code )
                            resetTilesButtonClick();
                            return;
                        }
                        else {
                            if (startCol > positions[i + 1].getValue()) {
                                startCol = positions[i + 1].getValue();
                            }
                        }
                    }
                }
                // else vertical
                else {

                    System.out.println("vertical");
                    vertical = true;
                    startRow = positions[0].getKey();
                    startCol = positions[0].getValue();

                    for (int i = 0; i < positionsIndex - 1; i++) {

                        if (positions[i].getValue().intValue() != positions[i + 1].getValue().intValue()) {
                            System.out.println("Word placed incorrect");
                            // not really need action event ( maybe later in the code )
                            resetTilesButtonClick();
                            return;
                        }
                        else {
                            if (startRow > positions[i + 1].getKey()) {
                                startRow = positions[i + 1].getKey();
                            }
                        }
                    }
                }
            }

            // check if there is null tiles that are still part of the word
            // and place tiles in word by order
            int i;
            if (vertical) {

                // checking if there are tiles before
                i = 1;
                while ((startRow - i) >= 0) {

                    if (gameBoardTiles[startRow - i][startCol] != null) {

                        // set the new start row
                        startRow--;
                    }
                    else { break; }
                }

                // add the first tile to the array
                tilesForWord.add(gameBoardTiles[startRow][startCol]);

                // checking if there are tiles after start to add to array
                while ((startRow + i) <= 14) {

                    if (gameBoardTiles[startRow + i][startCol] != null) {

                        tilesForWord.add(gameBoardTiles[startRow + i][startCol]);
                        i++;
                    }
                    else { break; }
                }
            }
            //not vertical
            else {

                // checking if there are tiles before
                i = 1;
                while ((startCol - i) >= 0) {

                    if (gameBoardTiles[startRow][startCol - i] != null) {

                        // set the new start row
                        startCol--;
                    }
                    else { break; }
                }

                // add the first tile to the array
                tilesForWord.add(gameBoardTiles[startRow][startCol]);

                // checking if there are tiles after start to add to array
                while ((startCol + i) <= 14) {

                    if (gameBoardTiles[startRow][startCol + i] != null) {

                        tilesForWord.add(gameBoardTiles[startRow][startCol + i]);
                        i++;
                    }
                    else { break; }
                }
            }
        }
        else {

            System.out.println("Word must contain 2 tiles or more");
            return;
        }


        Tile[] tilesArray = new Tile[tilesForWord.size()];
        for (int i = 0; i < tilesArray.length; i++) {
            tilesArray[i] = tilesForWord.get(i);
        }

        // build word from tiles
        Word word = new Word(tilesArray, startRow, startCol, vertical);
        System.out.println("Word: " + word + ", At: [" + word.getRow() + "," + word.getCol() + "], Vertical: " + word.isVertical());
        // if word legal pass turn else call reset button
        int wordScore = gameBoard.tryPlaceWord(word);
        if (wordScore > 0) {
            System.out.println("Score: " + wordScore);
            successPlaceWord(word);
            // update the score in the gui
            score.setText(String.valueOf(Integer.parseInt(score.getText()) + wordScore));
        }
        else {

            System.out.println("Word not legal");
            resetTilesButtonClick();
        }
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

    public void successPlaceWord(Word word) {

        // remove the used tiles
        for (Tile tile : word.getTiles()) {

            currentTiles.remove(tile);
        }
        generateHand(8 - currentTiles.size());
        resetPositionsArray();
        usedButtons.clear();
        clickedButton = null;
        enableButtons();
    }

    public void EndTurnButtonClick() {

        // check if the player didn't put tiles and then pressed end turn
        resetTilesButtonClick();

        // implement function to pass turn to the next player
        // if your turn then enable tiles
        // if not your turn then disable tiles


        // replace all the tiles ( only for testing with one player )
        currentTiles.clear();
        generateHand(8);
    }

    public void EndGameButtonClick() {

        //call resetPositionsArray();
        System.out.println("End Game Clicked");
    }
}