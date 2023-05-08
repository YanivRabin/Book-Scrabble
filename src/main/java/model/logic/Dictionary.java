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

    public Dictionary(String... args) {

        lru = new CacheManager(400, new LRU());
        lfu = new CacheManager(100, new LFU());
        bloomFilter = new BloomFilter(256,"MD5","SHA1");
        files = args;

        for (String file : files) {

            try {

                Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
                String[] arrWords;
                while (scanner.hasNext()) {

                    arrWords = scanner.next().split(" ");
                    for (String word : arrWords)
                        bloomFilter.add(word);
                }
                scanner.close();
            }
            catch (FileNotFoundException e) { e.printStackTrace(); }
        }
    }

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
