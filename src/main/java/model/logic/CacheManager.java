package model.logic;
import model.logic.CacheReplacementPolicy;

import java.util.HashSet;


public class CacheManager {

    int size;
    CacheReplacementPolicy crp;
    HashSet<String> cache;

    public CacheManager(int s, CacheReplacementPolicy c) {

        size = s;
        crp = c;
        cache = new HashSet<>();
    }

    public boolean query(String str) { return cache.contains(str); }

    public void add(String str) {

        if (cache.size() >= size)
            cache.remove(crp.remove());

        crp.add(str);
        cache.add(str);
    }
}
