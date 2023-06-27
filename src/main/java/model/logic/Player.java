package model.logic;

import com.google.gson.Gson;
import model.data.Tile;

import java.util.ArrayList;
import java.util.List;

public class Player {
    String socketIP;

    int numOfPlayersInGame;

    int playerIndex;
    String nickName;



    public String hostNickName;
    int currentScore;
    int prevScore;
    List<Character> currentTiles;
    List<Character> prevTiles;



    Character[][] currentBoard;
    Character[][] prevBoard;


    /**
     * The Player function is a constructor for the Player class.
     * It takes in three parameters: socketIP, nickName, and score.
     * The function sets the instance variables of this class to be equal to these parameters.

     *
     * @param  socketIP Store the ip address of the player's socket
     * @param  nickName Set the player's nickname
     * @param  score Set the currentscore and prevscore to a certain value
     *
     * @return A new player object with the given socketip, nickname and score
     *
     * @docauthor Trelent
     */
    public Player(String socketIP, String nickName, int score) {
        this.socketIP = socketIP;
        this.nickName = nickName;
        this.currentScore = score;
        this.prevScore = score;
        this.currentTiles = new ArrayList<>();
        this.currentBoard = new Character[15][15];
        this.prevBoard = new Character[15][15];
    }

    /**
     * The getSocketIP function returns the IP address of the socket.
     *
     *
     *
     * @return The ip address of the socket
     *
     * @docauthor Trelent
     */
    public String getSocketIP() {
        return socketIP;
    }

    /**
     * The getNickName function returns the nickName of a user.
     *
     *
     *
     * @return The nickname variable
     *
     * @docauthor Trelent
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * The getHostNickName function returns the host's nickname.
     *
     *
     *
     * @return The hostnickname variable
     *
     * @docauthor Trelent
     */
    public String getHostNickName() {
        return hostNickName;
    }

    /**
     * The getCurrentScore function returns the current score of the player.
     *
     *
     *
     * @return The current score
     *
     * @docauthor Trelent
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * The getCurrentBoard function returns the current board.
     *
     *
     *
     * @return The current board, which is a 2d array of characters
     *
     * @docauthor Trelent
     */
    public Character[][] getCurrentBoard() {
        return currentBoard;
    }

    /**
     * The getPlayerIndex function returns the playerIndex of the current player.
     *
     *
     *
     * @return The playerindex variable
     *
     * @docauthor Trelent
     */
    public int getPlayerIndex() {
        return playerIndex;
    }

    /**
     * The getCurrentTiles function returns a string of the current tiles in the player's hand.
     *
     *
     *
     * @return A string representing the current tiles on the player's rack
     *
     * @docauthor Trelent
     */
    public String getCurrentTiles() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Character c : this.currentTiles){
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    /**
     * The setCurrentScore function sets the currentScore variable to a new value.
     *
     *
     * @param currentScore currentScore Set the currentscore variable to a new value
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    /**
     * The setCurrentTiles function takes in a string of capital letters and sets the currentTiles variable to be a list of characters that are the same as those in the input string.
     *
     *
     * @param capitalTiles capitalTiles Set the currenttiles list to a new list of characters
     *
     * @return Nothing, so the return type is void
     *
     * @docauthor Trelent
     */
    public void setCurrentTiles(String capitalTiles) {
        List<Character> currentTiles = new ArrayList<>();
        for(int i = 0 ; i < capitalTiles.length() ; i++){
            currentTiles.add(capitalTiles.charAt(i));
        }
        this.currentTiles = currentTiles;
    }

    /**
     * The setNickName function sets the value of the nickName variable.
     *
     *
     * @param nickName nickName Set the nickname of the player
     *
     * @return Void, so it doesn't return anything
     *
     * @docauthor Trelent
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * The setSocketIP function sets the socketIP variable to the value of its parameter.
     *
     *
     * @param socketIP socketIP Set the socketip field
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void setSocketIP(String socketIP) {
        this.socketIP = socketIP;
    }

    /**
     * The addScore function adds the score parameter to the currentScore variable.
     *
     *
     * @param score score Add the score to the currentscore variable
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void addScore(int score) {
        this.currentScore += score;
    }

    /**
     * The addTiles function adds the tiles of a word to the currentTiles list.
     *
     *
     * @param  word Add the characters of the word to an arraylist
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void addTiles(String word) {
        for (int i = 0; i < word.length(); i++) {
            currentTiles.add(word.charAt(i));
        }
    }

    /**
     * The getBoard function takes in a JSON string and returns the board as a 2D array of characters.
     *
     *
     * @param  jsonValue Convert the jsonvalue to a 2d array of characters
     *
     * @return A 2d array of characters
     *
     * @docauthor Trelent
     */
    public void setCurrentBoard(String jsonValue) {
        this.currentBoard =  this.parseStringToCharacterArray(jsonValue);
    }


    /**
     * The printCurrentTiles function prints the current tiles in a player's hand.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void printCurrentTiles() {
        System.out.print("current tiles: ");
        for (Character currentTile : this.currentTiles) {
            System.out.print(currentTile);
        }
        System.out.println();
    }

    /**
     * The usingCurrentTiles function checks if the word that is being placed on the board
     * can be made using only tiles from the currentTiles array.
     *
     *
     * @param  word Check if the word is in the dictionary
     *
     * @return True if the word can be formed using the current tiles, otherwise it returns false
     *
     * @docauthor Trelent
     */
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

    /**
     * The getNumOfPlayersInGame function returns the number of players in the game.
     *
     *
     *
     * @return The number of players in the game
     *
     * @docauthor Trelent
     */
    public int getNumOfPlayersInGame() {
        return numOfPlayersInGame;
    }

    /**
     * The setNumOfPlayersInGame function sets the number of players in a game.
     *
     *
     * @param  numOfPlayersInGame Set the number of players in the game
     *
     * @return Void, which means it returns nothing
     *
     * @docauthor Trelent
     */
    public void setNumOfPlayersInGame(int numOfPlayersInGame) {
        this.numOfPlayersInGame = numOfPlayersInGame;
    }

    /**
     * The parseStringToCharacterArray function takes a String representation of the board and returns a 2D Character array
     * representing the same board. The function is used to convert from one representation to another, which can be useful
     * for testing purposes. For example, if you have an implementation that uses Strings as input but you want to test it with
     * your implementation that uses 2D arrays as input, then this function will allow you to do so. It also allows us (the TAs)
     * to test your code using our own implementations of BogglePlayer without having access to your source code! This is called &quot;black box&quot; testing.
     *
     * @param  boardString Store the boardstring
     *
     * @return A 2d array of characters
     *
     * @docauthor Trelent
     */
    public Character[][] parseStringToCharacterArray(String boardString) {
        String[] rows = boardString.trim().split("\n");
        int rowCount = rows.length;
        int colCount = rows[0].length();

        Character[][] characterBoard = new Character[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                char tileChar = rows[i].charAt(j);
                characterBoard[i][j] = (tileChar != '.') ? tileChar : null;
            }
        }

        return characterBoard;
    }

    /**
     * The getCurrentBoardAsTiles function is a helper function that returns the current board as an array of tiles.
     *
     *
     *
     * @return A 2d array of tile objects
     *
     * @docauthor Trelent
     */
    public Tile[][] getCurrentBoardAsTiles() {

        Tile[][] temp = new Tile[15][15];
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                if (currentBoard[row][col] != null) {
                    temp[row][col] = Tile.Bag.getBagModel().getTileForTileArray(currentBoard[row][col]);
                }
            }
        }
        return temp;
    }

}
