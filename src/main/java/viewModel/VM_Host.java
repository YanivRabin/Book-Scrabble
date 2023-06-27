package viewModel;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import model.data.Tile;
import model.data.Word;
import model.logic.BookScrabbleHandler;
import model.logic.Host;
import model.logic.MyServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class VM_Host extends Observable implements ViewModel, Observer {

    Host host;
    Tile[][] gameBoard;
    Tile.Bag gameBag;
    String ip, name;
    ArrayList<Tile> currentTiles;
    int port, players, playerTurn, myTurn;
    private final IntegerProperty scoreProperty;
    private final IntegerProperty playersProperty;
    Map<String, Integer> NameToScore;

    // constructor
    /**
     * The VM_Host function is the constructor for the VM_Host class.
     * It creates a new Host object and connects it to the main server,
     * then adds observers to both itself and its hostPlayer.

     *
     * @param  n Set the name of the host
     *
     * @return The ip and port of the server
     *
     * @docauthor Trelent
     */
    public VM_Host(String n) throws IOException {

        // init
        name = n;
        currentTiles = new ArrayList<>();
        scoreProperty = new SimpleIntegerProperty();
        playersProperty = new SimpleIntegerProperty();

        // create main server
        MyServer gameServer = new MyServer();
        gameServer.initMyServer(1234, new BookScrabbleHandler());
        gameServer.start();

        // create Host and connect him to the main server
        host = Host.getModel();
        host.setNickName(name);
        host.hostPlayer.setNickName(name);
        host.CreateSocketToServer(gameServer);
        host.start();

        // add observers
        host.addObserver(this); // vm_host ( this ) observe Host
        host.hostPlayer.addObserver(this); // vm_host ( this ) observe Guest

        try { Thread.sleep(500); }
        catch (InterruptedException e) { e.printStackTrace(); }

        // get ip and port of the host server
        ip = host.getIpAddress();
        port = host.getPort();

        // get the amount of players
        playersProperty.set(host.GuestList.size());

        // make turn for later to pass between players
        playerTurn = 0;
        myTurn = 0;

        // set score to 0
        scoreProperty.set(0);
    }

    // property
    /**
     * The playersProperty function returns the playersProperty object.
     *
     *
     *
     * @return An integerproperty
     *
     * @docauthor Trelent
     */
    public IntegerProperty playersProperty() { return playersProperty; }
    /**
     * The scoreProperty function returns the scoreProperty of the player.
     *
     *
     *
     * @return The scoreproperty instance variable
     *
     * @docauthor Trelent
     */
    @Override
    public IntegerProperty scoreProperty() { return scoreProperty; }

    // game functions
    /**
     * The startGame function is called when the host clicks on the start game button.
     * It sets up a new board and bag, then sends out messages to all of the players
     * telling them that they should start their games.  The host's game starts immediately,
     * but each guest will have to wait for a message from their client before starting.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    @Override
    public void startGame() {

        // set the amount of players
        players = playersProperty().getValue();
        // send start message to each guest, each one get 8 tiles
        host.SendStartGameMessage(host.getNickName());
        try { Thread.sleep(1000); }
        catch (InterruptedException e) { e.printStackTrace(); }
        // get board and tiles bag
        gameBoard = host.hostPlayer.player.getCurrentBoardAsTiles();
        gameBag = Tile.Bag.getBagModel();
        updateTiles();
    }
    /**
     * The tryPlaceWord function is used to check if a word can be placed on the board.
     * It does not actually place the word, but rather checks if it can be placed.
     * If it cannot, then an error message will appear and no points will be awarded for that turn.

     *
     * @param  word Place the word on the board
     *
     * @return The score of the word,
     *
     * @docauthor Trelent
     */
    @Override
    public int tryPlaceWord(Word word) {

        // get the current score
        int scoreBefore = scoreProperty.get();
        // try place word func
        host.hostPlayer.SendTryPlaceWordMessage(host.hostPlayer.NickName, host.NickName, word.toString(), word.getRow(), word.getCol(), word.isVertical());
        try { Thread.sleep(2000); }
        catch (InterruptedException e) { e.printStackTrace(); }
        updateScore();

        // if the (currentScore - prevScore == 0), its mean the word received 0 points
        if (scoreBefore != 0) {
            return scoreProperty.get() - scoreBefore;
        }
        // if its this first turn send back the current score
        else {
            return scoreProperty.get();
        }
    }
    /**
     * The passTurn function is used to send a message to the host player that it is their turn.
     *
     *
     *
     * @return A boolean
     *
     * @docauthor Trelent
     */
    @Override
    public void passTurn() {

        host.hostPlayer.sendPassTurnMessage();
    }
    /**
     * The updateTiles function is called when the player's tiles are updated.
     * It clears the currentTiles array, then converts the char array of tiles to a tile object and adds it to currentTiles.

     *
     *
     * @return A list of tiles
     *
     * @docauthor Trelent
     */
    @Override
    public void updateTiles() {

        currentTiles.clear();
        // get the player tiles and convert them from char to tile object
        char[] tiles = host.hostPlayer.player.getCurrentTiles().toCharArray();
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
     * @return The current player's turn
     *
     * @docauthor Trelent
     */
    @Override
    public void updatePlayerTurn() {
        playerTurn = (playerTurn + 1) % players;
        System.out.println("Turn: " + playerTurn);
    }
    /**
     * The challenge function is called when a player challenges the other player's word.
     * It sends a message to the hostPlayer with the current game board, so that they can
     * check if it is valid or not.

     *
     *
     * @return The game board
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

        host.hostPlayer.SendChallengeMessage(sb.toString());
    }
    /**
     * The updatePrev function is called by the host to update the previous state of
     * this object. This function should be overridden in any class that extends
     * StateObject and needs to store a previous state. The default implementation
     * does nothing, so it is not necessary for classes that do not need a previous
     * state to override this function.  For example, if an object has two variables: x and y, then its updatePrev() method might look like: &lt;pre&gt; public void updatePrev() { prevX = x; prevY = y; } &lt;/pre&gt; Note that you must call sendUpdatePrevToCurrent()
     *
     *
     * @return A void
     *
     * @docauthor Trelent
     */
    @Override
    public void updatePrev() {

        host.sendUpdatePrevToCurrent();
    }
    /**
     * The updateScore function updates the score of the player.

     *
     *
     * @return The current score of the player
     *
     * @docauthor Trelent
     */
    @Override
    public void updateScore() {

        scoreProperty.set(host.hostPlayer.player.getCurrentScore());
    }
    /**
     * The updateBoard function is called by the host to update the board in the client.
     * It does this by calling getCurrentBoardAsTiles() on the player object of hostPlayer,
     * which returns a 2D array of tiles representing what is currently on that player's board.

     *
     *
     * @return A tile[][] array
     *
     * @docauthor Trelent
     */
    @Override
    public void updateBoard() {

        gameBoard = host.hostPlayer.player.getCurrentBoardAsTiles();
    }
    /**
     * The endGame function is called when the game ends.
     * It sends a message to the host player, telling them that the game has ended.

     *
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
    @Override
    public void endGame() {
        host.hostPlayer.sendEndGame();
    }

    @Override
    public void generateNewTiles() {

        host.hostPlayer.sendNewTiles();
    }

    @Override
    public void newTiles() {

        updateTiles();
        setChanged();
        notifyObservers("update tiles");
    }

    // getters
    /**
     * The getIp function returns the ip address of the client.
     *
     *
     *
     * @return The ip address of the computer
     *
     * @docauthor Trelent
     */
    public String getIp() { return ip; }
    /**
     * The getPort function returns the port number of the server.
     *
     *
     *
     * @return The port number
     *
     * @docauthor Trelent
     */
    public int getPort() { return port; }
    /**
     * The getBoard function returns the gameBoard variable, which is a 2D array of Tile objects.
     *
     *
     *
     * @return The gameboard variable
     *
     * @docauthor Trelent
     */
    @Override
    public Tile[][] getBoard() { return gameBoard; }
    /**
     * The getCurrentTiles function returns the currentTiles ArrayList.
     *
     *
     *
     * @return The current tiles that are in the game
     *
     * @docauthor Trelent
     */
    @Override
    public ArrayList<Tile> getCurrentTiles() { return currentTiles; }
    /**
     * The getName function returns the name of the person.
     *
     *
     *
     * @return The name of the student
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
     * @return The player whose turn it is
     *
     * @docauthor Trelent
     */
    @Override
    public int getCurrentPlayer() { return playerTurn; }
    /**
     * The getMyTurn function returns the value of myTurn.
     *
     *
     *
     * @return The myturn variable
     *
     * @docauthor Trelent
     */
    @Override
    public int getMyTurn() { return myTurn; }
    /**
     * The getObservable function is a function that returns an Observable.
     * This function is used to get the observable from the class, so that it can be subscribed to.
     *
     *
     *
     * @return The observable object
     *
     * @docauthor Trelent
     */
    @Override
    public Observable getObservable() {
        return this;
    }

    // update
    /**
     * The update function is called whenever the host receives a message from one of its guests.
     * The function then parses the message and calls notifyObservers with an appropriate argument.
     * This allows for all observers to be notified when something happens in the game, such as a guest connecting or disconnecting,
     * or when it's time to update their board/score/etc.

     *
     * @param  o Identify which observable object is calling the update function
     * @param  arg Pass the message from the host to the viewmodel
        public void update(string arg) {

            string[] message = arg
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    @Override
    public void update(Observable o, Object arg) {

        String[] message = arg.toString().split(",");

        if (arg.equals("guest connect")) {
            Platform.runLater(() -> {
                System.out.println("host viewModel observer update: guest connect");
                playersProperty.set(host.GuestList.size());
            });
        }

        if (arg.equals("start game")) {
            System.out.println("host viewModel observer update: start game");
        }

        if (arg.equals("update board")) {
            System.out.println("host viewModel observer update: update board");
            gameBoard = host.hostPlayer.player.getCurrentBoardAsTiles();
            setChanged();
            notifyObservers("update board");
        }

        if (arg.equals("pass turn")) {
            System.out.println("host viewModel observer update: pass turn");
            setChanged();
            notifyObservers("pass turn");
        }

        if (arg.equals("challenge fail")) {
            System.out.println("host viewModel observer update: challenge fail");
            setChanged();
            notifyObservers("challenge fail");
        }

        if (arg.equals("challenge success")) {
            System.out.println("host viewModel observer update: challenge success");
            setChanged();
            notifyObservers("challenge success");
        }

        if (arg.equals("challenge alive")) {
            System.out.println("host viewModel observer update: challenge alive");
            setChanged();
            notifyObservers("challenge alive");
        }

        if (arg.equals("update score")) {
            System.out.println("host viewModel observer update: update score");
            setChanged();
            notifyObservers("update score");
        }

        if (message[0].equals("end game")) {
            System.out.println("host viewModel observer update: end game");
            setChanged();
            notifyObservers("end game," + message[1]);
        }

        if (message[0].equals("update map")) {
            System.out.println("host viewModel observer update: update map");
            NameToScore = host.NameToScore;
        }

        if (arg.equals("new tiles")) {
            System.out.println("host viewModel observer update: update map");
            newTiles();
        }
    }
}