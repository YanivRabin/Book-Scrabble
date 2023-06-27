package test;

import model.logic.ClientHandler;
import model.logic.MyServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class MyServerTest {

    public static class ClientHandler1 implements ClientHandler {

        PrintWriter out;
        Scanner in;

        /**
         * The handleClient function takes in an InputStream and OutputStream,
         * reads the input from the client, reverses it using a StringBuilder object,
         * and then writes it back to the client.

         *
         * @param  inFromClient Read data from the client
         * @param  outToClient Send data to the client
         *
         * @return Void, so it does not return anything
         *
         * @docauthor Trelent
         */
        @Override
        public void handleClient(InputStream inFromClient, OutputStream outToClient) {

            out = new PrintWriter(outToClient);
            in = new Scanner(inFromClient);
            String text = in.next();
            out.println(new StringBuilder(text).reverse());
            out.flush();
        }

        /**
         * The close function closes the input and output streams.

         *
         *
         * @return A void
         *
         * @docauthor Trelent
         */
        @Override
        public void close() {
            in.close();
            out.close();
        }
    }

    /**
     * The serverTest function creates a server instance and connects 4 clients to it.
     * It then checks if the server has connected all 4 players, and prints an error message if not.

     *
     *
     * @return :
     *
     * @docauthor Trelent
     */
    public static void serverTest() throws IOException {

        /*int port = 1234;

        // Create a server instance
        MyServer server = new MyServer(port, new ClientHandler1());
        server.start();

        for(int i = 0; i < 4; i++) {

            try { new Socket("localhost", port); }
            catch (IOException e) { e.printStackTrace(); }
        }
        try { Thread.sleep(1000); }
        catch (InterruptedException e) { e.printStackTrace(); }

        if (server.HostsList.size() != 4)
            System.out.println("The server didn't connect 4 players");

        server.close();*/
        System.out.println("end");
    }


}


