package test;

import model.logic.CacheReplacementPolicy;
import model.logic.LFU;

public class LFUTest {

    /**
     * The lfuTest function tests the LFU cache replacement policy.
     * It adds 5 elements to the cache, and then removes one element.
     * If it is not removed in the correct order, an error message will be printed out.

     *
     *
     * @return &quot;wrong implementation for lfu&quot;
     *
     * @docauthor Trelent
     */
    public static void lfuTest() {

        CacheReplacementPolicy lfu = new LFU();
        lfu.add("a");
        lfu.add("b");
        lfu.add("b");
        lfu.add("c");
        lfu.add("a");

        if(!lfu.remove().equals("c"))
            System.out.println("wrong implementation for LFU");
    }
}
