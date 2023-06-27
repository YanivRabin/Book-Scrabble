package test;

import model.logic.Dictionary;

public class DictionaryTest {

    /**
     * The dictionaryTest function tests the Dictionary class.
     * It creates a new dictionary object and then queries it for two words, &quot;IS&quot; and &quot;LAZY&quot;.
     * If either of these queries returns false, an error message is printed to the console.

     *
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
    public static void dictionaryTest() {

        Dictionary d = new Dictionary("text1.txt","text2.txt");

        if(!d.query("IS"))
            System.out.println("problem with dictionary in query 1");

        if(!d.challenge("LAZY"))
            System.out.println("problem with dictionary in query 2");
    }
}
