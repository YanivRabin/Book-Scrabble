package model.logic;
import java.util.*;


public class LRU implements CacheReplacementPolicy{

    LinkedHashSet<String> words = new LinkedHashSet<>();

    /**
     * The add function adds a word to the list of words.
     *
     *
     * @param  word Add a word to the list of words
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    @Override
    public void add(String word) {

        words.remove(word);
        words.add(word);
    }

    /**
     * The remove function removes the first element in the list.
     *
     *
     *
     * @return The next word in the list
     *
     * @docauthor Trelent
     */
    @Override
    public String remove() {

        Iterator<String> itr = words.iterator();
        String temp = itr.next();
        words.remove(itr.next());

        return temp;
    }
}
