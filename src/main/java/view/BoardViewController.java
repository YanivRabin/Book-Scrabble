package view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import model.data.Tile;
import model.data.Word;
import viewModel.VM_Guest;
import viewModel.VM_Host;
import viewModel.ViewModel;
import java.net.URL;
import java.util.*;

public class BoardViewController implements Initializable, Observer {

    ViewModel viewModel;

    Tile[][] gameBoard;

    ArrayList<Tile> currentTiles;  // the tiles in the hand
    ArrayList<Button> usedButtons; // the tiles button that used during turn

    Pair<Integer, Integer>[] positions; // the used tiles positions
    int positionsIndex;                 // an index to put in the position array, also for checking

    Tile selectedTile;    // the tile that selected
    Button clickedButton; // the tile button that selected

    boolean blockingTiles; // if selected tile then block other buttons

    @FXML
    private GridPane boardGrid;

    @FXML
    private Text score, message;

    @FXML
    private AnchorPane tilesContainer, anchorPane;

    @FXML
    private Button EndTurn, resetWord, TryPlaceWord;

    public void setViewModel(ViewModel vm) {

        viewModel = vm;
        viewModel.getObservable().addObserver(this); // board controller observe viewModel

        System.out.println("Player: " + vm.getName());
        viewModel.startGame();

        // bind the score text to the score property
        score.textProperty().bind(viewModel.scoreProperty().asString());

        // get the game board
        gameBoard = viewModel.getBoard();

        // init tile for hand
        currentTiles = new ArrayList<>();
        currentTiles = viewModel.getCurrentTiles();
        updateHand();

        // enable all buttons
        blockingTiles = false;
        usedButtons = new ArrayList<>();

        // create positions array for later
        positions = new Pair[8];
        positionsIndex = 0;

        if (viewModel instanceof VM_Guest) {

            message.setText("");

            // disable all
            disableButtons();
            TryPlaceWord.setDisable(true);
            resetWord.setDisable(true);
            EndTurn.setDisable(true);
        }
        else {
            message.setText("First word must be placed on the purple square");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void updateHand() {

        ObservableList<Node> children = tilesContainer.getChildren();

        int tileIndex = 0;
        for (Node child : children) {
            if (child instanceof Button button) {
                Tile tile = currentTiles.get(tileIndex);

                button.setText(String.valueOf(tile.letter));

                tileIndex++;
                if (tileIndex >= currentTiles.size()) {
                    break;
                }
            }
        }
    }

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

    @FXML
    public void handlePaneClick(MouseEvent event) {

        Pane clickedPane = (Pane) event.getSource();

        Integer rowIndex = GridPane.getRowIndex(clickedPane);
        Integer columnIndex = GridPane.getColumnIndex(clickedPane);

        // Perform actions based on the row and column
        int row = rowIndex;
        int column = columnIndex;
        System.out.println("Clicked pane at row: " + row + ", column: " + column);

        // check to see if a tile was selected
        if (blockingTiles && selectedTile != null) {

            // check if there is no tile
            if (gameBoard[row][column] == null) {

                // place a tile on board
                gameBoard[row][column] = selectedTile;
//                viewModel.placeTile(selectedTile, row, column);

                // place only letter
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
                    if (child instanceof Button button && child.equals(clickedButton)) {
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
                gameBoard[row][column] = null;
//                viewModel.removeTile(row,column);
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
        int startRow = 0;
        int startCol = 0;
        boolean vertical = false;
        boolean oneTileCheck = false;

        // if only placed one tile to continue word from other tiles
        if (positionsIndex == 1) {

            startRow = positions[0].getKey();
            startCol = positions[0].getValue();

            if (startRow == 14) {
                // check vertical
                if (this.gameBoard[startRow - 1][startCol] != null) {
                    vertical = true;
                    oneTileCheck = true;
                }
            }
            else if (startRow == 0) {
                if (gameBoard[startRow + 1][startCol] != null) {
                    vertical = true;
                    oneTileCheck = true;
                }
            }
            else {
                if (gameBoard[startRow - 1][startCol] != null || gameBoard[startRow + 1][startCol] != null) {
                    vertical = true;
                    oneTileCheck = true;
                }
            }

            if (startCol == 14) {
                if (gameBoard[startRow][startCol - 1] != null) {
                    vertical = false;
                    oneTileCheck = true;
                }
            }
            else if (startCol == 0) {
                if (gameBoard[startRow][startCol + 1] != null) {
                    vertical = false;
                    oneTileCheck = true;
                }
            }
            else {
                // check not vertical
                if (gameBoard[startRow][startCol - 1] != null || gameBoard[startRow][startCol + 1] != null) {
                    vertical = false;
                    oneTileCheck = true;
                }
            }

            if (!oneTileCheck) {

                System.out.println("Word must contain 2 tiles or more");
                message.setText("Word must contain 2 tiles or more");
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
                    startRow = positions[0].getKey();
                    startCol = positions[0].getValue();

                    for (int i = 0; i < positionsIndex - 1; i++) {

                        if (positions[i].getKey().intValue() != positions[i + 1].getKey().intValue()) {
                            System.out.println("Word placed incorrect");
                            message.setText("Word placed incorrect");
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
                            message.setText("Word placed incorrect");
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
            int i = 1;
            if (vertical) {

                // checking if there are tiles before
                while ((startRow - i) >= 0) {

                    if (gameBoard[startRow - i][startCol] != null) {

                        // set the new start row
                        startRow--;
                    }
                    else { break; }
                }

                // add the first tile to the array
                if (isTilePlacedDuringTurn(startRow, startCol)) {

                    tilesForWord.add(gameBoard[startRow][startCol]);
                }
                else {

                    tilesForWord.add(null);
                }

                // checking if there are tiles after start to add to array
                while ((startRow + i) <= 14) {

                    if (gameBoard[startRow + i][startCol] != null) {

                        if (isTilePlacedDuringTurn(startRow + i, startCol)) {

                            tilesForWord.add(gameBoard[startRow + i][startCol]);
                        }
                        else {

                            tilesForWord.add(null);
                        }
                        i++;
                    }
                    else { break; }
                }
            }
            //not vertical
            else {

                // checking if there are tiles before
                while ((startCol - i) >= 0) {

                    if (gameBoard[startRow][startCol - i] != null) {

                        // set the new start row
                        startCol--;
                    }
                    else { break; }
                }

                // add the first tile to the array
                if (isTilePlacedDuringTurn(startRow, startCol)) {

                    tilesForWord.add(gameBoard[startRow][startCol]);
                }
                else {

                    tilesForWord.add(null);
                }

                // checking if there are tiles after start to add to array
                while ((startCol + i) <= 14) {

                    if (gameBoard[startRow][startCol + i] != null) {

                        // add the first tile to the array
                        if (isTilePlacedDuringTurn(startRow, startCol + i)) {

                            tilesForWord.add(gameBoard[startRow][startCol + i]);
                        }
                        else {

                            tilesForWord.add(null);
                        }
                        i++;
                    }
                    else { break; }
                }
            }
        }
        else {

            System.out.println("Word must contain 2 tiles or more");
            message.setText("Word must contain 2 tiles or more");
            return;
        }


        Tile[] tilesArray = new Tile[tilesForWord.size()];
        for (int i = 0; i < tilesArray.length; i++) {
            tilesArray[i] = tilesForWord.get(i);
        }
        System.out.println();

        // build word from tiles
        Word word = new Word(tilesArray, startRow, startCol, vertical);
        System.out.println("Word: " + word + ", At: [" + word.getRow() + "," + word.getCol() + "], Vertical: " + word.isVertical());

        // if word legal pass turn else call reset button
        int wordScore = viewModel.tryPlaceWord(word);
        if (wordScore > 0) {

            // success
            System.out.println("Score: " + wordScore);
            successPlaceWord(word);
            message.setText("");

            // pass turn to next player
            viewModel.passTurn();
        }
        else {

            System.out.println("Word not legal");
            message.setText("Word not legal");
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
            if (child instanceof Button button && !child.equals(clickedButton) && !usedButtons.contains(child)) {
                button.setDisable(false);
            }
        }
    }

    public void disableButtons() {

        blockingTiles = true;
        for (Node child : tilesContainer.getChildren()) {
            if (child instanceof Button button && !child.equals(clickedButton)) {
                button.setDisable(true);
            }
        }
    }

    public void successPlaceWord(Word word) {

        // get the new tiles
        viewModel.updateTiles();
        updateHand();

        // reset all helpers for a new turn
        resetPositionsArray();
        usedButtons.clear();
        clickedButton = null;
        enableButtons();

        // send updated board to everyone
//        viewModel.updateBoard();
    }

    public void EndTurnButtonClick() {

        // check if the player didn't put tiles and then pressed end turn
        resetTilesButtonClick();
        viewModel.passTurn();
    }

    public void EndGameButtonClick() {

        //call resetPositionsArray();
        System.out.println("End Game Clicked");
    }

    @Override
    public void update(Observable o, Object arg) {

        if (o instanceof ViewModel vm) {

            if (arg.equals("pass turn")) {
                System.out.println("board observer update: pass turn");
                // update player turn for each player
                vm.updatePlayerTurn();
                // Check if it's this player's turn
                if (vm.getCurrentPlayer() == viewModel.getMyTurn()) {
                    // enable all
                    message.setText("Your turn!");
                    enableButtons();
                    TryPlaceWord.setDisable(false);
                    resetWord.setDisable(false);
                    EndTurn.setDisable(false);
                }
                else {
                    // disable all
                    message.setText("");
                    disableButtons();
                    TryPlaceWord.setDisable(true);
                    resetWord.setDisable(true);
                    EndTurn.setDisable(true);
                }
            }

            if (arg.equals("update board")) {
                System.out.println("board observer update: update board");
                gameBoard = viewModel.getBoard();
                for (int row = 0; row < 15; row++) {
                    for (int col = 0; col < 15; col++) {
                        if (gameBoard[row][col] != null) {
                            // place only letter
                            Label letter = new Label(String.valueOf(gameBoard[row][col].letter));
                            letter.setAlignment(Pos.CENTER);
                            // Set font size, weight and color of the letter
                            letter.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: white;");
                            // Set layout constraints to center the label within the pane
                            letter.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                            // adding the letter to the board
                            final int finalRow = row;
                            final int finalCol = col;
                            Platform.runLater(() -> {
                                if (!boardGrid.getChildren().contains(letter)) {
                                    boardGrid.add(letter, finalCol, finalRow);
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}