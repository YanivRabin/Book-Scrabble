package model.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer implements ClientHandler {

    ClientHandler clientHandler;
    int port;
    ServerSocket server;
    boolean stop;
    public final int maxPlayers = 4;
    public List<Socket> clients ;

    //ctr
    public MyServer(int port, ClientHandler ch) {

        this.clientHandler = ch;
        this.port = port;
        this.stop = false;
        this.clients = new ArrayList<>();
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
        this.server = new ServerSocket(port);
        System.out.println("Server started on port :" + this.port);
//        this.server.setSoTimeout(1000);
//        String hostAddress = this.server.getInetAddress().getHostAddress();
        while (!stop && this.clients.size() < this.maxPlayers) {

            Socket socketClient = this.server.accept();
            addClient(socketClient);
            }

        if (this.clients.size() == this.maxPlayers)
            System.out.println("Maximum number of players reached");
    }

    public void addClient(Socket socketClient) {

        this.clients.add(socketClient);
        System.out.println("Client Connected " + socketClient.toString());
    }

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {

    }

    @Override
    public void close() {

        stop = true;
        for (Socket c : this.clients) {
            try { c.close(); }
            catch (IOException e) { e.printStackTrace(); }
        }

        this.clients.clear();

        if (this.server != null && !this.server.isClosed()) {
            try { this.server.close(); }
            catch (IOException e) { e.printStackTrace(); }
            System.out.println("Server closed.");
        }
    }
}


