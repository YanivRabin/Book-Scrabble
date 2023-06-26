package test;

import model.logic.CacheManager;
import model.logic.LRU;

public class CacheManagerTest {

    /**
     * The cacheManagerTest function tests the CacheManager class.
     * It creates a new CacheManager object with 3 slots and an LRU replacement policy,
     * then queries for three nonexistent keys, which should all return false.
     * Then it adds those same three keys to the cache manager and queries for them again, which should all return true.
     * Finally it checks that adding a fourth key will evict the first one from the cache manager (LRU), so that querying for &quot;a&quot; returns false but querying for &quot;d&quot; returns true.  The function prints out error messages if any of these tests fail; otherwise it does nothing at
     *
     *
     * @return True
     *
     * @docauthor Trelent
     */
    public static void cacheManagerTest() {

        CacheManager exists=new CacheManager(3, new LRU());
        boolean b = exists.query("a");
        b|=exists.query("b");
        b|=exists.query("c");

        if(b)
            System.out.println("wrong result for CacheManager first queries");

        exists.add("a");
        exists.add("b");
        exists.add("c");

        b=exists.query("a");
        b&=exists.query("b");
        b&=exists.query("c");

        if(!b)
            System.out.println("wrong result for CacheManager second queries");

        boolean bf = exists.query("d"); // false, LRU is "a"
        exists.add("d");
        boolean bt = exists.query("d"); // true
        bf|= exists.query("a"); // false
        exists.add("a");
        bt &= exists.query("a"); // true, LRU is "b"

        if(bf || ! bt)
            System.out.println("wrong result for CacheManager last queries");

    }
}
