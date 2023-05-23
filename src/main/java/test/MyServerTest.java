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

        @Override
        public void handleClient(InputStream inFromClient, OutputStream outToClient) {

            out = new PrintWriter(outToClient);
            in = new Scanner(inFromClient);
            String text = in.next();
            out.println(new StringBuilder(text).reverse());
            out.flush();
        }

        @Override
        public void close() {
            in.close();
            out.close();
        }
    }

    public static void serverTest() {

        int port = 1234;

        // Create a server instance
        MyServer server = new MyServer(port, new ClientHandler1());
        server.start();

        for(int i = 0; i < 4; i++) {

            try { new Socket("localhost", port); }
            catch (IOException e) { e.printStackTrace(); }
        }
        try { Thread.sleep(1000); }
        catch (InterruptedException e) { e.printStackTrace(); }

        if (server.hosts.size() != 4)
            System.out.println("The server didn't connect 4 players");

        server.close();
        System.out.println("end");
    }


}


