package test;

import java.io.IOException;

import static test.BagTest.bagTest;
import static test.BloomFilterTest.bloomFilterTest;
import static test.BoardTest.boardTest;
import static test.CacheManagerTest.cacheManagerTest;
import static test.DictionaryManagerTest.dictionaryManagerTest;
import static test.DictionaryTest.dictionaryTest;
import static test.IOSearchTest.iOSearchTest;
import static test.LFUTest.lfuTest;
import static test.LRUTest.lruTest;
import static test.MyServerTest.serverTest;


public class MainTrain {

    /**
     * The main function is used to test the functionality of all the classes in this project.
     *
     *
     * @param  args Pass command line arguments to the main function
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public static void main(String[] args) throws IOException {

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

//        serverTest();
//        dictionaryManagerTest();

    }

}
