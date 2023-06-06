package model.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonHandler {
    // keys with capital
    // values not capital
    public JsonObject json;

    public JsonHandler() {
        json = new JsonObject();
    }

    public void addBoard(Character[][] board) {
        json.add("Board", new Gson().toJsonTree(board));
    }

    public void addSource(String nickname) {
        json.addProperty("Source", nickname);
    }

    public void addDestination(String destination) {
        json.addProperty("Destination", destination);
    }
    public void addMessageType(String message) {
        json.addProperty("MessageType", message);
    }
    public void addMessage(String message) {
        json.addProperty("Message", message);
    }
    public void addNewScore(int score) {json.addProperty("NewScore", score);}
    public void addWord(String word) {
        json.addProperty("Word", word);
    }
    public void addAction(String action) {
        json.addProperty("Action", action);
    }
    public void addStartTiles(String message) {
        json.addProperty("StartTiles", message);
    }
    public void addNewCurrentTiles(String currentTiles) {
        json.addProperty("NewCurrentTiles", currentTiles);
    }
    public void addCurrentTiles(String currentTiles) {
        json.addProperty("CurrentTiles", currentTiles);
    }
    public void addSocketSource(String socketSource) {
        json.addProperty("SocketSource", socketSource);
    }
    public void addVertical(Boolean bool) {
        json.addProperty("Vertical", bool);
    }
    public void addRow(int row) {
        json.addProperty("Row", row);
    }
    public void addColumn(int column) {
        json.addProperty("Column", column);
    }

    public void addSocketDestination(String socketDestination) {
        json.addProperty("SocketDestination", socketDestination);
    }

    public String toPrettyJsonString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    public String toJsonString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(json);
    }

    public static JsonObject convertStringToJsonObject(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, JsonObject.class);
    }

}
