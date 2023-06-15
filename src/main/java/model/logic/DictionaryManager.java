package model.logic;


import java.util.HashMap;

public class DictionaryManager {

    private static DictionaryManager singleDM = null;
    HashMap<String, Dictionary> books;

    //ctr
    /**
     * The DictionaryManager function is used to create a new DictionaryManager object.
     * The constructor initializes the books HashMap and adds three books to it: AliceInWonderland, HarryPotter, and TheMatrix.

     *
     *
     * @return A hashmap&lt;string, dictionary&gt;
     *
     * @docauthor Trelent
     */
    public DictionaryManager() {
        // add books
        books = new HashMap<>();
        books.put("AliceInWonderland", new Dictionary("src/main/resources/books/alice_in_wonderland.txt"));
        books.put("HarryPotter", new Dictionary("src/main/resources/books/HarrayPotter.txt"));
        books.put("TheMatrix", new Dictionary("src/main/resources/books/TheMatrix.txt"));
    }

    /**
     * The get function is a singleton function that returns the DictionaryManager
     * object. If there is no DictionaryManager object, it creates one and then
     * returns it. This ensures that only one instance of the class exists at any time.

     *
     *
     * @return A singleton dictionarymanager
     *
     * @docauthor Trelent
     */
    public static DictionaryManager get() {

        //singleton DictionaryManager
        if (singleDM == null)
            singleDM = new DictionaryManager();

        return singleDM;
    }

    private static class DictionaryManagerModelHelper {
        public static final DictionaryManager model_instance = new DictionaryManager();
    }

    /**
     * The getModel function is a static function that returns the singleton instance of the DictionaryManager class.
     *
     *
     *
     * @return The model instance
     *
     * @docauthor Trelent
     */
    public static DictionaryManager getModel() {
        return DictionaryManagerModelHelper.model_instance;
    }

    /**
     * The query function checks if a word exist in the books dictionary.
     *
     *
     * @param String... args Pass in a variable number of arguments
     *
     * @return True if the word is in any of the dictionaries
     *
     * @docauthor Trelent
     */
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

    /**
     * The challenge function checks if a word exist in the books dictionary.
     *
     *
     * @param String... args Pass in a variable number of arguments
     *
     * @return True if the word exists in any of the dictionaries
     *
     * @docauthor Trelent
     */
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

    /**
     * The getSize function returns the number of books in the library.
     *
     *
     *
     * @return The number of books in the library
     *
     * @docauthor Trelent
     */
    public int getSize() { return books.size(); }
}
