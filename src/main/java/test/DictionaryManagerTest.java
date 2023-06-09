package test;

import model.logic.DictionaryManager;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

public class DictionaryManagerTest {

    /**
     * The dictionaryManagerTest function tests the DictionaryManager class.
     * It creates three files, txt, and adds them to the dictionary manager.
     * Then it checks if a word is in one of the dictionaries using query().
     * Then it challenges a word from one dictionary against another using challenge().

     *
     *
     * @return -5
     *
     * @docauthor Trelent
     */
    public static void dictionaryManagerTest() {
        String t1[]=writeFile("t1.txt");
        String t2[]=writeFile("t2.txt");
        String t3[]=writeFile("t3.txt");

        DictionaryManager dm=DictionaryManager.get();
//
        if(!dm.query("t1.txt","t2.txt",t2[4]))
            System.out.println("problem for Dictionary Manager query (-5)");
//        if(!dm.query("t1.txt","t2.txt",t1[9]))
//            System.out.println("problem for Dictionary Manager query (-5)");
//        if(dm.query("t1.txt","t3.txt","2"+t3[2]))
//            System.out.println("problem for Dictionary Manager query (-5)");
//        if(dm.query("t2.txt","t3.txt","3"+t2[5]))
//            System.out.println("problem for Dictionary Manager query (-5)");
//        if(!dm.challenge("t1.txt","t2.txt","t3.txt",t3[2]))
//            System.out.println("problem for Dictionary Manager challenge (-5)");
//        if(dm.challenge("t2.txt","t3.txt","t1.txt","3"+t2[5]))
//            System.out.println("problem for Dictionary Manager challenge (-5)");
//
//        if(dm.getSize()!=3)
//            System.out.println("wrong size for the Dictionary Manager (-10)");

    }

    /**
     * The writeFile function takes a String name as an argument and returns a String array.
     * The function creates 10 random numbers between 10000 and 20000, stores them in the
     * txt array, then writes them to the file with name &quot;name&quot;. It then returns txt.

     *
     * @param  name Name the file that is being written to
     *
     * @return An array of strings
     *
     * @docauthor Trelent
     */
    public static String[] writeFile(String name) {
        Random r=new Random();
        String txt[]=new String[10];
        for(int i=0;i<txt.length;i++)
            txt[i]=""+(10000+r.nextInt(10000));

        try {
            PrintWriter out=new PrintWriter(new FileWriter(name));
            for(String s : txt) {
                out.print(s+" ");
            }
            out.println();
            out.close();
        }catch(Exception e) {}

        return txt;
    }
}
