package model.logic;

import com.google.gson.JsonObject;

public class MessageHandler {

    public JsonHandler jsonHandler;

    public MessageHandler() {
        jsonHandler = new JsonHandler();
    }

    public void CreateStartGameMessage(String tilesInCapital, String hostNickName){
        // only serverHost
        this.jsonHandler.addMessageType("start game");
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addStartTiles(tilesInCapital);
    }
    public void CreateTryAgainMessage(String destination, int prevScore, String action, String hostNickName){
        // only serverHost
        this.jsonHandler.addMessageType("try again");
        this.jsonHandler.addAction(action);
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addDestination(destination);
        this.jsonHandler.addNewScore(prevScore);
        // this.jsonHandler.addBoard(board);
        // addPrevBoard
    }
    public void CreateSuccessMessage(String destination, int newScore, String action, String newCurrentTiles, String hostNickName){
        // only serverHost
        this.jsonHandler.addMessageType("success");
        this.jsonHandler.addAction(action);
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addDestination(destination);
        this.jsonHandler.addNewScore(newScore);
        this.jsonHandler.addNewCurrentTiles(newCurrentTiles);
        // this.jsonHandler.addBoard(board);
        // addPrevBoard
    }
    public void CreateSucceededChallengeYouMessage(Character[][] board, String hostNickName){
        // only serverHost
        this.jsonHandler.addMessageType("succeeded in challenging you");
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addBoard(board);
    }
    public void CreateUpdateBoardMessage(Character[][] board, String hostNickName){
        // only serverHost
        this.jsonHandler.addMessageType("update board");
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addBoard(board);
    }
    public void CreateTryPlaceWordMessage(String source, String destination, String word,
                                          int row, int column, boolean vertical, String currentTiles){
        this.jsonHandler.addMessageType("try place word");
        this.jsonHandler.addSource(source);
        this.jsonHandler.addDestination(destination);
        this.jsonHandler.addWord(word);
        this.jsonHandler.addRow(row);
        this.jsonHandler.addColumn(column);
        this.jsonHandler.addVertical(vertical);
        this.jsonHandler.addCurrentTiles(currentTiles);
//        this.jsonHandler.addBoard(board);
        // addPrevBoard
    }
    public void CreateChallengeMessage(String source, String destination, String word,
                                       int row, int column, boolean vertical, String currentTiles){
        this.jsonHandler.addMessageType("challenge");
        this.jsonHandler.addSource(source);
        this.jsonHandler.addDestination(destination);
        this.jsonHandler.addWord(word);
        this.jsonHandler.addRow(row);
        this.jsonHandler.addColumn(column);
        this.jsonHandler.addVertical(vertical);
        this.jsonHandler.addCurrentTiles(currentTiles);
        // this.jsonHandler.addBoard(board);
        // addPrevBoard
    }

}
