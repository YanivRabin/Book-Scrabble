package model.data;


import java.util.Arrays;
import java.util.Objects;

public class Word {

    Tile[] tiles;
    boolean vertical;
    int row, col;

    public Word(Tile[] ts, int r, int c, boolean vert) {

        tiles = ts;
        vertical = vert;
        row = r;
        col = c;
    }


    public Tile[] getTiles() { return tiles; }
    public boolean isVertical() { return vertical; }
    public int getRow() { return row; }
    public int getCol() { return col; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return vertical == word.vertical && row == word.row && col == word.col && Arrays.equals(tiles, word.tiles);
    }

    /*@Override
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