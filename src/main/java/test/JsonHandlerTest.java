package test;

import model.logic.JsonHandler;

public class JsonHandlerTest {
    // Testing the JsonHandler class
    public static void main(String[] args) {
        Character[][] board = {{1, 2, 3}, {4, 5, 6}};
        String source = "John";
        String destination = "Guest";
        String message = "Hello, world!";
        String score = "100";
        String word = "WIN";
        String socketSource = "192.168.0.1:8080";
        String socketDestination = "192.168.0.2:9090";

        JsonHandler json = new JsonHandler();
        json.addBoard(board);
        json.addSource(source);
        json.addDestination(destination);
        json.addMessage(message);
        json.addNewScore(Integer.parseInt(score));
        json.addWord(word);
        json.addSocketSource(socketSource);
        json.addSocketDestination(socketDestination);

        System.out.println(json.toJsonString());
    }
}
