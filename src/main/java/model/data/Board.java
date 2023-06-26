/*
package model.data;

import javafx.scene.layout.Border;
import model.logic.Dictionary;
import model.logic.Host;

import java.io.IOException;
import java.util.ArrayList;

public class Board {

    private static Board singleBoard = null;
    Tile[][] board;
    char[][] bonusBoard;

    boolean firstTurn;
    Dictionary dictionary;

    public Board() {
        firstTurn = true;

        board = new Tile[15][15];
        bonusBoard = new char[][]{
                //R = RED    = TRIPLE WORD SCORE
                //A = AZURE  = DOUBLE LETTER SCORE
                //Y = YELLOW = DOUBLE WORD SCORE
                //B = BLUE   = TRIPLE LETTER SCORE
                //S = STAR   = DOUBLE WORD SCORE
                {'R', ' ', ' ', 'A', ' ', ' ', ' ', 'R', ' ', ' ', ' ', 'A', ' ', ' ', 'R'},
                {' ', 'Y', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'Y', ' '},
                {' ', ' ', 'Y', ' ', ' ', ' ', 'A', ' ', 'A', ' ', ' ', ' ', 'Y', ' ', ' '},
                {'A', ' ', ' ', 'Y', ' ', ' ', ' ', 'A', ' ', ' ', ' ', 'Y', ' ', ' ', 'A'},
                {' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' '},
                {' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' '},
                {' ', ' ', 'A', ' ', ' ', ' ', 'A', ' ', 'A', ' ', ' ', ' ', 'A', ' ', ' '},
                {'R', ' ', ' ', 'A', ' ', ' ', ' ', 'S', ' ', ' ', ' ', 'A', ' ', ' ', 'R'},
                {' ', ' ', 'A', ' ', ' ', ' ', 'A', ' ', 'A', ' ', ' ', ' ', 'A', ' ', ' '},
                {' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' '},
                {' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' '},
                {'A', ' ', ' ', 'Y', ' ', ' ', ' ', 'A', ' ', ' ', ' ', 'Y', ' ', ' ', 'A'},
                {' ', ' ', 'Y', ' ', ' ', ' ', 'A', ' ', 'A', ' ', ' ', ' ', 'Y', ' ', ' '},
                {' ', 'Y', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'Y', ' '},
                {'R', ' ', ' ', 'A', ' ', ' ', ' ', 'R', ' ', ' ', ' ', 'A', ' ', ' ', 'R'}
        };
    }

    public static Board getBoard() {

        // create new board, if already created then return the current
        if (singleBoard == null)
            singleBoard = new Board();

        return singleBoard;
    }


    private static class BoardModelHelper {
        public static final Board model_instance = new Board();
    }

    public static Board getBoardModel() {
        return Board.BoardModelHelper.model_instance;
    }



    public Tile[][] getTiles() {
        return board.clone();
    }

    public Character[][] parseBoard(Tile[][] board) {
        Character[][] charBoard = new Character[board.length][board[0].length];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Tile tile = board[i][j];
                charBoard[i][j] = tile != null ? tile.letter : null;
            }
        }

        return charBoard;
    }

    public boolean checkBoundaries(Word w) {

        if (w.vertical) {
            if (w.tiles.length + w.row > 14) {
                return false;
            }
        }

        if (!w.vertical) {
            if (w.tiles.length + w.col > 14) {
                return false;
            }
        }

        return true;
    }

    public boolean checkNeighbors(Word w) {

        int i;
        if (w.vertical) {
            //first letter
            if (w.row > 0 && board[w.row - 1][w.col] != null)
                return true;
            //middle letters
            for (i = w.row; i < w.tiles.length + w.row; i++)
                if (board[i][w.col - 1] != null || board[i][w.col + 1] != null)
                    return true;
            //last letter
            if (i < 14 && board[i][w.col] != null)
                return true;
        }
        if (!w.vertical) {
            if (w.col > 0 && board[w.row][w.col - 1] != null)
                return true;
            for (i = w.col; i < w.tiles.length + w.col; i++)
                if (board[w.row - 1][i] != null || board[w.row + 1][i] != null)
                    return true;
            if (i < 14 && board[w.row][i] != null)
                return true;
        }
        return false;
    }

    public boolean boardLegal(Word w) {

        //check word size
        if (w.tiles.length < 2)
            return false;

        //check if on board boundaries
        if (w.row < 0 || w.row > 14 || w.col < 0 || w.col > 14)
            return false;

        if (!checkBoundaries(w))
            return false;

        //check if the first word placed on the center star

        if (board[7][7] != null && firstTurn) {

            if ((w.vertical && (w.col != 7 || (w.row + w.tiles.length <= 7) || w.row >= 8)) ||
                    (!w.vertical && (w.row != 7 || (w.col + w.tiles.length <= 7) || w.col >= 8))) {

                return false;
            }
            else {

                firstTurn = false;
                return true;
            }
        }

        if (!checkNeighbors(w))
            return false;

//        if (!checkEmptyTile(w))
//            return false;

        return true;
    }

    public boolean dictionaryLegal(Word w) {

        */
