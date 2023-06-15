package model.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {

    ClientHandler clientHandler;
    ServerSocket server;
    private static MyServer singleServer = null;
    int port;
    String IP;
    boolean stop;
    public List<Socket> HostsList;
    static ExecutorService executorService = Executors.newFixedThreadPool(2); // only for one host


    //ctr
    /**
     * The MyServer function is a constructor that initializes the server with a port number and client handler.
     *
     *
     * @param int port Set the port number of the server
     * @param ClientHandler ch Pass the client handler to the server
     *
     * @return A boolean value
     *
     * @docauthor Trelent
     */
    public MyServer(int port, ClientHandler ch) {

        this.clientHandler = ch;
        this.port = port;
        this.stop = false;
        this.HostsList = new ArrayList<>();
    }

    /**
     * The getServer function is a static function that returns the singleton server.
     *
     *
     * @param int port Set the port number that the server will listen to
     * @param ClientHandler ch Set the client handler of the server
     *
     * @return The singleserver object
     *
     * @docauthor Trelent
     */
    public static MyServer getServer(int port, ClientHandler ch) {

        if (singleServer == null)
            singleServer = new MyServer(port, ch);

        return singleServer;
    }
    /**
     * The getPort function returns the port number of the server.
     *
     *
     *
     * @return The port number
     *
     * @docauthor Trelent
     */
    public int getPort() {
        return port;
    }
    /**
     * The getIP function returns the IP address of the client.
     *
     *
     *
     * @return The ip of the client
     *
     * @docauthor Trelent
     */
    public String getIP() {
        return IP;
    }

    /**
     * The getHostsList function returns the list of hosts that are connected to the server.
     *
     *
     *
     * @return A list of sockets
     *
     * @docauthor Trelent
     */
    public List<Socket> getHostsList() {
        return HostsList;
    }

    //start server
    /**
     * The start function creates a new thread and runs the runServer function in it.
     * This is done so that the server can be running in the background while other functions are being executed.

     *
     *
     * @return Void, so it is not possible to return anything from the start function
     *
     * @docauthor Trelent
     */
    public void start() {

        //run server in the background
        /*new Thread(() -> {

            try {
                runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();*/

        executorService.execute(()->{
            try {
                runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * The runServer function is responsible for opening a server socket on the port given in the constructor.
     * The function then waits for clients to connect, and when they do, it passes their input and output streams
     * to the clientHandler's handleClient method.

     *
     *
     * @return Void
     *
     * @docauthor Trelent
     */
    public void runServer() throws IOException {

        //open server with the port that given
        this.server = new ServerSocket(port);
        System.out.println("Main Server started on port :" + this.port);
//        this.server.setSoTimeout(1000);
        this.IP = this.server.getInetAddress().getHostAddress();
        while (!stop) {

            Socket host = this.server.accept();
            this.HostsList.add(host);
            /*Thread hostThread = new Thread(() -> {
                try {
                    this.clientHandler.handleClient(host.getInputStream(), host.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            hostThread.start();*/

            executorService.execute(() -> {
                try {
                    this.clientHandler.handleClient(host.getInputStream(), host.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("Host Connected, Number of Hosts Connected: "+ HostsList.size());

           // clientHandler.handleClient(host.getInputStream(), host.getOutputStream());
            // Implement with thread pool
            }
    }

    /**
     * The close function is used to close the server and all of its connections.
     * It does this by setting a boolean variable, stop, to true. This will cause
     * the while loop in run() to terminate when it next checks for stop's value.
     * The executorService is then shutdownNow(), which causes any threads that are
     * currently running tasks (i.e., those that have not yet terminated) to be interrupted
     * and shut down immediately upon their termination or interruption (whichever comes first).

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public void close() throws IOException {

        stop = true;
        executorService.shutdownNow();
        for (Socket c : this.HostsList) {
            try { c.close(); }
            catch (IOException e) { e.printStackTrace(); }
        }
        this.HostsList.clear();

        if (this.server != null && !this.server.isClosed()) {
            try { this.server.close(); }
            catch (IOException e) { e.printStackTrace(); }
            System.out.println("Main Server closed.");
        }
    }
}


