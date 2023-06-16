package model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Tile {

    public final char letter;
    public final int score;

    private Tile(char letter, int score) {

        this.letter = letter;
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return letter == tile.letter && score == tile.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(letter, score);
    }

    //inner class bag
    public static class Bag {

        private static Bag singleBag = null;
        int[] letterQuantity =  {9,  2,  2,  4,  12, 2,  3,  2,  9,  1,  1,  4,  2,  6,  8,  2,  1,  6,  4,  6,  4,  2,  2,  1,  2,  1 };
        final int[] checkQuantity =  {9,  2,  2,  4,  12, 2,  3,  2,  9,  1,  1,  4,  2,  6,  8,  2,  1,  6,  4,  6,  4,  2,  2,  1,  2,  1 };
        final Tile[] tiles = new Tile[26];

        private Bag() {

            char[] letters =    {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
            int[] letterScore = { 1,  3,  3,  2,  1,  4,  2,  4,  1,  8,  5,  1,  3,  1,  1,  3,  10, 1,  1,  1,  1,  4,  4,  8,  4,  10};

            for (int i = 0; i < 26; i++)
                tiles[i] = new Tile(letters[i], letterScore[i]);
        }

        public Tile getRand() {

            int rnd;

            if (bagSize() == 0)
                return null;

            do { rnd = new Random().nextInt(tiles.length); } while (letterQuantity[rnd] == 0);
            letterQuantity[rnd]--;
            return tiles[rnd];
        }

        public static Bag getBag() {

            // // create new bag, if already created then return the current
            if (singleBag == null)
                singleBag = new Bag();

            return singleBag;
        }


        private static class BagModelHelper {
            public static final Bag model_instance = new Bag();
        }

        public static Bag getBagModel() {
            return Bag.BagModelHelper.model_instance;
        }


        public int bagSize() {

            int size = 0;
            for (int s : letterQuantity)
                size += s;

            return size;
        }

        public int[] getQuantities() { return letterQuantity.clone(); }

        public Tile getTile(char c) {

            // check if the letter is valid and that there's no 0 amount of that letter
            if (c < 'A' || c > 'Z' || letterQuantity[c - 'A'] < 1)
                return null;

            letterQuantity[c - 'A']--;
            return tiles[c - 'A'];
        }
        public Tile getTileForTileArray(char c) {

            // check if the letter is valid and that there's no 0 amount of that letter
            if (c < 'A' || c > 'Z' || letterQuantity[c - 'A'] < 1)
                return null;

            return tiles[c - 'A'];
        }



        public void put(Tile t) {

            if (checkQuantity[t.letter - 'A'] > letterQuantity[t.letter - 'A'])
                letterQuantity[t.letter - 'A']++;
        }
    }
}