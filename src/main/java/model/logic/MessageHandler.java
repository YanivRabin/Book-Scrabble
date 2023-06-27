package model.logic;

import com.google.gson.JsonObject;
import model.data.Tile;

import java.net.Socket;

public class MessageHandler {

    public JsonHandler jsonHandler;


    /**
     * The MessageHandler function is responsible for handling the messages that are sent to the server.
     * It takes in a message and then parses it into a JSONObject, which is then used to determine what type of message was sent.
     * Depending on what type of message was received, different actions will be taken by the MessageHandler function.

     *
     *
     * @return A json object
     *
     * @docauthor Trelent
     */
    public MessageHandler() {
        jsonHandler = new JsonHandler();
    }


    /**
     * The CreateStartGameMessage function creates a JSON message that is sent to all players in the game.
     * The message contains information about the tiles in each player's capital, as well as other information
     * such as which player has which index and how many players are playing. This function is only called by
     * the serverHost, who sends this message to all clients at the start of a new game.

     *
     * @param  tilesInCapital Create a jsonarray of the tiles that are in the capital
     * @param  hostNickName Identify the player who is hosting the game
     * @param  playerIndex Determine the player's index in the game
     * @param  numOfPlayers Indicate how many players are in the game
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void CreateStartGameMessage(String tilesInCapital, String hostNickName, int playerIndex, int numOfPlayers){
        // only serverHost
        this.jsonHandler.addMessageType("start game");
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addStartTiles(tilesInCapital);
        this.jsonHandler.addPlayerIndex(playerIndex);
        this.jsonHandler.addNumOfPlayers(numOfPlayers);
    }

    /**
     * The CreateTryAgainMessage function creates a message that is sent to the client when they have lost.
     * The message contains information about their previous score, and the action that was performed.
     *
     *
     * @param  destination Specify the destination of the message
     * @param  prevScore Send the previous score to the client
     * @param  action Determine if the player is trying to connect or disconnect
     * @param  hostNickName Set the source of the message
     *
     * @return A jsonobject with the message type &quot;try again&quot;
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
     * @param  destination Specify the destination of the message
     * @param  newScore Update the score of the player who played a word
     * @param  action Determine what action the client is performing
     * @param  newCurrentTiles Update the client's current tiles
     * @param  hostNickName Identify the host of the game
     *
     * @return A json object that contains the following:
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

    }

    /**
     * The CreateSucceededChallengeYouMessage function creates a message that is sent to the client when they have successfully challenged another player.
     *
     *
     * @param  hostNickName Identify the host of the game
     * @param  prevScore Store the previous score of the host
     *
     * @return A jsonobject that contains the message type, source and prevscore
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
     * @param  board Set the board in the json message
     * @param  hostNickName Identify the host of the game
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void CreateUpdateBoardMessage(String board, String hostNickName){
        // only serverHost
        this.jsonHandler.addMessageType("update board");
        this.jsonHandler.addSource(hostNickName);
        this.jsonHandler.addBoard(board);
    }

    /**
     * The CreateTryPlaceWordMessage function creates a message that is sent to the server when a player
     * tries to place a word on the board. The function takes in several parameters, including:
     *
     *
     * @param  source Indicate the source of the message
     * @param  destination Determine where the message is going
     * @param  word Add the word to the message
     * @param  prevScore Check if the score of the word is higher than the previous score
     * @param  row Specify the row where the word will be placed

     * @param  column Specify the column that the word will be placed in
     * @param  vertical Determine if the word is placed vertically or horizontally
    public void createtryplacewordmessage(string source, string destination, string word, int prevscore,
                                          int row, int column){
            this
     * @param  currentTiles Send the tiles that are currently on the board to the server
     * @param  socketSource Determine if the message is coming from a socket or not
     *
     * @return A string
     *
     * @docauthor Trelent
     */
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
     * The CreateChallengeMessage function creates a challenge message.
     *
     *
     * @param  socketSource Identify the socket that sent the message
     * @param  prevBoard Send the board to the opponent
     *
     * @return A json object
     *
     * @docauthor Trelent
     */
    public void CreateChallengeMessage(String socketSource, String prevBoard){
        this.jsonHandler.addMessageType("challenge");
        this.jsonHandler.addSocketSource(socketSource);
        this.jsonHandler.addPrevBoard(prevBoard);
    }

    /**
     * The CreateMessageToGameServer function creates a message to be sent to the game server.
     *
     *
     * @param  message Add a message to the json object
     * @param  socketSource Identify the socket that sent the message
     *
     * @return A json object with the following structure:
     *
     * @docauthor Trelent
     */
    public void CreateMessageToGameServer(String message, String socketSource){
        this.jsonHandler.addMessageType("to game server");
        this.jsonHandler.addMessage(message);
        this.jsonHandler.addSocketSource(socketSource);
    }

    /**
     * The createPassTurnMessage function creates a message that is sent to the server when the player passes their turn.
     * The function adds a &quot;pass turn&quot; message type to the JSONHandler object's jsonObject variable, which is then used by
     * sendMessage() in order to send this information over TCP/IP.

     *
     *
     * @return A jsonobject that contains the message type &quot;pass turn&quot;
     *
     * @docauthor Trelent
     */
    public void createPassTurnMessage() {
        this.jsonHandler.addMessageType("pass turn");
    }
    /**
     * The createStopChallengeAlive function is used to create a JSON message that will be sent to the server.
     * The purpose of this function is to send a message that will stop the challenge alive timer on the server side.

     *
     *
     * @return A jsonobject with the message type challenge alive
     *
     * @docauthor Trelent
     */
    public void createStopChallengeAlive() {
        this.jsonHandler.addMessageType("challenge alive");
    }

    /**
     * The createEndGameMessage function creates a JSON message that is sent to the client when the game ends.
     *
     *
     * @param  winner Determine the winner of the game
     *
     * @return A jsonobject that contains the type of message and the winner
     *
     * @docauthor Trelent
     */
    public void createEndGameMessage(String winner) {
        this.jsonHandler.addMessageType("end game");
        this.jsonHandler.addMessage(winner);
    }

    /**
     * The updatePrevToCurrent function updates the previous state of the game to be equal to the current state.
     * This is done by setting all of the values in prevState equal to their corresponding values in currState.

     *
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public void updatePrevToCurrent() {
        this.jsonHandler.addMessageType("update prev to current");
    }

    /**
     * The createChallengeSuccessMessage function creates a JSONObject that contains the message type &quot;challenge success&quot;
     * and adds it to the jsonHandler's list of messages.

     *
     *
     * @return A message that says &quot;challenge success&quot;
     *
     * @docauthor Trelent
     */
    public void createChallengeSuccessMessage() {
        this.jsonHandler.addMessageType("challenge success");
    }

    /**
     * The createNewPlayerJoinedMessage function creates a new JSON message that is sent to the server when a new player joins the game.
     *
     *
     * @param  nickName Add the nickname of the player that joined to the message
     *
     * @return A jsonobject, which is a message that will be sent to the client
     *
     * @docauthor Trelent
     */
    public void createNewPlayerJoinedMessage(String nickName) {
        this.jsonHandler.addMessageType("new player joined");
        this.jsonHandler.addMessage(nickName);

    }

    public void CreateGenerateNewTilesMessage(String socketSource, String tiles){
        this.jsonHandler.addMessageType("generate new tiles");
        this.jsonHandler.addCurrentTiles(tiles);
        this.jsonHandler.addSocketSource(socketSource);
    }
}
