package model.logic;

import com.google.gson.JsonObject;
import model.data.Tile;

public class MessageHandler {

    public JsonHandler jsonHandler;

    /**
     * The MessageHandler function is responsible for handling the messages that are sent to the server.
     * It takes in a message and then parses it into a JSONObject, which is then used to determine what type of message was sent.
     * Depending on what type of message was received, different actions will be taken by the MessageHandler function.

     *
     *
     * @return A jsonhandler object
     *
     * @docauthor Trelent
     */
    public MessageHandler() {
        jsonHandler = new JsonHandler();
    }

    /**
     * The CreateStartGameMessage function creates a JSON message that is sent to all clients when the game starts.
     *
     *
     * @param String tilesInCapital Add the tiles to the message
     * @param String hostNickName Identify the host of the game
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void CreateStartGameMessage(String tilesInCapital, String hostNickName){
        // only serverHost
        this.jsonHandler.addMessageType("start game");
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addStartTiles(tilesInCapital);
    }
    /**
     * The CreateTryAgainMessage function creates a message that is sent to the client when they have lost.
     * The message contains information about their previous score, and the action that was performed by the serverHost.
     *
     *
     * @param String destination Specify the destination of the message
     * @param int prevScore Send the previous score to the client so that it can be displayed in a message
     * @param String action Determine if the player is trying to connect or disconnect
     * @param String hostNickName Identify the host of the game
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
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
    /**
     * The CreateSuccessMessage function is used to create a success message.
     *
     *
     * @param String destination Specify the destination of the message
     * @param int newScore Add the new score to the json message
     * @param String action Determine what type of message is being sent
     * @param String newCurrentTiles Add the new tiles to the currenttiles field in the json message
     * @param String hostNickName Identify the host of the game
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
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
    /**
     * The CreateSucceededChallengeYouMessage function creates a message that is sent to the client who has been challenged by another player.
     *
     *
     * @param Character[][] board Send the board to the client
     * @param String hostNickName Identify the host of the game
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void CreateSucceededChallengeYouMessage(String hostNickName, String prevScore){
        // only serverHost
        this.jsonHandler.addMessageType("succeeded in challenging you");
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addPrevScore(Integer.parseInt(prevScore));
    }
    /**
     * The CreateUpdateBoardMessage function creates a message that updates the board.
     *
     *
     * @param  board Send the board to the client
     * @param  hostNickName Identify the source of the message
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void CreateUpdateBoardMessage(Character[][] board, String hostNickName){
        // only serverHost
        this.jsonHandler.addMessageType("update board");
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addBoard(board);
    }

    public void CreateTryPlaceWordMessage(String source, String destination, String word, int prevScore,
                                          int row, int column, boolean vertical, String currentTiles, String socketSource){
        this.jsonHandler.addMessageType("try place word");
        this.jsonHandler.addSource(source);
        this.jsonHandler.addDestination(destination);
        this.jsonHandler.addWord(word);
        this.jsonHandler.addRow(row);
        this.jsonHandler.addColumn(column);
        this.jsonHandler.addVertical(vertical);
        this.jsonHandler.addCurrentTiles(currentTiles);
        this.jsonHandler.addSocketSource(socketSource);
        this.jsonHandler.addPrevScore(prevScore);
//        this.jsonHandler.addBoard(board);
        // addPrevBoard
    }
    /**
     * The CreateChallengeMessage function creates a JSON message that is sent to the server
     * when a player challenges another player's word. The function takes in the source, destination,
     * and word of the challenge as well as where on the board it was placed (row and column) and whether or not it was placed vertically.

     *
     * @param String source Identify the player who sent the message
     * @param String destination Specify the destination of the message
     * @param String word Add the word to the message
     * @param int row Indicate the row of the first letter in a word
     * @param int column Specify the column in which the word is placed
    public void createchallengeresponsemessage(string source, string destination, boolean valid){
            this
     * @param boolean vertical Indicate whether the word is placed vertically or horizontally
     * @param String currentTiles Add the current tiles to the json message

     *
     * @return A jsonobject with the following fields:
     *
     * @docauthor Trelent
     */
    public void CreateChallengeMessage(String source, String destination, String word,
                                       int row, int column, boolean vertical, String currentTiles, String socketSource){
        this.jsonHandler.addMessageType("challenge");
        this.jsonHandler.addSource(source);
        this.jsonHandler.addDestination(destination);
        this.jsonHandler.addWord(word);
        this.jsonHandler.addRow(row);
        this.jsonHandler.addColumn(column);
        this.jsonHandler.addVertical(vertical);
        this.jsonHandler.addCurrentTiles(currentTiles);
        this.jsonHandler.addSocketSource(socketSource);
    }

    public void CreateUpdateScore(String source){
        this.jsonHandler.addMessageType("update score");
        this.jsonHandler.addSource(source);
    }

}
