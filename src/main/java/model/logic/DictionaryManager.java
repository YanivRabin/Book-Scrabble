package model.logic;


import java.util.HashMap;

public class DictionaryManager {

    private static DictionaryManager singleDM = null;
    HashMap<String, Dictionary> books;

    //ctr
    public DictionaryManager() {
        // add books
        books = new HashMap<>();
        books.put("AliceInWonderland", new Dictionary("src/main/resources/books/alice_in_wonderland.txt"));
        books.put("HarryPotter", new Dictionary("src/main/resources/books/HarrayPotter.txt"));
        books.put("TheMatrix", new Dictionary("src/main/resources/books/TheMatrix.txt"));
    }

    public static DictionaryManager get() {

        //singleton DictionaryManager
        if (singleDM == null)
            singleDM = new DictionaryManager();

        return singleDM;
    }

    private static class DictionaryManagerModelHelper {
        public static final DictionaryManager model_instance = new DictionaryManager();
    }

    public static DictionaryManager getModel() {
        return DictionaryManagerModelHelper.model_instance;
    }

    public boolean query(String... args) {

        boolean wordExist = false;
        String word = args[args.length - 1];

        //iterate args except the last one
        for (int i = 0; i < args.length - 1; i++)
            //check if book not exist in books then add him
            if(!books.containsKey(args[i]))
                books.put(args[i], new Dictionary(args[i]));

        //iterate books dictionary and check if word exist
        for (Dictionary bookDictionary : books.values())
            if (bookDictionary.query(word))
                wordExist = true;

        return wordExist;
    }

    public boolean challenge(String... args) {

        boolean wordExist = false;
        String word = args[args.length - 1];

        //iterate args except the last one
        for (int i = 0; i < args.length - 1; i++)
            //check if book not exist in books then add him
            if(!books.containsKey(args[i]))
                books.put(args[i], new Dictionary(args[i]));

        //iterate books dictionary and check if word exist
        for (Dictionary bookDictionary : books.values())
            if (bookDictionary.challenge(word))
                wordExist = true;

        return wordExist;
    }

    public int getSize() { return books.size(); }
}
