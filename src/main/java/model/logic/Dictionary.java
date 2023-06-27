package model.logic;
import model.logic.IOSearcher;
import model.logic.LFU;
import model.logic.LRU;

import java.io.*;
import java.util.Scanner;


public class Dictionary {

    CacheManager lru, lfu;
    BloomFilter bloomFilter;
    String[] files;

    /**
     * The Dictionary function takes in a list of files and creates a bloom filter
     * from the words in those files. It also creates two caches, one for LRU and
     * one for LFU. The function then returns nothing.

     *
     * @param args args Pass an array of strings as a parameter to the function
     *
     * @return The dictionary object
     *
     * @docauthor Trelent
     */
    public Dictionary(String... args) {

        lru = new CacheManager(400, new LRU());
        lfu = new CacheManager(100, new LFU());
        bloomFilter = new BloomFilter(65536,"MD5","SHA1", "SHA-256", "SHA-512");
        files = args;
        for (String file : files) {

            try {

                Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
                String[] arrWords;
                while (scanner.hasNext()) {

                    arrWords = scanner.next().split(" ");
                    for (String word : arrWords)
                        bloomFilter.add(word.toUpperCase());
                }
                scanner.close();
            }
            catch (FileNotFoundException e) { e.printStackTrace(); }
        }
    }

    private static class DictionaryModelHelper {
        public static final Dictionary model_instance = new Dictionary();
    }

    /**
     * The getModel function returns the model instance of the Dictionary class.
     *
     *
     *
     * @return The model_instance variable
     *
     * @docauthor Trelent
     */
    public static Dictionary getModel() {
        return DictionaryModelHelper.model_instance;
    }

    /**
     * The query function checks if the string is in the exist list, not exist list or bloom filter.
     * If it is in the exist list, return true.
     * If it is in the not exist list, return false.
     * If it is in bloom filter but neither of two lists above, add to lru and return true; otherwise add to lfu and return false;

     *
     * @param str str Query the bloom filter
     *
     * @return True if the string is in the data structure,
     *
     * @docauthor Trelent
     */
    public boolean query(String str) {

        //check if string in the exist list
        if (lru.query(str))
            return true;

        //check if string in the not exist list
        else if (lfu.query(str))
            return false;

        //if exist in bloom filter, add to exist list
        else if (bloomFilter.contains(str)) {

            lru.add(str);
            return true;
        }

        //if not exist in bloom filter, add to not exist list
        else {

            lfu.add(str);
            return false;
        }
    }

    /**
     * The challenge function takes in a string and searches for it in the files.
     * If the string is found, then it adds that to the lru cache and returns true.
     * If not, then it adds that to the lfu cache and returns false.

     *
     * @param str str Search through the files and see if it is in there
     *
     * @return True if the string is found in the file
     *
     * @docauthor Trelent
     */
    public boolean challenge(String str) {

        if(IOSearcher.search(str, files)) {

            lru.add(str);
            return true;
        }
        else {

            lfu.add(str);
            return false;
        }
    }
}
