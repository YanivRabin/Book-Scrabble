package model.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    ClientHandler clientHandler;
    ServerSocket server;
    private static MyServer singleServer = null;
    int port;
    String IP;
    boolean stop;
    public List<Socket> hosts;

    //ctr
    public MyServer(int port, ClientHandler ch) {

        this.clientHandler = ch;
        this.port = port;
        this.stop = false;
        this.hosts = new ArrayList<>();
    }

    public static MyServer getServer(int port, ClientHandler ch) {

        if (singleServer == null)
            singleServer = new MyServer(port, ch);

        return singleServer;
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
        System.out.println("Main Server started on port :" + this.port);
//        this.server.setSoTimeout(1000);
        this.IP = this.server.getInetAddress().getHostAddress();
        while (!stop) {

            Socket host = this.server.accept();
            this.hosts.add(host);
            System.out.println("Host Connected, Number of Hosts Connected: "+hosts.size());

            clientHandler.handleClient(host.getInputStream(), host.getOutputStream());
            // Implement with thread pool
            }
    }


    public void close() {

        stop = true;
        for (Socket c : this.hosts) {
            try { c.close(); }
            catch (IOException e) { e.printStackTrace(); }
        }

        this.hosts.clear();

        if (this.server != null && !this.server.isClosed()) {
            try { this.server.close(); }
            catch (IOException e) { e.printStackTrace(); }
            System.out.println("Server closed.");
        }
    }
}


