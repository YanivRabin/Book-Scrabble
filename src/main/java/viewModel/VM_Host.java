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
    public void updatePlayerTurn() {
        playerTurn = (playerTurn + 1) % players;
        System.out.println("Turn: " + playerTurn);
    }
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
    @Override
    public void updatePrev() {

        host.sendUpdatePrevToCurrent();
    }
    @Override
    public void updateScore() {

        scoreProperty.set(host.hostPlayer.player.getCurrentScore());
    }
    @Override
    public void updateBoard() {

        gameBoard = host.hostPlayer.player.getCurrentBoardAsTiles();
    }
    @Override
    public void endGame() {
        host.hostPlayer.sendEndGame();
    }

    // getters
    public String getIp() { return ip; }
    public int getPort() { return port; }
    @Override
    public Tile[][] getBoard() { return gameBoard; }
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
            System.out.println("guest viewModel observer update: end game");
            setChanged();
            notifyObservers("end game," + message[1]);
        }

        if (message[0].equals("update map")) {
            System.out.println("guest viewModel observer update: update map");
            NameToScore = host.NameToScore;
        }
    }
}