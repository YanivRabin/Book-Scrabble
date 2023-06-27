package viewModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import model.data.Board;
import model.data.Tile;
import model.data.Word;
import model.logic.Guest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class VM_Guest extends Observable implements ViewModel, Observer {

    Guest guest;
    Tile[][] gameBoard;
    Tile.Bag gameBag;
    String name, hostNickName;
    ArrayList<Tile> currentTiles;
    int playerTurn, myTurn, players;
    private boolean isGameStart;
    private final Object gameStartMonitor = new Object();
    private final IntegerProperty scoreProperty;

    // constructor
    /**
     * The VM_Guest function creates a new guest with the name given in the parameter.
     * It also sets up an observer for this guest, and initializes some variables.

     *
     * @param  text Set the name of the guest
     *
     * @return The scoreproperty
     *
     * @docauthor Trelent
     */
    public VM_Guest(String text) {

        // create guest
        name = text;
        guest = new Guest(name);
        guest.addObserver(this); // vm_guest ( this ) observe Guest

        // init
        currentTiles = new ArrayList<>();
        scoreProperty = new SimpleIntegerProperty();
        isGameStart = false;

        try { Thread.sleep(500); }
        catch (InterruptedException e) { e.printStackTrace(); }

        // set score to 0
        scoreProperty.set(0);
    }

    // property
    /**
     * The scoreProperty function returns the scoreProperty of the player.
     *
     *
     *
     * @return The scoreproperty object
     *
     * @docauthor Trelent
     */
    @Override
    public IntegerProperty scoreProperty() {
        return scoreProperty;
    }

    // game functions
    /**
     * The connectToServer function is used to connect the client to a server.
     *
     *
     * @param  ip Connect to the server
     * @param  port Specify the port number to connect to
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
    public boolean connectToServer(String ip, int port) {

        // if connected return true
        try {

            guest.CreateSocketToHost(ip, port);

            try { Thread.sleep(500); }
            catch (InterruptedException e) { e.printStackTrace(); }

            return true;
        }
        // if didn't manage to connect return false
        catch (IOException e) {

            e.printStackTrace();
            return false;
        }
    }
    /**
     * The isGameStart function is a synchronized function that waits for the game to start.
     *
     *
     *
     * @return True if the game is started
     *
     * @docauthor Trelent
     */
    public synchronized boolean isGameStart() {

        synchronized (gameStartMonitor) {
            while (!isGameStart) {
                try {
                    gameStartMonitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    /**
     * The startGame function is called when the game begins. It sets up the board, bag, and turn variables
     * for use in other functions.

     *
     *
     * @return The gameboard, which is an array of tiles
     *
     * @docauthor Trelent
     */
    @Override
    public void startGame() {

        gameBoard = guest.player.getCurrentBoardAsTiles();
        gameBag = Tile.Bag.getBag();
        // make turn for later to pass between players
        myTurn = guest.player.getPlayerIndex();
        players = guest.player.getNumOfPlayersInGame();
        updateTiles();
    }
    /**
     * The tryPlaceWord function is responsible for placing a word on the board.
     * It will check if the word can be placed in that location, and if it can,
     * it will place it there. If not, then nothing happens. The function returns
     * an integer representing how many points were scored by placing this word on the board.

     *
     * @param  word Get the word that was placed on the board
     *
     * @return The score of the word
     *
     * @docauthor Trelent
     */
    @Override
    public int tryPlaceWord(Word word) {

        // get the current score
        int scoreBefore = scoreProperty.get();

        // try place word func
        guest.SendTryPlaceWordMessage(guest.NickName, hostNickName, word.toString(), word.getRow(), word.getCol(), word.isVertical());
        try { Thread.sleep(2000); }
        catch (InterruptedException e) { e.printStackTrace(); }
        updateScore();

        // if the currentScore - prevScore is 0, its mean the word received 0 points
        if (scoreBefore != 0) {
            return scoreProperty.get() - scoreBefore;
        }
        // if its this first turn send back the current score
        else {
            return scoreProperty.get();
        }
    }
    /**
     * The passTurn function sends a message to the server that the player has passed their turn.

     *
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
    @Override
    public void passTurn() {

        guest.sendPassTurnMessage();
    }
    /**
     * The updateTiles function is used to update the tiles that are currently in the player's hand.
     * It does this by first clearing out any previous tiles, then converting the char array of currentTiles
     * into a Tile object array and adding it to currentTiles. This allows us to use our tile objects for other functions later on.

     *
     *
     * @return A list of tile objects
     *
     * @docauthor Trelent
     */
    @Override
    public void updateTiles() {

        currentTiles.clear();
        // get the player tiles and convert them from char to tile object
        char[] tiles = guest.player.getCurrentTiles().toCharArray();
        for (char tile: tiles) {
            currentTiles.add(gameBag.getTileForTileArray(tile));
        }
    }
    /**
     * The updatePlayerTurn function is used to update the playerTurn variable.
     * The function adds 1 to the current value of playerTurn, and then uses modulo division by players (the number of players)
     * in order to ensure that the new value for playerTurn will be between 0 and (players - 1).

     *
     *
     * @return The next player's turn
     *
     * @docauthor Trelent
     */
    @Override
    public void updatePlayerTurn() {
        playerTurn = (playerTurn + 1) % players;
        System.out.println("Turn: " + playerTurn);
    }
    /**
     * The challenge function is used to send the game board to the guest player.
     * It does this by creating a string builder and then iterating through each tile in the gameBoard array,
     * appending it's letter value (or a '.' if it is null) to the string builder. After each row of tiles has been added,
     * a new line character is added so that when printed out on screen, they will be displayed as separate rows. The resulting StringBuilder object
     * is then sent via SendChallengeMessage() function from Player class which sends an appropriate message type with this information attached.


     *
     *
     * @return The game board in the form of a string
     *
     * @docauthor Trelent
     */
    @Override
    public void challenge() {

        StringBuilder sb = new StringBuilder();

        int rows = gameBoard.length;
        int cols = gameBoard[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile tile = gameBoard[i][j];
                sb.append(tile != null ? tile.letter : '.');
            }
            sb.append('\n');
        }

        guest.SendChallengeMessage(sb.toString());
    }
    /**
     * The updatePrev function is used to update the previous state of a cell.
     * This function is not used in this simulation, so it does nothing.

     *
     *
     * @return The value of the previous node
     *
     * @docauthor Trelent
     */
    @Override
    public void updatePrev() {}
    /**
     * The updateScore function updates the score of the player.

     *
     *
     * @return The scoreproperty
     *
     * @docauthor Trelent
     */
    @Override
    public void updateScore() {

        scoreProperty.set(guest.player.getCurrentScore());
    }
    /**
     * The updateBoard function is called by the GameController class when a player makes a move.
     * It updates the gameBoard variable to reflect the current state of the board, and then calls
     * updateView() to update all of its views.

     *
     *
     * @return The current board as a list of tiles
     *
     * @docauthor Trelent
     */
    @Override
    public void updateBoard() {

        gameBoard = guest.player.getCurrentBoardAsTiles();
    }
    /**
     * The endGame function is called when the game ends.
     * It sends a message to the guest player that the game has ended.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    @Override
    public void endGame() {
        guest.sendEndGame();
    }

    @Override
    public void generateNewTiles() {

        guest.sendNewTiles();
        try {Thread.sleep(1000);}
        catch (InterruptedException e) {e.printStackTrace();}
        updateTiles();
        setChanged();
        notifyObservers("update tiles");
    }

    @Override
    public void newTiles() {

        updateTiles();
        setChanged();
        notifyObservers("update tiles");
    }

    // getters
    /**
     * The getBoard function returns the gameBoard array.
     *
     *
     *
     * @return The gameboard array
     *
     * @docauthor Trelent
     */
    @Override
    public Tile[][] getBoard() {

        return gameBoard;
    }
    /**
     * The getCurrentTiles function returns the currentTiles arraylist.
     *
     *
     *
     * @return An arraylist of the current tiles
     *
     * @docauthor Trelent
     */
    @Override
    public ArrayList<Tile> getCurrentTiles() {
        return currentTiles;
    }
    /**
     * The getName function returns the name of the person.
     *
     *
     *
     * @return The name of the person
     *
     * @docauthor Trelent
     */
    @Override
    public String getName() { return name; }
    /**
     * The getCurrentPlayer function returns the current player's turn.
     *
     *
     *
     * @return The current player
     *
     * @docauthor Trelent
     */
    @Override
    public int getCurrentPlayer() {
        return playerTurn;
    }
    /**
     * The getMyTurn function returns the value of myTurn.
     *
     *
     *
     * @return The integer value of the myturn variable
     *
     * @docauthor Trelent
     */
    @Override
    public int getMyTurn() {
        return myTurn;
    }
    /**
     * The getObservable function is a function that returns an Observable.
     * This function is used to get the observable from the class, so that it can be subscribed to.
     *
     *
     *
     * @return An observable object, which is the interface that defines a class as an observable
     *
     * @docauthor Trelent
     */
    @Override
    public Observable getObservable() {
        return this;
    }

    // update
    /**
     * The update function is called when the guest receives a message from the host.
     * The update function will then notify all observers of this viewModel that there has been an update, and what type of update it was.
     *
     *
     * @param  o Determine which observable is calling the update function
     * @param  arg Pass the message from the server to the client
     *
     * @return A string, which is the message sent from server
     *
     * @docauthor Trelent
     */
    @Override
    public void update(Observable o, Object arg) {

        // "start game," + host.getNickName()
        String[] message = arg.toString().split(",");
        if (message[0].equals("start game")) {
            System.out.println("guest viewModel observer update: start game");
            isGameStart = true;
            // init hostNickName and my turn
            hostNickName = message[1];
            // get out of wait()
            synchronized (gameStartMonitor) { gameStartMonitor.notifyAll(); }
        }

        if (arg.equals("update board")) {
            System.out.println("guest viewModel observer update: update board");
            gameBoard = guest.player.getCurrentBoardAsTiles();
            setChanged();
            notifyObservers("update board");
        }

        if (arg.equals("pass turn")) {
            System.out.println("guest viewModel observer update: pass turn");
            setChanged();
            notifyObservers("pass turn");
        }

        if (arg.equals("challenge fail")) {
            System.out.println("guest viewModel observer update: challenge fail");
            setChanged();
            notifyObservers("challenge fail");
        }

        if (arg.equals("challenge alive")) {
            System.out.println("guest viewModel observer update: challenge alive");
            setChanged();
            notifyObservers("challenge alive");
        }

        if (arg.equals("challenge success")) {
            System.out.println("guest viewModel observer update: challenge success");
            setChanged();
            notifyObservers("challenge success");
        }

        if (arg.equals("update score")) {
            System.out.println("guest viewModel observer update: update score");
            setChanged();
            notifyObservers("update score");
        }

//        if (message[0].equals("new player joined")){
//            System.out.println("guest viewModel observer update: new player joined");
////            playersScore.put(message[1], 0);
//            NameToScore = guest.getNameToScore();
//            setChanged();
//            notifyObservers("new player joined");
//        }
        if (message[0].equals("end game")) {
            System.out.println("guest viewModel observer update: end game");
            setChanged();
            notifyObservers("end game," + message[1]);

        }

        if (arg.equals("new tiles")) {
            System.out.println("guest viewModel observer update: update map");
            newTiles();
        }
    }
}