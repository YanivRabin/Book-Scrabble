package model.logic;

import model.logic.ClientHandler;
import model.logic.DictionaryManager;

import java.io.*;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler {

    /**
     * The handleClient function is responsible for handling the client's request.
     * It reads a line from the client, splits it into words by comma and sends them to Query or Challenge function.
     * Then it writes true or false to the client according to what was returned from Query or Challenge function.

     *
     * @param InputStream inFromClient Read the input from the client
     * @param OutputStream outToClient Send data back to the client
     *
     * @return Void, since it is a void function
     *
     * @docauthor Trelent
     */
    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {

        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(inFromClient)));
        String str = null;
        String[] arrWords;

        //read line and split words by comma
        if (scanner.hasNextLine())
            str = scanner.nextLine();

        System.out.println(str);
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
