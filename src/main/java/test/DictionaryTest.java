package test;

import model.logic.Dictionary;

public class DictionaryTest {

    public static void dictionaryTest() {

        Dictionary d = new Dictionary("text1.txt","text2.txt");

        if(!d.query("IS"))
            System.out.println("problem with dictionary in query 1");

        if(!d.challenge("LAZY"))
            System.out.println("problem with dictionary in query 2");
    }
}
