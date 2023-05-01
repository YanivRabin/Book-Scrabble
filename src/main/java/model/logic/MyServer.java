package model.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MyServer implements ClientHandler {

    int port;
    ClientHandler clientHandler;
    ServerSocket server;
    boolean stop;

    //ctr
    public MyServer(int p, ClientHandler ch) {

        port = p;
        clientHandler = ch;
        stop = false;
    }

    //start server
    public void start() {

        //run server in the background
        new Thread(() -> {

            try {
                runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void runServer() throws IOException {

        //open server with the port that given
        server = new ServerSocket(port);
        server.setSoTimeout(1000);

        while (!stop) {

            try {

                //try to connect a client
                Socket client = server.accept();
                try {

                    //handle the given client with handleClient func
                    clientHandler.handleClient(client.getInputStream(), client.getOutputStream());
                    client.close();
                    clientHandler.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }
        }
        server.close();
    }

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {}

    @Override
    public void close() { stop = true; }

}
