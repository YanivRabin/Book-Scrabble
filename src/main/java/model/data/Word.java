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
     * @param Tile[] ts Store the tiles in the word
     * @param int r Set the row value of the word object
     * @param int c Set the column of the word
     * @param boolean vert Determine if the word is vertical or horizontal
     *
     * @return The tiles, row, column and boolean value of the word
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
     * @return The array of tiles
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
     * @return True if the line is vertical, false otherwise
     *
     * @docauthor Trelent
     */
    public boolean isVertical() { return vertical; }
    /**
     * The getRow function returns the row of the cell.
     *
     *
     *
     * @return The row number
     *
     * @docauthor Trelent
     */
    public int getRow() { return row; }
    /**
     * The getCol function returns the column of the current position.
     *
     *
     *
     * @return The column of the current position
     *
     * @docauthor Trelent
     */
    public int getCol() { return col; }

    /**
     * The equals function checks if two words are equal.
     *
     *
     * @param Object o Compare the current object with the parameter
     *
     * @return True if the two words are equal
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
     * The toString function returns a string representation of the Rack object.
     *
     *
     *
     * @return A string of the letters in the tiles array
     *
     * @docauthor Trelent
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (Tile tile : this.tiles)
            sb.append(tile.letter);

        return sb.toString();
    }
}
