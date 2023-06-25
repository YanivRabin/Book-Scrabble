package model.logic;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MyServer {

    ClientHandler clientHandler;
    ServerSocket server;
    private static MyServer singleServer = null;
    int port;
    String IP;
    boolean stop;
    public List<Socket> HostsList;
    static ExecutorService executorService = Executors.newFixedThreadPool(10); // only for one host

    BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();



    public MyServer() {
        this.stop = false;
        this.HostsList = new ArrayList<>();
    }

    public static MyServer getServer(int port, ClientHandler ch) {

        if (singleServer == null)
            singleServer = new MyServer();

        return singleServer;
    }

    private static class MyServerModelHelper {
        public static final MyServer model_instance = new MyServer();
    }

    /**
     * The getModel function is a static function that returns the model instance of the Host class.
     *
     *
     *
     * @return The model_instance variable
     *
     * @docauthor Trelent
     */
    public static MyServer getModel() {
        return MyServer.MyServerModelHelper.model_instance;
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

    public void initMyServer(int port, ClientHandler clientHandler){
        this.clientHandler = clientHandler;
        this.port = port;
    }


    //start server
    public void start() {
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
        executorService.submit(this::handleRequests);
        System.out.println("Main Server started on port :" + this.port);
        this.IP = this.server.getInetAddress().getHostAddress();
        while (!stop) {

            Socket host = this.server.accept();
            this.HostsList.add(host);

            executorService.execute(() -> {
                try {
                    this.handleClient(host.getInputStream(), host.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("Host Connected, Number of Hosts Connected: "+ HostsList.size());

            }
    }

    public void handleClient(InputStream inputStream, OutputStream outputStream) {
        while (!this.server.isClosed()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String jsonString = bufferedReader.readLine();
                if (jsonString != null)// Read an object from the server
                {
                    try {
                        inputQueue.put(jsonString); // Put the received object in the queue
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }  catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void handleRequests() {
        while (!this.server.isClosed()) {
            try {
                String jsonString = inputQueue.take();//blocking call
                System.out.println(jsonString);
                JsonObject json = JsonHandler.convertStringToJsonObject(jsonString);
                Socket currentHost = getSocket(json.get("SocketSource").getAsString());
                this.clientHandler.handleClient(new ByteArrayInputStream(jsonString.getBytes()),currentHost.getOutputStream());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public Socket getSocket(String source){
        String[] socketSplited = source.split(":");
        String ipSource = socketSplited[0].split("/")[1];
        String portSource = socketSplited[1];
        for(Socket s : this.HostsList){
            if (s.getPort() == Integer.parseInt(portSource)){
                return s;
            }
        }
        return null;
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


