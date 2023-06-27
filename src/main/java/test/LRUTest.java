package test;

import model.logic.CacheReplacementPolicy;
import model.logic.LRU;

public class LRUTest {

    /**
     * The lruTest function tests the LRU class by adding four elements to the cache,
     * and then removing one element. The function checks that the removed element is
     * equal to &quot;b&quot;. If it is not, an error message will be printed.

     *
     *
     * @return &quot;wrong implementation for lru&quot;
     *
     * @docauthor Trelent
     */
    public static void lruTest() {

        CacheReplacementPolicy lru = new LRU();
        lru.add("a");
        lru.add("b");
        lru.add("c");
        lru.add("a");

        if(!lru.remove().equals("b"))
            System.out.println("wrong implementation for LRU");
    }
}
