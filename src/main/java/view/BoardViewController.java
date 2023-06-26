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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BoardViewController implements Initializable, Observer {

    ViewModel viewModel; // VM_Host or VM_Guest
    Tile[][] gameBoard; // the player current board
    ArrayList<Tile> currentTiles;  // the tiles in the hand
    ArrayList<Button> usedButtons; // the tiles button that used during turn
    Pair<Integer, Integer>[] positions; // the used tiles positions
    int positionsIndex; // an index to put in the position array, also for checking
    Tile selectedTile; // the tile that selected
    Button clickedButton; // the tile button that selected
    boolean blockingTiles; // if selected tile then block other buttons
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1); // thread pool for challenge
    @FXML
    private GridPane boardGrid;
    @FXML
    private Text score, message;
    @FXML
    private AnchorPane tilesContainer;
    @FXML
    private Button EndTurn, resetWord, TryPlaceWord, challenge, EndGame;

    /**
     * The setViewModel function is used to set the viewModel for this controller.
     *
     *
     * @param  vm Set the viewmodel variable to the parameter
    public void updatehand() {


     *
     * @return Void
     *
     * @docauthor Trelent
     */
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

        // disable challenge for first turn
        challenge.setDisable(true);
    }

    /**
     * The initialize function is called when the FXML file is loaded.
     * It sets up the buttons and text fields to be used in this class.

     *
     * @param  location Specify the location of the fxml file
     * @param  resources Load the resources for the application
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * The updateHand function updates the hand of tiles displayed on the screen.
     * It does this by iterating through all of the children in a container, and if they are buttons, it sets their text to be equal to that tile's letter.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
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

    /**
     * The handleTileButtonClick function is called when a tile button is clicked.
     * It sets the selectedTile variable to the Tile object corresponding to the clicked button,
     * and disables all buttons if blockingTiles is true. If blockingTiles is false, it enables all buttons.

     *
     * @param  event Get the source of the event

     *
     * @return A tile object
     *
     * @docauthor Trelent
     */
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

    /**
     * The handlePaneClick function is called when a pane on the boardGrid is clicked.
     * It determines which pane was clicked and performs actions based on that information.
     *
     *
     * @param MouseEvent event Get the source of the event
     *
     * @return A void
     *
     * @docauthor Trelent
     */
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
                letter.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: white;");
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

    /**
     * The resetTilesButtonClick function is called when the user clicks on the reset tiles button.
     * It removes all of the tiles that were placed during this turn from both the gameBoard and boardGrid,
     * resets other variables to their original values, and enables all of the buttons in rackGrid.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void resetTilesButtonClick() {

        // remove the tiles from original board
        for (Pair<Integer, Integer> pair: positions) {

            if (pair != null) {

                int row = pair.getKey();
                int column = pair.getValue();
                gameBoard[row][column] = null;
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

    /**
     * The isTilePlacedDuringTurn function checks if a tile has been placed during the turn.
     *
     *
     * @param  row Check if the row of the tile matches any of the rows stored in positions array

    public boolean istileplacedduringturn(int row, int column) {


     * @param  column Check if the column is valid
    public boolean isvalidcolumn(int column) {


     *
     * @return A boolean value that is true if the tile was placed during the turn and false otherwise
     *
     * @docauthor Trelent
     */
    public boolean isTilePlacedDuringTurn(int row, int column) {

        // Check if the tile position matches any of the positions stored in the positions array
        for (Pair<Integer, Integer> position : positions) {
            if (position != null && position.getKey() == row && position.getValue() == column) {
                return true;
            }
        }
        return false;
    }

    /**
     * The TryPlaceWordButtonClick function is called when the user clicks on the &quot;Try Place Word&quot; button.
     * It checks if all of the tiles that were placed during this turn are in a legal word, and if so, it adds them to
     * the game board and passes turn to next player. If not, it resets all of those tiles back into their original positions.

     *
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
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
                    else {
                        break;
                    }
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
            successPlaceWord(word);
            // pass turn to next player
            viewModel.passTurn();
        }
        else {
            message.setText("Word not legal");
            resetTilesButtonClick();
        }
    }

    /**
     * The resetPositionsArray function clears the positions array by setting all elements to null.
     * It also resets the index of the positions array to 0.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void resetPositionsArray() {

        // Clear the array by setting elements to null
        Arrays.fill(positions, null);
        positionsIndex = 0;
    }

    /**
     * The enableButtons function is used to enable all the buttons in the tilesContainer.
     * This function is called when a button has been clicked and it's time for another button to be clicked.

     *
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public void enableButtons() {

        blockingTiles = false;
        for (Node child : tilesContainer.getChildren()) {
            if (child instanceof Button button && !child.equals(clickedButton) && !usedButtons.contains(child)) {
                button.setDisable(false);
            }
        }
    }

    /**
     * The disableButtons function disables all the buttons in the tilesContainer except for
     * the button that was clicked. This is done so that a user cannot click on multiple buttons
     * at once, which would cause an error. The disableButtons function also sets blockingTiles to true,
     * which prevents any other functions from being called while this function is running.

     *
     *
     * @return Void, which means that it does not return anything
     *
     * @docauthor Trelent
     */
    public void disableButtons() {

        blockingTiles = true;
        for (Node child : tilesContainer.getChildren()) {
            if (child instanceof Button button && !child.equals(clickedButton)) {
                button.setDisable(true);
            }
        }
    }

    /**
     * The successPlaceWord function is called when the user successfully places a word on the board.
     * It updates the tiles and hand, resets all helper variables for a new turn, and enables all buttons.

     *
     * @param  word Get the tiles that were used to place a word
    public void updatehand() {


     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void successPlaceWord(Word word) {

        // get the new tiles
        viewModel.updateTiles();
        updateHand();

        // reset all helpers for a new turn
        resetPositionsArray();
        usedButtons.clear();
        clickedButton = null;
        enableButtons();
    }

    /**
     * The EndTurnButtonClick function is called when the player clicks on the End Turn button.
     * It calls resetTilesButtonClick to make sure that all tiles are in their proper place, and then it passes turn control to the next player.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void EndTurnButtonClick() {

        // check if the player didn't put tiles and then pressed end turn
        resetTilesButtonClick();
        viewModel.passTurn();
    }

    /**
     * The EndGameButtonClick function is called when the user clicks on the End Game button.
     * It calls a function in the viewModel that ends the game and returns to main menu.

     *
     *
     * @return A void
     *
     * @docauthor Trelent
     */
    public void EndGameButtonClick() {

        viewModel.endGame();
    }

    /**
     * The challengeButtonClick function is called when the challenge button is clicked.
     * It calls the viewModel's challenge function, which will send a request to the server for a new game.

     *
     *
     * @return A void
     *
     * @docauthor Trelent
     */
    @FXML
    public void challengeButtonClick() {

        viewModel.challenge();
    }

    /**
     * The update function is called whenever the ViewModel notifies its observers.
     * The update function checks what type of notification it received and updates the board accordingly.

     *
     * @param  o Determine which observable object called the update function

     * @param  arg Pass the message from the observable to its observers
     *
     * @return A string with the following format:
     *
     * @docauthor Trelent
     */
    @Override
    public void update(Observable o, Object arg) {

        if (o instanceof ViewModel) {

            String[] observermessage = arg.toString().split(",");

            if (arg.equals("pass turn")) {
                executor.shutdownNow();  // Try to stop currently running tasks
                executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);  // Recreate the executor
                System.out.println("board observer update: pass turn");
                // update player turn for each player
                viewModel.updatePlayerTurn();
                // disable all
                message.setText("You have 5 seconds to try challenge");
                disableButtons();
                TryPlaceWord.setDisable(true);
                resetWord.setDisable(true);
                EndTurn.setDisable(true);
                challenge.setDisable(false);
                executor.submit(() -> {
                    try {
                        Thread.sleep(50000);
                        challenge.setDisable(true);
                        viewModel.updatePrev();
                        // Check if it's this player's turn
                        if (viewModel.getCurrentPlayer() == viewModel.getMyTurn()) {
                            // enable all
                            message.setText("Your turn!");
                            enableButtons();
                            TryPlaceWord.setDisable(false);
                            resetWord.setDisable(false);
                            EndTurn.setDisable(false);
                        }
                        else {
                            message.setText("");
                        }
                    }
                    catch (InterruptedException e) { System.out.println("Thread pool interrupted"); }
                });
            }

            if (arg.equals("challenge alive")) {
                System.out.println("board observer update: challenge alive");
                executor.shutdownNow();
                executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
                message.setText("Someone clicked challenge");
                // reset all player options and disable buttons
                resetTilesButtonClick();
                disableButtons();
                TryPlaceWord.setDisable(true);
                resetWord.setDisable(true);
                EndTurn.setDisable(true);
                challenge.setDisable(true);
            }

            if (arg.equals("challenge fail")) {
                System.out.println("board observer update: challenge fail");
                viewModel.updatePrev();
                // Check if it's this player's turn
                if (viewModel.getCurrentPlayer() == viewModel.getMyTurn()) {
                    // enable all
                    message.setText("Challenge failed, its your turn!");
                    enableButtons();
                    TryPlaceWord.setDisable(false);
                    resetWord.setDisable(false);
                    EndTurn.setDisable(false);
                }
                else {
                    message.setText("Challenge failed, continue play");
                }
            }

            if (arg.equals("update board")) {
                System.out.println("board observer update: update board");
                // Clear existing labels from the grid
                Platform.runLater(() ->  boardGrid.getChildren().removeIf(node -> node instanceof Label));
                // update board
                viewModel.updateBoard();
                // set the current board
                gameBoard = viewModel.getBoard();
                // change the board grid to the new game board
                for (int row = 0; row < 15; row++) {
                    for (int col = 0; col < 15; col++) {

                        if (gameBoard[row][col] != null) {
                            // place only letter
                            Label letter = new Label(String.valueOf(gameBoard[row][col].letter));
                            letter.setAlignment(Pos.CENTER);
                            // Set font size, weight and color of the letter
                            letter.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: white;");
                            // Set layout constraints to center the label within the pane
                            letter.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                            // adding the letter to the board
                            final int finalRow = row;
                            final int finalCol = col;
                            Platform.runLater(() -> boardGrid.add(letter, finalCol, finalRow));
                        }
                    }
                }
                // Check if it's this player's turn
                if (viewModel.getCurrentPlayer() == viewModel.getMyTurn()) {
                    // enable all
                    message.setText("Your turn!");
                    enableButtons();
                    TryPlaceWord.setDisable(false);
                    resetWord.setDisable(false);
                    EndTurn.setDisable(false);
                }
                else {
                    message.setText("");
                }
            }

            if (arg.equals("update score")) {
                System.out.println("board observer update: update score");
                viewModel.updateScore();
            }

            if (observermessage[0].equals("end game")) {
                System.out.println("board observer update: end game");
                message.setText("Game ended, winner is: " + observermessage[1]);
                disableButtons();
                TryPlaceWord.setDisable(true);
                resetWord.setDisable(true);
                EndTurn.setDisable(true);
                challenge.setDisable(true);
                EndGame.setDisable(true);
            }

//            if (arg.equals("challenge success")) {
//                System.out.println("guest viewModel observer update: challenge success");
//                // Check if it's this player's turn
//                if (viewModel.getCurrentPlayer() == viewModel.getMyTurn()) {
//                    // enable all
//                    message.setText("Challenge succeed, your turn!");
//                    enableButtons();
//                    TryPlaceWord.setDisable(false);
//                    resetWord.setDisable(false);
//                    EndTurn.setDisable(false);
//                }
//                else {
//                    message.setText("Challenge succeed");
//                }
//            }
        }
    }
}