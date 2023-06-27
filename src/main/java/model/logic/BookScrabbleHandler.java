package model.logic;

import com.google.gson.JsonObject;
import model.logic.ClientHandler;
import model.logic.DictionaryManager;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler {

    /**
     * The handleClient function is responsible for handling the client's request.
     * It reads a line from the client, and then sends back to it a response.
     *
     *
     * @param inFromClient inFromClient Read from the client
     * @param outToClient outToClient Write the response to the client
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inFromClient));
        try {

            String jsonString = bufferedReader.readLine();
            JsonObject json = JsonHandler.convertStringToJsonObject(jsonString);
            String str = json.get("Message").getAsString();
            String[] arrWords;


            System.out.println(str);
            arrWords = str.split(",");

            //first word is Query or Challenge
            String QorC = arrWords[0];

            DictionaryManager dictionaryManager = DictionaryManager.getModel();
            String[] args = new String[arrWords.length - 1];

            //copy all the files and the word to args
            for (int i = 0; i < arrWords.length - 1; i++)
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
            } else {

                out.println("false");
                out.flush();
            }

//            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The close function is used to close the connection between the client and server.
     * This function is called when a user logs out of their account, or if they are
     * disconnected from the server for any reason. The function closes all open streams,
     * and then closes the socket that was created in order to connect with this client.

     *
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    @Override
    public void close() {}
}
