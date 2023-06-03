package model.logic;

import model.logic.ClientHandler;
import model.logic.DictionaryManager;

import java.io.*;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler {

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {

        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(inFromClient)));
        String str = null;
        String[] arrWords;

        //read line and split words by comma
        if (scanner.hasNextLine())
            str = scanner.nextLine();

        arrWords = str.split(",");

        //first word is Query or Challenge
        String QorC = arrWords[0];

        DictionaryManager dictionaryManager = DictionaryManager.getModel();
        String[] args = new String[arrWords.length - 1];

        //copy all the files and the word to args
        for (int i = 0; i < arrWords.length - 1; i ++)
            args[i] = arrWords[i + 1];

        //write true or false to client
        PrintWriter out = new PrintWriter(outToClient);

        boolean flag;
        //send args to Query or Challenge
        if (QorC.equals("Q"))
            flag = dictionaryManager.query(args);
        else
            flag = dictionaryManager.challenge(args);

        if (flag) {

            out.println("true");
            out.flush();
        }
        else {

            out.println("false");
            out.flush();
        }

        scanner.close();
        out.close();
    }

    @Override
    public void close() {}
}
