package model.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import model.data.Tile;

public class JsonHandler {
    // keys with capital
    // values not capital
    public JsonObject json;

    public JsonHandler() {
        json = new JsonObject();
    }

    /*public void addBoard(Character[][] board) {
        json.add("Board", new Gson().toJsonTree(board));
    }*/

    public void addBoard(String board) {
        json.addProperty("Board", board);
    }


    /**
     * The addSource function adds a source to the JSONObject.
     *
     *
     * @param String nickname Add a nickname to the json object
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void addSource(String nickname) {
        json.addProperty("Source", nickname);
    }

    /**
     * The addDestination function adds a destination to the JSON object.
     *
     *
     * @param String destination Add a destination to the json object
     *
     * @return Nothing, it just adds a property to the json object
     *
     * @docauthor Trelent
     */
    public void addDestination(String destination) {
        json.addProperty("Destination", destination);
    }
    /**
     * The addMessageType function adds a message type to the JSON object.
     *
     *
     * @param String message Add a message to the json object
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void addMessageType(String message) {
        json.addProperty("MessageType", message);
    }
    /**
     * The addMessage function adds a message to the JSON object.
     *
     *
     * @param String message Add a message to the json object
     *
     * @return Void, which means it returns nothing
     *
     * @docauthor Trelent
     */
    public void addMessage(String message) {
        json.addProperty("Message", message);
    }
    /**
     * The addNewScore function adds a new score to the JSONObject.
     *
     *
     * @param int score Add a new score to the json object

    public void addnewname(string name) {json
     *
     * @return A void, so it does not return anything
     *
     * @docauthor Trelent
     */
    public void addNewScore(int score) {json.addProperty("NewScore", score);}
    public void addPrevScore(int score) {json.addProperty("PrevScore", score);}
    public void addPrevBoard(String prevBoard) {json.addProperty("PrevBoard", prevBoard);}

    /**
     * The addWord function adds a word to the JSON object.
     *
     *
     * @param String word Add a word to the json object
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void addWord(String word) {
        json.addProperty("Word", word);
    }
    /**
     * The addAction function adds an action to the JSON object.
     *
     *
     * @param String action Add an action to the json object
     *
     * @return A jsonobject, which is the json object that was created in the constructor
     *
     * @docauthor Trelent
     */
    public void addAction(String action) {
        json.addProperty("Action", action);
    }
    /**
     * The addStartTiles function adds the start tiles to the board.
     *
     *
     * @param String message Add the message to the json object
    public void addmessage(string message) {
            json
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public void addStartTiles(String message) {
        json.addProperty("StartTiles", message);
    }
    /**
     * The addNewCurrentTiles function adds the new current tiles to the JSON object.
     *
     *
     * @param String currentTiles Set the current tiles in the game
     *
     * @return A string that contains the new tiles on the board
     *
     * @docauthor Trelent
     */
    public void addNewCurrentTiles(String currentTiles) {
        json.addProperty("NewCurrentTiles", currentTiles);
    }
    /**
     * The addCurrentTiles function adds the current tiles to the JSON object.
     *
     *
     * @param String currentTiles Set the current tiles of the game
     *
     * @return The current tiles
     *
     * @docauthor Trelent
     */
    public void addCurrentTiles(String currentTiles) {
        json.addProperty("CurrentTiles", currentTiles);
    }
    /**
     * The addSocketSource function adds a socket source to the JSON object.
     *
     *
     * @param String socketSource Set the socket source for the json
     *
     * @return A void
     *
     * @docauthor Trelent
     */
    public void addSocketSource(String socketSource) {
        json.addProperty("SocketSource", socketSource);
    }
    /**
     * The addVertical function adds a boolean value to the JSONObject.
     *
     *
     * @param Boolean bool Determine whether the chart is displayed vertically or horizontally
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void addVertical(Boolean bool) {
        json.addProperty("Vertical", bool);
    }
    /**
     * The addRow function adds a row to the JSON object.
     *
     *
     * @param int row Add a row to the table
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void addRow(int row) {
        json.addProperty("Row", row);
    }
    /**
     * The addColumn function adds a column to the JSON object.
     *
     *
     * @param int column Specify the column number in which you want to add a new column
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public void addColumn(int column) {
        json.addProperty("Column", column);
    }
    public void addPlayerIndex(int index) {
        json.addProperty("PlayerIndex", index);
    }
    public void addNumOfPlayers(int num) {
        json.addProperty("NumOfPlayers", num);
    }



    /**
     * The addSocketDestination function adds a socket destination to the JSON object.
     *
     *
     * @param String socketDestination Set the socket destination
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public void addSocketDestination(String socketDestination) {
        json.addProperty("SocketDestination", socketDestination);
    }

    /**
     * The toPrettyJsonString function takes the JSONObject json and converts it to a string.
     * The GsonBuilder class is used to create a new Gson object, which is then used to convert the JSONObject into a string.
     * The setPrettyPrinting function of the GsonBuilder class makes sure that when converted,
     * all of the elements in json are properly indented and spaced out for readability purposes.

     *
     *
     * @return A string
     *
     * @docauthor Trelent
     */
    public String toPrettyJsonString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    /**
     * The toJsonString function takes the JSONObject json and converts it to a string.
     *
     *
     *
     * @return A string representation of the json object
     *
     * @docauthor Trelent
     */
    public String toJsonString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(json);
    }

    /**
     * The convertStringToJsonObject function takes in a String and converts it to a JsonObject.
     *
     *
     * @param String jsonString Convert the jsonstring into a jsonobject
     *
     * @return A jsonobject
     *
     * @docauthor Trelent
     */
    public static JsonObject convertStringToJsonObject(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, JsonObject.class);
    }

}
