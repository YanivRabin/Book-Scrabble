package model.logic;
import java.util.*;


public class LFU implements CacheReplacementPolicy {

    LinkedHashMap<String, Integer> words = new LinkedHashMap<>();

    /**
     * The add function adds a word to the map. If the word is already in the map,
     * it increments its value by 1. Otherwise, it creates a new entry with value 1.

     *
     * @param  word Add a word to the map
     *
     * @return Nothing, but it will add the word to the map if it is not already there
     *
     * @docauthor Trelent
     */
    @Override
    public void add(String word) {

        if (words.containsKey(word))
            words.replace(word, words.get(word), words.get(word) + 1);
        else
            words.put(word, 1);
    }

    /**
     * The remove function removes the least frequently used word from the map.
     *
     *
     *
     * @return The string that is removed from the map
     *
     * @docauthor Trelent
     */
    @Override
    public String remove() {

        Iterator<String> itr = words.keySet().iterator();
        int min = words.get(itr.next());
        String str = itr.next();
        for (Map.Entry<String, Integer> i : words.entrySet()) {

            if (i.getValue() < min) {

                min = i.getValue();
                str = i.getKey();
            }
        }
        if (min > 1)
            words.replace(str, min, min - 1);
        else
            words.remove(str);

        return str;
    }
}
