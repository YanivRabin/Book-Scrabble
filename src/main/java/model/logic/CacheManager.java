package model.logic;
import model.logic.CacheReplacementPolicy;

import java.util.HashSet;


public class CacheManager {

    int size;
    CacheReplacementPolicy crp;
    HashSet<String> cache;

    /**
     * The CacheManager function is responsible for managing the cache.
     * It will add a new page to the cache if it does not already exist in the cache, and update its frequency.
     * If it does exist, then we simply update its frequency.

     *
     * @param int s Set the size of the cache
     * @param CacheReplacementPolicy c Determine which cache replacement policy to use
     *
     * @return A new cachemanager object with the given size and replacement policy
     *
     * @docauthor Trelent
     */
    public CacheManager(int s, CacheReplacementPolicy c) {

        size = s;
        crp = c;
        cache = new HashSet<>();
    }

    /**
     * The query function takes a string as input and returns true if the string is in the cache,
     * false otherwise.

     *
     * @param String str Check if the cache contains a specific string
     *
     * @return True if and only if for some k &gt;= 1, the last k characters queried (in order from oldest to newest, including this letter just queried) spell one of the strings in the given list
     *
     * @docauthor Trelent
     */
    public boolean query(String str) { return cache.contains(str); }

    /**
     * The add function adds a string to the cache.
     * If the cache is full, it removes an element from the cache using crp's remove function.
     * Then, it adds str to both crp and cache.

     *
     * @param String str Add a string to the cache
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public void add(String str) {

        if (cache.size() >= size)
            cache.remove(crp.remove());

        crp.add(str);
        cache.add(str);
    }
}
