package model.data;

import model.logic.Dictionary;
import model.logic.DictionaryManager;
import model.logic.Host;
import java.io.IOException;
import java.util.ArrayList;

public class Board {

    private static Board singleBoard = null;
    Tile[][] board;
    char[][] bonusBoard;

    boolean firstTurn;

//    Dictionary dictionary;

    public Board() {

//        dictionary = new Dictionary("src/main/resources/books/alice_in_wonderland.txt");

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
        if (w.getRow() == 7 && w.getCol() == 7 && firstTurn) {

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

    public boolean dictionaryLegal(Word w) {

        StringBuilder text = new StringBuilder("Q," + w.toString());
        Host.getModel().SendMessageToGameServer(text.toString());
        boolean res;
        try {
            res = Host.getModel().GetMessageFromGameServer().equals("true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        return res;
        return true;
    }

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

    public int tryPlaceWord(Word word) {

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



    // function for GUI
    public void placeTile(Tile selectedTile, int row, int column) {

        board[row][column] = selectedTile;
    }
    public void removeTile(int row, int column) {

        board[row][column] = null;
    }
}
