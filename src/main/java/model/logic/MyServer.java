package model.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    int port;
    ServerSocket server;
    boolean stop;
    final int maxPleyers = 4;
    List<Client> clients ;
    int connectedClients ;

    //ctr
    public MyServer(int port) {

        this.port = port;
        this.stop = false;
        this.clients = new ArrayList<>();
        this.connectedClients = 0;
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
        this.server.setSoTimeout(1000);
        String hostAddress = this.server.getInetAddress().getHostAddress();
        while (!stop && this.connectedClients < this.maxPleyers) {
            try {
                //try to connect a client
                Socket socketClient = this.server.accept();
                Client client = new Client(socketClient);
                this.clients.add(client);
                this.connectedClients ++;
                client.start();

                if (this.connectedClients == this.maxPleyers)
                {
                    System.out.println("Maximum number of players reached");
                    break;
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }
            finally {
                this.ServerClose();
            }
        }
    }

    public void ServerClose() throws IOException {
        stop = true;
        for (Client c : this.clients){
            c.close();
        }
        this.clients.clear();

        if (this.server != null && !this.server.isClosed()) {
            this.server.close();
            System.out.println("Server closed.");
        }

    }
}



