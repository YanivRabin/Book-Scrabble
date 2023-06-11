package model.logic;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Player {
    String socketIP;
    String nickName;
    int currentScore;
    int prevScore;
    List<Character> currentTiles;
    List<Character> prevTiles;
    Character[][] currentBoard;
    Character[][] prevBoard;


    public Player(String socketIP, String nickName, int score, List<Character> currentTiles) {
        this.socketIP = socketIP;
        this.nickName = nickName;
        this.currentScore = score;
        this.currentTiles = currentTiles;
    }

    public Player(String socketIP, String nickName, int score) {
        this.socketIP = socketIP;
        this.nickName = nickName;
        this.currentScore = score;
        this.currentTiles = new ArrayList<>();
    }

    public String getSocketIP() {
        return socketIP;
    }

    public String getNickName() {
        return nickName;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public String getCurrentTiles() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Character c : this.currentTiles){
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public void setCurrentTiles(String capitalTiles) {
        List<Character> currentTiles = new ArrayList<>();
        for(int i = 0 ; i < capitalTiles.length() ; i++){
            currentTiles.add(capitalTiles.charAt(i));
        }
        this.currentTiles = currentTiles;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setSocketIP(String socketIP) {
        this.socketIP = socketIP;
    }

    public void addScore(int score) {
        this.currentScore += score;
    }

    public void addTiles(String word) {
        for (int i = 0; i < word.length(); i++) {
            currentTiles.add(word.charAt(i));
        }
    }
    public void setCurrentBoard(String currentBoard) {
        Gson gson = new Gson();
        Character[][] board = gson.fromJson(currentBoard, Character[][].class);
        this.currentBoard = board;
    }

    public Character[][] getBoard(String jsonValue) {
        Gson gson = new Gson();
        Character[][] board = gson.fromJson(jsonValue, Character[][].class);
        return board;
    }


    public void printCurrentTiles() {
        System.out.print("current tiles: ");
        for (Character currentTile : this.currentTiles) {
            System.out.print(currentTile);
        }
        System.out.println();
    }

    public boolean usingCurrentTiles(String word){
        // Checking legal choosing tiles to try place
        int c = 0;
        int counterNull = 0;
        for(int i = 0 ; i < word.length();i++){
            if(word.charAt(i) != '_'){
                // checking null
                for(Character t : this.currentTiles){
                    if(t == word.charAt(i)){
                        c++;
                    }
                }
            }
            else{
                counterNull++;
            }
        }
        return c == word.length() - counterNull;
    }

}
