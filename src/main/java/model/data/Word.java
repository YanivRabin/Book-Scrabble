package model.data;


import java.util.Arrays;
import java.util.Objects;

public class Word {

    Tile[] tiles;
    boolean vertical;
    int row, col;

    /**
     * The Word function returns the word that is formed by the tiles in a Word object.
     *
     *
     * @param Tile[] ts Store the tiles that make up a word
     * @param int r Set the row of the word
     * @param int c Set the column of the word
     * @param boolean vert Determine the orientation of the word
     *
     * @return The tiles, row, column and verticality of the word
     *
     * @docauthor Trelent
     */
    public Word(Tile[] ts, int r, int c, boolean vert) {

        tiles = ts;
        vertical = vert;
        row = r;
        col = c;
    }


    /**
     * The getTiles function returns the tiles array.
     *
     *
     *
     * @return The tiles array
     *
     * @docauthor Trelent
     */
    public Tile[] getTiles() { return tiles; }
    /**
     * The isVertical function returns a boolean value that indicates whether the
     * current instance of the class is vertical or not.
     *
     *
     *
     * @return True if the line is vertical, and false otherwise
     *
     * @docauthor Trelent
     */
    public boolean isVertical() { return vertical; }
    /**
     * The getRow function returns the row of the cell.
     *
     *
     *
     * @return The row number of the cell
     *
     * @docauthor Trelent
     */
    public int getRow() { return row; }
    /**
     * The getCol function returns the column of the current position.
     *
     *
     *
     * @return The value of the col variable
     *
     * @docauthor Trelent
     */
    public int getCol() { return col; }

    /**
     * The equals function checks if two words are equal.
     *
     *
     * @param o o Compare the current object to another object
     *
     * @return True if the two objects are equal
     *
     * @docauthor Trelent
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return vertical == word.vertical && row == word.row && col == word.col && Arrays.equals(tiles, word.tiles);
    }

    /**
     * The toString function is used to print out the board in a readable format.
     * It takes each tile on the board and prints it's letter, or an underscore if there is no tile.

     *
     *
     * @return The letters of the tiles that are in the rack
     *
     * @docauthor Trelent
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (Tile tile : this.tiles) {

            if (tile == null) {
                sb.append("_");
            }
            else {
                sb.append(tile.letter);
            }
        }

        return sb.toString();
    }
}