package model.logic;
import java.util.*;


public class LFU implements CacheReplacementPolicy {

    LinkedHashMap<String, Integer> words = new LinkedHashMap<>();

    @Override
    public void add(String word) {

        if (words.containsKey(word))
            words.replace(word, words.get(word), words.get(word) + 1);
        else
            words.put(word, 1);
    }

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
