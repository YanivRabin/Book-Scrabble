package model.data;

import model.logic.Host;

import java.io.IOException;
import java.util.ArrayList;

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
     * @return The board object
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
     * The getBoardModel function returns the board model.
     *
     *
     *
     * @return The value of the boardmodelhelper
     *
     * @docauthor Trelent
     */
    public static Board getBoardModel() {
        return Board.BoardModelHelper.model_instance;
    }



    public Tile[][] getTiles() {
        return board.clone();
    }

    /**
     * The checkBoundaries function checks to see if the word is within the boundaries of the board.
     *
     *
     * @param Word w Get the length of the word
     *
     * @return False if the word is placed outside of the board
     *
     * @docauthor Trelent
     */
    public boolean checkBoundaries(Word w) {

        if (w.vertical){
            if (w.tiles.length + w.row > 14){
                return false;
            }
        }
        if (!w.vertical){
            if (w.tiles.length + w.col > 14){
                return false;
            }
        }
        return true;
    }

    /**
     * The checkNeighbors function checks to see if the word being placed is touching another word.
     *
     *
     * @param Word w Pass in the word that is being checked
     *
     * @return True if the word is adjacent to another
     *
     * @docauthor Trelent
     */
    public boolean checkNeighbors(Word w) {
    // part of another word

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
//
//    /**
//     * The checkEmptyTile function checks if there are any old tiles in the right place.
//     *
//     *
//     * @param Word w Get the row and column of the word to be placed on the board
//     *
//     * @return True if the tiles of a word are placed in empty tiles on the board
//     *
//     * @docauthor Trelent
//     */
//    public boolean checkEmptyTile(Word w) {
//
//        //return true if there is old tiles in the right place
//
//        if (w.vertical) {
//
//            int i = w.row;
//            for (Tile t : w.tiles) {
//
//                if (t == null){
//                    if (board[i][w.col] == null)
//                        return false;
//                }
//                else{
//                    if (board[i][w.col] != null)
//                        return false;
//                }
//                i++;
//            }
//        }
//        if (!w.vertical) {
//
//            int i = w.col;
//            for (Tile t : w.tiles) {
//
//                if (t == null){
//                    if (board[w.row][i] == null){
//                        return false;
//                    }
//                }
//                else{
//                    if (board[w.row][i] != null){
//                        return false;
//                    }
//                }
//                i++;
//            }
//        }
//        return true;
//    }

    /**
     * The boardLegal function checks if the word is legal to be placed on the board.
     * It first checks if the word is within boundaries of the board, then it checks
     * if there are any empty tiles in between words that are already on the board.
     * Finally, it makes sure that all letters in a word touch at least one other letter.

     *
     * @param Word w Check if the word is legal on the board
    public boolean checkboundaries(word w) {


     *
     * @return True if the word is legal on the board, false otherwise
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
        if (board[7][7] == null && firstTurn) {
            if ((w.vertical && (w.col != 7 || (w.row + w.tiles.length <= 7) || w.row >= 8)) ||
                    (!w.vertical && (w.row != 7 || (w.col + w.tiles.length <= 7) || w.col >= 8))){
                return false;
            }
            else{
                firstTurn = false;
                return true;
            }
        }

        if (!checkNeighbors(w))
            return false;

/*        if (!checkEmptyTile(w))
            return false;*/

        return true;
    }

    /**
     * The dictionaryLegal function checks if the word is in the dictionary.
     *
     *
     * @param Word w Pass in the word object that is being checked
        public boolean dictionarylegal(word w) {

            stringbuilder text = new stringbuilder(&quot;q,&quot; + w
     *
     * @return True if the word is legal according to
     *
     * @docauthor Trelent
     */
    public boolean dictionaryLegal(Word w) {

//      w is word object
//      w = word, row, col, vert

        StringBuilder text = new StringBuilder("Q," + w.toString());


        Host.getModel().SendMessageToGameServer(text.toString());
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        boolean res;
        try {
            res = Host.getModel().GetMessageFromGameServer().equals("true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    /**
     * The getWords function takes in a Word object and returns an ArrayList of all the words that are formed by placing
     * the tiles from this word on the board. The first element of this ArrayList is always w itself, since it is a word
     * that has been placed on the board. The rest of these elements are new words formed by placing w's tiles onto existing
     * letters already on the board. If there are no other words formed, then getWords will return an ArrayList with only one element: w itself.

     *
     * @param Word w Get the words that are formed by placing

     *
     * @return An arraylist of word objects
     *
     * @docauthor Trelent
     */
    public ArrayList<Word> getWords(Word w) {

        ArrayList<Word> words = new ArrayList<>();
        words.add(w);

        if (w.vertical) {

            int i = w.row;

            //w.tile = [w,i,n]
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

        //make the new word full word if there is nulls

        int i = 0;
        for (Tile t : w.tiles) {

            if (w.vertical) {

                if (t == null)
                    w.tiles[i] = board[w.row + i][w.col];

                i++;
            }
            if (!w.vertical) {

                if (t == null)
                    w.tiles[i] = board[w.row][w.col + i];

                i++;
            }
        }

        return words;
    }

    /**
     * The getScore function takes a Word object as an argument and returns the score of that word.
     * The function first creates an ArrayList of words from the given Word object, then iterates through each word in this list.
     * For each letter in a given word, it checks if there is any bonus on that tile (i.e., if it's on a red or yellow square).
     * If so, it multiplies the score by 2 or 3 accordingly and adds this to its running total for that particular word.
     * It also keeps track of whether there are multiple bonuses for one particular letter (i.e., if
     *
     * @param Word w Pass the word that is being played to the getscore function
     *
     * @return The total score of the word
     *
     * @docauthor Trelent
     */
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
                    //bonusBoard[word.row + i][word.col] = ' ';
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
                    //bonusBoard[word.row][word.col + i] = ' ';
                }
                i++;
            }
            sum += (wordScore * wordBonus);
        }
        return sum;
    }

    /**
     * The tryPlaceWord function takes a Word object as an argument and returns the score of that word if it is placed on the board.
     * If the word cannot be placed, 0 is returned.
     *
     *
     * @param Word w Pass the word that is being placed on the board
     *
     * @return The score of the word if it fits on the board
     *
     * @docauthor Trelent
     */
    public int tryPlaceWord(Word word) {

        if (boardLegal(word) && dictionaryLegal(word)) {
            return getScore(word);
        }

        return 0;
    }

    public void placeTile(Tile selectesTile, int row, int col) {
        board[row][col] = selectesTile;
    }

    public void removeTile(int row, int col) {
        board[row][col] = null;
    }
}