/*//*
/ for GUI check
        if (!dictionary.query(w.toString()))
            return false;

        if (!dictionary.challenge(w.toString()))
            return false;

        return true;*//*


        StringBuilder text = new StringBuilder("Q," + w.toString());
        Host.getModel().SendMessageToGameServer(text.toString());
        boolean res;
        try {
            res = Host.getModel().GetMessageFromGameServer().equals("true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
    public ArrayList<Word> getWords(Word w) {

        ArrayList<Word> words = new ArrayList<>();
        words.add(w);

        if (w.vertical) {

            int i = w.row;

            for (Tile t : w.tiles) {

                int j = w.col;
                ArrayList<Tile> temp = new ArrayList<>();

                // add only new words
                if (t != null) {

                    while (j > 0 && board[i][j - 1] != null)
                        j--;

                    int tempCol = j;
                    while (j < 15 && board[i][j] != null) {
                        temp.add(board[i][j]);
                        j++;
                    }

                    Tile[] tempT = temp.toArray(new Tile[0]);
                    Word tempWord = new Word(tempT, i, tempCol, false);
                    if (dictionaryLegal(tempWord) && boardLegal(tempWord))
                        words.add(tempWord);
                }
                i++;
            }
        }
        if (!w.vertical) {

            int i = w.col;

            for (Tile t : w.tiles) {

                int j = w.row;
                ArrayList<Tile> temp = new ArrayList<>();

                if (t != null) {

                    while (j > 0 && board[j - 1][i] != null)
                        j--;

                    int tempRow = j;
                    while (j < 15 && board[j][i] != null) {
                        temp.add(board[j][i]);
                        j++;
                    }

                    Tile[] tempT = temp.toArray(new Tile[0]);
                    Word tempWord = new Word(tempT, tempRow, i, true);
                    if (dictionaryLegal(tempWord) && boardLegal(tempWord))
                        words.add(tempWord);
                }
                i++;
            }
        }

        return words;
    }

    public int getScore(Word w) {

        int sum = 0;

        ArrayList<Word> words = getWords(w);

        for (Word word : words) {

            int i = 0;
            int wordBonus = 1; // total word bonuses
            int wordScore = 0;

            for (Tile t : word.tiles) {

                if (word.vertical) {

                    switch (bonusBoard[word.row + i][word.col]) {

                        case 'R':
                            wordScore += t.score;
                            wordBonus *= 3;
                            break;

                        case 'Y':
                        case 'S':
                            wordScore += t.score;
                            wordBonus *= 2;
                            bonusBoard[7][7] = ' ';
                            break;

                        case 'B':
                            wordScore += t.score * 3;
                            break;

                        case 'A':
                            wordScore += t.score * 2;
                            break;

                        default:
                            wordScore += t.score;
                            break;
                    }
                }
                if (!word.vertical) {

                    switch (bonusBoard[word.row][word.col + i]) {

                        case 'R':
                            wordScore += t.score;
                            wordBonus *= 3;
                            break;

                        case 'Y':
                        case 'S':
                            wordScore += t.score;
                            wordBonus *= 2;
                            bonusBoard[7][7] = ' ';
                            break;

                        case 'B':
                            wordScore += t.score * 3;
                            break;

                        case 'A':
                            wordScore += t.score * 2;
                            break;

                        default:
                            wordScore += t.score;
                            break;
                    }
                }
                i++;
            }
            sum += (wordScore * wordBonus);
        }
        return sum;
    }

    public int tryPlaceWord(Word word) {

        if (boardLegal(word) && dictionaryLegal(word)) {
            return getScore(word);
        }

        return 0;
    }



    // function for GUI
    public void placeTile(Tile selectedTile, int row, int column) {

        board[row][column] = selectedTile;
    }
    public void removeTile(int row, int column) {

        board[row][column] = null;
    }
}
*/


