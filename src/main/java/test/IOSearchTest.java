package test;

import model.logic.IOSearcher;

import java.io.FileWriter;
import java.io.PrintWriter;

public class IOSearchTest {

    /**
     * The iOSearchTest function tests the IOSearcher class.
     * It creates two text files, one with a string of words and another with a different string of words.
     * Then it searches for the word &quot;is&quot; in both files using the search function from IOSearcher and prints out whether or not it was found.
     * Next, it searches for the word &quot;cat&quot; in both files using the search function from IOSearcher and prints out whether or not it was found.

     *
     *
     * @return The following:
     *
     * @docauthor Trelent
     */
    public static void iOSearchTest() throws Exception {

        String words1 = "the quick brown fox \n jumps over the lazy dog";
        String words2 = "A Bloom filter is a space efficient probabilistic data structure, \n conceived by Burton Howard Bloom in 1970";
        PrintWriter out = new PrintWriter(new FileWriter("text1.txt"));
        out.println(words1);
        out.close();
        out = new PrintWriter(new FileWriter("text2.txt"));
        out.println(words2);
        out.close();

        if(!IOSearcher.search("is", "text1.txt","text2.txt"))
            System.out.println("IOsearch did not found a word");

        if(IOSearcher.search("cat", "text1.txt","text2.txt"))
            System.out.println("IOsearch found a word that does not exist");
    }
}
