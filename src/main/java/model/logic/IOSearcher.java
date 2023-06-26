package model.logic;
import java.io.*;
import java.util.Locale;
import java.util.Scanner;


public class IOSearcher {

    /**
     * The search function takes a string and an array of strings as arguments.
     * It then searches through each file in the array for the given string,
     * returning true if it is found and false otherwise.

     *
     * @param str str Compare the words in the file to
     * @param args args Pass an array of strings to the function
     *
     * @return True if the string str is found in any of the files
     *
     * @docauthor Trelent
     */
    public static boolean search(String str, String... args)  {

        String[] files = args;

        for (String file : files) {

            try {

                Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
                String[] arrWords;
                while (scanner.hasNext()) {

                    arrWords = scanner.next().split(" ");
                    for (String word : arrWords) {

                        String word2 = word.toUpperCase();
                        if (word2.equals(str))
                            return true;
                    }

                }
                scanner.close();
            }
            catch (FileNotFoundException e) { e.printStackTrace(); }
        }
        return false;
    }
}