package model.data;

import model.logic.Dictionary;
import model.logic.DictionaryManager;
import model.logic.Host;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Board {

    private static Board singleBoard = null;
    Tile[][] board;
    char[][] bonusBoard;

    boolean firstTurn;

    public Board() {

        firstTurn = true;

        board = new Tile[15][15];
        bonusBoard = new char[][]{
                //R = RED    = TRIPLE WORD SCORE
                //A = AZURE  = DOUBLE LETTER SCORE
                //Y = YELLOW = DOUBLE WORD SCORE
                //B = BLUE   = TRIPLE LETTER SCORE
                //S = STAR   = DOUBLE WORD SCORE
                {'R', ' ', ' ', 'A', ' ', ' ', ' ', 'R', ' ', ' ', ' ', 'A', ' ', ' ', 'R'},
                {' ', 'Y', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'Y', ' '},
                {' ', ' ', 'Y', ' ', ' ', ' ', 'A', ' ', 'A', ' ', ' ', ' ', 'Y', ' ', ' '},
                {'A', ' ', ' ', 'Y', ' ', ' ', ' ', 'A', ' ', ' ', ' ', 'Y', ' ', ' ', 'A'},
                {' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' '},
                {' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' '},
                {' ', ' ', 'A', ' ', ' ', ' ', 'A', ' ', 'A', ' ', ' ', ' ', 'A', ' ', ' '},
                {'R', ' ', ' ', 'A', ' ', ' ', ' ', 'S', ' ', ' ', ' ', 'A', ' ', ' ', 'R'},
                {' ', ' ', 'A', ' ', ' ', ' ', 'A', ' ', 'A', ' ', ' ', ' ', 'A', ' ', ' '},
                {' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' '},
                {' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' ', ' ', 'Y', ' ', ' ', ' ', ' '},
                {'A', ' ', ' ', 'Y', ' ', ' ', ' ', 'A', ' ', ' ', ' ', 'Y', ' ', ' ', 'A'},
                {' ', ' ', 'Y', ' ', ' ', ' ', 'A', ' ', 'A', ' ', ' ', ' ', 'Y', ' ', ' '},
                {' ', 'Y', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'B', ' ', ' ', ' ', 'Y', ' '},
                {'R', ' ', ' ', 'A', ' ', ' ', ' ', 'R', ' ', ' ', ' ', 'A', ' ', ' ', 'R'}
        };
    }

    /**
     * The getBoard function is a static function that returns the current board.
     * If there is no board, it creates one and then returns it.

     *
     *
     * @return The current board
     *
     * @docauthor Trelent
     */
    public static Board getBoard() {

        // create new board, if already created then return the current
        if (singleBoard == null)
            singleBoard = new Board();

        return singleBoard;
    }


    private static class BoardModelHelper {
        public static final Board model_instance = new Board();
    }

    /**
     * The getBoardModel function returns the Board object that is used to store all of the information about
     * the current state of a game. This function is called by other classes in order to access this information.

     *
     *
     * @return The boardmodelhelper
     *
     * @docauthor Trelent
     */
    public static Board getBoardModel() {
        return Board.BoardModelHelper.model_instance;
    }



    /**
     * The getTiles function returns a copy of the board.
     *
     *
     *
     * @return A copy of the board array
     *
     * @docauthor Trelent
     */
    public Tile[][] getTiles() {
        return board.clone();
    }

    /**
     * The checkBoundaries function checks to see if the word is within the boundaries of the board.
     *
     *
     * @param w w Access the length of the word, and whether it is vertical or horizontal
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
    public boolean checkBoundaries(Word w) {

        if (w.vertical) {
            if (w.tiles.length + w.row > 14) {
                return false;
            }
        }

        if (!w.vertical) {
            if (w.tiles.length + w.col > 14) {
                return false;
            }
        }

        return true;
    }

    /**
     * The checkNeighbors function checks to see if the word being placed has any neighbors.
     *
     *
     * @param w w Pass in the word that is being placed on the board
     *
     * @return True if the word has a neighbor
     *
     * @docauthor Trelent
     */
    public boolean checkNeighbors(Word w) {

        int i;
        if (w.vertical) {
            //first letter
            if (w.row > 0 && board[w.row - 1][w.col] != null)
                return true;
            //middle letters
            for (i = w.row; i < w.tiles.length + w.row; i++)
                if (board[i][w.col - 1] != null || board[i][w.col + 1] != null)
                    return true;
            //last letter
            if (i < 14 && board[i][w.col] != null)
                return true;
        }
        if (!w.vertical) {
            if (w.col > 0 && board[w.row][w.col - 1] != null)
                return true;
            for (i = w.col; i < w.tiles.length + w.col; i++)
                if (board[w.row - 1][i] != null || board[w.row + 1][i] != null)
                    return true;
            if (i < 14 && board[w.row][i] != null)
                return true;
        }
        return false;
    }

    /**
     * The boardLegal function checks if the word is legal to be placed on the board.
     * It first checks if the word is within boundaries of the board, then it checks
     * if there are any neighboring words that are connected to this new word. If all these
     * conditions pass, then we know that this new word can be placed on our gameboard.

     *
     * @param w w Pass the word that is being checked for legality
     *
     * @return False if the word is not on the board
     *
     * @docauthor Trelent
     */
    public boolean boardLegal(Word w) {

        //check word size
        if (w.tiles.length < 2)
            return false;

        //check if on board boundaries
        if (w.row < 0 || w.row > 14 || w.col < 0 || w.col > 14)
            return false;

        if (!checkBoundaries(w))
            return false;

        //check if the first word placed on the center star
        if (w.getRow() == 7 && w.getCol() == 7 && firstTurn) {
//        if (w.getRow() == 7 && w.getCol() == 7 && board[7][7] == null) {
            if ((w.vertical && (w.col != 7 || (w.row + w.tiles.length <= 7) || w.row >= 8)) ||
                    (!w.vertical && (w.row != 7 || (w.col + w.tiles.length <= 7) || w.col >= 8))) {
                return false;
            }
            else {
                return true;
            }
        }

        if (!checkNeighbors(w))
            return false;

        return true;
    }

    /**
     * The dictionaryLegal function checks whether a word is legal according to the dictionary.
     *
     *
     * @param w w Pass the word that is being checked for legality
     *
     * @return True if the word is in the dictionary, and false otherwise
     *
     * @docauthor Trelent
     */
    public boolean dictionaryLegal(Word w) {

        StringBuilder text = new StringBuilder("Q," + w.toString());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Host.getModel().getSocketToMyServer().getInetAddress());
        stringBuilder.append(":");
        stringBuilder.append(Host.getModel().getSocketToMyServer().getLocalPort());
        String socketSource = stringBuilder.toString();

        String jsonString = Host.getModel().CreateMessageToGameServer(text.toString(),socketSource);
        Host.getModel().SendMessageToGameServer(jsonString);
        boolean res = false;
        try {
            res = Boolean.parseBoolean(Host.getModel().inputQueueFromGameServer.take());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    /**
     * The getWords function takes in a Word object and returns an ArrayList of all the words that are formed by placing
     * the tiles from the input word onto the board. The function first checks if it is a vertical or horizontal word,
     * then iterates through each tile in w.tiles to find any new words that are formed when placing this tile on top of
     * another letter already on the board. If there is no letter below/to-the-right of this tile, then we know there will be no new word created here so we skip over it. Otherwise, we add every Tile to our temp ArrayList until either
     *
     * @param  w the direction of the word
     *
     * @return An arraylist of word objects
     *
     * @docauthor Trelent
     */
    public ArrayList<Word> getWords(Word w) {

        ArrayList<Word> words = new ArrayList<>();

        if (w.vertical) {

            int i = w.row;

            for (Tile t : w.tiles) {

                int j = w.col;
                ArrayList<Tile> temp = new ArrayList<>();

                // add only new words
                if (t != null) {

                    while (j > 0 && board[i][j - 1] != null)
                        j--;

                    int tempCol = j;
                    while (j < 15 && board[i][j] != null) {
                        temp.add(board[i][j]);
                        j++;
                    }

                    Tile[] tempT = temp.toArray(new Tile[0]);
                    Word tempWord = new Word(tempT, i, tempCol, false);
                    if (!tempWord.toString().equals("")) {
                        if (dictionaryLegal(tempWord) && boardLegal(tempWord)) {
                            words.add(tempWord);
                        }
                        else if (tempWord.getTiles().length > 1) {
                            System.out.println("Error: " + tempWord + " not legal");
                            return null;
                        }
                    }
                }
                i++;
            }
        }
        if (!w.vertical) {

            int i = w.col;

            for (Tile t : w.tiles) {

                int j = w.row;
                ArrayList<Tile> temp = new ArrayList<>();

                if (t != null) {

                    while (j > 0 && board[j - 1][i] != null)
                        j--;

                    int tempRow = j;
                    while (j < 15 && board[j][i] != null) {
                        temp.add(board[j][i]);
                        j++;
                    }

                    Tile[] tempT = temp.toArray(new Tile[0]);
                    Word tempWord = new Word(tempT, tempRow, i, true);
                    if (!tempWord.toString().equals("")) {
                        if (dictionaryLegal(tempWord) && boardLegal(tempWord)) {
                            words.add(tempWord);
                        }
                        else if (tempWord.getTiles().length > 1) {
                            System.out.println("Error: " + tempWord + " not legal");
                            return null;
                        }
                    }
                }
                i++;
            }
        }

        return words;
    }

    /**
     * The getWordsForChallenge function takes in a Word object and returns an ArrayList of all the words that are formed
     * by the tiles on the board. The function first checks if it is a vertical word, then iterates through each tile in
     * w.tiles and adds all new words to an ArrayList called &quot;words&quot;. If it is not a vertical word, then we do the same thing
     * but for horizontal words instead. We also add w itself to this list because we want to challenge every single word that was played on this turn.

     *
     * @param w w Determine the row and column of the word

     *
     * @return An arraylist of all the words that are formed by placing a word on the board
     *
     * @docauthor Trelent
     */
    public ArrayList<Word> getWordsForChallenge(Word w) {

        ArrayList<Word> words = new ArrayList<>();

        if (w.vertical) {

            int i = w.row;

            for (Tile t : w.tiles) {

                int j = w.col;
                ArrayList<Tile> temp = new ArrayList<>();

                // add only new words
                if (t != null) {

                    while (j > 0 && board[i][j - 1] != null)
                        j--;

                    int tempCol = j;
                    while (j < 15 && board[i][j] != null) {
                        temp.add(board[i][j]);
                        j++;
                    }

                    Tile[] tempT = temp.toArray(new Tile[0]);
                    Word tempWord = new Word(tempT, i, tempCol, false);
                    if (!tempWord.toString().equals("") && boardLegal(tempWord)) {
                        words.add(tempWord);
                    }
                }
                i++;
            }
        }
        if (!w.vertical) {

            int i = w.col;

            for (Tile t : w.tiles) {

                int j = w.row;
                ArrayList<Tile> temp = new ArrayList<>();

                if (t != null) {

                    while (j > 0 && board[j - 1][i] != null)
                        j--;

                    int tempRow = j;
                    while (j < 15 && board[j][i] != null) {
                        temp.add(board[j][i]);
                        j++;
                    }

                    Tile[] tempT = temp.toArray(new Tile[0]);
                    Word tempWord = new Word(tempT, tempRow, i, true);
                    if (!tempWord.toString().equals("") && boardLegal(tempWord)) {
                        words.add(tempWord);
                    }
                }
                i++;
            }
        }


        ArrayList<Tile> tiles = new ArrayList<>();
        int row = w.getRow();
        int col = w.getCol();
        for (Tile t: w.tiles) {
            tiles.add(board[row][col]);
            if (w.isVertical()) {
                row++;
            }
            else {
                col++;
            }
        }
        Tile[] tempT = tiles.toArray(new Tile[0]);
        Word tempWord = new Word(tempT, w.getRow(), w.getCol(), w.isVertical());
        words.add(tempWord);
        return words;
    }

    /**
     * The getScore function takes in a Word object and returns the score of that word.
     * The function first checks if the word is legal by checking if it is in the dictionary.
     * If it isn't, then 0 is returned because no points are awarded for an illegal word.
     * Otherwise, we check to see if there are any other words formed when placing this new word on the board.
     * If there aren't any other words formed (i.e., this was our first turn), then we just calculate and return
     * the score of this one new word placed on our board without worrying about bonuses or anything else since
     *
     * @param //word Get the row, column and orientation of the word
     *
     * @return The score of the word w if it is placed on the board
     *
     * @docauthor Trelent
     */
    public int getScore(Word w) {

        int sum = 0;

        int row = w.getRow();
        int col = w.getCol();
        Tile[] tempTiles = new Tile[w.getTiles().length];
        int count = 0;
        for (Tile tile : w.getTiles()) {

            if (board[row][col] == null) {
                tempTiles[count] = tile;
            }
            else {
                tempTiles[count] = board[row][col];
            }

            count++;

            if (w.isVertical()) {
                row++;
            }
            else {
                col++;
            }
        }

        Word tempWord = new Word(tempTiles, w.getRow(), w.getCol(), w.isVertical());
        if (!dictionaryLegal(tempWord)) {
            return 0;
        }

        ArrayList<Word> words = new ArrayList<>();
        // don't check for other words in first turn
        if (firstTurn) {

            firstTurn = false;
        }
        else {

            // getWords return null if one of the words that try to place is not legal
            words = getWords(w);
            if (words == null) {
                return 0;
            }
        }
        words.add(tempWord);

        for (Word word : words) {

            System.out.println(word);

            int i = 0;
            int wordBonus = 1; // total word bonuses
            int wordScore = 0;

            for (Tile t : word.tiles) {

                if (word.vertical) {

                    switch (bonusBoard[word.row + i][word.col]) {

                        case 'R':
                            wordScore += t.score;
                            wordBonus *= 3;
                            break;

                        case 'Y':
                        case 'S':
                            wordScore += t.score;
                            wordBonus *= 2;
                            bonusBoard[7][7] = ' ';
                            break;

                        case 'B':
                            wordScore += t.score * 3;
                            break;

                        case 'A':
                            wordScore += t.score * 2;
                            break;

                        default:
                            wordScore += t.score;
                            break;
                    }
                }
                if (!word.vertical) {

                    switch (bonusBoard[word.row][word.col + i]) {

                        case 'R':
                            wordScore += t.score;
                            wordBonus *= 3;
                            break;

                        case 'Y':
                        case 'S':
                            wordScore += t.score;
                            wordBonus *= 2;
                            bonusBoard[7][7] = ' ';
                            break;

                        case 'B':
                            wordScore += t.score * 3;
                            break;

                        case 'A':
                            wordScore += t.score * 2;
                            break;

                        default:
                            wordScore += t.score;
                            break;
                    }
                }
                i++;
            }
            sum += (wordScore * wordBonus);
        }
        return sum;
    }


    /**
     * The tryPlaceWord function takes a Word object as an argument and returns the score of that word.
     * It first checks if the board is empty, in which case it sets firstTurn to true. Then it calls
     * boardLegal to check if the word can be placed on the board, and then gets its score by calling getScore.
     * If all these conditions are met, then tryPlaceWord places each tile from that word onto its corresponding spot on
     * the gameboard (by setting each element of 2D array &quot;board&quot; equal to a Tile object). Finally, tryPlaceWord returns
     * either 0 or this score
     *
     * @param  word Get the row and column of the word
     *
     * @return The score of the word if it is legal, and 0 otherwise
     *
     * @docauthor Trelent
     */
    public int tryPlaceWord(Word word) {

        if (board[7][7] == null) {
            firstTurn = true;
        }

        if (boardLegal(word)) {
            int score = getScore(word);
            if (score > 0) {

                int row = word.getRow();
                int col = word.getCol();
                for (Tile t : word.getTiles()) {

                    if (t != null) {
                        board[row][col] = t;
                    }
                    if (word.isVertical()) {
                        row++;
                    }
                    else {
                        col++;
                    }
                }
            }
            return score;
        }

        return 0;
    }

    /**
     * The parseBoardToString function takes a 2D array of Tiles and returns a String representation
     * of the board. The string is formatted as follows:
     * 	- Each row is separated by a newline character '\n'
     *  - If the tile at position (i, j) exists, then its letter should be printed in place of the '.' character. Otherwise, print out '.'.

     *
     * @param  board Represent the board
     *
     * @return A string representation of the board
     *
     * @docauthor Trelent
     */
    public String parseBoardToString(Tile[][] board) {
        StringBuilder sb = new StringBuilder();

        int rows = board.length;
        int cols = board[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile tile = board[i][j];
                sb.append(tile != null ? tile.letter : '.');
            }
            sb.append('\n');
        }

        return sb.toString();
    }


    /**
     * The parseBoardToCharacterArray function takes in a 2D array of Tile objects and returns a 2D array of Characters.
     * The function iterates through the board, and for each tile it finds, it adds the letter to the characterBoard.
     * If there is no tile at that position on the board, then null is added to that position in characterBoard instead.

     *
     * @param  board Represent the board that is passed in
     *
     * @return A 2d array of characters
     *
     * @docauthor Trelent
     */
    public Character[][] parseBoardToCharacterArray(Tile[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        Character[][] characterBoard = new Character[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Tile tile = board[i][j];
                characterBoard[i][j] = (tile != null) ? tile.letter : null;
            }
        }

        return characterBoard;
    }

    /**
     * The parseCharacterArrayToString function takes a 2D array of characters and returns a string representation
     * of the board. The function is used to print out the current state of the game board.
     *
     *
     * @param  board Represent the board
     *
     * @return A string representation of the board
     *
     * @docauthor Trelent
     */
    public String parseCharacterArrayToString(Character[][] board) {
        StringBuilder sb = new StringBuilder();

        int rows = board.length;
        int cols = board[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Character tile = board[i][j];
                sb.append((tile != null) ? tile : '.');
            }
            sb.append('\n');
        }

        return sb.toString();
    }


    // function for GUI
    /**
     * The placeTile function places a tile on the board.
     *
     *
     * @param  selectedTile Place the tile on the board
     * @param  row Determine the row that the tile will be placed in
     * @param  column Represent the column of the board that is being placed
     *
     * @return Nothing, so it is void
     *
     * @docauthor Trelent
     */
    public void placeTile(Tile selectedTile, int row, int column) {

        board[row][column] = selectedTile;
    }
    /**
     * The removeTile function removes a tile from the board.
     *
     *
     * @param  row Determine which row the tile is in
     * @param  column Specify the column of the tile to be removed
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void removeTile(int row, int column) {

        board[row][column] = null;
    }
}