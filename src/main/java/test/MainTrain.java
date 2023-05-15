package test;
import static test.BagTest.bagTest;
import static test.BloomFilterTest.bloomFilterTest;
import static test.BoardTest.boardTest;
import static test.CacheManagerTest.cacheManagerTest;
import static test.DictionaryTest.dictionaryTest;
import static test.IOSearchTest.iOSearchTest;
import static test.LFUTest.lfuTest;
import static test.LRUTest.lruTest;


public class MainTrain {

    public static void main(String[] args) {

        bagTest();
        boardTest();
        lruTest();
        lfuTest();
        cacheManagerTest();
        bloomFilterTest();
        try {
            iOSearchTest();
        } catch(Exception e) {
            System.out.println("you got some exception");
        }
        dictionaryTest();
//        if(testServer()) {
//
//            testDM();
//            testBSCH();
//        }
    }

}
