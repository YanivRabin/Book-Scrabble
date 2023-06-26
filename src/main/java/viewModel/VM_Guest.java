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
    @Override
    public IntegerProperty scoreProperty() {
        return scoreProperty;
    }

    // game functions
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
    @Override
    public void startGame() {

        gameBoard = guest.player.getCurrentBoardAsTiles();
        gameBag = Tile.Bag.getBag();
        // make turn for later to pass between players
        myTurn = guest.player.getPlayerIndex();
        players = guest.player.getNumOfPlayersInGame();
        updateTiles();
    }
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
    @Override
    public void passTurn() {

        guest.sendPassTurnMessage();
    }
    @Override
    public void updateTiles() {

        currentTiles.clear();
        // get the player tiles and convert them from char to tile object
        char[] tiles = guest.player.getCurrentTiles().toCharArray();
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

        guest.SendChallengeMessage(sb.toString());
    }
    @Override
    public void updatePrev() {}
    @Override
    public void updateScore() {

        scoreProperty.set(guest.player.getCurrentScore());
    }
    @Override
    public void updateBoard() {

        gameBoard = guest.player.getCurrentBoardAsTiles();
    }
    @Override
    public void endGame() {
        guest.sendEndGame();
    }

    // getters
    @Override
    public Tile[][] getBoard() {

        return gameBoard;
    }
    @Override
    public ArrayList<Tile> getCurrentTiles() {
        return currentTiles;
    }
    @Override
    public String getName() { return name; }
    @Override
    public int getCurrentPlayer() {
        return playerTurn;
    }
    @Override
    public int getMyTurn() {
        return myTurn;
    }
    @Override
    public Observable getObservable() {
        return this;
    }

    // update
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
    }
}