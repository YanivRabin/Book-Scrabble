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

    // constructor
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
    public IntegerProperty playersProperty() { return playersProperty; }
    @Override
    public IntegerProperty scoreProperty() { return scoreProperty; }

    // game functions
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
    @Override
    public int tryPlaceWord(Word word) {

        // get the current score
        int score = scoreProperty.get();

        // try place word func
        host.hostPlayer.SendTryPlaceWordMessage(host.hostPlayer.NickName, host.NickName, word.toString(), word.getRow(), word.getCol(), word.isVertical());
        try { Thread.sleep(2000); }
        catch (InterruptedException e) { e.printStackTrace(); }

        // set the new score
        scoreProperty.set(host.hostPlayer.player.getCurrentScore());

        // print for test
        System.out.println("prev score: " + score);
        System.out.println("current score: " + host.hostPlayer.player.getCurrentScore());

        // if the currentScore - prevScore is 0, its mean the word received 0 points
        if (score != 0) {
            return scoreProperty.get() - score;
        }
        // if its this first turn send back the current score
        else {
            return scoreProperty.get();
        }
    }
    @Override
    public void placeTile(Tile selectedTile, int row, int col) {

//        gameBoard.placeTile(selectedTile, row, col);
    }
    @Override
    public void removeTile(int row, int column) {

//        gameBoard.removeTile(row,column);
    }
    @Override
    public void passTurn() {

        host.hostPlayer.sendPassTurnMessage();
    }
    @Override
    public void updateTiles() {

        currentTiles.clear();
        // get the player tiles and convert them from char to tile object
        char[] tiles = host.hostPlayer.player.getCurrentTiles().toCharArray();
        for (char tile: tiles) {
            currentTiles.add(gameBag.getTileForTileArray(tile));
        }
    }
    @Override
    public void updateBoard() {

//        host.hostPlayer.sendUpdateBoardMessage();

//        gameBoard = Board.getBoard();
//        setChanged();
//        notifyObservers("update board");
    }
    @Override
    public void updatePlayerTurn() {
        playerTurn = (playerTurn + 1) % players;
        System.out.println("Turn: " + playerTurn);
    }

    // getters
    public String getIp() { return ip; }
    public int getPort() { return port; }
    @Override
    public Tile[][] getBoard() { return gameBoard; }
    //    public Tile[][] getBoard() { return gameBoard.getTiles(); }
    @Override
    public ArrayList<Tile> getCurrentTiles() { return currentTiles; }
    @Override
    public String getName() { return name; }
    @Override
    public int getCurrentPlayer() { return playerTurn; }
    @Override
    public int getMyTurn() { return myTurn; }
    @Override
    public Observable getObservable() {
        return this;
    }

    // update
    @Override
    public void update(Observable o, Object arg) {

//        String[] message = arg.toString().split(",");

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
    }
}