package model.data;

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

        dictionary = new Dictionary(
                "/Users/yaniv/Documents/IntelliJ/Book-Scrabble/src/main/java/test/mobydick.txt",
                "/Users/yaniv/Documents/IntelliJ/Book-Scrabble/src/main/java/test/shakespeare.txt",
                "/Users/yaniv/Documents/IntelliJ/Book-Scrabble/src/main/java/test/Harry_Potter.txt",
                "/Users/yaniv/Documents/IntelliJ/Book-Scrabble/src/main/java/test/pg10.txt"
        );
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
        public static final Host model_instance = new Host();
    }

    public static Host getBoardModel() {
        return Board.BoardModelHelper.model_instance;
    }



    public Tile[][] getTiles() {
        return board.clone();
    }

    public boolean checkBoundaries(Word w) {

        if (w.vertical)
            if (w.tiles.length + w.row > 14)
                return false;

        if (!w.vertical)
            if (w.tiles.length + w.col > 14)
                return false;

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

    public boolean checkEmptyTile(Word w) {

        //return true if there is old tiles in the right place

        if (w.vertical) {

            int i = w.row;
            for (Tile t : w.tiles) {

                if (t == null){
                    if (board[i][w.col] == null)
                        return false;
                }
                else{
                    if (board[i][w.col] != null)
                        return false;
                }
                i++;
            }
        }
        if (!w.vertical) {

            int i = w.col;
            for (Tile t : w.tiles) {

                if (t == null)
                    if (board[w.row][i] == null)
                        return false;
                    else
                    if (board[i][w.row] != null)
                        return false;
                i++;
            }
        }
        return true;
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

        // for GUI check
        if (!dictionary.query(w.toString()))
            return false;

        if (!dictionary.challenge(w.toString()))
            return false;

        return true;
    }

//        StringBuilder text = new StringBuilder("Q," + w.toString());
//        Host.getModel().OutToServer(text.toString());
//        boolean res;
//        try {
//            res = Host.getModel().InFromServer().equals("true");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return res;
//    }
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
