package test;

import model.logic.Dictionary;

public class DictionaryTest {

    public static void dictionaryTest() {

        Dictionary d = new Dictionary("text1.txt","text2.txt");

        if(!d.query("is"))
            System.out.println("problem with dictionarry in query");

        if(!d.challenge("lazy"))
            System.out.println("problem with dictionarry in query");
    }
}
